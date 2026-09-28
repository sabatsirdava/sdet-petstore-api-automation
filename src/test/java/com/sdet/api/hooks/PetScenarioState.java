package com.sdet.api.hooks;

/**
 * Holds the pet ID created by the current scenario so the Cucumber hook
 * can remove it after the scenario finishes.
 */
public final class PetScenarioState {

    private static final ThreadLocal<Long> CREATED_PET_ID = new ThreadLocal<>();

    private PetScenarioState() {
    }

    public static void rememberCreatedPet(long id) {
        CREATED_PET_ID.set(id);
    }

    public static Long createdPetId() {
        return CREATED_PET_ID.get();
    }

    public static void clear() {
        CREATED_PET_ID.remove();
    }
}