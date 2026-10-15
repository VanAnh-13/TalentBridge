package com.example.talentbridge.company.presentation;

import com.example.talentbridge.company.domain.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyResponse {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String logoUrl;
    private String createdAt;
    private String updatedAt;

    public static CompanyResponse from(Company c) {
        return new CompanyResponse(
                c.getId(), c.getName(), c.getDescription(), c.getAddress(), c.getLogoUrl(),
                c.getCreatedAt() == null ? null : c.getCreatedAt().toString(),
                c.getUpdatedAt() == null ? null : c.getUpdatedAt().toString());
    }
}
