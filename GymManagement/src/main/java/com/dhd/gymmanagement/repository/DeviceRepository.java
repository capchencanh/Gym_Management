package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    
    List<Device> findByNameContainingIgnoreCase(String name);
    
    List<Device> findByTypeContainingIgnoreCase(String type);
    
    List<Device> findByStatus(Device.DeviceStatus status);
    
    List<Device> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT d FROM Device d WHERE " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type LIKE %:type%) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:location IS NULL OR d.location LIKE %:location%)")
    List<Device> searchDevices(@Param("name") String name,
                               @Param("type") String type,
                               @Param("status") Device.DeviceStatus status,
                               @Param("location") String location);
    
    long countByStatus(Device.DeviceStatus status);
    
    @Query("SELECT d FROM Device d")
    List<Device> findByIsDeletedFalse();
    
    @Query("SELECT d FROM Device d")
    List<Device> findAllActiveDevices();
    
    @Query("SELECT COUNT(d) FROM Device d")
    long countByIsDeletedFalse();
    
    @Query("SELECT d FROM Device d WHERE d.maintenanceDate IS NOT NULL AND d.maintenanceDate <= CURRENT_DATE")
    List<Device> findDevicesNeedingMaintenance();
    
    @Query("SELECT d FROM Device d")
    List<Device> findAllDevices();
}
