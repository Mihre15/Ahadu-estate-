package com.Ahadu_backend.app.agent.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AgentResponseDto {
    private Long id;
    private String name;
    private String phone;

    private String agencyName;

    private String licenseNumber;
}
