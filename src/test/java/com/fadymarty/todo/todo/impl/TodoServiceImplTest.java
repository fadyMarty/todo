package com.fadymarty.todo.todo.impl;

import com.fadymarty.todo.category.Category;
import com.fadymarty.todo.category.CategoryRepository;
import com.fadymarty.todo.todo.Todo;
import com.fadymarty.todo.todo.TodoMapper;
import com.fadymarty.todo.todo.TodoRepository;
import com.fadymarty.todo.todo.request.TodoRequest;
import com.fadymarty.todo.todo.request.TodoUpdateRequest;
import com.fadymarty.todo.todo.response.TodoResponse;
import com.fadymarty.todo.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Unit Tests")
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Category testCategory;
    private Todo testTodo;
    private TodoRequest todoRequest;
    private TodoUpdateRequest todoUpdateRequest;
    private TodoResponse todoResponse;


    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .id("user-123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testCategory = Category.builder()
                .id("category-123")
                .name("Work")
                .description("Work related todos")
                .build();

        testTodo = Todo.builder()
                .id("todo-123")
                .title("Test Todo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .user(testUser)
                .category(testCategory)
                .build();

        todoRequest = TodoRequest.builder()
                .title("New Todo")
                .description("New Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .categoryId("category-123")
                .build();

        todoUpdateRequest = TodoUpdateRequest.builder()
                .title("Updated Todo")
                .description("Updated Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(19, 0))
                .categoryId("category-123")
                .build();

        todoResponse = TodoResponse.builder()
                .id("todo-1234")
                .title("Test tpdo")
                .description("Test Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .done(false)
                .build();
    }

    @Nested
    @DisplayName("Create Todo Tests")
    class CreateTodoTests {

        @Test
        @DisplayName("Should create todo successfully when valid valid request and category exists")
        void shouldCreateTodoSuccessfully() {
            String userId = "user-123";
            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
                    .thenReturn(Optional.of(testCategory));
            when(todoMapper.toTodo(todoRequest)).thenReturn(testTodo);
            when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

            String result = todoService.createTodo(todoRequest, userId);

            assertNotNull(result);
            assertEquals("todo-123", result);
            verify(categoryRepository, times(1))
                    .findByIdAndUserId(todoRequest.getCategoryId(), userId);
            verify(todoMapper, times(1)).toTodo(todoRequest);
            verify(todoRepository, times(1)).save(testTodo);

            verify(todoRepository).save(argThat(todo -> todo.getCategory() != null
                    && todo.getCategory().getId().equals("category-123")));

        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when category not found")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
            String userId = "user-123";
            when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.createTodo(todoRequest, userId)
            );

            assertEquals(
                    "No category was found for that user with id " + todoRequest.getCategoryId(),
                    exception.getMessage()
            );
            verify(categoryRepository, times(1))
                    .findByIdAndUserId(todoRequest.getCategoryId(), userId);
            verifyNoInteractions(todoMapper);
            verifyNoInteractions(todoRepository);

        }

        @Test
        @DisplayName("Should Handle null cateforyId in request")
        void shouldHandleNullCatIdInRequest() {
            String userId = "user-123";
            todoRequest.setCategoryId(null);
            when(categoryRepository.findByIdAndUserId(null, userId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.createTodo(todoRequest, userId)
            );

            assertNotNull(exception);
            assertEquals(
                    "No category was found for that user with id " + todoRequest.getCategoryId(),
                    exception.getMessage()
            );
            verify(categoryRepository, times(1))
                    .findByIdAndUserId(todoRequest.getCategoryId(), userId);
            verifyNoInteractions(todoMapper);
            verifyNoInteractions(todoRepository);

        }

    }

    @Nested
    @DisplayName("Update Todo Tests")
    class UpdateTodoTests {

        @Test
        @DisplayName("Should update successfully a Todo when todo and category exist")
        void shouldSuccessfullyUpdateTodo() {
            String userId = "user-123";
            String todoId = "todo-123";
            when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
            when(categoryRepository.findByIdAndUserId(testTodo.getCategory().getId(), userId))
                    .thenReturn(Optional.of(testCategory));
            when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

            todoService.updateTodo(todoUpdateRequest, todoId, userId);

            verify(todoRepository, times(1)).findById(todoId);
            verify(categoryRepository).findByIdAndUserId(testTodo.getCategory().getId(), userId);
            verify(todoMapper).mergeTodo(testTodo, todoUpdateRequest);
            verify(todoRepository).save(testTodo);

            assertEquals(testCategory, testTodo.getCategory());

        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when Todo not found")
        void shouldThrowEntityNotFoundExceptionWhenTodoNotFound() {
            String userId = "user-123";
            String todoId = "todo-123";
            when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, todoId, userId)
            );

            assertEquals("Todo not found with id: " + todoId, exception.getMessage());
            verify(todoRepository, times(1)).findById(todoId);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(todoMapper);
            verify(todoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when Category not found")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
            String userId = "user-123";
            String todoId = "todo-123";
            when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
            when(categoryRepository.findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.updateTodo(todoUpdateRequest, todoId, userId)
            );

            assertEquals(
                    "No category was found for that user with id " + todoUpdateRequest.getCategoryId(),
                    exception.getMessage()
            );
            verify(todoRepository, times(1)).findById(todoId);
            verify(categoryRepository).findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId);
            verifyNoInteractions(todoMapper);
            verify(todoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Find Todo By Id test")
    class FindTodoByIdTests {

        @Test
        @DisplayName("Should return todo response when todo exists")
        void shouldReturnTodoResponse() {
            String todoId = "todo-123";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.of(testTodo));
            when(todoMapper.toTodoResponse(testTodo))
                    .thenReturn(todoResponse);

            TodoResponse result = todoService.findTodoById(todoId);

            assertNotNull(result);
            assertEquals(todoResponse, result);
            verify(todoRepository).findById(todoId);
            verify(todoMapper).toTodoResponse(testTodo);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when todo not found")
        void shouldThrowEntityNotFoundExceptionWhenTodoNotFound() {
            String todoId = "non-existing-todo";
            when(todoRepository.findById(todoId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.findTodoById(todoId)
            );

            assertEquals("No todo found with id " + todoId, exception.getMessage());
            verify(todoRepository).findById(todoId);
            verifyNoInteractions(todoMapper);

        }

        @Test
        @DisplayName("Should handle null todo ID")
        void shouldHandleNullId() {
            when(todoRepository.findById(null))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> todoService.findTodoById(null)
            );

            assertEquals("No todo found with id null", exception.getMessage());
            verify(todoRepository).findById(null);
            verifyNoInteractions(todoMapper);
        }
    }
}