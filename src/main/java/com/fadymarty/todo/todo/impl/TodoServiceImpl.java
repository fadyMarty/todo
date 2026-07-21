package com.fadymarty.todo.todo.impl;

import com.fadymarty.todo.category.Category;
import com.fadymarty.todo.category.CategoryRepository;
import com.fadymarty.todo.todo.Todo;
import com.fadymarty.todo.todo.TodoMapper;
import com.fadymarty.todo.todo.TodoRepository;
import com.fadymarty.todo.todo.TodoService;
import com.fadymarty.todo.todo.request.TodoRequest;
import com.fadymarty.todo.todo.request.TodoUpdateRequest;
import com.fadymarty.todo.todo.response.TodoResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TodoMapper todoMapper;

    @Override
    public String createTodo(TodoRequest request, String userId) {
        Category category = checkAndReturnCategory(request.getCategoryId(), userId);
        Todo todo = todoMapper.toTodo(request);
        todo.setCategory(category);
        return todoRepository.save(todo).getId();
    }

    @Override
    public void updateTodo(TodoUpdateRequest request, String todoId, String userId) {
        Todo todoToUpdate = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));
        Category category = checkAndReturnCategory(request.getCategoryId(), userId);

        todoMapper.mergeTodo(todoToUpdate, request);
        todoToUpdate.setCategory(category);

        todoRepository.save(todoToUpdate);
    }

    @Override
    public TodoResponse findTodoById(String todoId) {
        return todoRepository.findById(todoId)
                .map(todoMapper::toTodoResponse)
                .orElseThrow(() -> new EntityNotFoundException("No todo found with id " + todoId));
    }

    @Override
    public List<TodoResponse> findAllTodosForToday(String userId) {
        return todoRepository.findAllByUserId(userId)
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findAllTodosByCategory(String catId, String userId) {
        return todoRepository.findAllByUserIdAndCategoryId(userId, catId)
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public List<TodoResponse> findAllDueTodos(String userId) {
        return todoRepository.findAllDueTodos(userId)
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList();
    }

    @Override
    public void deleteTodoById(String todoId) {
        todoRepository.deleteById(todoId);
    }

    private Category checkAndReturnCategory(String categoryId, String userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("No category was found for that user with id " + categoryId));
    }
}