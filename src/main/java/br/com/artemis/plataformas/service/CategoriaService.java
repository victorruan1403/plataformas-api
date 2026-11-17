package br.com.artemis.plataformas.service;

import br.com.artemis.plataformas.dto.CategoriaRequest;
import br.com.artemis.plataformas.dto.CategoriaResponse;
import br.com.artemis.plataformas.dto.PlataformaResponse;
import br.com.artemis.plataformas.exception.RecursoNaoEncontradoException;
import br.com.artemis.plataformas.exception.RegraNegocioException;
import br.com.artemis.plataformas.model.Categoria;
import br.com.artemis.plataformas.repository.CategoriaRepository;
import br.com.artemis.plataformas.repository.PlataformaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final PlataformaRepository plataformaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, PlataformaRepository plataformaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.plataformaRepository = plataformaRepository;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest req) {
        if (categoriaRepository.existsByNomeIgnoreCase(req.nome())) {
            throw new RegraNegocioException("Já existe uma categoria com o nome '" + req.nome() + "'");
        }
        Categoria categoria = new Categoria(req.nome().trim(), req.descricao());
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return CategoriaResponse.de(obter(id));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest req) {
        Categoria categoria = obter(id);
        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(req.nome(), id)) {
            throw new RegraNegocioException("Já existe uma categoria com o nome '" + req.nome() + "'");
        }
        categoria.setNome(req.nome().trim());
        categoria.setDescricao(req.descricao());
        return CategoriaResponse.de(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = obter(id);
        // Regra de negócio: não é possível excluir categoria que ainda possui plataformas
        if (plataformaRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Não é possível excluir a categoria: existem plataformas vinculadas");
        }
        categoriaRepository.delete(categoria);
    }

    /** Consulta com filtro no path: GET /categorias/{id}/plataformas */
    @Transactional(readOnly = true)
    public List<PlataformaResponse> listarPlataformas(Long categoriaId) {
        obter(categoriaId);
        return plataformaRepository.findByCategoriaId(categoriaId).stream().map(PlataformaResponse::de).toList();
    }

    private Categoria obter(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + id + " não encontrada"));
    }
}
