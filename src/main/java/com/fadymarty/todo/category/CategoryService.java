package com.fadymarty.todo.category;

import com.fadymarty.todo.category.request.CategoryRequest;
import com.fadymarty.todo.category.request.CategoryUpdateRequest;
import com.fadymarty.todo.category.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    String createCategory(CategoryRequest request, String userId);

    void updateCategory(CategoryUpdateRequest request, String catId, String userId);

    List<CategoryResponse> findAllByOwner(String userId);

    CategoryResponse findCategoryById(String catId);

    void deleteCategoryById(String catId);
}