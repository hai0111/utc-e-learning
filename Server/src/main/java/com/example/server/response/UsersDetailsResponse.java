package com.example.server.response;

import com.example.server.enums.Role;
import com.example.server.security.service.UserDetailsImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UsersDetailsResponse {

    private UUID uuid;

    private String name;

    private String code;

    private String email;

    private String roleName;

    private Boolean isActive;

    private String createdBy;

    private String updateBy;

    public static UsersDetailsResponse toUserDetailsResponse(UserDetailsImpl userDetails, String roleName) {
        UsersDetailsResponse usersDetailsResponse = new UsersDetailsResponse();
        usersDetailsResponse.setUuid(userDetails.getId());
        usersDetailsResponse.setName(userDetails.getName());
        usersDetailsResponse.setCode(userDetails.getCode());
        usersDetailsResponse.setEmail(userDetails.getEmail());
        usersDetailsResponse.setIsActive(userDetails.getIsActive());
        usersDetailsResponse.setRoleName(roleName);
        if (!roleName.equals(Role.ADMIN.toString())) {
            usersDetailsResponse.setCreatedBy(userDetails.getCreatedBy().getName());
            usersDetailsResponse.setUpdateBy(userDetails.getUpdatedBy().getName());
        }
        return usersDetailsResponse;
    }
}
