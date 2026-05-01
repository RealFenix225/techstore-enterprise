package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import com.techstore.model.Provider;
import com.techstore.model.enums.TipoPago;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "AV_TRANSACCION_PAGO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvTransaccionPago extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTADO_CUENTA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaccion_pago_estado_cuenta"))
    @ToString.Exclude
    private AvEstadoCuenta estadoCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PAGO", nullable = false, length = 30)
    private TipoPago tipoPago;

    // CHECK: monto > 0
    @Column(name = "MONTO", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    // Solo poblado cuando tipoPago == TRIANGULACION_MAYORISTA.
    // CHECK: (TRIANGULACION_MAYORISTA => NOT NULL) AND (otros => NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVEEDOR_DESTINO_ID", nullable = true,
            foreignKey = @ForeignKey(name = "fk_transaccion_pago_proveedor_destino"))
    @ToString.Exclude
    private Provider proveedorDestino;
}
