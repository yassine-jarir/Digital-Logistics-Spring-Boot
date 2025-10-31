package com.logistic.digitale_logistic.dto;

import com.logistic.digitale_logistic.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private boolean active;

    public Boolean getActive() {
        return active;
    }
}
