package com.gerentes.agenda.security;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.model.Usuario;
import com.gerentes.agenda.repository.UsuarioRepository;

/**
 * Utilidades de seguridad para obtener información del usuario autenticado.
 */
@Component
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public SecurityUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Verifica si el usuario está autenticado.
     *
     * @return true si el usuario está autenticado, false en caso contrario
     */
    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Obtiene el nombre de usuario del usuario autenticado.
     *
     * @return Optional con el nombre de usuario si está autenticado, empty en caso contrario
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }

    /**
     * Obtiene el usuario autenticado desde la base de datos.
     *
     * @return Optional con el usuario si está autenticado y existe, empty en caso contrario
     */
    public Optional<Usuario> getCurrentUser() {
        return getCurrentUsername()
                .flatMap(usuarioRepository::findByUsername);
    }

    /**
     * Obtiene el gerente asociado al usuario autenticado, si existe.
     *
     * @return Optional con el gerente si el usuario está autenticado y tiene un gerente asociado, empty en caso contrario
     */
    public Optional<Gerente> getCurrentGerente() {
        return getCurrentUser()
                .map(Usuario::getGerente);
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     *
     * @param role El nombre del rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /**
     * Verifica si el usuario tiene alguno de los roles especificados.
     *
     * @param roles Los roles a verificar
     * @return true si el usuario tiene al menos uno de los roles, false en caso contrario
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> Stream.of(roles).anyMatch(authority::equals));
    }
}