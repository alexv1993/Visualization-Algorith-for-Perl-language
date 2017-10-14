package com.company;


import com.company.frame.DrawFrame;

import com.company.frame.JGraphXFrame;
import com.company.parser.Element;
import com.company.parser.RecursionPerlParser;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        // write your code here

        String filename = "C:\\Users\\ALEX\\Desktop\\Perl.txt";
        RecursionPerlParser parser = new RecursionPerlParser(filename);
        parser.parse(-1, null);

        //System.exit(1);

        HashMap<Integer, Element> hashMap = parser.getAbstractMap();


        printHash(hashMap);
        //теперь обходим HashMap

        EventQueue.invokeLater(() ->
        {
            //SimpleFrame frame = new SimpleFrame();
//            JFrame frame = new DrawFrame(hashMap);
//            frame.setTitle("DrawTest");
            //frame.getContentPane().setBackground(Color.white);
            try {
                JFrame frame = new JGraphXFrame(hashMap);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {

            }

        });
    }

    public static void printHash(HashMap<Integer, Element> map) {
        Iterator<Map.Entry<Integer, Element>> it = map.entrySet().iterator();

        LinkedList<Element> sdf = new LinkedList<>();


        while (it.hasNext()) {
            int count = 0;
            Map.Entry<Integer, Element> pair = it.next();
            Element element = pair.getValue();
            switch (element.getType()) {
                case "var":
                    System.out.println(element.getCommand());
                    break;
                case "function":
                    System.out.println(element.getCommand());
                    break;
                case "branch":
                    System.out.println(element.getCondition());
                    List<Element> linkedList = element.getBody();
                    if (linkedList != null) {
                        count++;
                        listEntry(linkedList, count);
                    }
                    break;
                case "while":
                    System.out.println(element.getCondition());
                    List<Element> whileElementList = element.getBody();
                    if (whileElementList != null) {
                        count++;
                        listEntry(whileElementList, count);
                    }
                    break;
            }
        }
    }

    public static void listEntry(List<Element> linkedList, int count) {
        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    for (int j = 0; j < count; j++)
                        System.out.print(" ");
                    System.out.println(element.getCommand());
                    break;
                case "function":
                    for (int j = 0; j < count; j++)
                        System.out.print(" ");
                    System.out.println(element.getCommand());
                    break;
                case "branch":
                    for (int j = 0; j < count; j++)
                        System.out.print(" ");
                    System.out.println(element.getCondition());
                    List<Element> innerLinkedList = element.getBody();
                    if (innerLinkedList != null) {
                        count++;
                        listEntry(innerLinkedList, count);
                    }
                    break;
                case "while":
                    for (int j = 0; j < count; j++)
                        System.out.print(" ");
                    System.out.println(element.getCondition());
                    List<Element> innerWhileLinkedList = element.getBody();
                    if (innerWhileLinkedList != null) {
                        count++;
                        listEntry(innerWhileLinkedList, count);
                    }
                    break;

            }
        }
    }
}
