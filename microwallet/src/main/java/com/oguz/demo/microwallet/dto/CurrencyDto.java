package com.oguz.demo.microwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyDto {
    private static final long serialVersionUID = 1234L;

    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String code;
}
