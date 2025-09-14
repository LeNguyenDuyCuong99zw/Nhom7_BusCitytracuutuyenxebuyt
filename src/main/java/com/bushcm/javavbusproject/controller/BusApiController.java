package com.bushcm.javavbusproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bushcm.javavbusproject.entity.BusRoute;
import com.bushcm.javavbusproject.entity.BusStop;
import com.bushcm.javavbusproject.repository.BusRouteRepository;
import com.bushcm.javavbusproject.repository.BusStopRepository;
import com.bushcm.javavbusproject.services.ExternalBusApiService;

@RestController
@RequestMapping("/api/bus")
public class BusApiController {
    private final ExternalBusApiService apiService;
    private final BusRouteRepository busRouteRepo;
    private final BusStopRepository busStopRepo;

    public BusApiController(
        ExternalBusApiService apiService,
        BusRouteRepository busRouteRepo,
        BusStopRepository busStopRepo
    ) {
        this.apiService = apiService;
        this.busRouteRepo = busRouteRepo;
        this.busStopRepo = busStopRepo;
    }

    @GetMapping("/stops")
    public String getStopsInBounds(
        @RequestParam double swLng,
        @RequestParam double swLat,
        @RequestParam double neLng,
        @RequestParam double neLat
    ) {
        return apiService.getBusStopsInBounds(swLng, swLat, neLng, neLat);
    }

    @GetMapping("/path")
    public String findBusPath(
        @RequestParam double startLat,
        @RequestParam double startLng,
        @RequestParam double endLat,
        @RequestParam double endLng,
        @RequestParam(defaultValue = "2") int maxTrip
    ) {
        return apiService.findBusPath(startLat, startLng, endLat, endLng, maxTrip);
    }

    // Thêm API lấy danh sách tuyến
    @GetMapping("/routes")
    public List<BusRoute> getRoutes() {
        return busRouteRepo.findAll();
    }

    // Thêm API lấy danh sách trạm theo tuyến
    @GetMapping("/route/{id}/stops")
    public List<BusStop> getStopsByRoute(@PathVariable Long id) {
        return busStopRepo.findByRouteIdOrderByStopOrderAsc(id);
    }
}