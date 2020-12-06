package com.example.facilityreservation.web.bean;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginBean {

    @Length(min = 4, max = 10, message = "{min}文字以上{max}文字以下で入力してください。")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "半角英数字のみで入力してください。")
    private String username;

    @Length(min = 6, max = 20, message = "{min}文字以上{max}文字以下で入力してください。")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "半角英数字のみで入力してください。")
    private String password;
}
