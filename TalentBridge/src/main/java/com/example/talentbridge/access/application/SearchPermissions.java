package com.example.talentbridge.access.application;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.PermissionRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchPermissions {

    private final PermissionRepository permissions;

    public PageResult<Permission> execute(FilterExpression filter, PageQuery page) {
        return permissions.search(filter, page);
    }

    /** Every match, without paging. */
    public List<Permission> executeUnpaged(FilterExpression filter) {
        return permissions.findAll(filter);
    }
}
