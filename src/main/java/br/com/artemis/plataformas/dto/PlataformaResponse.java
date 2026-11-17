package br.com.artemis.plataformas.dto;

import br.com.artemis.plataformas.model.Plataforma;
import br.com.artemis.plataformas.model.StatusPlataforma;
import java.math.BigDecimal;

public record PlataformaResponse(
        Long id,
        String codigo,
        String modelo,
        BigDecimal alturaTrabalho,
        Integer capacidadeKg,
        StatusPlataforma status,
        Long categoriaId,
        String categoriaNome) {

    public static PlataformaResponse de(Plataforma p) {
        return new PlataformaResponse(
                p.getId(),
                p.getCodigo(),
                p.getModelo(),
                p.getAlturaTrabalho(),
                p.getCapacidadeKg(),
                p.getStatus(),
                p.getCategoria().getId(),
                p.getCategoria().getNome());
    }
}
