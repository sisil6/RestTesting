package APITests;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class FirstAPITest {

    @Test
    public void getPosts(){
        given()
                .when()
                .get("http://training.skillo-bg.com:3100/posts/public?take=6&skip=0")
                .then()
                .log()
                .all(); //can be .body() or anything we need of the response

    }
}

