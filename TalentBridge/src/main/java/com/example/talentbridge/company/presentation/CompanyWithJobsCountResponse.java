package com.example.talentbridge.company.presentation;

import com.example.talentbridge.company.application.SearchCompanies.CompanyWithJobCount;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompanyWithJobsCountResponse extends CompanyResponse {

    private Long jobsCount;

    public static CompanyWithJobsCountResponse from(CompanyWithJobCount view) {
        CompanyResponse base = CompanyResponse.from(view.company());
        CompanyWithJobsCountResponse res = new CompanyWithJobsCountResponse();
        res.setId(base.getId());
        res.setName(base.getName());
        res.setDescription(base.getDescription());
        res.setAddress(base.getAddress());
        res.setLogoUrl(base.getLogoUrl());
        res.setCreatedAt(base.getCreatedAt());
        res.setUpdatedAt(base.getUpdatedAt());
        res.setJobsCount(view.jobsCount());
        return res;
    }
}
