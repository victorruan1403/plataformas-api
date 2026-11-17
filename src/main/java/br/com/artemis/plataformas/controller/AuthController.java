package br.com.artemis.plataformas.controller;

import br.com.artemis.plataformas.dto.LoginRequest;
import br.com.artemis.plataformas.dto.RegistroRequest;
import br.com.artemis.plataformas.dto.TokenResponse;
import br.com.artemis.plataformas.dto.UsuarioResponse;
import br.com.artemis.plataformas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody @Valid RegistroRequest req) {
        return authService.registrar(req);
    }

    @ExceptionHandler(AuthService.CredenciaisInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail credenciaisInvalidas(AuthService.CredenciaisInvalidasException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
