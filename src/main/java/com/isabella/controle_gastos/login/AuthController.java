package com.isabella.controle_gastos.login;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ✅ Login busca usuário no banco e compara senha com BCrypt
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> dados) {
        String username = dados.get("usuario");
        String senhaInformada = dados.get("senha");

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(senhaInformada, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }

        String token = JwtUtil.gerarToken(username);
        return Map.of("token", token);
    }

    // ✅ Endpoint para cadastrar usuário com senha criptografada
    @PostMapping("/cadastrar")
    public Map<String, String> cadastrar(@RequestBody Map<String, String> dados) {
        String username = dados.get("usuario");
        String senha = dados.get("senha");

        if (usuarioRepository.findByUsuario(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsuario(username);
        novoUsuario.setSenha(passwordEncoder.encode(senha)); // ✅ senha criptografada
        usuarioRepository.save(novoUsuario);

        return Map.of("mensagem", "Usuário cadastrado com sucesso");
    }
}