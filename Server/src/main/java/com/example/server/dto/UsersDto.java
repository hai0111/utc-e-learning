package com.example.server.dto;

import java.util.UUID;

public interface UsersDto {

    UUID getId();

    String getName();

    String getCode();

    String getEmail();

    String getRole();

    Boolean getIsActive();

    String getCreatedBy();

    String getUpdateBy();
}
