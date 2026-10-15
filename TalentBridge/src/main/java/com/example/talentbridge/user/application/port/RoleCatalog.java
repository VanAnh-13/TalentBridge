package com.example.talentbridge.user.application.port;

import java.util.Optional;

/** Output port towards the access module. */
public interface RoleCatalog {

    boolean exists(Long roleId);

    Optional<Long> idOfName(String roleName);
}
