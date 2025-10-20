package ru.netology.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class SchemaTest {

    @BeforeAll
    static void setUp() throws InterruptedException {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 9999;

        // подождём готовности именно accounts-эндпоинта (до 30 сек)
        for (int i = 0; i < 30; i++) {
            try {
                int code = given().when().get("/api/v1/demo/accounts").then().extract().statusCode();
                if (code == 200) return;
            } catch (Exception ignored) {}
            Thread.sleep(1000);
        }
    }

    @Test
    @DisplayName("Схема accounts соответствует accounts.schema.json")
    void accountsMatchSchema() {
        given()
                .when()
                .get("/api/v1/demo/accounts")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("accounts.schema.json"));
    }
}