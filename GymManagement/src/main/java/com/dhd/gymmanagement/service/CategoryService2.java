package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Category2;
import com.dhd.gymmanagement.repository.CategoryRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService2 {
    
    @Autowired
    private CategoryRepository2 categoryRepository2;
    
    public List<Category2> getCategories2() {
        return categoryRepository2.findAll();
    }
    
    public Category2 getCategory2ById(Integer id) {
        return categoryRepository2.findById(id).orElse(null);
    }
    
    public Category2 createCategory2(Category2 category2) {
        return categoryRepository2.save(category2);
    }
    
    public Category2 updateCategory2(Category2 category2) {
        return categoryRepository2.save(category2);
    }
    
    public void deleteCategory2(Integer id) {
        categoryRepository2.deleteById(id);
    }
}
