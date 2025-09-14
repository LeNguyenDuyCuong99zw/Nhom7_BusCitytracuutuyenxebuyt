package com.bushcm.javavbusproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bus_route")
public class BusRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number")
    private String routeNumber;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "fare")
    private String fare;

    @Column(name = "headway_minutes")
    private Integer headwayMinutes;

    @Column(name = "schedule_description", columnDefinition = "text")
    private String scheduleDescription;

    @Column(name = "schedule_chart", columnDefinition = "text")
    private String scheduleChart;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getFare() { return fare; }
    public void setFare(String fare) { this.fare = fare; }

    public Integer getHeadwayMinutes() { return headwayMinutes; }
    public void setHeadwayMinutes(Integer headwayMinutes) { this.headwayMinutes = headwayMinutes; }

    public String getScheduleDescription() { return scheduleDescription; }
    public void setScheduleDescription(String scheduleDescription) { this.scheduleDescription = scheduleDescription; }

    public String getScheduleChart() { return scheduleChart; }
    public void setScheduleChart(String scheduleChart) { this.scheduleChart = scheduleChart; }
}