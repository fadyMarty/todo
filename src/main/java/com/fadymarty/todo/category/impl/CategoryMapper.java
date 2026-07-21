package com.fadymarty.todo.category.impl;

import com.fadymarty.todo.category.Category;
import com.fadymarty.todo.category.request.CategoryRequest;
import com.fadymarty.todo.category.request.CategoryUpdateRequest;
import com.fadymarty.todo.category.response.CategoryResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void mergeCategory(Category categoryToUpdate, CategoryUpdateRequest request) {
        if (StringUtils.isNotBlank(request.getName())
                && !categoryToUpdate.getName().equals(request.getName())) {
            categoryToUpdate.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getDescription())
                && !categoryToUpdate.getDescription().equals(request.getDescription())) {
            categoryToUpdate.setDescription(request.getDescription());
        }
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .description(category.getDescription())
                .todoCount(category.getTodos().size())
                .build();
    }
}