package com.gerentes.agenda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.model.Rol;
import com.gerentes.agenda.model.Usuario;
import com.gerentes.agenda.repository.RolRepository;
import com.gerentes.agenda.repository.UsuarioRepository;

/**
 * Servicio para manejar operaciones relacionadas con los Usuarios.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, 
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Guarda un usuario en la base de datos, encriptando su contraseña.
     * 
     * @param usuario El usuario a guardar
     * @return El usuario guardado con su ID asignado
     */
    public Usuario guardarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param id ID del usuario a actualizar
     * @param usuarioActualizado Datos actualizados del usuario
     * @return El usuario actualizado o null si no se encuentra
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioActualizado.setId(id);
                    // Solo encriptar la contraseña si ha cambiado
                    if (usuarioActualizado.getPassword() != null && 
                        !usuarioActualizado.getPassword().equals(usuarioExistente.getPassword())) {
                        usuarioActualizado.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
                    }
                    return usuarioRepository.save(usuarioActualizado);
                })
                .orElse(null);
    }

    /**
     * Actualiza la contraseña de un usuario.
     * 
     * @param usuario Usuario a actualizar
     * @param newPassword Nueva contraseña (se codificará automáticamente)
     * @return El usuario actualizado
     */
    public Usuario actualizarPassword(Usuario usuario, String newPassword) {
        usuario.setPassword(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }

    /**
     * Desactiva un usuario (no lo elimina).
     * 
     * @param id ID del usuario a desactivar
     * @return true si se desactivó correctamente, false si no se encontró
     */
    public boolean desactivarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    /**
     * Activa un usuario desactivado.
     * 
     * @param id ID del usuario a activar
     * @return true si se activó correctamente, false si no se encontró
     */
    public boolean activarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    /**
     * Busca un usuario por su ID.
     * 
     * @param id ID del usuario a buscar
     * @return Optional con el usuario si existe, o vacío si no existe
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre del usuario a buscar
     * @return Optional con el usuario si existe, o vacío si no existe
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Lista todos los usuarios.
     * 
     * @return Lista de todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id ID del usuario a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Asigna un rol a un usuario.
     * 
     * @param usuarioId ID del usuario
     * @param rolNombre Nombre del rol a asignar
     * @return El usuario actualizado o null si el usuario o rol no existen
     */
    public Usuario asignarRol(Long usuarioId, String rolNombre) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        Optional<Rol> rolOpt = rolRepository.findByNombre(rolNombre);
        
        if (usuarioOpt.isPresent() && rolOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.agregarRol(rolOpt.get());
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    /**
     * Crea un usuario para un gerente con el rol "GERENTE".
     * 
     * @param gerente El gerente para el que se crea el usuario
     * @param username Nombre de usuario
     * @param password Contraseña sin encriptar
     * @return El usuario creado o null si no se pudo crear
     */
    public Usuario crearUsuarioGerente(Gerente gerente, String username, String password) {
        // Verificar si ya existe un usuario con ese username
        if (usuarioRepository.existsByUsername(username)) {
            return null;
        }
        
        // Buscar o crear el rol GERENTE
        Rol rolGerente = rolRepository.findByNombre("ROLE_GERENTE")
                .orElse(new Rol("ROLE_GERENTE"));
                
        if (rolGerente.getId() == null) {
            rolGerente = rolRepository.save(rolGerente);
        }
        
        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setGerente(gerente);
        usuario.setActivo(true);
        usuario.agregarRol(rolGerente);
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Crea un usuario con rol de administrador.
     * 
     * @param username Nombre de usuario del administrador
     * @param password Contraseña sin encriptar
     * @return El usuario creado o null si no se pudo crear
     */
    public Usuario crearUsuarioAdmin(String username, String password) {
        // Verificar si ya existe un usuario con ese username
        if (usuarioRepository.existsByUsername(username)) {
            return null;
        }
        
        // Buscar o crear el rol ADMIN
        Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                .orElse(new Rol("ROLE_ADMIN"));
                
        if (rolAdmin.getId() == null) {
            rolAdmin = rolRepository.save(rolAdmin);
        }
        
        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setActivo(true);
        usuario.agregarRol(rolAdmin);
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Verifica si un nombre de usuario ya está en uso.
     * 
     * @param username Nombre de usuario a verificar
     * @return true si ya existe, false en caso contrario
     */
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Busca al usuario asociado a un gerente.
     * 
     * @param gerente El gerente asociado
     * @return Optional con el usuario si existe, vacío si no existe
     */
    public Optional<Usuario> buscarPorGerente(Gerente gerente) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getGerente() != null && u.getGerente().equals(gerente))
                .findFirst();
    }
}