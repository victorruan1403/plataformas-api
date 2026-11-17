package br.com.artemis.plataformas.dto;

import br.com.artemis.plataformas.model.Perfil;
import br.com.artemis.plataformas.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Perfil perfil) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }
}
