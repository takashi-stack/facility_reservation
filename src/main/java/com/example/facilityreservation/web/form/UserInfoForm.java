package com.example.facilityreservation.web.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.example.facilityreservation.domain.enums.PermissionLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank
    @Length(min = 4, max = 10, message = "{min}文字以上{max}文字以下で入力してください。")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "半角英数字のみで入力してください。")
    private String username;

    @NotBlank
    @Length(min = 6, max = 20, message = "{min}文字以上{max}文字以下で入力してください。")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "半角英数字のみで入力してください。")
    private String password;

    @NotNull
    private Integer permission = 1;

    @NotNull
    private String note;

    public String getPermissionText() {
        return PermissionLevel.values()[permission].getText();
    }
}
