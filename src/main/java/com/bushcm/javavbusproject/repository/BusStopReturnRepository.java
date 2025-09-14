package com.bushcm.javavbusproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bushcm.javavbusproject.entity.BusStopReturn;

public interface BusStopReturnRepository extends JpaRepository<BusStopReturn, Long> {
    List<BusStopReturn> findByRouteIdOrderByStopOrderAsc(Long routeId);
}
