package com.example.talentbridge.user.application.port;

/** Output port towards the company module. */
public interface CompanyCatalog {

    boolean exists(Long companyId);
}
