package br.com.artemis.plataformas.service;

import br.com.artemis.plataformas.dto.LoginRequest;
import br.com.artemis.plataformas.dto.RegistroRequest;
import br.com.artemis.plataformas.dto.TokenResponse;
import br.com.artemis.plataformas.dto.UsuarioResponse;
import br.com.artemis.plataformas.exception.RegraNegocioException;
import br.com.artemis.plataformas.model.Perfil;
import br.com.artemis.plataformas.model.Usuario;
import br.com.artemis.plataformas.repository.UsuarioRepository;
import br.com.artemis.plataformas.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Cadastro público: sempre cria usuário com perfil COMUM. */
    public UsuarioResponse registrar(RegistroRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new RegraNegocioException("E-mail já cadastrado");
        }
        Usuario usuario = new Usuario(req.nome().trim(), req.email().trim().toLowerCase(),
                passwordEncoder.encode(req.senha()), Perfil.COMUM);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    public TokenResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email())
                .filter(u -> passwordEncoder.matches(req.senha(), u.getSenha()))
                .orElseThrow(() -> new CredenciaisInvalidasException());
        return jwtService.gerar(usuario);
    }

    public static class CredenciaisInvalidasException extends RuntimeException {
        public CredenciaisInvalidasException() {
            super("E-mail ou senha inválidos");
        }
    }
}
