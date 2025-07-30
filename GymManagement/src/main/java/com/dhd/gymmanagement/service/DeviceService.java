package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Device;
import java.util.List;
import java.util.Optional;

public interface DeviceService {
    
    List<Device> getAllDevices();
    Optional<Device> getDeviceById(Integer deviceId);
    Device createDevice(Device device);
    Device updateDevice(Integer deviceId, Device device);
    void deleteDevice(Integer deviceId);
    
    List<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location);
    List<Device> getDevicesByStatus(Device.DeviceStatus status);
    List<Device> getDevicesNeedingMaintenance();
    
    long countDevicesByStatus(Device.DeviceStatus status);
    long countTotalDevices();
    
    List<Device> getAllDevicesForDebug();
    
    List<Device> getAllDevicesForStats();
    
    void updateDeviceStatus(Integer deviceId, Device.DeviceStatus status);
    void addMaintenanceNote(Integer deviceId, String note);
    void updateMaintenanceDate(Integer deviceId, String maintenanceDate);
    void updateLastServiceDate(Integer deviceId, String serviceDate);
}
