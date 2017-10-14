package com.company.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ALEX on 16.04.2017.
 */
public class RecursionPerlParser {
    private HashMap<Integer, Element> abstractMap;
    private String filename;
    private int count;
    private FileInputStream fin = null;

    public RecursionPerlParser(String filename) {
        this.filename = filename;
        this.abstractMap = new HashMap<>();
        count = 0;

        try {
            fin = new FileInputStream(filename);
        } catch (IOException e) {

        }
    }

    public void parse(int EOF, List<Element> elementList) {
        try {
            int i;

            //происходит чтение всего входного потока данных
            while ((i = fin.read()) != EOF) {
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
                    if (EOF == (int) '}') {
                        elementList.add(element);
                    } else {
                        abstractMap.put(count, element);
                        count++;
                    }

                } else if (((char) i != ' ') & ((char) i != ';') & ((char) i != '\r') &
                        ((char) i != '\n') & ((char) i != '{') & ((char) i != '\t')) {  //нашли вызов функции, системный оператор
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
                            element.setType("while");
                            StringBuilder conditionCycleBuilder = new StringBuilder();
                            while (k != '(') {
                                k = (char) fin.read();
                            }

                            k = (char) fin.read();
                            while ((k != ')')) {
                                conditionCycleBuilder.append(k);
                                k = (char) fin.read();
                            }
                            element.setCondition(conditionCycleBuilder.toString());
                            List<Element> elementCycleList = new LinkedList<>();
                            element.setBody(elementCycleList);

                            parse((int) '}', elementCycleList);

                            if (EOF == (int) '}') {
                                elementList.add(element);
                            } else {
                                abstractMap.put(count, element);
                                count++;
                            }
                            break;
                        case "if":
                            // будет доработан в соответствии с развитием алгоритма. Дополнительные правила
                            element.setType("branch");
                            StringBuilder conditionBuilder = new StringBuilder();
                            //делаем
                            k = (char) fin.read();
                            while ((k != ')')) {
                                conditionBuilder.append(k);
                                k = (char) fin.read();
                            }
                            element.setCondition(conditionBuilder.toString());

                            List<Element> listElement = new LinkedList<>();
                            element.setBody(listElement);

                            parse((int) '}', listElement);

                            if (EOF == (int) '}') {
                                elementList.add(element);
                            } else {
                                abstractMap.put(count, element);
                                count++;
                            }

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
                            if (EOF == (int) '}') {
                                elementList.add(element);
                            } else {
                                abstractMap.put(count, element);
                                count++;
                            }
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public HashMap<Integer, Element> getAbstractMap() {
        return abstractMap;
    }

    public void setAbstractMap(HashMap<Integer, Element> abstractMap) {
        this.abstractMap = abstractMap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FileInputStream getFin() {
        return fin;
    }

    public void setFin(FileInputStream fin) {
        this.fin = fin;
    }
}
