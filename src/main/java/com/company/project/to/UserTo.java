package com.company.project.to;

import com.company.project.model.AbstractBaseEntity;
import com.company.project.web.validator.HasEmail;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UserTo extends AbstractBaseEntity implements HasEmail, Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(min = 3, max = 50)
    @SafeHtml
    private String name;

    @NotBlank
    @Size(min = 5, max = 100)
    @Email
    @SafeHtml
    private String email;

    @NotBlank
    @Size(min = 5, max = 100)
    private String password;

    public UserTo() {
    }

    public UserTo(Integer id, String name, String email, String password) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserTo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
