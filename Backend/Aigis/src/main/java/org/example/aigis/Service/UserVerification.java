package org.example.aigis.Service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserVerification {
    public boolean verifyUser(Authentication authentication, String usernameUnique) {
        return authentication.getName().equals(usernameUnique);
    }
}
