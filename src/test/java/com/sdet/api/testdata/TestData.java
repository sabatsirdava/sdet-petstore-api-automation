package com.sdet.api.testdata;

import com.sdet.api.model.Pet;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class TestData {

    public static final String UPDATED_NAME_SUFFIX = "-updated";
    public static final String UPSERT_PET_NAME = "upsert-characterization";
    public static final String AVAILABLE_STATUS = "available";
    public static final String SOLD_STATUS = "sold";
    public static final String NON_NUMERIC_ID = "abc";

    private static final String PHOTO_URL_TEMPLATE =
            "https://example.test/photos/%d.png";

    private static final String MISSING_NAME_PAYLOAD_TEMPLATE = """
            {"id":%d,"photoUrls":["https://example.test/missing-name.png"]}
            """;

    private static final String MALFORMED_JSON = "{\"id\":";

    private static final long ID_OFFSET = 100_000_000L;
    private static final long ID_RANGE = 800_000_000L;

    private static final AtomicLong NEXT_ID =
            new AtomicLong(
                    (System.currentTimeMillis() % ID_RANGE) + ID_OFFSET
            );

    private TestData() {
    }

    public static long uniquePetId() {
        return NEXT_ID.getAndIncrement();
    }

    public static Pet pet(long id, String name, String status) {
        return new Pet(
                id,
                name,
                List.of(PHOTO_URL_TEMPLATE.formatted(id)),
                status
        );
    }

    public static String updatedName(String originalName) {
        return originalName + UPDATED_NAME_SUFFIX;
    }

    public static String missingNamePayload(long id) {
        return MISSING_NAME_PAYLOAD_TEMPLATE.formatted(id);
    }

    public static String malformedJson() {
        return MALFORMED_JSON;
    }
}