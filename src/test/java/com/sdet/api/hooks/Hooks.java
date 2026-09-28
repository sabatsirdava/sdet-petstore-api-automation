package com.sdet.api.hooks;

import com.sdet.api.client.PetApiClient;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    private final PetApiClient petApiClient = new PetApiClient();

    @After
    public void cleanupCreatedPet() {
        Long petId = PetScenarioState.createdPetId();

        if (petId == null) {
            return;
        }

        try {
            int statusCode = petApiClient.deletePet(petId).statusCode();

            if (statusCode == 200 || statusCode == 404) {
                LOGGER.info("Cleanup for pet {} completed with status {}", petId, statusCode);
            } else {
                LOGGER.warn("Cleanup for pet {} returned unexpected status {}", petId, statusCode);
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Cleanup failed for pet {}", petId, exception);
        } finally {
            PetScenarioState.clear();
        }
    }
}