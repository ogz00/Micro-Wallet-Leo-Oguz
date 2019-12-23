package com.oguz.demo.microwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto implements Serializable {
    private static final long serialVersionUID = 1234L;

    private Long Id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String country;
}
