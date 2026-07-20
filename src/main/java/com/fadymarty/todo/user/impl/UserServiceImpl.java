package com.fadymarty.todo.user.impl;

import com.fadymarty.todo.exception.BusinessException;
import com.fadymarty.todo.exception.ErrorCode;
import com.fadymarty.todo.user.User;
import com.fadymarty.todo.user.UserMapper;
import com.fadymarty.todo.user.UserRepository;
import com.fadymarty.todo.user.UserService;
import com.fadymarty.todo.user.request.ChangePasswordRequest;
import com.fadymarty.todo.user.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userEmail: " + userEmail));
    }

    @Override
    public void updateProfileInfo(ProfileUpdateRequest request, String userId) {
        final User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        userMapper.mergeUserInfo(savedUser, request);
        userRepository.save(savedUser);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String userId) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
        }

        final User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(),
                savedUser.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        final String encoded = passwordEncoder.encode(request.getNewPassword());
        savedUser.setPassword(encoded);
        userRepository.save(savedUser);
    }

    @Override
    public void deactivateAccount(String userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void reactiveAccount(String userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void deleteAccount(String userId) {
    }
}
