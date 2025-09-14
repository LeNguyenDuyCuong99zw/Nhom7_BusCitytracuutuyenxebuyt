package com.bushcm.javavbusproject.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalBusApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getBusStopsInBounds(double swLng, double swLat, double neLng, double neLat) {
        String url = String.format(
            "http://apicms.ebms.vn/businfo/getstopsinbounds/%f/%f/%f/%f",
            swLng, swLat, neLng, neLat
        );
        return restTemplate.getForObject(url, String.class);
    }

    public String findBusPath(double startLat, double startLng, double endLat, double endLng, int maxTrip) {
        String url = String.format(
            "http://apicms.ebms.vn/pathfinding/getpathbystop/%f,%f/%f,%f/%d",
            startLat, startLng, endLat, endLng, maxTrip
        );
        return restTemplate.getForObject(url, String.class);
    }
}
