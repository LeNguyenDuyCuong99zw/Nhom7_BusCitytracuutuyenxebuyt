package com.bushcm.javavbusproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bushcm.javavbusproject.entity.BusStopReturn;
import com.bushcm.javavbusproject.services.BusStopReturnService;

@RestController
@RequestMapping("/api/bus/route")
public class BusStopReturnController {
    @Autowired
    private BusStopReturnService busStopReturnService;

    @GetMapping("/{routeId}/stops-return")
    public List<BusStopReturn> getStopsReturn(@PathVariable Long routeId) {
        return busStopReturnService.getStopsByRouteId(routeId);
    }
}
