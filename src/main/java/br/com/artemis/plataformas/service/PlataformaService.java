package br.com.artemis.plataformas.service;

import br.com.artemis.plataformas.dto.PlataformaRequest;
import br.com.artemis.plataformas.dto.PlataformaResponse;
import br.com.artemis.plataformas.exception.RecursoNaoEncontradoException;
import br.com.artemis.plataformas.exception.RegraNegocioException;
import br.com.artemis.plataformas.model.Categoria;
import br.com.artemis.plataformas.model.Plataforma;
import br.com.artemis.plataformas.model.StatusPlataforma;
import br.com.artemis.plataformas.repository.CategoriaRepository;
import br.com.artemis.plataformas.repository.PlataformaRepository;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;
    private final CategoriaRepository categoriaRepository;

    public PlataformaService(PlataformaRepository plataformaRepository, CategoriaRepository categoriaRepository) {
        this.plataformaRepository = plataformaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public PlataformaResponse criar(PlataformaRequest req) {
        if (plataformaRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new RegraNegocioException("Já existe uma plataforma com o código '" + req.codigo() + "'");
        }
        Plataforma plataforma = new Plataforma();
        preencher(plataforma, req);
        return PlataformaResponse.de(plataformaRepository.save(plataforma));
    }

    /**
     * Consulta com múltiplos parâmetros opcionais:
     * GET /plataformas?modelo=&status=&categoriaId=&alturaMin=
     * Somente os filtros informados entram na consulta.
     */
    @Transactional(readOnly = true)
    public List<PlataformaResponse> listar(String modelo, StatusPlataforma status, Long categoriaId, BigDecimal alturaMin) {
        Specification<Plataforma> filtro = (root, query, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();
            if (modelo != null && !modelo.isBlank()) {
                condicoes.add(cb.like(cb.lower(root.<String>get("modelo")), "%" + modelo.trim().toLowerCase() + "%"));
            }
            if (status != null) {
                condicoes.add(cb.equal(root.get("status"), status));
            }
            if (categoriaId != null) {
                condicoes.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (alturaMin != null) {
                condicoes.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("alturaTrabalho"), alturaMin));
            }
            return cb.and(condicoes.toArray(new Predicate[0]));
        };
        return plataformaRepository.findAll(filtro).stream().map(PlataformaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PlataformaResponse buscarPorId(Long id) {
        return PlataformaResponse.de(obter(id));
    }

    @Transactional
    public PlataformaResponse atualizar(Long id, PlataformaRequest req) {
        Plataforma plataforma = obter(id);
        if (plataformaRepository.existsByCodigoIgnoreCaseAndIdNot(req.codigo(), id)) {
            throw new RegraNegocioException("Já existe uma plataforma com o código '" + req.codigo() + "'");
        }
        preencher(plataforma, req);
        return PlataformaResponse.de(plataforma);
    }

    @Transactional
    public void excluir(Long id) {
        Plataforma plataforma = obter(id);
        // Regra de negócio: equipamento locado não pode ser excluído
        if (plataforma.getStatus() == StatusPlataforma.LOCADA) {
            throw new RegraNegocioException("Não é possível excluir uma plataforma que está locada");
        }
        plataformaRepository.delete(plataforma);
    }

    private void preencher(Plataforma plataforma, PlataformaRequest req) {
        Categoria categoria = categoriaRepository.findById(req.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + req.categoriaId() + " não encontrada"));
        plataforma.setCodigo(req.codigo().trim().toUpperCase());
        plataforma.setModelo(req.modelo().trim());
        plataforma.setAlturaTrabalho(req.alturaTrabalho());
        plataforma.setCapacidadeKg(req.capacidadeKg());
        plataforma.setStatus(req.status());
        plataforma.setCategoria(categoria);
    }

    private Plataforma obter(Long id) {
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plataforma " + id + " não encontrada"));
    }
}
