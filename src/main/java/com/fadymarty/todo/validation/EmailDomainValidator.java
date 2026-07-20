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
            @Value("${app.security.disposable-email}") final List<String> domains
    ) {
        blocked = domains.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || !email.contains("@")) {
            return true;
        }
        final int index = email.indexOf("@") + 1;
        final int dotIndex = email.lastIndexOf(".");
        final String domain = email.substring(index, dotIndex);
        return !blocked.contains(domain);
    }
}
