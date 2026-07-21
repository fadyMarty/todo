package com.fadymarty.todo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailDomainValidator implements ConstraintValidator<NonDisposableEmail, String> {

    private final Set<String> blocked;

    public EmailDomainValidator(
            @Value("${app.security.disposable-email}")
            List<String> domains
    ) {
        blocked = domains.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext ctx) {
        if (email == null || !email.contains("@")) {
            return true;
        }
        int atIndex = email.lastIndexOf('@') + 1;
        int dotIndex = email.lastIndexOf('.');
        String domain = email.substring(atIndex, dotIndex).toLowerCase();
        return !blocked.contains(domain);
    }
}