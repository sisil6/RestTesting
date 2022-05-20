package APITests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.Test;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.annotations.IBeforeMethod;

import java.util.ArrayList;


public class APITests {

    static String authToken;
    static Integer registeredUserId;
    private String email;
    private String password;
    private String responseBody;
    private Integer loggedUserId;
    private Integer commentId;
    private Integer postID;


    @BeforeTest
    public void registerNewUser() throws JsonProcessingException {
        registerPOJO register = new registerPOJO();
        var currentTime = System.currentTimeMillis();
        register.setUsername("Si" + currentTime);
        register.setEmail("Si" + currentTime + "@S2");
        email = register.getEmail();
        register.setBirthDate("15.09.1991");
        register.setPassword("Ts" + currentTime + "@S2");
        password = register.getPassword();
        register.setPublicInfo("Hello, I am " + "Si" + currentTime);

        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(register);
        baseURI = "http://training.skillo-bg.com:3100"; //no var because the import on the top is static

        Response response = given()
                .header("Content-Type", "application/json")//we need only the content type here - from headers Postman.
                .body(convertedJson) // add the POJO in the body, as in the Postman body text
                .when()  // here we do the action - every action on new line
                .post("/users"); // change get, post, put as needed .  automatically adds the baseURI - rst is doing this on itself
        response
                .then()
                .statusCode(201);
        // convert the response body to string
        String registerResponseBody = response.getBody().asString();
        registeredUserId = JsonPath.parse(registerResponseBody).read("$.id");
    }

    // login method - reuse for all tests in need of it
    @Test(priority = -10)
    public void Login() throws JsonProcessingException {
        LoginPOJO login = new LoginPOJO();
        login.setUsernameOrEmail(email);
        login.setPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(login);
        baseURI = "http://training.skillo-bg.com:3100"; //no var because the import on the top is static
        Response response = given()
                .header("Content-Type", "application/json")//we need only the content type here - from headers Postman.
                .body(convertedJson) // add the POJO in the body, as in the Postman body text
                .when()  // here we do the action - every action on new line
                .post("/users/login"); // change get, post, put as needed .  automatically adds the baseURI - rst is doing this on itself
        // convert the response body to string
        responseBody = response.getBody().asString();
        authToken = JsonPath.parse(responseBody).read("$.token"); // read from the body of the response the json Path
        loggedUserId = JsonPath.parse(responseBody).read("$.user.id");
    }


    @Test(priority = -9)
    public void loginTest() throws JsonProcessingException {
        Login();
        Assert.assertEquals(loggedUserId, registeredUserId, "Logged user's Id equals Registered user's Id");
    }

    @Test(priority = -8)
    public void getPosts() throws JsonProcessingException {
        Login();
        ValidatableResponse validatableResponse =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("take", 4)
                        .queryParam("skip", 0)
                        .when()
                        .get("/posts/public")
                        .then()
                        .log()
                        .all()
                        .statusCode(200);
        ArrayList<Integer> postIds = new ArrayList<>();
        postIds = validatableResponse.extract().path("id");
        Assert.assertNotEquals(postIds, null);
//        for (int element : postIds) {                 //extracts all elements of the list
//            System.out.println(element);
//        }
        postID = postIds.get(1);
    }

    @Test(priority = -7)
    public void getSinglePost() throws JsonProcessingException {
        Login();

        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/posts/" + postID)
                .then()
                .log()
                .all()
                .statusCode(200);

    }


    @Test(priority = -6)
    public void likePost() throws JsonProcessingException {
        Login();
        actionsPOJO likePost = new actionsPOJO();
        likePost.setAction(("likePost"));
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(likePost)
                //     .body("/action/LikePost")  //we can hardcode here, but it is not recommendable can be this, can be the upper line one
                .when()
                .patch("/posts/" + postID)
                .then()
                .log()
                .all();

    }

    @Test(priority = -4)
    public void commentPost() throws JsonProcessingException {
        Login();
        actionsPOJO commentPost = new actionsPOJO();
        commentPost.setContent(("Nice post here"));
        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .post("/posts/" + postID + "/comment")
                .then()
                .assertThat().body("content", equalTo("Nice post here"))
                .assertThat().body("user.id", equalTo(registeredUserId))
                .log()
                .all()
                .statusCode(201);
        responseBody = validatableResponse.toString();
        commentId = validatableResponse.extract().path("id");

    }

    @Test(priority = -3)
    public void getCommentsOfAPost() throws JsonProcessingException {
        Login();
        ValidatableResponse validatableResponse = given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + authToken)
                        .when()
                        .get("/posts/" + postID + "/comments")
                        .then()
                        .log()
                        .all()
                        .statusCode(200);

        ArrayList<Integer> commentsIds = new ArrayList<>();
        commentsIds = validatableResponse.extract().path("id");
        Assert.assertNotEquals(commentsIds, null);
        for (int element : commentsIds) {
            if(element == commentId){
                System.out.println("The newly added comment is in the list");
            }
        }
    }

    @Test
    public void deleteLastComment() throws JsonProcessingException {
        Login();
        actionsPOJO deleteLastComment = new actionsPOJO();
        deleteLastComment.setContent(("Nice post here"));
        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(deleteLastComment)
                .when()
                .delete("/posts/" + postID + "/comments/" + commentId)
                .then()
                //     .body(posts.caption.equals) - this is the test in the postman
                .log()
                .all()
                .statusCode(200);

        responseBody = validatableResponse.toString();
        var deletedCommentId = validatableResponse.extract().path("id");
        Assert.assertEquals(commentId, deletedCommentId);

    }
}
