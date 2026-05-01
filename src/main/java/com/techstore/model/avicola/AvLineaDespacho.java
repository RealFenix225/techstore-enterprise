package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import com.techstore.model.enums.TipoEmpaque;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "AV_LINEA_DESPACHO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvLineaDespacho extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTADO_CUENTA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_linea_despacho_estado_cuenta"))
    @ToString.Exclude
    private AvEstadoCuenta estadoCuenta;

    // CHECK: peso_bruto_kg > 0 (gestionado en DB como constraint, validado en DTO)
    @Column(name = "PESO_BRUTO_KG", precision = 8, scale = 2, nullable = false)
    private BigDecimal pesoBrutoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EMPAQUE", nullable = false, length = 10)
    private TipoEmpaque tipoEmpaque;

    @Column(name = "CANTIDAD_EMPAQUE", nullable = false)
    private Integer cantidadEmpaque;

    // Aves con tripa
    @Column(name = "POLLOS_CT", nullable = false)
    @Builder.Default
    private Integer pollosCt = 0;

    // Aves sin tripa
    @Column(name = "POLLOS_ST", nullable = false)
    @Builder.Default
    private Integer pollosSt = 0;

    // Si TRUE, el DespachoService inyecta +350g por cada pollo_st en el cálculo de peso neto
    @Column(name = "FLAG_PESO_FANTASMA", nullable = false)
    @Builder.Default
    private Boolean flagPesoFantasma = false;
}
