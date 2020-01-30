package Main;

import Indexer.BuildIndex;
import Searcher.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class MainMenu {
    private final BufferedReader bufferedReader;

    public MainMenu(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    private void printMenu() {
        System.out.println("Индексация             [1]");
        System.out.println("Поиск по индексу       [2]");
        System.out.println("Выход из приложения    [3]");
    }

    public void start(){
        if (bufferedReader != null) {
            int key;
            do {
                printMenu();
                System.out.print("Введите номер меню: ");
                try {
                    key = Integer.parseInt(bufferedReader.readLine());
                } catch (IOException | NumberFormatException e) {
                    key = 4;
                    System.out.println("Ошибка ввода номера меню...\n");
                }
                switch (key) {
                    case 1:
                        try {
                            new BuildIndex(bufferedReader);
                        } catch (AccessDeniedException e){
                            System.out.println("Нет доступа к папке");
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Ошибка ввода данных...\n");
                        } catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Ошибка...\n");
                        }

                        break;
                    case 2:
                        try {
                            new Search(bufferedReader);
                        } catch (IOException e) {
                            System.out.println("Ошибка ввода данных...\n");
                        } catch (Exception e) {
                            System.out.println("Ошибка при чтении индекса...\n");
                        }
                        break;
                    case 3:
                        System.out.println("Завершение программы...");
                        break;
                    default:
                        System.out.println("Вы ввели неверное значение меню...\n");
                }
            } while (key != 3);
        }
    }
}
