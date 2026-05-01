package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import com.techstore.model.Provider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "AV_INGRESO_CARGA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvIngresoCarga extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JORNADA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingreso_carga_jornada"))
    @ToString.Exclude
    private AvJornadaDiaria jornada;

    // Reutiliza la tabla de proveedores existente de TechStore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROVEEDOR_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingreso_carga_proveedor"))
    @ToString.Exclude
    private Provider proveedor;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvLineaIngreso> lineasIngreso = new ArrayList<>();
}
