package com.market.feed.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Jwt {

    private String jwt;
    private LocalDate date;
    private String userId;
}
