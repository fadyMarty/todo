package com.fadymarty.todo.category;

import com.fadymarty.todo.category.request.CategoryRequest;
import com.fadymarty.todo.category.request.CategoryUpdateRequest;
import com.fadymarty.todo.category.response.CategoryResponse;
import com.fadymarty.todo.common.RestResponse;
import com.fadymarty.todo.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<RestResponse> createCategory(
            @RequestBody
            @Valid
            CategoryRequest request,
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        String catId = categoryService.createCategory(request, userId);
        return ResponseEntity.status(CREATED).body(new RestResponse(catId));
    }

    @PutMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> updateCategory(
            @RequestBody
            @Valid
            CategoryUpdateRequest request,
            @PathVariable("category-id")
            String categoryId,
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        categoryService.updateCategory(request, categoryId, userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories(
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(categoryService.findAllByOwner(userId));
    }

    @GetMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<CategoryResponse> findCategoryById(
            @PathVariable("category-id")
            String categoryId
    ) {
        return ResponseEntity.ok(categoryService.findCategoryById(categoryId));
    }

    @DeleteMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> deleteCategoryById(
            @PathVariable("category-id")
            String categoryId
    ) {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.ok().build();
    }
}
