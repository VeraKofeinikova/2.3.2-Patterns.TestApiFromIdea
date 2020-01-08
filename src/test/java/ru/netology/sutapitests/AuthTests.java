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

import java.util.Random;

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
    
    public static final String[] names = {
            "vasya",
            "gera",
            "marina",
            "kostya",
            "vlad"
    };
    Random random = new Random();
    String login = names[random.nextInt(names.length)];

    public static final String[] passwords = {
            "password1",
            "password2",
            "password3",
            "password4",
            "password5"
    };
    String password = passwords[random.nextInt(passwords.length)];

    @BeforeEach
    public void clearCookies() {
        open("http://localhost:9999");
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Test
    @DisplayName("Успешный вход. Пользователь active, имеет валидные логин и пароль")
    void loginSuccessUserActiveValidLoginValidPassword() {
        UserRandom userRandom = new UserRandom(login, password, "active");
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(login);
        form.$("[data-test-id=password] input").setValue(password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь blocked, имеет валидные логин и пароль")
    void loginFailedUserBlockedValidLoginValidPassword() {
        UserRandom userRandom = new UserRandom(login, password, "blocked");
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(login);
        form.$("[data-test-id=password] input").setValue(password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь active, неправильный логин-правильный пароль")
    void loginFailedUserActiveNotValidLoginValidPassword() {
        UserRandom userRandom = new UserRandom(login, password, "active");
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("oksana");
        form.$("[data-test-id=password] input").setValue(password);
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Невозможно войти. Пользователь active, правильный логин-неправильный пароль")
    void loginFailedUserActiveValidLoginNotValidPassword() {
        UserRandom userRandom = new UserRandom(login, password, "active");
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue(login);
        form.$("[data-test-id=password] input").setValue("password19");
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("notification__content")).shouldBe(visible).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    //данные захардкоржены намеренно, для уверенности что логин один и тот же, а пароли разные
    @DisplayName("Дважды передаем пользователя с одним и тем же именем. На второй раз у него перезаписывается пароль. Оба раза должен произойти успешный логин")
    void reWriteUserWhichChangePassword() {
        UserRandom userRandom = new UserRandom("aleksei", "password10", "active");
        Gson gson = new Gson();
        String json = gson.toJson(userRandom);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("aleksei");
        form.$("[data-test-id=password] input").setValue("password10");
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));

        UserRandom userRandomChangedPassword = new UserRandom("aleksei", "password15", "active");
        Gson gson2 = new Gson();
        String json2 = gson2.toJson(userRandomChangedPassword);

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(json2) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then()// "тогда ожидаем"
                .statusCode(200); // код 200 OK

        open("http://localhost:9999");
        //SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("aleksei");
        form.$("[data-test-id=password] input").setValue("password15");
        form.$(By.className("button_theme_alfa-on-white")).click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));
        //close();
    }
}