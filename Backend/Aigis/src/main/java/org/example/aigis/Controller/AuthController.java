package org.example.aigis.Controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aigis.DTO.Auth.*;
import org.example.aigis.Service.AuthenticationService;
import org.example.aigis.Model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    public ApiResponse<AuthResponseDTO> login(@RequestBody AuthLoginDTO loginDTO, HttpServletResponse response) {
        authenticationService.login(loginDTO.getUsernameUnique(), loginDTO.getPassword(), response);
        return new ApiResponse<>("Login succes", HttpStatus.OK);
    }

    // TODO: Remove this
    @GetMapping(value = "/validate/{token}")
    public ApiResponse<String> validate(@PathVariable String token) {
        boolean isValid = authenticationService.isValidToken(token);
        return new ApiResponse<>(isValid ? "valid" : "invalid");
    }

    @GetMapping("/checkId/{id}")
    public ApiResponse<IdCheck> checkId(@PathVariable String id, Authentication authentication) {
        return new ApiResponse<>(new IdCheck(this.authenticationService.isIdOfSelf(UUID.fromString(id), authentication)), HttpStatus.OK);
    }

    @GetMapping(value = "/authenticated")
    public ApiResponse<AuthCheckResponseDTO> checkAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        AuthCheckResponseDTO authCheckResponseDTO = this.authenticationService.checkAuthenticated(request);

        if (!authCheckResponseDTO.isAuthenticated()) {
            // Clears the cookies in case the browser still has them stored whilst they're invalid
            Cookie cookie = this.authenticationService.getEmptyCookie("token");
            response.addCookie(cookie);
        }

        return new ApiResponse<>(authCheckResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        response.addCookie(this.authenticationService.logout());
    }

    @GetMapping("/isAdmin")
    public ApiResponse<AdminCheckResponseDTO> isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return new ApiResponse<>(new AdminCheckResponseDTO(isAdmin), HttpStatus.OK);
    }
}
