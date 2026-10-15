package com.example.talentbridge.shared.application;

/** Output port: the authenticated caller of the current request. */
public interface CurrentUser {

    /** Login email of the caller. */
    String email();
}
