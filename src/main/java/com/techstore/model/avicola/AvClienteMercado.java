package com.techstore.model.avicola;

import com.techstore.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "AV_CLIENTE_MERCADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AvClienteMercado extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "NOMBRE_ALIAS", nullable = false, length = 100)
    private String nombreAlias;

    @Column(name = "TELEFONO_WHATSAPP", length = 20)
    private String telefonoWhatsapp;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AvEstadoCuenta> estadosCuenta = new ArrayList<>();
}
