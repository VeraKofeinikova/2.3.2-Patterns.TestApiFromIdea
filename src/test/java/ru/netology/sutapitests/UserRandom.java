package ru.netology.sutapitests;

import java.util.Random;

public class UserRandom {
    String login;
    String password;
    String status;

    public UserRandom(String login, String password, String status) {
        this.login = login;
        this.password = password;
        this.status = status;
    }

    public static UserRandom createNewUserActive() {

        final String[] names = {
                "vasya",
                "gera",
                "marina",
                "kostya",
                "vlad"
        };

        final String[] passwords = {
                "password1",
                "password2",
                "password3",
                "password4",
                "password5"
        };

        Random random = new Random();

        String login = names[random.nextInt(names.length)];
        String password = passwords[random.nextInt(passwords.length)];
        String status = "active";

        UserRandom user = new UserRandom(login, password, status);
        return user;
    }

    public static UserRandom createNewUserBlocked() {

        final String[] names = {
                "vasya",
                "gera",
                "marina",
                "kostya",
                "vlad"
        };

        final String[] passwords = {
                "password1",
                "password2",
                "password3",
                "password4",
                "password5"
        };

        Random random = new Random();

        String login = names[random.nextInt(names.length)];
        String password = passwords[random.nextInt(passwords.length)];
        String status = "blocked";

        UserRandom user = new UserRandom(login, password, status);
        return user;
    }

    public static UserRandom hardcodedData1() {
        String login = "aleksei";
        String password = "password10";
        String status = "active";
        UserRandom user = new UserRandom(login, password, status);
        return user;
    }

    public static UserRandom hardcodedData2() {
        String login = "aleksei";
        String password = "password15";
        String status = "active";
        UserRandom user = new UserRandom(login, password, status);
        return user;
    }
}



