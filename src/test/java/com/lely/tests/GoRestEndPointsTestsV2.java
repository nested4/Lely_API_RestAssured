package com.lely.tests;

import com.lely.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GoRestEndPointsTestsV2 {

    private static final String BASE_URL = ConfigurationReader.getProperty("base_URL");
    private static final String ACCESS_TOKEN = ConfigurationReader.getProperty("access_token");

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

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
        String email = ConfigurationReader.getProperty("new_user_email");
        String name = ConfigurationReader.getProperty("new_user_name");
        String gender = ConfigurationReader.getProperty("new_user_gender");
        String status = ConfigurationReader.getProperty("new_user_status");

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
        String email = ConfigurationReader.getProperty("new_user_email");
        String name = ConfigurationReader.getProperty("new_user_name");
        String gender = ConfigurationReader.getProperty("new_user_gender");
        String status = ConfigurationReader.getProperty("new_user_status");

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
