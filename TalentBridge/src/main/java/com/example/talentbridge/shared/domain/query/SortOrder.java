package com.example.talentbridge.shared.domain.query;

public record SortOrder(String property, Direction direction) {
    public enum Direction { ASC, DESC }
}
