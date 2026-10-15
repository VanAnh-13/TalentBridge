package com.example.talentbridge.job.application.port;

import java.util.Collection;
import java.util.Set;

/** Output port towards the skill module. */
public interface SkillCatalog {

    /** The ids among {@code ids} that belong to an existing skill. */
    Set<Long> existingIds(Collection<Long> ids);
}
