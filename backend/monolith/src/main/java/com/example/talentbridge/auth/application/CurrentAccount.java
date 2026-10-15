package com.example.talentbridge.auth.application;

import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.user.application.GetUser;
import com.example.talentbridge.user.application.GetUserAccount;
import com.example.talentbridge.user.domain.UserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurrentAccount {

    private final CurrentUser currentUser;
    private final GetUserAccount getUserAccount;
    private final GetUser getUser;

    /** The signed-in user (session information). */
    public SessionUser session() {
        return SessionUser.from(getUserAccount.execute(currentUser.email()));
    }

    /** The signed-in user's profile. */
    public UserDetails details() {
        return getUser.executeForCurrentUser();
    }
}
