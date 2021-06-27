package com.dbase.helpers;

import com.dbase.models.Address;
import com.dbase.models.PhoneNumber;
import com.dbase.models.User;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserGenerator {

    private static Random random = new Random();

    public User getUserWithRandomData() {
        List<Address> adrList = Stream.of(new Address(0, getString("yyyyy")), new Address(0, getString("yyyyy"))).collect(Collectors.toList());
        List<PhoneNumber> pnList = Stream.of(new PhoneNumber(0, getString("xxxxxxx")), new PhoneNumber(0, getString("xxxxxxx"))).collect(Collectors.toList());
        return new User(0, getString("yyyyy"), getString("yyyyyxx"), getString("yxyxyxyx"), adrList, pnList);
    }

    private String getString(String pattern) {
        StringBuilder sb = new StringBuilder();
        String setOfLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String setOfNumbers = "1234567890";
        for (Character ch : pattern.toCharArray()) {
            if (ch.equals('y')) sb.append(getRandomSymbol(setOfLetters));
            if (ch.equals('x')) sb.append(getRandomSymbol(setOfNumbers));
        }
        return sb.toString();
    }

    private static Character getRandomSymbol(String word) {
        return word.charAt(random.nextInt(word.length() - 1));
    }

}