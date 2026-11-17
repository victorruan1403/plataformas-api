package br.com.artemis.plataformas.dto;

public record TokenResponse(String token, String tipo, long expiraEmSegundos, String perfil) {
}
