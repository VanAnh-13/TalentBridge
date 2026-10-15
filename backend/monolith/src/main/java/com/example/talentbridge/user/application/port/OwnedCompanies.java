package com.example.talentbridge.user.application.port;

/** Output port towards the company module. */
public interface OwnedCompanies {

    /** Companies owned by the user stay, but have no owner any more. */
    void releaseOwnedBy(Long userId);
}
