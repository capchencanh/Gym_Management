package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity for gym management system
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Column(name = "avatar_url")
    @Size(max = 500, message = "URL avatar không được vượt quá 500 ký tự")
    private String avatarUrl;

    @Column(name = "phone_number", nullable = false, unique = true)
    @Pattern(regexp = "^$|^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    @JsonIgnore
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    @Column(nullable = false)
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải có từ 2 đến 100 ký tự")
    private String name;

    @Column
    @Pattern(regexp = "^(Nam|Nữ|Khác)?$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;

    @Column
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private Date birthdate;

    @Column
    @DecimalMin(value = "50.0", message = "Chiều cao phải lớn hơn 50cm")
    @DecimalMax(value = "250.0", message = "Chiều cao phải nhỏ hơn 250cm")
    private Double height;

    @Column
    @DecimalMin(value = "20.0", message = "Cân nặng phải lớn hơn 20kg")
    @DecimalMax(value = "300.0", message = "Cân nặng phải nhỏ hơn 300kg")
    private Double weight;

    @Column(name = "fitness_goal", columnDefinition = "text")
    @Size(max = 1000, message = "Mục tiêu thể hình không được vượt quá 1000 ký tự")
    private String fitnessGoal;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private int isDeleted = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserAvailability> availabilities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PTAssignment> ptAssignments = new ArrayList<>();

    public enum Role {
        ADMIN, PT, USER
    }
}
