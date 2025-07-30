package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
} 