package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Device;
import com.dhd.gymmanagement.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findByIsDeletedFalse();
    }
    
    @Override
    public Optional<Device> getDeviceById(Integer deviceId) {
        return deviceRepository.findById(deviceId);
    }
    
    @Override
    public Device createDevice(Device device) {
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        device.setIsDeleted(false);
        return deviceRepository.save(device);
    }
    
    @Override
    public Device updateDevice(Integer deviceId, Device deviceDetails) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        device.setName(deviceDetails.getName());
        device.setType(deviceDetails.getType());
        device.setStatus(deviceDetails.getStatus());
        device.setLocation(deviceDetails.getLocation());
        device.setMaintenanceDate(deviceDetails.getMaintenanceDate());
        device.setLastServiceDate(deviceDetails.getLastServiceDate());
        device.setNotes(deviceDetails.getNotes());
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
    
    @Override
    public void deleteDevice(Integer deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        device.setIsDeleted(true);
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
    
    @Override
    public List<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location) {
        return deviceRepository.searchDevices(name, type, status, location);
    }
    
    @Override
    public List<Device> getDevicesByStatus(Device.DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }
    
    @Override
    public List<Device> getDevicesNeedingMaintenance() {
        return deviceRepository.findDevicesNeedingMaintenance();
    }
    
    @Override
    public long countDevicesByStatus(Device.DeviceStatus status) {
        return deviceRepository.countByStatus(status);
    }
    
    @Override
    public long countTotalDevices() {
        return deviceRepository.countByIsDeletedFalse();
    }
    
    @Override
    public List<Device> getAllDevicesForDebug() {
        return deviceRepository.findAllDevices();
    }
    
    @Override
    public List<Device> getAllDevicesForStats() {
        return deviceRepository.findAllActiveDevices();
    }
    
    @Override
    public void updateDeviceStatus(Integer deviceId, Device.DeviceStatus status) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        device.setStatus(status);
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
    
    @Override
    public void addMaintenanceNote(Integer deviceId, String note) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        String currentNotes = device.getNotes();
        String newNotes = currentNotes != null ? currentNotes + "\n" + note : note;
        device.setNotes(newNotes);
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
    
    @Override
    public void updateMaintenanceDate(Integer deviceId, String maintenanceDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        if (maintenanceDate != null && !maintenanceDate.trim().isEmpty()) {
            device.setMaintenanceDate(LocalDate.parse(maintenanceDate));
        }
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
    
    @Override
    public void updateLastServiceDate(Integer deviceId, String serviceDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        if (serviceDate != null && !serviceDate.trim().isEmpty()) {
            device.setLastServiceDate(LocalDate.parse(serviceDate));
        }
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }
} 