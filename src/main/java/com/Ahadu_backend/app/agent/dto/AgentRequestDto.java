package com.Ahadu_backend.app.agent.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentRequestDto {
    private String name;

    private String phone;

    private String agencyName;

    private String licenseNumber;
}
