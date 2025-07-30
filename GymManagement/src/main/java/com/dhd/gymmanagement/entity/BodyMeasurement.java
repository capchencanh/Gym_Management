package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "body_measurements")
public class BodyMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    private Integer measurementId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private Double weight;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(name = "muscle_mass")
    private Double muscleMass;

    @Column
    private Double chest;

    @Column
    private Double waist;

    @Column
    private Double bicep;

    @Column
    private Double thigh;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "measured_at")
    private Timestamp measuredAt;

    public Integer getMeasurementId() { return measurementId; }
    public void setMeasurementId(Integer measurementId) { this.measurementId = measurementId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getBodyFatPercentage() { return bodyFatPercentage; }
    public void setBodyFatPercentage(Double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }
    public Double getMuscleMass() { return muscleMass; }
    public void setMuscleMass(Double muscleMass) { this.muscleMass = muscleMass; }
    public Double getChest() { return chest; }
    public void setChest(Double chest) { this.chest = chest; }
    public Double getWaist() { return waist; }
    public void setWaist(Double waist) { this.waist = waist; }
    public Double getBicep() { return bicep; }
    public void setBicep(Double bicep) { this.bicep = bicep; }
    public Double getThigh() { return thigh; }
    public void setThigh(Double thigh) { this.thigh = thigh; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(Timestamp measuredAt) { this.measuredAt = measuredAt; }
}
