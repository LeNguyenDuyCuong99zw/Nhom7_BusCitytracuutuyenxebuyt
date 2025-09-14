package com.bushcm.javavbusproject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bushcm.javavbusproject.entity.BusStopReturn;
import com.bushcm.javavbusproject.repository.BusStopReturnRepository;

@Service
public class BusStopReturnService {
    @Autowired
    private BusStopReturnRepository busStopReturnRepository;

    public List<BusStopReturn> getStopsByRouteId(Long routeId) {
        return busStopReturnRepository.findByRouteIdOrderByStopOrderAsc(routeId);
    }
}
