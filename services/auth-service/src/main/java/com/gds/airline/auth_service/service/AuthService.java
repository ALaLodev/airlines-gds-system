package com.gds.airline.auth_service.service;

import com.gds.airline.auth_service.dto.AuthResponse;
import com.gds.airline.auth_service.dto.LoginRequest;
import com.gds.airline.auth_service.dto.RegisterRequest;
import com.gds.airline.auth_service.entity.Role;
import com.gds.airline.auth_service.entity.User;
import com.gds.airline.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request){
        // Validar que el usuario no exista ya en el GDS
         if (userRepository.findByEmail(request.getEmail()).isPresent()){
             throw new RuntimeException("El email ya pertenece q una agencia registrada.");
         }

         // Construir el nuevo Usuario (Entidad)
         User user = User.builder()
                 .email(request.getEmail())
                 .password(passwordEncoder.encode(request.getPassword()))
                 .role(Role.valueOf(request.getRole()))
                 .build();

         userRepository.save(user);

        // Convertimos nuestra Entidad a UserDetails para generar el token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .message("Usuario creado con éxito en el sistema GDS")
                .token(token)
                .build();
    }

    public AuthResponse login(LoginRequest request){
        // 1. Spring Security comprueba automáticamente si el email y la contraseña coinciden.
        // Si la contraseña está mal, esto lanza una excepción y aborta el proceso.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        // Si llegamos aquí, las credenciales son correctas. Buscamos al usuario en la DB.
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        // Lo convertimos a UserDetails
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
        // Generamos token y lo devolvemos
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder().message("Login exitoso. Bienvenido al GDS.").token(token).build();
    }
}
