package com.sdet.api.client;

import com.sdet.api.config.ApiConfig;
import com.sdet.api.model.Pet;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * The only class in the framework that interacts directly with RestAssured.
 */
public class PetApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetApiClient.class);

    private static final String PET_PATH = "/pet";
    private static final String FIND_BY_STATUS_PATH = PET_PATH + "/findByStatus";

    public Response createPet(Pet pet) {
        Response response = givenRequest()
                .body(pet)
                .when()
                .post(PET_PATH);

        logResponse("POST", PET_PATH, response);
        return response;
    }

    public Response createPetRaw(String requestBody) {
        Response response = givenRequest()
                .body(requestBody)
                .when()
                .post(PET_PATH);

        logResponse("POST", PET_PATH, response);
        return response;
    }

    public Response getPet(String id) {
        String path = PET_PATH + "/" + id;

        Response response = givenRequest()
                .when()
                .get(path);

        logResponse("GET", path, response);
        return response;
    }

    public Response updatePet(Pet pet) {
        Response response = givenRequest()
                .body(pet)
                .when()
                .put(PET_PATH);

        logResponse("PUT", PET_PATH, response);
        return response;
    }

    public Response deletePet(long id) {
        String path = PET_PATH + "/" + id;

        Response response = givenRequest()
                .when()
                .delete(path);

        logResponse("DELETE", path, response);
        return response;
    }

    public Response findByStatus(String status) {
        Response response = givenRequest()
                .queryParam("status", status)
                .when()
                .get(FIND_BY_STATUS_PATH);

        logResponse("GET", FIND_BY_STATUS_PATH + "?status=" + status, response);
        return response;
    }

    private RequestSpecification givenRequest() {
        return given()
                .baseUri(ApiConfig.baseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    private void logResponse(String method, String path, Response response) {
        LOGGER.info("{} {} -> {}", method, path, response.statusCode());
    }
}