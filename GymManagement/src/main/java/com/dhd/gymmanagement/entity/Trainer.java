package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trainers")
public class Trainer {
    @Id
    @Column(name = "trainer_id")
    private Integer trainerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "trainer_id")
    private User user;

    @Column
    private String specialization;

    @Column(columnDefinition = "text")
    private String schedule;

    public Integer getTrainerId() { return trainerId; }
    public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}
