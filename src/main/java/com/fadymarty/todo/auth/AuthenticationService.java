package com.fadymarty.todo.auth;

import com.fadymarty.todo.auth.request.AuthenticationRequest;
import com.fadymarty.todo.auth.request.RefreshRequest;
import com.fadymarty.todo.auth.request.RegistrationRequest;
import com.fadymarty.todo.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request);
}
