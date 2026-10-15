package com.example.talentbridge.auth.application;

import com.example.talentbridge.user.application.GetUserAccount;
import com.example.talentbridge.user.application.RegisterUser;
import com.example.talentbridge.user.application.UserData;
import com.example.talentbridge.user.domain.Gender;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterAccount {

    private final RegisterUser registerUser;
    private final GetUserAccount getUserAccount;

    /** Self registration; a recruiter gets the RECRUITER role, anybody else USER. */
    public SessionUser execute(String name, String email, String password, LocalDate dob, String address,
                               Gender gender, boolean recruiter) {
        registerUser.execute(new UserData(name, email, password, dob, address, gender, null, null),
                recruiter ? "RECRUITER" : "USER");
        return SessionUser.from(getUserAccount.execute(email));
    }
}
