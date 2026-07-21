package com.fadymarty.todo.todo;

import com.fadymarty.todo.common.RestResponse;
import com.fadymarty.todo.todo.request.TodoRequest;
import com.fadymarty.todo.todo.request.TodoUpdateRequest;
import com.fadymarty.todo.todo.response.TodoResponse;
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
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name = "Todos", description = "Todo API")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#request.categoryId)")
    public ResponseEntity<RestResponse> createTodo(
            @RequestBody
            @Valid
            TodoRequest request,
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        String todoId = todoService.createTodo(request, userId);
        return ResponseEntity
                .status(CREATED)
                .body(new RestResponse(todoId));
    }

    @PutMapping(("/{todo-id}"))
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<Void> updateTodo(
            @RequestBody
            @Valid
            TodoUpdateRequest request,
            @PathVariable("todo-id")
            String todoId,
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        todoService.updateTodo(request, todoId, userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{todo-id}")
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<TodoResponse> findTodoById(
            @PathVariable("todo-id")
            String todoId
    ) {
        return ResponseEntity.ok(todoService.findTodoById(todoId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TodoResponse>> findAllTodosByUserId(
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(todoService.findAllTodosForToday(userId));
    }

    @GetMapping("/category/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<List<TodoResponse>> findAllTodosByCategory(
            @PathVariable("category-id")
            String categoryId,
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(todoService.findAllTodosByCategory(categoryId, userId));
    }

    @GetMapping("/due")
    public ResponseEntity<List<TodoResponse>> findAllDueTodos(
            Authentication authentication
    ) {
        String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(todoService.findAllDueTodos(userId));
    }

    @DeleteMapping("/{todo-id}")
    @PreAuthorize("@todoSecurityService.isTodoOwner(#todoId)")
    public ResponseEntity<Void> deleteTodoById(
            @PathVariable("todo-id")
            String todoId
    ) {
        todoService.deleteTodoById(todoId);
        return ResponseEntity.ok().build();
    }
}