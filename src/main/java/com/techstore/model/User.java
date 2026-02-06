package com.techstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email; //Esto sera para el loggin

    @Column(nullable = false)
    private String password; // Acá se guardará el HASH raro

    @Enumerated(EnumType.STRING)
    private Role role;

    // --- Zona de traducción (UserDetails) ---
    // Estos métodos mdicen a Spring Security como interpretar datos

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword(){
        return password; //Esto le pasa la contraseña cifrada a Spring
    }

    @Override
    public String getUsername(){
        return email; //Spring espera un "username", con esto le estamos pasando un email
    }

    @Override
    public boolean isAccountNonExpired(){
        return true; //La cuenta no debe caducar
    }

    @Override
    public boolean isAccountNonLocked(){
        return true; //La cuenta no se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; //La contraseña nunca caduda
    }

    @Override
    public boolean isEnabled() {
        return true; //El usuario está activo
    }
}