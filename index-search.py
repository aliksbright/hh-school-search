import re
from collections import defaultdict

# функции принимают файлы в формате txt
regex = (r'\,*\:*\;*\.*\!*\?*\#*')  # пример стоп-знаков
not_indexed = ('in', 'about', 'for', 'on')  # пример стоп-слов


def index(indexFile, inputFile):
    index_dict = defaultdict(list)

    with open(inputFile, 'r') as file_input:
        # читаем с файла разбиваем по символу конца строки
        list_data = file_input.read().split('\n')

    for item in list_data:
        item_splited = re.sub(regex, '', item).split()
        for index in item_splited:
            if index not in not_indexed:
                index_dict[index.lower()].append(item)

    with open(indexFile, 'w') as file_out:
        for key in index_dict:
            # пишем результат в файл в строковом типе
            file_out.write(str(key)+':'+str(index_dict[key])+'\n')


def search(indexFile, request):
    matches = []

    with open(indexFile, 'r') as file_input:
        # читаем с файла разбиваем по символу конца строки
        list_data = file_input.read().split('\n')

    for item in list_data:
        item_splited = item.split(':')

        if request.lower() == item_splited[0]:
            matches = item_splited[1]

    return matches
