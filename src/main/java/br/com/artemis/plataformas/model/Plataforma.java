package br.com.artemis.plataformas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plataformas")
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificação interna do equipamento, ex.: PTA.1 */
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "altura_trabalho", nullable = false, precision = 6, scale = 2)
    private BigDecimal alturaTrabalho;

    @Column(name = "capacidade_kg", nullable = false)
    private Integer capacidadeKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPlataforma status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Plataforma() {
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public BigDecimal getAlturaTrabalho() { return alturaTrabalho; }
    public void setAlturaTrabalho(BigDecimal alturaTrabalho) { this.alturaTrabalho = alturaTrabalho; }
    public Integer getCapacidadeKg() { return capacidadeKg; }
    public void setCapacidadeKg(Integer capacidadeKg) { this.capacidadeKg = capacidadeKg; }
    public StatusPlataforma getStatus() { return status; }
    public void setStatus(StatusPlataforma status) { this.status = status; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
