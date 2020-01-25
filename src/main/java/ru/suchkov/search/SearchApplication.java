package ru.suchkov.search;


import ru.suchkov.search.index.Indexer;

import java.nio.file.Path;

public class SearchApplication {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Нет ни одного аргумента");
		} else {
			String param0 = args[0].toLowerCase();
			if ("--index".equals(param0) || "-i".equals(param0)) {
				if (args.length != 3) {
					System.out.println("Необходимо ввести путь к индексируемому файлу " +
							"и директорию для построения индекса");
				} else {
					new Indexer(Path.of(args[1]), Path.of(args[2])).build();
				}
			} else {
				System.out.println("Неизвестная команда.");
			}
		}
	}
}
