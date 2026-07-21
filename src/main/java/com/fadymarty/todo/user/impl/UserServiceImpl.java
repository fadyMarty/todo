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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fadymarty.todo.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));
    }

    @Override
    public void updateProfileInfo(ProfileUpdateRequest request, String userId) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        userMapper.mergeUserInfo(savedUser, request);
        userRepository.save(savedUser);
    }

    @Override
    public void changePassword(ChangePasswordRequest req, String userId) {

        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            throw new BusinessException(CHANGE_PASSWORD_MISMATCH);
        }

        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getCurrentPassword(), savedUser.getPassword())) {
            throw new BusinessException(INVALID_CURRENT_PASSWORD);
        }

        String encoded = passwordEncoder.encode(req.getNewPassword());
        savedUser.setPassword(encoded);
        userRepository.save(savedUser);
    }

    @Override
    public void deactivateAccount(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void reactivateAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

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