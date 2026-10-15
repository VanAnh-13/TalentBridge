package com.example.talentbridge.user.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserDetails save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<UserDetails> findDetailsById(Long id);

    Optional<UserDetails> findDetailsByEmail(String email);

    Optional<UserAccount> findAccountByEmail(String email);

    PageResult<UserDetails> search(FilterExpression filter, PageQuery page);

    boolean existsByEmail(String email);

    List<User> findByCompanyId(Long companyId);

    List<User> findByRoleId(Long roleId);

    /** Removes the user. Resumes and company ownership are handled by the use case beforehand. */
    void delete(User user);
}
