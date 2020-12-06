package com.example.facilityreservation.web.form;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacilityForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "必須入力です。")
    @Length(min = 1, max = 10, message = "{min}文字以上{max}文字以下で入力してください。")
    private String name;

    @NotNull
    @Min(value = 1)
    private Integer typeId = 1;

    @NotNull(message = "1名以上で入力してください。")
    @Min(value = 1, message = "1名以上で入力してください。")
    private Integer capacity;
}
