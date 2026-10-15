package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.access.domain.Role;
import com.example.talentbridge.access.domain.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchRoles {

    private final RoleRepository roles;

    public PageResult<Role> execute(FilterExpression filter, PageQuery page) {
        return roles.search(filter, page);
    }
}
