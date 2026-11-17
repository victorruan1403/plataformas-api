package br.com.artemis.plataformas.controller;

import br.com.artemis.plataformas.dto.PlataformaRequest;
import br.com.artemis.plataformas.dto.PlataformaResponse;
import br.com.artemis.plataformas.model.StatusPlataforma;
import br.com.artemis.plataformas.service.PlataformaService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plataformas")
public class PlataformaController {

    private final PlataformaService service;

    public PlataformaController(PlataformaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PlataformaResponse> criar(@RequestBody @Valid PlataformaRequest req) {
        PlataformaResponse criada = service.criar(req);
        return ResponseEntity.created(URI.create("/plataformas/" + criada.id())).body(criada);
    }

    /** Múltiplos parâmetros de consulta, todos opcionais. */
    @GetMapping
    public List<PlataformaResponse> listar(@RequestParam(required = false) String modelo,
                                           @RequestParam(required = false) StatusPlataforma status,
                                           @RequestParam(required = false) Long categoriaId,
                                           @RequestParam(required = false) BigDecimal alturaMin) {
        return service.listar(modelo, status, categoriaId, alturaMin);
    }

    @GetMapping("/{id}")
    public PlataformaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PlataformaResponse atualizar(@PathVariable Long id, @RequestBody @Valid PlataformaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
