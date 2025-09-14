package com.bushcm.javavbusproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bushcm.javavbusproject.entity.BusStop;

public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    List<BusStop> findByRouteIdOrderByStopOrderAsc(Long routeId);
}