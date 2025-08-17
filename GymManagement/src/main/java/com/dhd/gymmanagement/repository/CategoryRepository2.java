package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Category2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository2 extends JpaRepository<Category2, Integer> {
  
}
