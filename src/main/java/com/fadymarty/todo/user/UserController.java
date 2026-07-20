package com.fadymarty.todo.user;

import com.fadymarty.todo.user.request.ChangePasswordRequest;
import com.fadymarty.todo.user.request.ProfileUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfileInfo(
            @RequestBody
            @Valid final ProfileUpdateRequest request,
            final Authentication principal
    ) {
        userService.updateProfileInfo(request, getUserId(principal));
    }

    @PostMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody
            @Valid final ChangePasswordRequest request,
            final Authentication principal
    ) {
        userService.changePassword(request, getUserId(principal));
    }

    @PatchMapping("/me/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccount(final Authentication principal) {
        userService.deactivateAccount(getUserId(principal));
    }

    @PatchMapping("/me/reactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivateAccount(final Authentication principal) {
        userService.reactiveAccount(getUserId(principal));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(final Authentication principal) {
        userService.deleteAccount(getUserId(principal));
    }

    private String getUserId(Authentication principal) {
        return ((User) principal.getPrincipal()).getId();
    }
}
