package com.example.talentbridge.subscriber.application.port;

import com.example.talentbridge.subscriber.domain.SubscribedSkill;

import java.util.List;

/** Output port: looks up the skills the subscription may refer to. */
public interface SkillCatalog {

    /** The existing skills among {@code ids}; unknown ids are ignored. */
    List<SubscribedSkill> findExisting(List<Long> ids);
}
