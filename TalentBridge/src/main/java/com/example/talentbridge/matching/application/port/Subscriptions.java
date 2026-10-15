package com.example.talentbridge.matching.application.port;

import java.util.List;

/** Output port towards the subscriber module: the skills a person follows. */
public interface Subscriptions {

    List<String> skillsOf(String email);
}
