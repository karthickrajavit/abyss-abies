package com.abies.utils;

import com.github.javafaker.Faker;

import java.util.UUID;

public class FakerUtils {

    public static String generateName(){
        Faker faker = new Faker();
        return "Playlist " + faker.regexify("[A-Za-z0-9 ,_-]{10}");
    }

    public static String generateDescription(){
        Faker faker = new Faker();
        return "Description " + faker.regexify("[ A-Za-z0-9_@./#&+-]{50}");
    }

    public static String generateUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
