package ru.netology.sutapitests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class AuthTests {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeEach
    public void clearCookies() {
        open("http://localhost:9999");
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Test
    @DisplayName("Успешный вход. Пользователь active, имеет валидные логин и пароль")
    void loginSuccessUserActiveValidLoginValidPassword() {
        UserRandom userRandom = UserRandom.createNewUserActive();
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(userRandom.login);
        form.$("[data-test-id=password] input").setValue(userRandom.password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь blocked, имеет валидные логин и пароль")
    void loginFailedUserBlockedValidLoginValidPassword() {
        UserRandom userRandom = UserRandom.createNewUserBlocked();
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(userRandom.login);
        form.$("[data-test-id=password] input").setValue(userRandom.password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь active, неправильный логин-правильный пароль")
    void loginFailedUserActiveNotValidLoginValidPassword() {
        UserRandom userRandom = UserRandom.createNewUserActive();
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("oksana");
        form.$("[data-test-id=password] input").setValue(userRandom.password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь active, правильный логин-неправильный пароль")
    void loginFailedUserActiveValidLoginNotValidPassword() {
        UserRandom userRandom = UserRandom.createNewUserActive();
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(userRandom.login);
        form.$("[data-test-id=password] input").setValue("password19");
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    //данные захардкоржены намеренно, для уверенности что логин один и тот же, а пароли разные
    @DisplayName("Дважды передаем пользователя с одним и тем же именем. На второй раз у него перезаписывается пароль. Оба раза должен произойти успешный логин")
    void reWriteUserWhichChangePassword() {
        UserRandom userRandom = UserRandom.hardcodedData1();
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(userRandom.login);
        form.$("[data-test-id=password] input").setValue(userRandom.password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));

        UserRandom userRandomChangedPassword = UserRandom.hardcodedData2();
        Gson gson2 = new Gson();
        String json2 = gson2.toJson(userRandomChangedPassword);

        given()
                .spec(requestSpec)
                .body(json)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        open("http://localhost:9999");
        //SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(userRandomChangedPassword.login);
        form.$("[data-test-id=password] input").setValue(userRandomChangedPassword.password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));
        //close();
    }
}