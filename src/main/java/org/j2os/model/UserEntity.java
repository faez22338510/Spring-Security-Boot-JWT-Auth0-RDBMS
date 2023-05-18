package org.j2os.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/*
    Bahador, Amirsam
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    public UserEntity() {
        this.roles = new HashSet<>();
    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public void addRole(String role) {
        this.roles.add(role);
    }
}
