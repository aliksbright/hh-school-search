
import java.io.BufferedReader;
import java.io.IOException;

class MainMenu {
    private final BufferedReader bufferedReader;

    MainMenu(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    private void printMenu() {
        System.out.println("Индексация             [1]");
        System.out.println("Поиск по индексу       [2]");
        System.out.println("Выход из приложения    [3]");
    }

    void start(){
        if (bufferedReader != null) {
            int key;
            do {
                printMenu();
                System.out.print("Введите номер меню: ");
                try {
                    key = Integer.parseInt(bufferedReader.readLine());
                } catch (IOException e) {
                    key = 4;
                    System.out.println("Ошибка ввода номера меню...\n");
                }
                switch (key) {
                    case 1:
                        try {
                            new BuildIndex(bufferedReader);
                        } catch (IOException e) {
                            System.out.println("Ошибка ввода данных...\n");
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
