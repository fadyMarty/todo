package com.fadymarty.todo.category.impl;

import com.fadymarty.todo.category.Category;
import com.fadymarty.todo.category.CategoryRepository;
import com.fadymarty.todo.category.CategoryService;
import com.fadymarty.todo.category.request.CategoryRequest;
import com.fadymarty.todo.category.request.CategoryUpdateRequest;
import com.fadymarty.todo.category.response.CategoryResponse;
import com.fadymarty.todo.exception.BusinessException;
import com.fadymarty.todo.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public String createCategory(CategoryRequest request, String userId) {
        checkCategoryUnicityForUser(request.getName(), userId);

        Category category = categoryMapper.toCategory(request);

        return categoryRepository.save(category).getId();
    }

    @Override
    public void updateCategory(CategoryUpdateRequest request, String catId, String userId) {
        Category categoryToUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("No category found with id: " + catId));

        checkCategoryUnicityForUser(request.getName(), userId);

        categoryMapper.mergeCategory(categoryToUpdate, request);
        categoryRepository.save(categoryToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllByOwner(String userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse findCategoryById(String catId) {
        return categoryRepository.findById(catId)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new EntityNotFoundException("No category found with id: " + catId));
    }

    @Override
    public void deleteCategoryById(String catId) {

    }

    private void checkCategoryUnicityForUser(String name, String userId) {
        boolean alreadyExistsForUser = categoryRepository.findByNameAndUser(name, userId);
        if (alreadyExistsForUser) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS_FOR_USER);
        }
    }
}