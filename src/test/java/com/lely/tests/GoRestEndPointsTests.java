package com.lely.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GoRestEndPointsTests {

    private static final String BASE_URL = "https://gorest.co.in/public/v1";
    private static final String ACCESS_TOKEN = "1db9c9b6c959682be7c96f74ca532c3cb0bd331f46b86a92602f8d319481b6f5";

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * In gorest.co.in ID Values are 7 Digits now, in task it says 4. I have updated the task as 7 digits.
     */
    @Test
    public void testIdValuesAreSevenDigitIntegersAndNotNull() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .get("/users");

        response.then().statusCode(200);

        // Get the JSON response body
        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assert that all data.id values are 7-digit integers and not null
        response.then().body("data.id",everyItem(notNullValue()));
        response.then().body("data.id",everyItem(isA(Integer.class)));
        response.then().body("data.id",everyItem(greaterThan(999999)));
        response.then().body("data.id",everyItem(lessThan(10000000)));
    }

    @Test
    public void testCreateUser() {
        //Change the email to create a new user succesfully
        String email = "umut400@gmail.com";
        String name = "test";
        String gender = "male";
        String status = "active";

        // Create the request body JSON object
        String requestBody = String.format("{\"email\":\"%s\",\"name\":\"%s\",\"gender\":\"%s\",\"status\":\"%s\"}",
                email, name, gender, status);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .body(requestBody)
                .when()
                .post("/users");

        response.then().statusCode(201);

        // Assert that the response matches the given data
        // Adjust the JSON path expressions according to the actual structure of the response
        Assert.assertEquals(response.path("data.email"), email);
        Assert.assertEquals(response.path("data.name"), name);
        Assert.assertEquals(response.path("data.gender"), gender);
        Assert.assertEquals(response.path("data.status"), status);
    }

    @Test
    public void testDuplicateUserCreation() {
        String email = "umut400@gmail.com";
        String name = "test";
        String gender = "male";
        String status = "active";

        // Create the request body JSON object
        String requestBody = String.format("{\"email\":\"%s\",\"name\":\"%s\",\"gender\":\"%s\",\"status\":\"%s\"}",
                email, name, gender, status);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .body(requestBody)
                .when()
                .post("/users");

        response.then().statusCode(422);

        // Assert that the response message indicates that the user is not created due to duplication
        Assert.assertEquals(response.path("data[0].message"), "has already been taken");
    }
}
