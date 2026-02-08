package com.techstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "PROVIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // <--- CRÍTICO: Cambiado de @Builder a @SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Provider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "TAX_ID", nullable = false, unique = true)
    private String taxId;
}