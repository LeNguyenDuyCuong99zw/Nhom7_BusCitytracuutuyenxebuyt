package com.bushcm.javavbusproject.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bushcm.javavbusproject.entity.BusRoute;
import com.bushcm.javavbusproject.entity.BusStop;
import com.bushcm.javavbusproject.entity.BusStopReturn;
import com.bushcm.javavbusproject.entity.User;
import com.bushcm.javavbusproject.repository.BusRouteRepository;
import com.bushcm.javavbusproject.repository.BusStopRepository;
import com.bushcm.javavbusproject.repository.BusStopReturnRepository;
import com.bushcm.javavbusproject.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final UserRepository userRepo;
    private final BusRouteRepository routeRepo;
    private final BusStopRepository stopRepo;
    private final BusStopReturnRepository stopReturnRepo;

    public AdminController(UserRepository userRepo, BusRouteRepository routeRepo, BusStopRepository stopRepo, BusStopReturnRepository stopReturnRepo) {
        this.userRepo = userRepo;
        this.routeRepo = routeRepo;
        this.stopRepo = stopRepo;
        this.stopReturnRepo = stopReturnRepo;
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return "redirect:/"; // not authorized
        }
        return "admin/admin";
    }

    @RequestMapping("/api/admin/users")
    @ResponseBody
    public List<User> listUsers(HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return List.of();
        }
        return userRepo.findAll();
    }

    @PostMapping("/api/admin/delete-user")
    @ResponseBody
    public Map<String, Object> deleteUser(@RequestBody Map<String, Object> body, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            final Object idObj = body.get("id");
            if (idObj == null) return Map.of("error","missing");
            Long id = Long.valueOf(String.valueOf(idObj));
            userRepo.deleteById(id);
            return Map.of("ok", true);
        } catch (Exception e) {
            return Map.of("error", "failed", "msg", e.getMessage());
        }
    }

    // Routes CRUD
    @RequestMapping("/api/admin/routes")
    @ResponseBody
    public List<BusRoute> listRoutes(HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return List.of();
        }
        return routeRepo.findAll();
    }

    @PostMapping("/api/admin/save-route")
    @ResponseBody
    public Map<String, Object> saveRoute(@RequestBody BusRoute route, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            var saved = routeRepo.save(route);
            return Map.of("ok", true, "route", saved);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }

    @PostMapping("/api/admin/delete-route")
    @ResponseBody
    public Map<String, Object> deleteRoute(@RequestBody Map<String,Object> body, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            Long id = Long.valueOf(String.valueOf(body.get("id")));
            routeRepo.deleteById(id);
            return Map.of("ok", true);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }

    // Outbound stops (route direction)
    @RequestMapping("/api/admin/stops")
    @ResponseBody
    public List<BusStop> listStops(HttpSession session, Long routeId) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return List.of();
        }
        if (routeId == null) return stopRepo.findAll();
        return stopRepo.findByRouteIdOrderByStopOrderAsc(routeId);
    }

    @PostMapping("/api/admin/save-stop")
    @ResponseBody
    public Map<String,Object> saveStop(@RequestBody BusStop s, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            var saved = stopRepo.save(s);
            return Map.of("ok", true, "stop", saved);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }

    @PostMapping("/api/admin/delete-stop")
    @ResponseBody
    public Map<String,Object> deleteStop(@RequestBody Map<String,Object> body, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            Long id = Long.valueOf(String.valueOf(body.get("id")));
            stopRepo.deleteById(id);
            return Map.of("ok", true);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }

    // Return stops (reverse direction)
    @RequestMapping("/api/admin/stops-return")
    @ResponseBody
    public List<BusStopReturn> listReturnStops(HttpSession session, Long routeId) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return List.of();
        }
        if (routeId == null) return stopReturnRepo.findAll();
        return stopReturnRepo.findByRouteIdOrderByStopOrderAsc(routeId);
    }

    @PostMapping("/api/admin/save-stop-return")
    @ResponseBody
    public Map<String,Object> saveStopReturn(@RequestBody BusStopReturn s, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            var saved = stopReturnRepo.save(s);
            return Map.of("ok", true, "stop", saved);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }

    @PostMapping("/api/admin/delete-stop-return")
    @ResponseBody
    public Map<String,Object> deleteStopReturn(@RequestBody Map<String,Object> body, HttpSession session) {
        final Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(String.valueOf(role))) {
            return Map.of("error","unauth");
        }
        try {
            Long id = Long.valueOf(String.valueOf(body.get("id")));
            stopReturnRepo.deleteById(id);
            return Map.of("ok", true);
        } catch (Exception e) {
            return Map.of("error","failed","msg",e.getMessage());
        }
    }
}
