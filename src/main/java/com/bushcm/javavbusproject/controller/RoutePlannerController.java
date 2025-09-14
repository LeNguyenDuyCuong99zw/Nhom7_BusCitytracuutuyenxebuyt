package com.bushcm.javavbusproject.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bushcm.javavbusproject.services.RoutePlannerService;

@RestController
public class RoutePlannerController {

    private final RoutePlannerService planner;

    public RoutePlannerController(RoutePlannerService planner) {
        this.planner = planner;
    }

    @GetMapping("/api/bus/plan")
    public ResponseEntity<Map<String,Object>> plan(
            @RequestParam("startLat") double startLat,
            @RequestParam("startLng") double startLng,
            @RequestParam("endLat") double endLat,
            @RequestParam("endLng") double endLng) {

        Map<String,Object> plan = planner.plan(startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(plan);
    }
}
