package ru.netology.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RestTest {

    @BeforeAll
    static void setUp() throws InterruptedException {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 9999;


        for (int i = 1; i <= 30; i++) {
            try {
                int s1 = given().when().get("/api/demo").then().extract().statusCode();
                if (s1 == 200) return;
            } catch (Exception ignored) { }
            try {
                int s2 = given().when().get("/api/v1/demo/accounts").then().extract().statusCode();
                if (s2 == 200) return;
            } catch (Exception ignored) { }
            Thread.sleep(1000);
        }

    }

    @Test
    @DisplayName("Сервис отвечает 200 хотя бы на одном из учебных эндпоинтов")
    void serviceAliveOnKnownEndpoint() {

        Response r1 = given().when().get("/api/demo").then().extract().response();
        if (r1.statusCode() == 200) {

            given()
                    .when().get("/api/demo")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(containsString(ContentType.JSON.toString()))
                    .body("demo", equalTo(true));
            return;
        }


        Response r2 = given().when().get("/api/v1/demo/accounts").then().extract().response();
        if (r2.statusCode() == 200) {

            given()
                    .when().get("/api/v1/demo/accounts")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("", not(empty()))
                    .body("[0].balance", greaterThanOrEqualTo(0));
            return;
        }


        throw new AssertionError("Сервис не отвечает 200 ни на /api/demo (" + r1.statusCode() +
                "), ни на /api/v1/demo/accounts (" + r2.statusCode() + ").");
    }
}