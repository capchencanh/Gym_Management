package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.TrainingClass;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private Integer maxParticipants;
    @JsonProperty("trainerId")
    private Integer trainerId;
    @JsonProperty("trainerName")
    private String trainerName;
    private String schedule;
    private Double price;
    private Integer isDeleted;

    public ClassDTO() {}

    public ClassDTO(TrainingClass classEntity) {
        this.id = classEntity.getClassId();
        this.name = classEntity.getName();
        this.description = classEntity.getDescription();
        this.duration = classEntity.getDurationMinutes();
        this.maxParticipants = classEntity.getMaxParticipants();
        this.schedule = classEntity.getSchedule();
        this.price = classEntity.getPrice();
        this.isDeleted = classEntity.getIsDeleted();

        if (classEntity.getTrainer() != null) {
            this.trainerId = classEntity.getTrainer().getTrainerId();
            String name = classEntity.getTrainer().getName();
            this.trainerName = (name != null && !name.isBlank()) ? name : "Chưa phân công";
        } else {
            this.trainerName = "Chưa phân công";
        }
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getTrainerId() { return trainerId; }
    public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
