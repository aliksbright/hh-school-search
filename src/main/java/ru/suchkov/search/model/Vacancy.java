package ru.suchkov.search.model;

import lombok.Data;

@Data
public class Vacancy {
    private String name;
    private String area;
    private String requirement;
    String employer;
    private float salary;
}
