package com.dhd.gymmanagement.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dhd.gymmanagement.entity.Device;
import com.dhd.gymmanagement.repository.DeviceRepository;
import com.dhd.gymmanagement.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private Cloudinary cloudinary;
    
    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findByIsDeletedFalse();
    }
    
    @Override
    public Page<Device> getAllDevices(Pageable pageable) {
        return deviceRepository.findByIsDeletedFalse(pageable);
    }
    
    @Override
    public Optional<Device> getDeviceById(Integer deviceId) {
        return deviceRepository.findById(deviceId);
    }
    
    @Override
    public Device createDevice(Device device) {
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setCreatedAt(localNow);
        device.setUpdatedAt(localNow);
        device.setIsDeleted(0);
        return deviceRepository.save(device);
    }
    
    public Device createDeviceWithImage(Device device, MultipartFile imageFile) {
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setCreatedAt(localNow);
        device.setUpdatedAt(localNow);
        device.setIsDeleted(0);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "folder", "gym_devices"));
                device.setImage((String) uploadResult.get("secure_url"));
            } catch (IOException e) {
                // Log error but don't fail the device creation
                System.err.println("Error uploading image: " + e.getMessage());
            }
        }
        
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
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        
        return deviceRepository.save(device);
    }
    
    public Device updateDeviceWithImage(Integer deviceId, Device deviceDetails, MultipartFile imageFile) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        device.setName(deviceDetails.getName());
        device.setType(deviceDetails.getType());
        device.setStatus(deviceDetails.getStatus());
        device.setLocation(deviceDetails.getLocation());
        device.setMaintenanceDate(deviceDetails.getMaintenanceDate());
        device.setLastServiceDate(deviceDetails.getLastServiceDate());
        device.setNotes(deviceDetails.getNotes());
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "folder", "gym_devices"));
                device.setImage((String) uploadResult.get("secure_url"));
            } catch (IOException e) {
                System.err.println("Error uploading image: " + e.getMessage());
            }
        }
        
        return deviceRepository.save(device);
    }
    
    @Override
    public void deleteDevice(Integer deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        device.setIsDeleted(1);
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        deviceRepository.save(device);
    }
    
    @Override
    public List<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location) {
        return deviceRepository.searchDevices(name, type, status, location);
    }
    
    @Override
    public Page<Device> searchDevices(String name, String type, Device.DeviceStatus status, String location, Pageable pageable) {
        return deviceRepository.searchDevices(name, type, status, location, pageable);
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
    public List<Device> getAllDevicesForStats() {
        return deviceRepository.findAllActiveDevices();
    }
    
    @Override
    public void updateDeviceStatus(Integer deviceId, Device.DeviceStatus status) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        device.setStatus(status);
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        deviceRepository.save(device);
    }
    
    @Override
    public void addMaintenanceNote(Integer deviceId, String note) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        String currentNotes = device.getNotes();
        String newNotes = currentNotes != null ? currentNotes + "\n" + note : note;
        device.setNotes(newNotes);
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        deviceRepository.save(device);
    }
    
    @Override
    public void updateMaintenanceDate(Integer deviceId, String maintenanceDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        if (maintenanceDate != null && !maintenanceDate.trim().isEmpty()) {
            device.setMaintenanceDate(LocalDate.parse(maintenanceDate));
        }
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        deviceRepository.save(device);
    }
    
    @Override
    public void updateLastServiceDate(Integer deviceId, String serviceDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        
        if (serviceDate != null && !serviceDate.trim().isEmpty()) {
            device.setLastServiceDate(LocalDate.parse(serviceDate));
        } else {
            device.setLastServiceDate(null);
        }
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        device.setUpdatedAt(localNow);
        deviceRepository.save(device);
    }
    
    @Override
    public Device removeDeviceImage(Integer deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        

        if (device.getImage() != null && !device.getImage().isEmpty()) {
            try {

                String imageUrl = device.getImage();
                if (imageUrl.contains("cloudinary.com")) {

                    String[] urlParts = imageUrl.split("/");
                    String fileName = urlParts[urlParts.length - 1];
                    String publicId = "gym_devices/" + fileName.substring(0, fileName.lastIndexOf("."));
                    
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            } catch (Exception e) {
                System.err.println("Error deleting image from Cloudinary: " + e.getMessage());

            }
        }
        

        device.setImage(null);
        
        return deviceRepository.save(device);
    }
} 
