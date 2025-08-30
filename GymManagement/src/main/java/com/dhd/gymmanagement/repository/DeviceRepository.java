package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    
    List<Device> findByNameContainingIgnoreCase(String name);
    Page<Device> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Device> findByTypeContainingIgnoreCase(String type);
    Page<Device> findByTypeContainingIgnoreCase(String type, Pageable pageable);
    
    List<Device> findByStatus(Device.DeviceStatus status);
    Page<Device> findByStatus(Device.DeviceStatus status, Pageable pageable);
    
    List<Device> findByLocationContainingIgnoreCase(String location);
    Page<Device> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    @Query("SELECT d FROM Device d WHERE " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type LIKE %:type%) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:location IS NULL OR d.location LIKE %:location%)")
    List<Device> searchDevices(@Param("name") String name,
                               @Param("type") String type,
                               @Param("status") Device.DeviceStatus status,
                               @Param("location") String location);
    @Query("SELECT d FROM Device d WHERE " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type LIKE %:type%) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:location IS NULL OR d.location LIKE %:location%)")
    Page<Device> searchDevices(@Param("name") String name,
                               @Param("type") String type,
                               @Param("status") Device.DeviceStatus status,
                               @Param("location") String location,
                               Pageable pageable);
    
    long countByStatus(Device.DeviceStatus status);
    
    @Query("SELECT d FROM Device d WHERE d.isDeleted = 0")
    List<Device> findByIsDeletedFalse();
    @Query("SELECT d FROM Device d WHERE d.isDeleted = 0")
    Page<Device> findByIsDeletedFalse(Pageable pageable);
    
    @Query("SELECT d FROM Device d WHERE d.isDeleted = 0")
    List<Device> findAllActiveDevices();
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.isDeleted = 0")
    long countByIsDeletedFalse();
    
    @Query("SELECT d FROM Device d WHERE d.isDeleted = 0 AND d.maintenanceDate IS NOT NULL AND d.maintenanceDate <= CURRENT_DATE")
    List<Device> findDevicesNeedingMaintenance();
    
    @Query("SELECT d FROM Device d WHERE d.isDeleted = 0")
    List<Device> findAllDevices();
}
