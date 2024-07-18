package org.example.spring1.user;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.spring1.exception.EntityNotFoundException;
import org.example.spring1.user.model.ERole;
import org.example.spring1.user.model.Role;
import org.example.spring1.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createAdminUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Error: Role is not found."));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}