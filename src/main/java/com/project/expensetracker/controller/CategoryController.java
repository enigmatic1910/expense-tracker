package com.project.expensetracker.controller;

import com.project.expensetracker.dto.SystemCategoryDto;
import com.project.expensetracker.service.category.CategoryService;
import com.project.expensetracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepo userRepo;

    @GetMapping("/system")
    public ResponseEntity<List<SystemCategoryDto>> getAllSystemCategory() {
        return ResponseEntity.ok(categoryService.getAllSystemCategory());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SystemCategoryDto>> getAllCategoriesForUser(Authentication authentication) {
        String userId = resolveUserId(authentication);
        List<SystemCategoryDto> categories = categoryService.getAllForUser(userId)
                .stream()
                .map(c -> new SystemCategoryDto(c.getId(), c.getName()))
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<SystemCategoryDto> addCategory(@RequestBody SystemCategoryDto request, Authentication authentication) {
        String userId = resolveUserId(authentication);
        SystemCategoryDto saved = categoryService.saveCategory(request.name(), userId);
        return ResponseEntity.ok(saved);
    }

    private String resolveUserId(Authentication authentication) {
        return userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
    }
}
