package ru.suchkov.search;


import ru.suchkov.search.index.Indexer;
import ru.suchkov.search.search.Searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class SearchApplication {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Нет ни одного аргумента");
		} else {
			String param0 = args[0].toLowerCase();
			if ("--index".equals(param0) || "-index".equals(param0) || "-i".equals(param0)) {
				if (args.length != 3) {
					System.out.println("Необходимо ввести путь к индексируемому файлу " +
							"и директорию для построения индекса");
				} else {
					new Indexer(Path.of(args[1]), Path.of(args[2])).build();
				}
			} else {
				if ("--search".equals(param0) || "-search".equals(param0) || "-s".equals(param0)) {
					if (args.length != 3) {
						System.out.println("Необходимо ввести путь к индексируемому файлу " +
								"и директорию с построенныминдексом");
					} else {
						Searcher searcher = new Searcher(Path.of(args[1]), Path.of(args[2]));
						try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
							System.out.print("Введите поисковый запрос $ ");
							for (String line = reader.readLine();
								 !line.toLowerCase().equals("exit()"); line = reader.readLine()) {
								System.out.println(searcher.searchExpression(line));
								System.out.print("$ ");
							}
						} catch (IOException e) {
							System.out.println("Ошибка ввода");
						}
					}
				} else {
					System.out.println("Неизвестная команда.");
				}
			}
		}
	}
}
