package com.oguz.demo.microwallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "currency")
@NoArgsConstructor
public class Currency extends BaseEntity {
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @NotNull(message = "Currency name must be provided")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Currency code must be provided")
    @Column(nullable = false, unique = true)
    private String code;

    public Currency(String name, String code) {
        super();
        this.name = name;
        this.code = code;
    }
}
