package com.company.frame;

import com.company.component.DrawComponent;
import com.company.parser.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

/**
 * Created by ALEX on 23.04.2017.
 */
public class DrawFrame extends JFrame {
    public DrawFrame(HashMap<Integer, Element> elementHashMap) {
        add(new DrawComponent(elementHashMap));
//        add(drawComponent);

        //In a container that uses a BorderLayout:
        DrawComponent drawComponent = new DrawComponent(elementHashMap);

        JScrollPane scrollPane = new JScrollPane(drawComponent);

        setPreferredSize(new Dimension(450, 410));
        add(scrollPane, BorderLayout.CENTER);
        pack();
    }
}
