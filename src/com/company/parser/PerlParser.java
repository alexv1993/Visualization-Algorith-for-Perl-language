package com.company.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ALEX on 15.04.2017.
 */
public class PerlParser {
    private HashMap<Integer, Element> abstractMap;
    private String filename;
    private int count;

    public PerlParser(String filename) {
        this.filename = filename;
        this.abstractMap = new HashMap<>();
        count = 0;
    }

    //реализация конечного автомама(больше всего на него похоже)
    public void parse() {
        //для начала считывания из файла всех команд/операций
        try (FileInputStream fin = new FileInputStream(filename)) {

            int i;

            //происходит чтение всего входного потока данных
            while ((i = fin.read()) != -1) {
                //считанный символ
                char ch = (char) i;

                int j;
                //нашли идентификацию переменных
                StringBuilder builder = new StringBuilder();

                //нашли переменную
                if ((char) i == '$') {
                    //нашли элемент - переменную
                    Element element = new Element();
                    element.setType("var");

                    //начинаем формировать текст команды
                    builder.append((char) i);

                    while (((char) (i = fin.read())) != ';') {
                        //нашли обозначение
                        builder.append((char) i);
                    }
                    //устанавливаем текст команды
                    element.setCommand(builder.toString());
                    abstractMap.put(count, element);
                    count++;


                } else if (((char) i != ' ') & ((char) i != ';') & ((char) i != '\r') &
                        ((char) i != '\n')) {  //нашли вызов функции, системный оператор
                    Element element = new Element();

                    //начинаем формировать текст команды
                    // проверка на то, что это может быть либо условием, либо циклом
                    builder.append((char) i);
                    char k = (char) fin.read();

                    //пока не встретил конец вызова функции или вызова с передачей аргументов
                    while ((k != ' ') & (k != '(')) {
                        builder.append(k);
                        k = (char) fin.read();
                    }
                    //определили, что там будет идти за команда
                    String command = builder.toString();
                    //проверка, что это if
                    //проверка, что это while

                    switch (command) {
                        case "while":
                            // проверяем, пробел ли последний символ
                            //проверяем открывающуюся фигурную скобку
                            //проверяем закрывающуюся скобку



                            break;
                        case "if":
                            // будет доработан в соответствии с развитием алгоритма. Дополнительные правила
                            element.setType("branch");

                            StringBuilder conditionBuilder = new StringBuilder();
                            //делаем
                            while ((k != ')')) {
                                conditionBuilder.append(k);
                            }
                            element.setCondition(conditionBuilder.toString());

                            List<Element> elementList = new LinkedList<>();

                            break;
                        default:
                            //найден оператор языка
                            //необходимо смотреть, на каком символе произошла остановка
                            element.setType("function");
                            element.setNameFunction(builder.toString());
                            //пробел, команда без операторов
                            builder.append(k);
                            while (((char) (i = fin.read())) != ';') {
                                //нашли обозначение
                                builder.append((char) i);
                            }
                            //устанавливаем текст команды
                            element.setCommand(builder.toString());
                            abstractMap.put(count, element);
                            count++;

                            break;
                    }
                }
            }
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }

    public void lexParse() {

    }
}
