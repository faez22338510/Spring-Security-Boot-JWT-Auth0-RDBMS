package org.j2os.service;

import lombok.extern.slf4j.Slf4j;
import org.j2os.model.UserEntity;
import org.j2os.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/*
    Bahador, Amirsam
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(UserEntity userEntity) {
        log.info("save");
        userRepository.save(userEntity);
    }

    public UserEntity findByUsername(String username) {
        log.info("findByUsername");
        return userRepository.findByUsername(username);
    }

    public void addRole(String username, String role) {
        log.info("addRole");
        UserEntity userEntity = userRepository.findByUsername(username);
        userEntity.addRole(role);
        userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername");
        UserEntity userEntity = userRepository.findByUsername(username);
        Set<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new User(username, userEntity.getPassword(), authorities);
    }
}
