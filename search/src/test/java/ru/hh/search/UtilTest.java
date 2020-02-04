package ru.hh.search;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import static ru.hh.search.Util.*;

public class UtilTest {

    @Test
    public void when() {
        assertEquals(
                List.of("\"поле", "русское", "поле", "c++\""),
                getTokens("\"Поле Русское поле, c++\"")
        );

    }
}