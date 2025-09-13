package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;

/**
 * Trainer entity for gym management system
 */
@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    @Column(name = "is_deleted")
    @Builder.Default
    private Integer isDeleted = 0;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PTAssignment> assignments = new ArrayList<>();
    
    public String getName() {
        return user != null ? user.getName() : "Unknown";
    }
}
