package com.fadymarty.todo.category.security;

import com.fadymarty.todo.category.Category;
import com.fadymarty.todo.category.CategoryRepository;
import com.fadymarty.todo.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorySecurityService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public boolean isCategoryOwner(String categoryId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userId = ((User) authentication.getPrincipal()).getId();
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return category.getCreatedBy().equals(userId)
                || category.getCreatedBy().equals("APP");
    }
}