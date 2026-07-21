package com.fadymarty.todo.todo.security;

import com.fadymarty.todo.todo.Todo;
import com.fadymarty.todo.todo.TodoRepository;
import com.fadymarty.todo.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoSecurityService {

    private final TodoRepository todoRepository;

    public boolean isTodoOwner(String todoId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userId = ((User) authentication.getPrincipal()).getId();

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo is not found with id: " + todoId));

        return todo.getUser()
                .getId()
                .equals(userId);
    }
}