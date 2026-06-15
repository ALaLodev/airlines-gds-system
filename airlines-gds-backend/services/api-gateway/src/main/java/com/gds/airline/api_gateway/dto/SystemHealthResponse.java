package com.gds.airline.api_gateway.dto;

import java.util.List;
import java.util.Map;

public class SystemHealthResponse {
    private Map<String, ServiceStatus> services;
    private double globalLatencyMs;
    private double requestVolumeRpm;
    private InfrastructureLoad infrastructureLoad;
    private List<SystemLog> logs;

    public SystemHealthResponse() {
    }

    public SystemHealthResponse(Map<String, ServiceStatus> services, double globalLatencyMs, double requestVolumeRpm, InfrastructureLoad infrastructureLoad, List<SystemLog> logs) {
        this.services = services;
        this.globalLatencyMs = globalLatencyMs;
        this.requestVolumeRpm = requestVolumeRpm;
        this.infrastructureLoad = infrastructureLoad;
        this.logs = logs;
    }

    public Map<String, ServiceStatus> getServices() {
        return services;
    }

    public void setServices(Map<String, ServiceStatus> services) {
        this.services = services;
    }

    public double getGlobalLatencyMs() {
        return globalLatencyMs;
    }

    public void setGlobalLatencyMs(double globalLatencyMs) {
        this.globalLatencyMs = globalLatencyMs;
    }

    public double getRequestVolumeRpm() {
        return requestVolumeRpm;
    }

    public void setRequestVolumeRpm(double requestVolumeRpm) {
        this.requestVolumeRpm = requestVolumeRpm;
    }

    public InfrastructureLoad getInfrastructureLoad() {
        return infrastructureLoad;
    }

    public void setInfrastructureLoad(InfrastructureLoad infrastructureLoad) {
        this.infrastructureLoad = infrastructureLoad;
    }

    public List<SystemLog> getLogs() {
        return logs;
    }

    public void setLogs(List<SystemLog> logs) {
        this.logs = logs;
    }

    public static class ServiceStatus {
        private String status; // UP, DOWN, WARNING
        private double uptime; // 99.98 etc
        private String message;

        public ServiceStatus() {
        }

        public ServiceStatus(String status, double uptime, String message) {
            this.status = status;
            this.uptime = uptime;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getUptime() {
            return uptime;
        }

        public void setUptime(double uptime) {
            this.uptime = uptime;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class InfrastructureLoad {
        private int activeNodes;
        private double cpuUsage;
        private double memoryUsage;
        private double diskIo;

        public InfrastructureLoad() {
        }

        public InfrastructureLoad(int activeNodes, double cpuUsage, double memoryUsage, double diskIo) {
            this.activeNodes = activeNodes;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.diskIo = diskIo;
        }

        public int getActiveNodes() {
            return activeNodes;
        }

        public void setActiveNodes(int activeNodes) {
            this.activeNodes = activeNodes;
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public double getMemoryUsage() {
            return memoryUsage;
        }

        public void setMemoryUsage(double memoryUsage) {
            this.memoryUsage = memoryUsage;
        }

        public double getDiskIo() {
            return diskIo;
        }

        public void setDiskIo(double diskIo) {
            this.diskIo = diskIo;
        }
    }

    public static class SystemLog {
        private String timestamp;
        private String level; // INFO, WARN, ALERT
        private String message;

        public SystemLog() {
        }

        public SystemLog(String timestamp, String level, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
