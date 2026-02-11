package com.example.webapplication.dto;


import com.example.webapplication.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UpdatePasswordDto implements DtoPasswordMatcher {

    @Size(min = 8, message = "Password min. length is 8")
    private String oldPassword;

    @Size(min = 8, message = "Password min. length is 8")
    private String newPassword;

    @Override
    public String getPassword() {
        return newPassword;
    }

    @Override
    public String getMatchingPassword() {
        return oldPassword;
    }
}
