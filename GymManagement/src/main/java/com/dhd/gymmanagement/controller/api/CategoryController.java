package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.CategoryDTO;
import com.dhd.gymmanagement.entity.Category;
import com.dhd.gymmanagement.entity.Category2;
import com.dhd.gymmanagement.service.CategoryService;
import com.dhd.gymmanagement.service.CategoryService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CategoryService2 categoryService2;
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> list() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(CategoryDTO::new)
            .collect(Collectors.toList());
        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/categories2")
    public ResponseEntity<List<Category2>> listCategories2() {
        return new ResponseEntity<>(categoryService2.getCategories2(), HttpStatus.OK);
    }
}
