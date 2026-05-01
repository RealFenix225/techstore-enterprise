package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "AV_LINEA_INGRESO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvLineaIngreso extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INGRESO_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_linea_ingreso_ingreso_carga"))
    @ToString.Exclude
    private AvIngresoCarga ingreso;

    @Column(name = "JABAS_MACHO", nullable = false)
    @Builder.Default
    private Integer jabasMacho = 0;

    @Column(name = "JABAS_HEMBRA", nullable = false)
    @Builder.Default
    private Integer jabasHembra = 0;

    // CHECK: peso_bruto_kg > 0
    @Column(name = "PESO_BRUTO_KG", precision = 8, scale = 2, nullable = false)
    private BigDecimal pesoBrutoKg;

    // Default 7.00 kg; editable si se mezclan jabas de distintos proveedores.
    // CHECK: tara_unitaria_jaba BETWEEN 5.00 AND 15.00
    @Column(name = "TARA_UNITARIA_JABA", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taraUnitariaJaba = new BigDecimal("7.00");

    // Nueva regla de negocio: aves muertas/asfixiadas en tránsito.
    // Se descuentan del inventario teórico y se devuelven al proveedor.
    @Column(name = "POLLOS_AHOGADOS_MUERTOS", nullable = false)
    @Builder.Default
    private Integer pollosAhogadosMuertos = 0;
}
