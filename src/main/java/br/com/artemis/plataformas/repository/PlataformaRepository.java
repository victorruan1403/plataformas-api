package br.com.artemis.plataformas.repository;

import br.com.artemis.plataformas.model.Plataforma;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlataformaRepository extends JpaRepository<Plataforma, Long>, JpaSpecificationExecutor<Plataforma> {

    @EntityGraph(attributePaths = "categoria")
    List<Plataforma> findByCategoriaId(Long categoriaId);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    boolean existsByCategoriaId(Long categoriaId);
}
