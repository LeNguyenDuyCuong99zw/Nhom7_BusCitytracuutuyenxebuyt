package com.bushcm.javavbusproject.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bushcm.javavbusproject.entity.BusRoute;
import com.bushcm.javavbusproject.entity.BusStop;
import com.bushcm.javavbusproject.entity.BusStopReturn;
import com.bushcm.javavbusproject.repository.BusRouteRepository;
import com.bushcm.javavbusproject.repository.BusStopRepository;
import com.bushcm.javavbusproject.repository.BusStopReturnRepository;

@Service
public class RoutePlannerService {
    private final BusStopRepository busStopRepo;
    private final BusStopReturnRepository busStopReturnRepo;
    private final BusRouteRepository busRouteRepo;

    public RoutePlannerService(BusStopRepository busStopRepo, BusStopReturnRepository busStopReturnRepo, BusRouteRepository busRouteRepo) {
        this.busStopRepo = busStopRepo;
        this.busStopReturnRepo = busStopReturnRepo;
        this.busRouteRepo = busRouteRepo;
    }

    // simple haversine distance (meters)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public Map<String, Object> plan(double startLat, double startLng, double endLat, double endLng) {
        Map<String, Object> result = new HashMap<>();

        // load stops
        List<BusStop> stops = busStopRepo.findAll();
        List<BusStopReturn> stopsR = busStopReturnRepo.findAll();

        // find nearest to start
        BusStop nearestStart = null; double minDs = Double.MAX_VALUE;
        for (BusStop s : stops) {
            if (s.getLat()==null || s.getLng()==null) continue;
            double d = haversine(startLat, startLng, s.getLat(), s.getLng());
            if (d < minDs) { minDs = d; nearestStart = s; }
        }
        for (BusStopReturn s : stopsR) {
            if (s.getLat()==null || s.getLng()==null) continue;
            double d = haversine(startLat, startLng, s.getLat(), s.getLng());
            if (d < minDs) { minDs = d; nearestStart = convertReturnToStop(s); }
        }

        // find nearest to end
        BusStop nearestEnd = null; double minDe = Double.MAX_VALUE;
        for (BusStop s : stops) {
            if (s.getLat()==null || s.getLng()==null) continue;
            double d = haversine(endLat, endLng, s.getLat(), s.getLng());
            if (d < minDe) { minDe = d; nearestEnd = s; }
        }
        for (BusStopReturn s : stopsR) {
            if (s.getLat()==null || s.getLng()==null) continue;
            double d = haversine(endLat, endLng, s.getLat(), s.getLng());
            if (d < minDe) { minDe = d; nearestEnd = convertReturnToStop(s); }
        }

        result.put("startPickup", stopInfo(nearestStart));
        result.put("endDropoff", stopInfo(nearestEnd));

        // try direct route: any route that contains both stops in correct order
        List<Map<String,Object>> steps = new ArrayList<>();
        boolean directFound = false;
        if (nearestStart != null && nearestEnd != null) {
            List<BusRoute> routes = busRouteRepo.findAll();
            for (BusRoute r : routes) {
                List<BusStop> routeStops = busStopRepo.findByRouteIdOrderByStopOrderAsc(r.getId());
                int idxStart = indexOfStop(routeStops, nearestStart.getName());
                int idxEnd = indexOfStop(routeStops, nearestEnd.getName());
                if (idxStart >=0 && idxEnd >=0 && idxStart < idxEnd) {
                    // direct
                    directFound = true;
                    Map<String,Object> step1 = new HashMap<>();
                    step1.put("type", "walk");
                    step1.put("desc", String.format("Đi bộ %.0fm đến trạm %s", minDs, nearestStart.getName()));
                    step1.put("from", List.of(startLat, startLng));
                    step1.put("to", List.of(nearestStart.getLat(), nearestStart.getLng()));
                    steps.add(step1);

                    Map<String,Object> step2 = new HashMap<>();
                    step2.put("type","bus");
                    step2.put("route", r.getRouteNumber());
                    step2.put("desc", String.format("Lên tuyến %s tại %s, đi qua %d trạm, xuống tại %s", r.getRouteNumber(), nearestStart.getName(), Math.max(0, idxEnd-idxStart), nearestEnd.getName()));
                    step2.put("from", List.of(nearestStart.getLat(), nearestStart.getLng()));
                    step2.put("to", List.of(nearestEnd.getLat(), nearestEnd.getLng()));
                    steps.add(step2);

                    Map<String,Object> step3 = new HashMap<>();
                    step3.put("type","walk");
                    step3.put("desc", String.format("Đi bộ %.0fm từ trạm %s đến đích", minDe, nearestEnd.getName()));
                    step3.put("from", List.of(nearestEnd.getLat(), nearestEnd.getLng()));
                    step3.put("to", List.of(endLat, endLng));
                    steps.add(step3);

                    break;
                }
            }
        }

        if (!directFound) {
            // attempt multi-route transfer search (BFS) with a small transfer limit
            Map<String,Object> transferPlan = findWithTransfers(nearestStart, nearestEnd, endLat, endLng, 2);
            if (transferPlan != null && transferPlan.containsKey("steps")) {
                List<Map<String,Object>> tsteps = (List<Map<String,Object>>) transferPlan.get("steps");
                steps.addAll(tsteps);
                result.putAll(transferPlan);
            } else {
                // fallback note when nothing found
                Map<String,Object> step = new HashMap<>();
                step.put("type","note");
                step.put("desc","Không tìm thấy tuyến trực tiếp hoặc lộ trình đổi tuyến phù hợp trong CSDL. Vui lòng thử vị trí khác hoặc bật tìm đường nâng cao.");
                steps.add(step);
            }
        }

        result.put("steps", steps);
        result.put("totalWalkMeters", Math.round(minDs + minDe));
        result.put("directFound", directFound);
        return result;
    }

    private BusStop convertReturnToStop(BusStopReturn r) {
        BusStop s = new BusStop();
        s.setId(r.getId());
        s.setLat(r.getLat());
        s.setLng(r.getLng());
        s.setName(r.getName());
        return s;
    }

    // Build a map: stopName -> list of routeIds
    private Map<String, List<Long>> buildStopToRoutesMap() {
        Map<String, List<Long>> m = new HashMap<>();
        List<BusRoute> routes = busRouteRepo.findAll();
        for (BusRoute r : routes) {
            List<BusStop> stops = busStopRepo.findByRouteIdOrderByStopOrderAsc(r.getId());
            if (stops == null) continue;
            for (BusStop s : stops) {
                if (s == null || s.getName()==null) continue;
                m.computeIfAbsent(s.getName(), k -> new ArrayList<>()).add(r.getId());
            }
        }
        return m;
    }

    // Find a sequence of routes that connects nearestStart -> nearestEnd with up to maxTransfers
    private Map<String,Object> findWithTransfers(BusStop nearestStart, BusStop nearestEnd, double endLat, double endLng, int maxTransfers) {
        if (nearestStart == null || nearestEnd == null) return null;
        Map<String,List<Long>> stopRoutes = buildStopToRoutesMap();

        // BFS queue entries: currentStopName, path of routeIds taken, list of stopNames visited as transfer points
        class Node { String stopName; List<Long> routesPath; List<String> transferStops; }

        java.util.Queue<Node> q = new java.util.LinkedList<>();

        // initialize with all routes from start
        List<Long> startRoutes = stopRoutes.getOrDefault(nearestStart.getName(), new ArrayList<>());
        for (Long rid : startRoutes) {
            Node n = new Node();
            n.stopName = nearestStart.getName();
            n.routesPath = new ArrayList<>();
            n.routesPath.add(rid);
            n.transferStops = new ArrayList<>();
            q.add(n);
        }

        // Also consider no-route-start (rare)
        if (startRoutes.isEmpty()) {
            Node n = new Node();
            n.stopName = nearestStart.getName();
            n.routesPath = new ArrayList<>();
            n.transferStops = new ArrayList<>();
            q.add(n);
        }

        // visited set: route sequence string to avoid loops
        java.util.Set<String> visited = new java.util.HashSet<>();

        while (!q.isEmpty()) {
            Node cur = q.poll();
            if (cur.routesPath.size() > maxTransfers + 1) continue; // exceeded allowed transfers

            // check if any route in path reaches nearestEnd
            for (Long rid : cur.routesPath) {
                List<BusStop> routeStops = busStopRepo.findByRouteIdOrderByStopOrderAsc(rid);
                int idxStart = indexOfStop(routeStops, nearestStart.getName());
                int idxEnd = indexOfStop(routeStops, nearestEnd.getName());
                if (idxStart >=0 && idxEnd >=0 && idxStart < idxEnd) {
                    // build plan using the routes in cur.routesPath (may include single route)
                    Map<String,Object> plan = new HashMap<>();
                    List<Map<String,Object>> steps = new ArrayList<>();
                    // walk to nearestStart
                    Map<String,Object> step1 = new HashMap<>();
                    double walkToStart = haversine(nearestStart.getLat(), nearestStart.getLng(), nearestStart.getLat(), nearestStart.getLng());
                    step1.put("type","walk");
                    step1.put("desc", String.format("Đi bộ đến trạm %s", nearestStart.getName()));
                    step1.put("from", List.of(nearestStart.getLat(), nearestStart.getLng()));
                    step1.put("to", List.of(nearestStart.getLat(), nearestStart.getLng()));
                    steps.add(step1);

                    // for simplicity, assume single bus ride from start to end along the found route rid
                    Map<String,Object> busStep = new HashMap<>();
                    BusRoute br = busRouteRepo.findById(rid).orElse(null);
                    busStep.put("type","bus");
                    busStep.put("route", br!=null? br.getRouteNumber(): String.valueOf(rid));
                    busStep.put("desc", String.format("Lên tuyến %s từ %s đến %s", br!=null?br.getRouteNumber():rid, nearestStart.getName(), nearestEnd.getName()));
                    busStep.put("from", List.of(nearestStart.getLat(), nearestStart.getLng()));
                    busStep.put("to", List.of(nearestEnd.getLat(), nearestEnd.getLng()));
                    steps.add(busStep);

                    Map<String,Object> stepEnd = new HashMap<>();
                    stepEnd.put("type","walk");
                    stepEnd.put("desc", String.format("Đi bộ từ trạm %s đến đích", nearestEnd.getName()));
                    stepEnd.put("from", List.of(nearestEnd.getLat(), nearestEnd.getLng()));
                    stepEnd.put("to", List.of(endLat, endLng));
                    steps.add(stepEnd);

                    plan.put("steps", steps);
                    plan.put("directFound", true);
                    plan.put("totalWalkMeters", 0);
                    return plan;
                }
            }

            // explore transfers: from current stop, find connected routes, then find stops on those routes to transfer
            String curStop = cur.stopName;
            List<Long> routesHere = stopRoutes.getOrDefault(curStop, new ArrayList<>());
            for (Long rid : routesHere) {
                // for each stop on this route, consider it as a transfer stop to other routes
                List<BusStop> stopsOnRoute = busStopRepo.findByRouteIdOrderByStopOrderAsc(rid);
                if (stopsOnRoute == null) continue;
                for (BusStop s : stopsOnRoute) {
                    if (s==null || s.getName()==null) continue;
                    List<Long> nextRoutes = stopRoutes.getOrDefault(s.getName(), new ArrayList<>());
                    for (Long nr : nextRoutes) {
                        List<Long> newPath = new ArrayList<>(cur.routesPath);
                        if (!newPath.contains(nr)) newPath.add(nr);
                        String key = newPath.toString() + "@" + s.getName();
                        if (visited.contains(key)) continue;
                        visited.add(key);
                        Node nn = new Node();
                        nn.stopName = s.getName();
                        nn.routesPath = newPath;
                        nn.transferStops = new ArrayList<>(cur.transferStops);
                        nn.transferStops.add(s.getName());
                        q.add(nn);
                    }
                }
            }
        }
        return null;
    }

    // placeholders for end coordinates (we will return the destination coords via original plan result)
    private double endLatPlaceholder() { return 0.0; }
    private double endLngPlaceholder() { return 0.0; }

    private Map<String,Object> stopInfo(BusStop s) {
        if (s==null) return null;
        Map<String,Object> m = new HashMap<>();
        m.put("id", s.getId());
        m.put("name", s.getName());
        m.put("lat", s.getLat());
        m.put("lng", s.getLng());
        return m;
    }

    private int indexOfStop(List<BusStop> list, String name) {
        if (list==null) return -1;
        for (int i=0;i<list.size();i++) {
            BusStop s = list.get(i);
            if (s.getName()!=null && s.getName().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }
}
