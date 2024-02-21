package org.example.aigis.Service;


import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.Role;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Optional<String> register(String usernameUnique, String username, String password, String about) {
        Optional<User> foundUser = userDAO.findByUsernameUnique(usernameUnique);
        if (foundUser.isPresent()) {
            return Optional.empty();
        }

        User user = User.builder()
                .username(username)
                .usernameUnique(usernameUnique)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .about(about)
                .build();

        userDAO.save(user);
        String token = jwtService.generateToken(Map.of("id", user.getId()), user.getId() );
        return Optional.of(token);
    }

    public String login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        User user = userDAO.loadUserByUsername(username);
        return jwtService.generateToken(Map.of("id", user.getId()), user.getId());
    }

    public boolean isValidToken(String token) {
        try {
            jwtService.validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
