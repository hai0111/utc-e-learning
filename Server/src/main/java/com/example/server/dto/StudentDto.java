package com.example.server.dto;

import java.util.UUID;

public interface StudentDto {

    UUID getId();

    String getCode();

    String getEmail();

    String getName();
}
