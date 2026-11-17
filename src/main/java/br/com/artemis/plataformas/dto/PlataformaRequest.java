package br.com.artemis.plataformas.dto;

import br.com.artemis.plataformas.model.StatusPlataforma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PlataformaRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 100) String modelo,
        @NotNull @Positive BigDecimal alturaTrabalho,
        @NotNull @Positive Integer capacidadeKg,
        @NotNull StatusPlataforma status,
        @NotNull Long categoriaId) {
}
