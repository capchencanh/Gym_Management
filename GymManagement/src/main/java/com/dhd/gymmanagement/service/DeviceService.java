package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DeviceService {
    
    List<Device> getAllDevices();
    Page<Device> getAllDevices(Pageable pageable);
    Optional<Device> getDeviceById(Integer deviceId);
    Device createDevice(Device device);
    Device createDeviceWithImage(Device device, MultipartFile imageFile);
    Device updateDevice(Integer deviceId, Device device);
    Device updateDeviceWithImage(Integer deviceId, Device device, MultipartFile imageFile);
    void deleteDevice(Integer deviceId);
    
    List<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location);
    Page<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location, Pageable pageable);
    List<Device> getDevicesByStatus(Device.DeviceStatus status);
    List<Device> getDevicesNeedingMaintenance();
    
    long countDevicesByStatus(Device.DeviceStatus status);
    long countTotalDevices();
    
    List<Device> getAllDevicesForStats();
    
    void updateDeviceStatus(Integer deviceId, Device.DeviceStatus status);
    void addMaintenanceNote(Integer deviceId, String note);
    void updateMaintenanceDate(Integer deviceId, String maintenanceDate);
    void updateLastServiceDate(Integer deviceId, String serviceDate);
    

    Device removeDeviceImage(Integer deviceId);
}
