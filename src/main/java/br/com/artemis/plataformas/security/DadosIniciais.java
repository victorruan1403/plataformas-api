package br.com.artemis.plataformas.security;

import br.com.artemis.plataformas.model.Perfil;
import br.com.artemis.plataformas.model.Usuario;
import br.com.artemis.plataformas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Cria um ADMIN e um COMUM na primeira execução, para facilitar os testes. */
@Configuration
public class DadosIniciais {

    @Bean
    CommandLineRunner criarUsuarios(UsuarioRepository repo, PasswordEncoder encoder,
                                    @Value("${app.seed.admin-email}") String adminEmail,
                                    @Value("${app.seed.admin-senha}") String adminSenha,
                                    @Value("${app.seed.comum-email}") String comumEmail,
                                    @Value("${app.seed.comum-senha}") String comumSenha) {
        return args -> {
            if (!repo.existsByEmailIgnoreCase(adminEmail)) {
                repo.save(new Usuario("Administrador", adminEmail, encoder.encode(adminSenha), Perfil.ADMIN));
            }
            if (!repo.existsByEmailIgnoreCase(comumEmail)) {
                repo.save(new Usuario("Usuário Comum", comumEmail, encoder.encode(comumSenha), Perfil.COMUM));
            }
        };
    }
}
