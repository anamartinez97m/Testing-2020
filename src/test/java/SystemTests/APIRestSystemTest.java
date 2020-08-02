package SystemTests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static es.codeurjc.shop.Application.start;
import static es.codeurjc.shop.Application.stop;
import static io.restassured.path.json.JsonPath.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class APIRestSystemTest {

    @BeforeAll
    public static void setupClass() {
        start();
    }

    @AfterAll
    public static void tearDownClass() {
        stop();
    }

    @Test
    public void createPurchaseHasCreditAndStockTest() {

        // Creates a new product
        Response responseProduct =
                given().
                    body("{\n" +
                            "\t\"name\": \"Test\",\n" +
                            "\t\"cost\": 12,\n" +
                            "\t\"stock\": 10\n" +
                            "}").
                when().
                    contentType(ContentType.JSON).
                    post("http://localhost:8080/api/products/").andReturn();

        int productId = from(responseProduct.getBody().asString()).get("id");

        // Creates a new customer
        Response responseCustomer =
                given().
                    body("{\n" +
                            "\t\"name\": \"Test\",\n" +
                            "\t\"credit\": 100\n" +
                            "}").
                when().
                    contentType(ContentType.JSON).
                    post("http://localhost:8080/api/customers/").andReturn();

        int customerId = from(responseCustomer.getBody().asString()).get("id");

        // Creates a new purchase with the product and customer above
        given().
            body("{\n" +
                    "\t\"productId\": " + productId + ",\n" +
                    "\t\"customerId\": " + customerId + "\n" +
                    "}").
        when().
            contentType(ContentType.JSON).
            post("http://localhost:8080/api/purchases/").
        then().
            statusCode(200);
        }

    @Test
    public void notCreatePurchaseWithNoCreditTest() {

        // Creates a new product
        Response responseProduct =
                given().
                    body("{\n" +
                            "\t\"name\": \"Test\",\n" +
                            "\t\"cost\": 12,\n" +
                            "\t\"stock\": 10\n" +
                            "}").
                when().
                    contentType(ContentType.JSON).
                    post("http://localhost:8080/api/products/").andReturn();

        int productId = from(responseProduct.getBody().asString()).get("id");

        // Creates a new customer
        Response responseCustomer =
                given().
                    body("{\n" +
                            "\t\"name\": \"Test\",\n" +
                            "\t\"credit\": 11\n" +
                            "}").
                when().
                    contentType(ContentType.JSON).
                    post("http://localhost:8080/api/customers/").andReturn();

        int customerId = from(responseCustomer.getBody().asString()).get("id");

        // Creates a new purchase with the product and customer above
        given().
            body("{\n" +
                    "\t\"productId\": " + productId + ",\n" +
                    "\t\"customerId\": " + customerId + "\n" +
                    "}").
        when().
            contentType(ContentType.JSON).
            post("http://localhost:8080/api/purchases/").
        then().
            statusCode(400).
            body("message", equalTo("CustomerCreditLimitExceededException"));
    }

    @Test
    public void notCreatePurchaseWithNoStock() {

        // Creates a new product
        Response responseProduct =
                given().
                        body("{\n" +
                                "\t\"name\": \"Test\",\n" +
                                "\t\"cost\": 12,\n" +
                                "\t\"stock\": 0\n" +
                                "}").
                        when().
                        contentType(ContentType.JSON).
                        post("http://localhost:8080/api/products/").andReturn();

        int productId = from(responseProduct.getBody().asString()).get("id");

        // Creates a new customer
        Response responseCustomer =
                given().
                        body("{\n" +
                                "\t\"name\": \"Test\",\n" +
                                "\t\"credit\": 100\n" +
                                "}").
                        when().
                        contentType(ContentType.JSON).
                        post("http://localhost:8080/api/customers/").andReturn();

        int customerId = from(responseCustomer.getBody().asString()).get("id");

        // Creates a new purchase with the product and customer above
        given().
                body("{\n" +
                        "\t\"productId\": " + productId + ",\n" +
                        "\t\"customerId\": " + customerId + "\n" +
                        "}").
                when().
                contentType(ContentType.JSON).
                post("http://localhost:8080/api/purchases/").
                then().
                statusCode(400).
                body("message", equalTo("ProductStockWithdrawExceededException"));
    }
}
