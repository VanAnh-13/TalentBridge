package com.example.talentbridge.company.presentation;

import com.example.talentbridge.company.application.ListCompanyMembers.CompanyMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private boolean owner;

    public static MemberResponse from(CompanyMember m) {
        return new MemberResponse(m.id(), m.name(), m.email(), m.owner());
    }
}
