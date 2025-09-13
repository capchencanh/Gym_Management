package com.dhd.gymmanagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.sql.Timestamp;

/**
 * MembershipPackage entity for gym management system
 */
@Entity
@Table(name = "membership_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(nullable = false)
    private String name;

    @Column(name = "duration_months", nullable = false)
    @JsonProperty("duration_months")
    private Integer durationMonths;

    @Column(nullable = false)
    private Double price;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private Integer isDeleted = 0;
}
