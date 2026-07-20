package com.fadymarty.todo.auth.impl;

import com.fadymarty.todo.auth.AuthenticationService;
import com.fadymarty.todo.auth.request.AuthenticationRequest;
import com.fadymarty.todo.auth.request.RefreshRequest;
import com.fadymarty.todo.auth.request.RegistrationRequest;
import com.fadymarty.todo.auth.response.AuthenticationResponse;
import com.fadymarty.todo.exception.BusinessException;
import com.fadymarty.todo.exception.ErrorCode;
import com.fadymarty.todo.role.Role;
import com.fadymarty.todo.role.RoleRepository;
import com.fadymarty.todo.security.JwtService;
import com.fadymarty.todo.user.User;
import com.fadymarty.todo.user.UserMapper;
import com.fadymarty.todo.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final User user = (User) auth.getPrincipal();
        final String token = jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = jwtService.refreshAccessToken(user.getUsername());
        final String tokenType = "Bearer";

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        checkUserEmail(request.getEmail());
        checkUserPhoneNumber(request.getPhoneNumber());
        checkPasswords(request.getPassword(), request.getConfirmPassword());

        final Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role user does not exists"));
        final List<Role> roles = new ArrayList<>();
        roles.add(userRole);

        final User user = userMapper.toUser(request);
        user.setRoles(roles);
        log.debug("Saving user {}", user);
        userRepository.save(user);

        final List<User> users = new ArrayList<>();
        users.add(user);
        userRole.setUsers(users);

        roleRepository.save(userRole);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        final String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        final String tokenType = "Bearer";
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType(tokenType)
                .build();
    }

    private void checkUserEmail(String email) {
        final boolean emailExists = userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkUserPhoneNumber(String phoneNumber) {
        final boolean phoneNumberExists = userRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExists) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void checkPasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
}
