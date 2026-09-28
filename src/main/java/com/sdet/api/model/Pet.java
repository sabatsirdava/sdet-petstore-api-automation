package com.sdet.api.model;

import java.util.List;

public record Pet(long id, String name, List<String> photoUrls, String status) {

    public Pet {
        if (photoUrls == null || photoUrls.isEmpty()) {
            throw new IllegalArgumentException(
                    "A Pet must contain at least one photo URL"
            );
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "A Pet must contain a valid status"
            );
        }
    }
}