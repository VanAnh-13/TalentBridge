package com.example.talentbridge.user.application;

import com.example.talentbridge.user.domain.Gender;

import java.time.LocalDate;

/** Fields shared by the commands that create or edit a user. A company / role id of -1 clears the link. */
public record UserData(String name, String email, String password, LocalDate dob, String address, Gender gender,
                       Long companyId, Long roleId) {
}
