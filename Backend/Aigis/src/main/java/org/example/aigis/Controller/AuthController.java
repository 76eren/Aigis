package org.example.aigis.Controller;


import org.example.aigis.DTO.Auth.AuthLoginDTO;
import org.example.aigis.Service.AuthenticationService;
import org.example.aigis.DTO.Auth.AuthRegisterDTO;
import org.example.aigis.DTO.Auth.AuthResponseDTO;
import org.example.aigis.Model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    public ApiResponse<AuthResponseDTO> login(@RequestBody AuthLoginDTO loginDTO) {
        String token = authenticationService.login(loginDTO.getUsernameUnique(), loginDTO.getPassword());
        return new ApiResponse<>(new AuthResponseDTO(token));
    }

    @GetMapping(value = "/validate/{token}")
    public ApiResponse<String> validate(@PathVariable String token) {
        boolean isValid = authenticationService.isValidToken(token);
        return new ApiResponse<>(isValid ? "valid" : "invalid");
    }
}
