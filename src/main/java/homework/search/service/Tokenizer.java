package homework.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.stream.Collectors;
//Разбиваем строку на отдельные слова
public class Tokenizer {
//    private static boolean trimFilter(String t) {
//        return t.trim().length() > 1;
//    }

    public List<String> execute(String text){
        if (text == null){
            return new ArrayList<>();
        }
        return Arrays.asList(text.toLowerCase().split("[-.,:;!?\\s]+"));
//                .stream()
//                .filter(Tokenizer::trimFilter)
//                .collect(Collectors.toList());
    }
}