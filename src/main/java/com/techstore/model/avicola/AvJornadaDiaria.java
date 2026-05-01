package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import com.techstore.model.enums.EstadoJornada;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "AV_JORNADA_DIARIA", uniqueConstraints = {
        @UniqueConstraint(name = "uq_jornada_fecha_operativa", columnNames = "FECHA_OPERATIVA")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvJornadaDiaria extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "FECHA_OPERATIVA", nullable = false)
    private LocalDate fechaOperativa;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    @Builder.Default
    private EstadoJornada estado = EstadoJornada.ABIERTA;

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvEstadoCuenta> estadosCuenta = new ArrayList<>();

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvIngresoCarga> ingresos = new ArrayList<>();
}
