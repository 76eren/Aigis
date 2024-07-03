package org.example.aigis.Service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aigis.DTO.Auth.AuthCheckResponseDTO;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.Role;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public void login(String username, String password, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<User> userOptional = userDAO.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = jwtService.generateToken(Map.of("id", user.getId()), user.getId());

                Cookie cookie = new Cookie("token", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 7);
                response.addCookie(cookie);
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("Authentication failed", e);
        }
    }

    // TODO: Remove this
    public boolean isValidToken(String token) {
        try {
            jwtService.validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Cookie logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = this.jwtService.generateToken(Map.of("id", authentication.getPrincipal()), (UUID) authentication.getPrincipal());
        jwtService.invalidateToken(token);

        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(0);
        return cookie;
    }

    public AuthCheckResponseDTO checkAuthenticated(HttpServletRequest request) {
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser") && jwtService.isTokenValid(jwt, (UUID) authentication.getPrincipal());

        return AuthCheckResponseDTO
                .builder()
                .isAuthenticated(isAuthenticated)
                .build();
    }

    public Cookie getEmptyCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(0);
        return cookie;
    }

    public boolean isIdOfSelf(UUID id, Authentication authentication) {
        User authUser = userDAO.findById((UUID) authentication.getPrincipal()).orElse(null);
        return authUser != null && authUser.getId().equals(id);
    }


}
