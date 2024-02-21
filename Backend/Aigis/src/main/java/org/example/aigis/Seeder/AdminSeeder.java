package org.example.aigis.Seeder;


import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.Role;
import org.example.aigis.Model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminSeeder {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.password}")
    private String adminPassword;

    public void seed() {
        var admin = User.builder()
                .username(adminName)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .about("Admin account")
                .usernameUnique("admin")
                .build();
        try {
            this.userDAO.save(admin);
        } catch (Exception e) {
            logger.warn("couldn't create admin account: " + e.getMessage());
        }
    }
}