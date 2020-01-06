package ru.netology.sutapitests;

import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Random;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AuthTest {
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
    String name = names[random.nextInt(names.length)];

    public static final String[] passwords = {
            "password1",
            "password2",
            "password3",
            "password4",
            "password5"
    };
    String password = passwords[random.nextInt(passwords.length)];

    @Test
    @DisplayName("Успешный вход")

    void loginSuccess() {
        UserRandom userRandom = new UserRandom(name, password, "active");
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
        form.$("[data-test-id=login] input").setValue(name);
        form.$("[data-test-id=password] input").setValue(password);
        form.$("[data-test-id=action-login] button").click();
        $(By.className("heading_theme_alfa-on-white")).shouldHave(exactText("Личный кабинет"));
    }
}