package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.Trainer;

public class TrainerDTO {
    private Integer id;
    private String name;
    private String email;
    private String specialization;
    private String schedule;
    private Integer isDeleted;

    public TrainerDTO() {}

    public TrainerDTO(Trainer trainer) {
        this.id = trainer.getTrainerId();
        this.specialization = trainer.getSpecialization();
        this.schedule = trainer.getSchedule();
        this.isDeleted = trainer.getIsDeleted();

        if (trainer.getUser() != null) {
            this.name = trainer.getUser().getName();
            this.email = trainer.getUser().getEmail();
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
