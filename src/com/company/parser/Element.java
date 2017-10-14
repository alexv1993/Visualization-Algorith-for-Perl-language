package com.company.parser;

import java.util.List;

/**
 * Created by ALEX on 15.04.2017.
 */
public class Element {
    private String type;             //что за операция
    private String command;         //Полный текст команды
    private String condition;       //Условие
    private String nameFunction;    //имя функции

    //наполнение самого Body(для циклов)
    private List<Element> body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Element> getBody() {
        return body;
    }

    public void setBody(List<Element> body) {
        this.body = body;
    }

    public String getNameFunction() {
        return nameFunction;
    }

    public void setNameFunction(String nameFunction) {
        this.nameFunction = nameFunction;
    }
}
