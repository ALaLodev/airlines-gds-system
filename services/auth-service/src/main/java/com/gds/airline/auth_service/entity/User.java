package com.gds.airline.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data // Anotación de Lombok que genera getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
