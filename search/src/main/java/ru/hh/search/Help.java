package ru.hh.search;

import java.util.StringJoiner;

/**
 * Хелпуха.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Help implements Action {

    private StringJoiner sj;

    @Override
    public void start(String[] args) {
        this.sj = new StringJoiner(System.lineSeparator())
                .add("Добро пожаловать в поисковик им. Бухтоярова =)")
                .add("Для индексации документа введите: ")
                .add("-i <путь до документа> <путь куда записать файл индекса>")
                .add("<Путь до документа> можно ввести путь до папки с несколькими документами, ")
                .add("тогда проиндексируются все документы в этой папке.")
                .add("<Путь куда записать файл индекса> ели в пути не указать название файла,")
                .add("то файл индекса будет называться index.xml.")
                .add("")
                .add("Для поиска введите:")
                .add("-s <путь до файла индекса>")
                .add("далее программа попросит ввести поисковый запрос.");
        System.out.println(this.sj.toString());
    }
}
