package com.github.merelysnow.mobcoins.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {

    private final String name;
    private double mobcoins;

}
