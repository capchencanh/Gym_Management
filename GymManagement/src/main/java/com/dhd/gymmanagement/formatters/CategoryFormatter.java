package com.dhd.gymmanagement.formatters;

import com.dhd.gymmanagement.entity.Category;
import com.dhd.gymmanagement.repository.CategoryRepository;
import org.springframework.format.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

@Component
public class CategoryFormatter implements Formatter<Category> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category parse(String text, Locale locale) throws ParseException {
        try {
            Integer id = Integer.valueOf(text);
            Optional<Category> category = categoryRepository.findById(id);
            return category.orElse(null);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid category id: " + text, 0);
        }
    }

    @Override
    public String print(Category category, Locale locale) {
        return (category != null && category.getId() != null) ? category.getId().toString() : "";
    }
} 