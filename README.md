# hh-school-search
## Дз по поиску 

Реализовать свой простой поиск на java, который может работать в 2 режимах:

1. Индексация: на вход подаётся название файла(директории) индекса и исходный файл, где каждая строка является отдельным документом в индексе, и происходит построение индекса, который записывается в файл(директорию). 

2. Поиск: на вход подаётся название файла индекса и запрос, на выходе получаем список документов подходящих под этот запрос.

Начать можно с написания наивной реализации инвертированного индекса и запросов по одному слову. В итоге должна получиться jar-ка, которая может работать в 2 режимах. Сдавать в виде PR к этому репозиторию с инструкцией для сборки и запуска, и списком что было реализованно. 

## Доп. задания (не обязательно, в порядке усложнения): 
* написать реализацию запросов AND и NOT
* написать реализацию фразовых запросов
* применить оптимизации при построении индекса из лекции
* написать реализацию OR с задаваемым минимальным количеством вхождений (пример: есть запрос java OR scala OR kotlin, мы хотим все документы где есть минимум 2 слова)

## Сборка
Сборка через maven. Pom в комплекте. Собирал через *mvn clean install*.

## Запуск через терминал
Запускал через maven через команду *mvn exec:java -Dexec.mainClass="Run" -Dexec.args=***"список аргументов"**
Аргументы для запуска имеют следующую структуру:
1. Первый аргумент - режим работы. Возможно два: *search* или *index*
2. Второй аргумент - путь до файла с индексом.
3. Третий и последующие
  * Если режим индексации, то путь до файла с документами. Пример запроса: *index index.txt docs.txt*
  * Если режим поиска, то возможны следующие варианты:
    * Простой поиск. Например *search index.txt developer*
    * Поиск с **AND** или **NOT**. Скобки не учитываются. Например, *search index.txt not developer and java*
    * Поиск фразового запроса. Например, *search index.txt java developer*
    * Поиск с логическим **OR**. Например, *search index.txt java OR developer OR программист 2*

## Срок сдачи

31.01.2019 23:59

## UPD 1

не использовать сторонние библиотеки полнотекста (lucene)
