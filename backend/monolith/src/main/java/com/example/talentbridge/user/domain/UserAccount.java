package com.example.talentbridge.user.domain;

import java.util.List;

/** Read model for authentication: the user, their role and the permissions it grants ("METHOD /path"). */
public record UserAccount(User user, UserDetails.RoleSummary role, List<String> permissions) {
}
