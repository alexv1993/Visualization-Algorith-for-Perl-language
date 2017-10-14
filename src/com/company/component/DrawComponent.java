package com.company.component;

import com.company.parser.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;


/**
 * Created by ALEX on 23.04.2017.
 */
public class DrawComponent extends JPanel {
    private static final int DEFAULT_WIDTH = 1366;
    private static final int DEFAULT_HEIGHT = 1366;
    private HashMap<Integer, Element> elementHashMap;
    double dist = 25;


    public DrawComponent(HashMap<Integer, Element> elementHashMap) {
        this.elementHashMap = elementHashMap;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

//        Rectangle2D rect = new Rectangle2D.Double(leftX, topY, width, height);
//        g2.draw(rect);

        printHash(elementHashMap, g2);

    }


    public void printHash(HashMap<Integer, Element> map, Graphics2D g2) {
        Iterator<Map.Entry<Integer, Element>> it = map.entrySet().iterator();


        double leftX = DEFAULT_WIDTH/2;
        double topY = 100;
        double width = 100;
        double height = 50;
        int leftXStr;
        int leftYStr;


        while (it.hasNext()) {
            Map.Entry<Integer, Element> pair = it.next();
            Element element = pair.getValue();
            switch (element.getType()) {
                case "var":
                    Rectangle2D rectV = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "function":
                    Rectangle2D rectF = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "branch":
                    Rectangle2D rectB = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCondition(), leftXStr, leftYStr);
                    topY += height + dist;
                    java.util.List<Element> linkedList = element.getBody();
                    if (linkedList != null) {
                        listEntry(linkedList, g2, leftX, topY, width, height);
                    }
                    break;
            }
        }
    }

    public void listEntry(java.util.List<Element> linkedList, Graphics2D g2, double leftX, double topY,
                          double width, double height) {
        int leftXStr;
        int leftYStr;

        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    Rectangle2D rectV = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "function":
                    Rectangle2D rectF = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "branch":
                    Rectangle2D rectB = new Rectangle2D.Double(leftX, topY, width, height);
                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
                    leftXStr = (int) leftX + (int) width/3;
                    leftYStr = (int) topY + (int) height/2;
                    g2.drawString(element.getCondition(), leftXStr, leftYStr);
                    topY += height + dist;
                    java.util.List<Element> innerlinkedList = element.getBody();
                    if (innerlinkedList != null) {
                        listEntry(innerlinkedList, g2, leftX, topY, width, height);
                    }
                    break;
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {

        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
