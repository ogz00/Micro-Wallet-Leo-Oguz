package com.oguz.demo.microwallet.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "app_role")
@NoArgsConstructor
public class AppRole extends BaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Role description must be provided")
    @Column
    private String description;
}
