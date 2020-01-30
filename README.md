# hh-school-search
## Дз по поиску 

### Сборка проекта
mvn clean package

### Запуск в режиме индексации
В директории для индекса создастся файл index.json

java -jar target/search-1-jar-with-dependencies.jar index <директория куда положить файл индекса> <путь к индексируемому файлу>

***Пример:*** java -jar target/search-1-jar-with-dependencies.jar index src/main/resources src/main/resources/test.txt

### Запуск в режиме поиска
java -jar target/search-1-jar-with-dependencies.jar search <путь к файлу индекса> <слово для поиска>

***Пример:*** java -jar target/search-1-jar-with-dependencies.jar search src/main/resources/index.json следует


### Реализован только поиск по одному слову
