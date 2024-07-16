package org.example.aigis.Config;

import lombok.RequiredArgsConstructor;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.Role;
import org.example.aigis.Model.User;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class UserSecurity implements AuthorizationManager<RequestAuthorizationContext> {
    private final UserDAO userDao;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        UUID userId =UUID.fromString(object.getVariables().get("id"));
        Authentication authentication1 = authentication.get();
        return new AuthorizationDecision(hasUserId(authentication1, userId));
    }

    public boolean hasUserId(Authentication authentication, UUID userId) {
        UUID id = (UUID) authentication.getPrincipal();
        User user = userDao.findById(id).orElse(null);
        if (user != null) {
            return user.getId().equals(userId) || user.getRole().equals(Role.ADMIN);
        }
        return false;
    }
}
