package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "AV_ESTADO_CUENTA", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_estado_cuenta_jornada_cliente",
                columnNames = {"JORNADA_ID", "CLIENTE_ID"}
        )
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvEstadoCuenta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JORNADA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estado_cuenta_jornada"))
    @ToString.Exclude
    private AvJornadaDiaria jornada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLIENTE_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estado_cuenta_cliente"))
    @ToString.Exclude
    private AvClienteMercado cliente;

    // NULL durante la madrugada; se fija al mediodía (CIERRE_PARCIAL de jornada)
    @Column(name = "PRECIO_FIJADO", precision = 6, scale = 2)
    private BigDecimal precioFijado;

    // Descuento manual aplicado por ADMIN. Default 0 explícito para evitar NPE en cálculos.
    @Column(name = "DESCUENTO_RECTIFICACION", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal descuentoRectificacion = BigDecimal.ZERO;

    @OneToMany(mappedBy = "estadoCuenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvLineaDespacho> lineasDespacho = new ArrayList<>();

    @OneToMany(mappedBy = "estadoCuenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvTransaccionPago> transacciones = new ArrayList<>();
}
