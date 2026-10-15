package com.example.talentbridge.access.application.port;

/** Output port towards the user module: users lose a role that is being deleted. */
public interface UserRoleDetacher {

    void detachUsersFromRole(Long roleId);
}
