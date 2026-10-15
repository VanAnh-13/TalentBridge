package com.example.talentbridge.digest.domain;

import java.util.List;

/** The jobs worth telling one subscriber about. */
public record JobDigest(String recipient, List<DigestJob> jobs) {
}
