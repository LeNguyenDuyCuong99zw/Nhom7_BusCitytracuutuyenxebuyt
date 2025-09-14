package com.bushcm.javavbusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bushcm.javavbusproject.entity.BusRoute;

public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {}