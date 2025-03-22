package com.example.twitter.services;

import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Servicio para la gestión de usuarios en la aplicación.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userRepository  Repositorio para gestionar usuarios.
     * @param passwordEncoder Codificador de contraseñas para seguridad.
     * @param jwtService      Servicio para la generación y validación de JWTs.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica a un usuario con su nombre de usuario y contraseña.
     * Si la autenticación es exitosa, genera un token JWT.
     *
     * @param username Nombre de usuario del usuario a autenticar.
     * @param password Contraseña del usuario.
     * @return Un token JWT si la autenticación es exitosa.
     * @throws RuntimeException Si el usuario no existe o la contraseña es
     *                          incorrecta.
     */
    public String authUser(String username, String password) {
        if (!userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario no existe");
        }

        User user = userRepository.findByUsername(username).get();
        System.out.println(user.getId());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return this.jwtService.sign(user.getId(), user.getUsername());
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Si el nombre de usuario ya existe, lanza una excepción.
     *
     * @param user Objeto usuario con los datos a registrar.
     * @return El usuario guardado en la base de datos.
     * @throws RuntimeException Si el nombre de usuario ya está en uso.
     */
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword().strip()));

        return userRepository.save(user);
    }

    /**
     * Obtiene una lista de todos los usuarios registrados.
     *
     * @return Lista de usuarios en la base de datos.
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Verifica si un usuario existe en el sistema por su nombre de usuario.
     *
     * @param username Nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
