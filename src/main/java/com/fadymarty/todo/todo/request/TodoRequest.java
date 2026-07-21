package com.fadymarty.todo.todo.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoRequest {

    @NotBlank(message = "VALIDATION.TODO.TITLE.NOT_BLANK")
    private String title;
    @NotBlank(message = "VALIDATION.TODO.DESCRIPTION.NOT_BLANK")
    private String description;
    @FutureOrPresent(message = "VALIDATION.TODO.START_DATE.FUTURE_OR_PRESENT")
    private LocalDate startDate;
    @FutureOrPresent(message = "VALIDATION.TODO.END_DATE.FUTURE_OR_PRESENT")
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String categoryId;
}