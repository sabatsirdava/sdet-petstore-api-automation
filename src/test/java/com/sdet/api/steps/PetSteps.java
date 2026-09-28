package com.sdet.api.steps;

import com.sdet.api.client.PetApiClient;
import com.sdet.api.hooks.PetScenarioState;
import com.sdet.api.model.Pet;
import com.sdet.api.testdata.ApiTestConstants;
import com.sdet.api.testdata.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

public class PetSteps {

    private final PetApiClient petApiClient = new PetApiClient();

    private Response response;
    private Pet requestPet;
    private long petId;

    @Given("I have a unique pet named {string} with status {string}")
    public void iHaveAUniquePet(String name, String status) {
        petId = TestData.uniquePetId();
        requestPet = TestData.pet(petId, name, status);
    }

    @When("I create the pet")
    public void iCreateThePet() {
        response = petApiClient.createPet(requestPet);
        PetScenarioState.rememberCreatedPet(petId);
    }

    @When("I create the pet with malformed JSON")
    public void iCreatePetWithMalformedJson() {
        response = petApiClient.createPetRaw(TestData.malformedJson());
    }

    @When("I create the pet with a missing name")
    public void iCreatePetWithMissingName() {
        petId = TestData.uniquePetId();

        response = petApiClient.createPetRaw(
                TestData.missingNamePayload(petId)
        );

        PetScenarioState.rememberCreatedPet(petId);
    }

    @When("I read the pet")
    public void iReadThePet() {
        response = petApiClient.getPet(String.valueOf(petId));
    }

    @When("I read the pet using a never-used ID")
    public void iReadUsingNeverUsedId() {
        petId = TestData.uniquePetId();
        response = petApiClient.getPet(String.valueOf(petId));
    }

    @When("I read the pet using a non-numeric ID")
    public void iReadUsingNonNumericId() {
        response = petApiClient.getPet(TestData.NON_NUMERIC_ID);
    }

    @When("I update the pet")
    public void iUpdateThePet() {
        requestPet = TestData.pet(
                petId,
                TestData.updatedName(requestPet.name()),
                TestData.SOLD_STATUS
        );

        response = petApiClient.updatePet(requestPet);
        PetScenarioState.rememberCreatedPet(petId);
    }

    @When("I update a never-used ID with a valid pet")
    public void iUpdateNeverUsedId() {
        petId = TestData.uniquePetId();

        requestPet = TestData.pet(
                petId,
                TestData.UPSERT_PET_NAME,
                TestData.AVAILABLE_STATUS
        );

        response = petApiClient.updatePet(requestPet);
        PetScenarioState.rememberCreatedPet(petId);
    }

    @When("I delete the pet")
    public void iDeleteThePet() {
        response = petApiClient.deletePet(petId);
    }

    @When("I delete a never-used ID")
    public void iDeleteNeverUsedId() {
        petId = TestData.uniquePetId();
        response = petApiClient.deletePet(petId);
    }

    @When("I find pets by the status {string}")
    public void iFindByStatus(String status) {
        response = petApiClient.findByStatus(status);
    }

    @When("I find pets by the unsupported status {string}")
    public void iFindByUnsupportedStatus(String status) {
        response = petApiClient.findByStatus(status);
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        Assert.assertEquals(
                response.statusCode(),
                expectedStatus,
                "Unexpected response body: " + response.asString()
        );
    }

    @Then("the response content type should be JSON")
    public void responseContentTypeShouldBeJson() {
        String contentType = response.contentType();

        Assert.assertTrue(
                contentType != null
                        && contentType.contains(ApiTestConstants.JSON_CONTENT_TYPE),
                "Expected JSON content type but was: " + contentType
        );
    }

    @Then("the created pet should contain its request values")
    @Then("the pet should contain its request values")
    public void petShouldContainRequestValues() {
        Assert.assertEquals(
                response.jsonPath().getLong("id"),
                petId
        );

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                requestPet.name()
        );

        Assert.assertEquals(
                response.jsonPath().getString("status"),
                requestPet.status()
        );

        Assert.assertEquals(
                response.jsonPath().getList("photoUrls").get(0),
                requestPet.photoUrls().get(0)
        );
    }

    @Then("the pet should contain the updated values")
    public void petShouldContainUpdatedValues() {
        Assert.assertEquals(
                response.jsonPath().getLong("id"),
                petId
        );

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                requestPet.name()
        );

        Assert.assertEquals(
                response.jsonPath().getString("status"),
                requestPet.status()
        );
    }

    @Then("the response should confirm deletion of the pet")
    public void responseShouldConfirmDeletion() {
        Assert.assertEquals(
                response.jsonPath().getInt("code"),
                ApiTestConstants.OK
        );

        Assert.assertEquals(
                response.jsonPath().getString("message"),
                String.valueOf(petId)
        );
    }

    @Then("the response should report that the pet was not found")
    public void responseShouldReportPetNotFound() {
        Assert.assertEquals(
                response.jsonPath().getInt("code"),
                ApiTestConstants.PET_NOT_FOUND_CODE
        );

        Assert.assertEquals(
                response.jsonPath().getString("type"),
                ApiTestConstants.ERROR_TYPE
        );

        Assert.assertEquals(
                response.jsonPath().getString("message"),
                ApiTestConstants.PET_NOT_FOUND_MESSAGE
        );
    }

    @Then("the response should report malformed input")
    public void responseShouldReportMalformedInput() {
        Assert.assertEquals(
                response.jsonPath().getInt("code"),
                ApiTestConstants.BAD_REQUEST
        );

        Assert.assertEquals(
                response.jsonPath().getString("message"),
                ApiTestConstants.BAD_INPUT_MESSAGE
        );
    }

    @Then("the response should omit the missing name field")
    public void responseShouldOmitMissingName() {
        Assert.assertEquals(
                response.jsonPath().getLong("id"),
                petId
        );

        Assert.assertFalse(
                response.jsonPath().getMap("$").containsKey("name"),
                "The live API unexpectedly added a name field"
        );
    }

    @Then("the response should show that the update created the pet")
    public void responseShouldShowUpdateCreatedPet() {
        Assert.assertEquals(
                response.jsonPath().getLong("id"),
                petId
        );

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                requestPet.name()
        );

        Assert.assertEquals(
                response.jsonPath().getString("status"),
                requestPet.status()
        );
    }

    @Then("the response should describe a numeric ID parsing error")
    public void responseShouldDescribeNumericIdParsingError() {
        Assert.assertEquals(
                response.jsonPath().getInt("code"),
                ApiTestConstants.NOT_FOUND
        );

        String message = response.jsonPath().getString("message");

        Assert.assertTrue(
                message.contains(ApiTestConstants.NUMBER_FORMAT_EXCEPTION),
                "Expected NumberFormatException in message but was: " + message
        );
    }

    @Then("the response should contain pets with status {string}")
    public void responseShouldContainPetsWithStatus(String expectedStatus) {
        List<String> statuses =
                response.jsonPath().getList("status", String.class);

        Assert.assertNotNull(
                statuses,
                "Expected a list of pet statuses"
        );

        Assert.assertFalse(
                statuses.isEmpty(),
                "Expected at least one pet in the response"
        );

        for (String status : statuses) {
            Assert.assertEquals(
                    status,
                    expectedStatus,
                    "Found a pet with an unexpected status"
            );
        }
    }

    @Then("the created pet should be included in the response")
    public void createdPetShouldBeIncludedInResponse() {
        List<Long> ids = response.jsonPath().getList("id", Long.class);

        Assert.assertNotNull(
                ids,
                "Expected pet IDs in the response"
        );

        Assert.assertTrue(
                ids.contains(petId),
                "Expected created pet ID " + petId
                        + " to be included in the response"
        );
    }

    @Then("the response should contain an empty JSON array")
    public void responseShouldContainEmptyJsonArray() {
        List<Object> pets = response.jsonPath().getList("$");

        Assert.assertNotNull(
                pets,
                "Expected a JSON array response"
        );

        Assert.assertTrue(
                pets.isEmpty(),
                "Expected an empty JSON array but found: "
                        + response.asString()
        );
    }
}