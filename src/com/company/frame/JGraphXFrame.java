package com.company.frame;

import com.company.parser.Element;
import com.company.shape.Parallelogram;
import com.company.shape.ParallelogramPerimeter;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStyleRegistry;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ALEX on 30.04.2017.
 */
public class JGraphXFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1366;
    private static final int DEFAULT_HEIGHT = 1366;
    double leftX = DEFAULT_WIDTH / 2;
    double topY = 100;
    double width = 100;
    double height = 50;
    private HashMap<Integer, Element> elementHashMap;
    double dist = 25;
    mxGraph graph;
    Object parent;
    JPanel buttonPanel;

    public JGraphXFrame(HashMap<Integer, Element> elementHashMap) throws IOException {
        super("Perl Visualization");

        // create menu bar
        MenuBar menubar = new MenuBar();
        setMenuBar(menubar);

        // create file menu
        Menu fileMenu = new Menu("File");
        menubar.add(fileMenu);

        MenuItem saveMI = fileMenu.add(new MenuItem("Save As"));
        saveMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fDialog = new FileDialog(JGraphXFrame.this, "Save", FileDialog.SAVE);
                fDialog.setVisible(true);
                String path = fDialog.getDirectory() + fDialog.getFile() + ".png";
                File f = new File(path);
                try {
                    BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
                    ImageIO.write(image, "PNG", f);
                } catch (Exception exc) {

                }
            }
        });

        this.elementHashMap = elementHashMap;

        graph = new mxGraph();
        parent = graph.getDefaultParent();
//        List<Object> list = new LinkedList<Object>();

        mxGraphics2DCanvas.putShape("parallelogram", new Parallelogram());
        mxStyleRegistry.putValue("parallelogramPerimeter", new ParallelogramPerimeter());
        Map<String, Object> EdgeStyle = graph.getStylesheet().getDefaultEdgeStyle();

        EdgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
        EdgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        EdgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);


        mxCircleLayout layout = new mxCircleLayout(graph);
        layout.setDisableEdgeStyle(false);
        layout.execute(graph.getDefaultParent());

        buttonPanel = new JPanel();


        for (int i = 0; i < 100; i++) {
            stmtList.add(0);
        }

        graph.getModel().beginUpdate();
        try {
            //printHash(elementHashMap, graph, parent);
            //newPrintHash(elementHashMap, graph, parent);
            printTree();
//            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
//            ImageIO.write(image, "PNG", new File("D:\\graph.png"));

        } finally {
            graph.getModel().endUpdate();
        }
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.WEST);

        //setPreferredSize(new Dimension(450, 410));
        pack();

    }


    //стек просто анализируемых элементов, содержит ссылку на элемент, обход которого производится
    private Stack<Object> elementGraphStack = new Stack<>();
    //стек предыдущих элементов
    private Stack<Object> prevStack = new Stack<>();
    //стек признаков анализируемых элементов
    // 1 - завершено  0 - не завершено
    private Stack<Integer> stmtStack = new Stack<>();
    //стек типов анализируемых элементов
    //1 - IF 2 - While
    private Stack<Integer> typeStack = new Stack<>();
    //уровень вложения
    private int count = 0;

    //коллекция button и индексов
    private HashMap<Integer, JButton> buttonHashMap = new HashMap<>();

    //коллекция признаков
    //0 - по умолчанию раскрыты
    //1 - по умолчанию закрыты
    private LinkedList<Integer> stmtList = new LinkedList<Integer>();

    //идентификаторы самих IFиWhile
    private LinkedList<Integer> ifWhileList = new LinkedList<Integer>();
    private int countIfWhile = 0;


    public void printTree() {
        Iterator<Map.Entry<Integer, Element>> it = elementHashMap.entrySet().iterator();
        //рисуем терминатор начала
        Object v1 = graph.insertVertex(parent, null, "начало", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        //необходимо в стек последних элементов положить этот элемент
        prevStack.push(v1);
        //увеличиваем координату отрисовки элементов по оси У
        topY += height + dist;

        //обход основых узлов
        while (it.hasNext()) {
            //получить ссылку на элемент хеш-таблицы
            Map.Entry<Integer, Element> pair = it.next();
            //получаем элемент из таблицы
            Element element = pair.getValue();
            //првоеряем тип элемента
            switch (element.getType()) {
                case "var":
                    //отрисвока элемента без соединения
                    Object v = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, v, 1);
                    prevStack.pop();
                    prevStack.push(v);
                    break;
                case "function":
                    //проврека, будет ли отрисовка обычной функции, либо это будет параллелограмм
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    //отрисовка объекта
                    Object vFunc = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, vFunc, 1);
                    prevStack.pop();
                    prevStack.push(vFunc);
                    break;
                case "branch":
                    //создание кнопки c номер
                    countIfWhile++;
                    JButton b = new JButton(String.valueOf(countIfWhile));
                    buttonHashMap.put(countIfWhile, b);
                    b.addActionListener(new ButtonAction());
                    b.setBounds((int) leftX - 100, (int) topY, 20, 20);
                    add(b);

                    //отрисовка ромба
                    Object v3 = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, v3, 1);
                    prevStack.pop();
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.push(v3);
                    //фиксируем графический элемент
                    elementGraphStack.push(v3);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(1);
                    java.util.List<Element> ifLinkedList = element.getBody();
                    if (ifLinkedList != null) {
                        if (stmtList.get(countIfWhile) == 0) {
                            //внетренняя отрисовка
                            //v1 = listEntryObj();
                            printTreeEntry(ifLinkedList);
                            //извлекаем занесенный элемент
                            //меняем элемент по индексу вложения
                            stmtStack.set(count, 1);
                            //stmtStack.pop();
                            //ставим признак того, что для этого if обработка закончена
                            //stmtStack.push(1);
                        } else {
                            //отрисвока элемента без соединения
                            Object vIn = graph.insertVertex(parent, null, " ... ", leftX, topY, width, height);
                            //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                            topY += height + dist;
                            drawLine(graph, parent, vIn, 0);
                            prevStack.pop();
                            prevStack.push(vIn);
                            stmtStack.set(count, 1);
                        }

                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }

                    break;
                case "while":
                    countIfWhile++;
                    //создание кнопки c номер
                    JButton bWhile = new JButton(String.valueOf(countIfWhile));
                    buttonHashMap.put(countIfWhile, bWhile);
                    bWhile.addActionListener(new ButtonAction());
                    bWhile.setBounds((int) leftX - 100, (int) topY, 20, 20);
                    add(bWhile);

                    //отрисовка ромба
                    Object vWhile = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, vWhile, 1);
                    prevStack.pop();
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.push(vWhile);
                    //фиксируем графический элемент
                    elementGraphStack.push(vWhile);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(2);
                    java.util.List<Element> whileLinkedList = element.getBody();
                    if (whileLinkedList != null) {
                        if (stmtList.get(countIfWhile) == 0) {
                            //внетренняя отрисовка
                            //v1 = listEntryObj();
                            printTreeEntry(whileLinkedList);
                            //извлекаем занесенный элемент
                            //меняем элемент по индексу вложения
                            stmtStack.set(count, 1);
                            //stmtStack.pop();
                            //ставим признак того, что для этого if обработка закончена
                            //stmtStack.push(1);
                        } else {
                            //отрисвока элемента без соединения
                            Object vIn = graph.insertVertex(parent, null, " ... ", leftX, topY, width, height);
                            //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                            topY += height + dist;
                            drawLine(graph, parent, vIn, 0);
                            prevStack.pop();
                            prevStack.push(vIn);
                            stmtStack.set(count, 1);
                        }
                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }
                    countIfWhile++;
                    break;
            }
        }
        //отрисовка последнего элемента
        Object end = graph.insertVertex(parent, null, "конец", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        drawLine(graph, parent, end, 1);

    }

    public void printTreeEntry(java.util.List<Element> linkedList) {
        count++;
        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    //отрисвока элемента без соединения
                    Object v = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, v, i);
                    prevStack.pop();
                    prevStack.push(v);
                    break;
                case "function":
                    //проврека, будет ли отрисовка обычной функции, либо это будет параллелограмм
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    //отрисовка объекта
                    Object vFunc = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, vFunc, i);
                    prevStack.pop();
                    prevStack.push(vFunc);
                    break;
                case "branch":
                    countIfWhile++;
                    //создание кнопки c номер
                    JButton b = new JButton(String.valueOf(countIfWhile));
                    buttonHashMap.put(countIfWhile, b);
                    b.addActionListener(new ButtonAction());
                    b.setBounds((int) leftX - 100, (int) topY, 20, 20);
                    add(b);

                    //отрисовка ромба
                    Object v3 = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, v3, i);
                    //фиксируем графический элемент
                    elementGraphStack.push(v3);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(1);
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.pop();
                    prevStack.push(v3);
                    java.util.List<Element> ifLinkedList = element.getBody();
                    if (ifLinkedList != null) {
                        if (stmtList.get(countIfWhile) == 0) {
                            //внетренняя отрисовка
                            //v1 = listEntryObj();
                            printTreeEntry(ifLinkedList);
                            stmtStack.set(count, 1);
                            //извлекаем занесенный элемент
                            //stmtStack.pop();
                            //ставим признак того, что для этого if обработка закончена
                            //stmtStack.push(1);
                        } else {

                            //отрисвока элемента без соединения
                            Object vIn = graph.insertVertex(parent, null, " ... ", leftX, topY, width, height);
                            //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                            topY += height + dist;
                            drawLine(graph, parent, vIn, 0);
                            prevStack.pop();
                            prevStack.push(vIn);
                            stmtStack.set(count, 1);
                        }
                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }
                    break;
                case "while":
                    countIfWhile++;
                    //создание кнопки c номер
                    JButton bWhile = new JButton(String.valueOf(countIfWhile));
                    buttonHashMap.put(countIfWhile, bWhile);
                    bWhile.addActionListener(new ButtonAction());
                    bWhile.setBounds((int) leftX - 100, (int) topY, 20, 20);
                    add(bWhile);

                    //отрисовка ромба
                    Object vWhile = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, vWhile, 1);
                    prevStack.pop();
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.push(vWhile);
                    //фиксируем графический элемент
                    elementGraphStack.push(vWhile);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(2);
                    java.util.List<Element> whileLinkedList = element.getBody();
                    if (whileLinkedList != null) {
                        if (stmtList.get(countIfWhile) == 0) {

                            //внетренняя отрисовка
                            //v1 = listEntryObj();
                            printTreeEntry(whileLinkedList);
                            //извлекаем занесенный элемент
                            //меняем элемент по индексу вложения
                            stmtStack.set(count, 1);
                            //stmtStack.pop();
                            //ставим признак того, что для этого if обработка закончена
                            //stmtStack.push(1);
                        } else {
                            //отрисвока элемента без соединения
                            Object vIn = graph.insertVertex(parent, null, " ... ", leftX, topY, width, height);
                            //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                            topY += height + dist;
                            drawLine(graph, parent, vIn, 0);
                            prevStack.pop();
                            prevStack.push(vIn);
                            stmtStack.set(count, 1);
                        }
                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }
                    break;
            }
        }
        count--;
    }

    private class ButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {

            //((mxGraphModel) graph.getModel()).clear();

            topY = 100;
            elementGraphStack.clear();
            prevStack.clear();
            stmtStack.clear();
            typeStack.clear();
            countIfWhile = 0;
            count = 0;
            graph.getModel().beginUpdate();
            try {
                int index = Integer.parseInt(arg0.getActionCommand());
                if (stmtList.get(index) == 0) {
                    stmtList.set(index, 1);
                } else if (stmtList.get(index) == 1) {
                    stmtList.set(index, 0);
                }
                graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
                printTree();
            } finally {
                graph.getModel().endUpdate();
            }
        }

    }

    public void newPrintHash(HashMap<Integer, Element> map, mxGraph graph, Object parent) {
        Iterator<Map.Entry<Integer, Element>> it = map.entrySet().iterator();
        //рисуем терминатор начала
        Object v1 = graph.insertVertex(parent, null, "начало", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        //необходимо в стек последних элементов положить этот элемент
        prevStack.push(v1);
        //увеличиваем координату отрисовки элементов по оси У
        topY += height + dist;

        //обход основых узлов
        while (it.hasNext()) {
            //получить ссылку на элемент хеш-таблицы
            Map.Entry<Integer, Element> pair = it.next();
            //получаем элемент из таблицы
            Element element = pair.getValue();
            //првоеряем тип элемента
            switch (element.getType()) {
                case "var":
                    //отрисвока элемента без соединения
                    Object v = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, v, 1);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана для цикла while
//                                }
//                            } else {
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), v);
//                        //положить этот элемент в стек предыдущих элементов
//                    }
                    prevStack.pop();
                    prevStack.push(v);
                    break;
                case "function":
                    //проврека, будет ли отрисовка обычной функции, либо это будет параллелограмм
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    //отрисовка объекта
                    Object vFunc = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, vFunc, 1);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), vFunc);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            vFunc, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана
//                                }
//                            } else {
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), vFunc);
//                        //положить этот элемент в стек предыдущих элементов
//                    }
                    prevStack.pop();
                    prevStack.push(vFunc);
                    break;
                case "branch":
                    //отрисовка ромба
                    Object v3 = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, v3, 1);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v3);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            v3, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана для цикла while
//                                }
//                            } else {
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), v3);
//                    }
                    prevStack.pop();
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.push(v3);
                    //фиксируем графический элемент
                    elementGraphStack.push(v3);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(1);
                    java.util.List<Element> ifLinkedList = element.getBody();
                    if (ifLinkedList != null) {
                        //внетренняя отрисовка
                        //v1 = listEntryObj();
                        newListEntryObject(ifLinkedList, graph, parent);
                        //извлекаем занесенный элемент
                        //меняем элемент по индексу вложения
                        stmtStack.set(count, 1);
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }
                    break;
            }
        }
        //отрисовка последнего элемента
        Object end = graph.insertVertex(parent, null, "конец", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        if (!stmtStack.empty()) {
            //завершилась обработка - 1
            //не завершилась  - 0
            //ищем элементы, отрисовка тела которых уже завершилась
            while ((stmtStack.size() > 0)) {
                if ((stmtStack.peek() == 1)) {
                    //извлечь это значение из стека
                    stmtStack.pop();
                    //дальше необходимо проверить, анализ какого элемента проводился
                    //извлекаем элемент из typeStack
                    int type = typeStack.pop();

                    //проверяем, это if или while
                    //1 - if
                    //2 - while
                    if (type == 1) {
                        //рисуем от последнего элемента  из стека предыдущих элементов стрелку
                        graph.insertEdge(parent, null, null, prevStack.peek(), end);
                        //рисуем от последнего if стрелку к текущему элементу
                        graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
                                end, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                        "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                    } else if (type == 2) {
                        //отрисовка будет доделана для цикла while
                    }
                } else {
                    break;
                }
            }
        } else {
            //обычная отрисовка линии от предыдущего элемента
            graph.insertEdge(parent, null, null, prevStack.pop(), end);
            //положить этот элемент в стек предыдущих элементов
        }

        //соединение ветвей. Тот же самый алгоритм
    }

    public void newListEntryObject(java.util.List<Element> linkedList, mxGraph graph, Object parent) {
        count++;
        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    //отрисвока элемента без соединения
                    Object v = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, v, i);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана для цикла while
//                                }
//                            } else {
//                                //обычная отрисовка линии от предыдущего элемента
//                                if (i > 0) {
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v);
//                                } else {
//                                    graph.insertEdge(parent, null, "да", prevStack.peek(), v);
//                                }
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), v);
//                    }
                    prevStack.pop();
                    prevStack.push(v);
                    break;
                case "function":
                    //проврека, будет ли отрисовка обычной функции, либо это будет параллелограмм
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    //отрисовка объекта
                    Object vFunc = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                    //для начала проверяется стек типов на предмет того, что в нем могут находиться анализируемые элементы
                    topY += height + dist;
                    drawLine(graph, parent, vFunc, i);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), vFunc);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            vFunc, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана
//                                }
//                            } else {
//                                //обычная отрисовка линии от предыдущего элемента
//                                if (i > 0) {
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), vFunc);
//                                } else {
//                                    graph.insertEdge(parent, null, "да", prevStack.peek(), vFunc);
//                                }
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), vFunc);
//
//                    }
                    prevStack.pop();
                    prevStack.push(vFunc);
                    break;
                case "branch":
                    //отрисовка ромба
                    Object v3 = graph.insertVertex(parent, null, element.getCondition(),
                            leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    topY += height + dist;
                    drawLine(graph, parent, v3, i);
//                    if (!stmtStack.empty()) {
//                        //завершилась обработка - 1
//                        //не завершилась  - 0
//                        //ищем элементы, отрисовка тела которых уже завершилась
//                        while ((stmtStack.size() > 0)) {
//                            if (stmtStack.peek() == 1) {
//                                //извлечь это значение из стека
//                                stmtStack.pop();
//                                //дальше необходимо проверить, анализ какого элемента проводился
//                                //извлекаем элемент из typeStack
//                                int type = typeStack.pop();
//
//                                //проверяем, это if или while
//                                //1 - if
//                                //2 - while
//                                if (type == 1) {
//                                    //рисуем от последнего элемента  из стека предыдущих элементов стрелку
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v3);
//                                    //рисуем от последнего if стрелку к текущему элементу
//                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
//                                            v3, "exitX=1;exitY=0.5;exitPerimeter=1;" +
//                                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
//                                } else if (type == 2) {
//                                    //отрисовка будет доделана для цикла while
//                                }
//                            } else {
//                                //обычная отрисовка линии от предыдущего элемента
//                                if (i > 0) {
//                                    graph.insertEdge(parent, null, null, prevStack.peek(), v3);
//                                } else {
//                                    graph.insertEdge(parent, null, "да", prevStack.peek(), v3);
//                                }
//                                break;
//                            }
//                        }
//                    } else {
//                        //обычная отрисовка линии от предыдущего элемента
//                        graph.insertEdge(parent, null, null, prevStack.peek(), v3);
//                    }
                    //фиксируем графический элемент
                    elementGraphStack.push(v3);
                    //фиксируем признак конца обработка 0 - процесс отрисовки тела не завершен
                    stmtStack.push(0);
                    //тип обрабатываемого элемента - ветвление
                    typeStack.push(1);
                    //положить этот элемент в стек предыдущих элементов
                    prevStack.pop();
                    prevStack.push(v3);
                    java.util.List<Element> ifLinkedList = element.getBody();
                    if (ifLinkedList != null) {
                        //внетренняя отрисовка
                        //v1 = listEntryObj();
                        newListEntryObject(ifLinkedList, graph, parent);
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    } else {
                        stmtStack.set(count, 1);
                        //извлекаем занесенный элемент
                        //stmtStack.pop();
                        //ставим признак того, что для этого if обработка закончена
                        //stmtStack.push(1);
                    }
                    break;
            }
        }
        count--;
    }

    public void drawLine(mxGraph graph, Object parent, Object v, int i) {
        boolean whileFlag = false;
        if (!stmtStack.empty()) {
            //завершилась обработка - 1
            //не завершилась  - 0
            //ищем элементы, отрисовка тела которых уже завершилась

            while ((stmtStack.size() > 0)) {
                if (stmtStack.peek() == 1) {
                    //извлечь это значение из стека
                    stmtStack.pop();
                    //дальше необходимо проверить, анализ какого элемента проводился
                    //извлекаем элемент из typeStack
                    int type = typeStack.pop();

                    //проверяем, это if или while
                    //1 - if
                    //2 - while
                    if (type == 1) {
                        if (typeStack.size() > 0) {
                            //вверху while и он завершен
                            if ((typeStack.peek() == 2) & (stmtStack.peek() == 1)) {

                                //извлечь IF
                                Object ifObj = elementGraphStack.pop();
                                //если флаг установлен, то отрисовка только одной линии
                                if (whileFlag) {
                                    //из IF к while
                                    graph.insertEdge(parent, null, null, ifObj, elementGraphStack.peek(), "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1.0;exitY=0.5;exitPerimeter=1;"
                                            + "entryX=0;entryY=0.5;entryPerimeter=1;");
                                } else {
                                    //из IF к while
                                    graph.insertEdge(parent, null, null, ifObj, elementGraphStack.peek(), "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1.0;exitY=0.5;exitPerimeter=1;"
                                            + "entryX=0;entryY=0.5;entryPerimeter=1;");
                                    //из последнего объекта к While
                                    graph.insertEdge(parent, null, null, v, elementGraphStack.peek(), "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=0.5;exitY=1.0;exitPerimeter=1;"
                                            + "entryX=0;entryY=0.5;entryPerimeter=1;");

                                }
                            } else {
                                //рисуем от последнего элемента  из стека предыдущих элементов стрелку
                                graph.insertEdge(parent, null, null, prevStack.peek(), v);
                                //рисуем от последнего if стрелку к текущему элементу
                                graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
                                        v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                                "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                            }
                        } else {
                            if (whileFlag) {
                                //рисуем от последнего if стрелку к текущему элементу
                                graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
                                        v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                                "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                            } else {
                                //рисуем от последнего элемента  из стека предыдущих элементов стрелку
                                graph.insertEdge(parent, null, null, prevStack.peek(), v);
                                //рисуем от последнего if стрелку к текущему элементу
                                graph.insertEdge(parent, null, "нет", elementGraphStack.pop(),
                                        v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                                "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                            }
                        }
                    } else if (type == 2) {
                        //отрисовка будет доделана для цикла while
                        if (typeStack.size() > 0) {
                            //IF
                            if ((typeStack.peek() == 1) & (stmtStack.peek() == 1)) {
                                //извлечь while
                                if (whileFlag) {
                                    //линия от while к if
                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(), v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                            "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                                } else {

                                    graph.insertEdge(parent, null, null, prevStack.peek(), elementGraphStack.peek(), "exitX=0.5;exitY=1.0;exitPerimeter=1;" +
                                            "entryX=0;entryY=0.5;entryPerimeter=1;");
                                    graph.insertEdge(parent, null, "нет", elementGraphStack.peek(), v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                            "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                                    whileFlag = true;
                                    elementGraphStack.pop();
                                }
                            } else if ((typeStack.peek() == 2) & (stmtStack.peek() == 1)) {
                                if (whileFlag) {
                                    //1 линия к конечному элементу
                                    graph.insertEdge(parent, null, "нет", elementGraphStack.pop(), v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                            "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                                } else {
                                    Object whileObj = elementGraphStack.pop();
                                    graph.insertEdge(parent, null, null, prevStack.peek(), whileObj, "exitX=0.5;exitY=1.0;exitPerimeter=1;" +
                                            "entryX=0;entryY=0.5;entryPerimeter=1;");
                                    graph.insertEdge(parent, null, "нет", whileObj, elementGraphStack.peek(), "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                            "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                                    whileFlag = true;
                                }
                            } else {
                                graph.insertEdge(parent, null, null, prevStack.peek(), elementGraphStack.peek(), "exitX=0.5;exitY=1.0;exitPerimeter=1;" +
                                        "entryX=0;entryY=0.5;entryPerimeter=1;");
                                graph.insertEdge(parent, null, "нет", elementGraphStack.pop(), v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                        "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                                whileFlag = true;
                            }
                        } else {
                            //стандартная отрисовка while
                            graph.insertEdge(parent, null, null, prevStack.peek(), elementGraphStack.peek(), "exitX=0.5;exitY=1.0;exitPerimeter=1;" +
                                    "entryX=0;entryY=0.5;entryPerimeter=1;");
                            graph.insertEdge(parent, null, "нет", elementGraphStack.pop(), v, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                            whileFlag = true;
                        }
                    }
                } else {
                    //обычная отрисовка линии от предыдущего элемента
                    if (!whileFlag) {
                        if (i > 0) {
                            graph.insertEdge(parent, null, null, prevStack.peek(), v);
                        } else {
                            graph.insertEdge(parent, null, "да", prevStack.peek(), v);
                        }
                    }
                    break;
                }
            }
        } else {
            //обычная отрисовка линии от предыдущего элемента
            graph.insertEdge(parent, null, null, prevStack.peek(), v);
        }
    }

    public void printHash(HashMap<Integer, Element> map, mxGraph graph, Object parent) {
        Iterator<Map.Entry<Integer, Element>> it = map.entrySet().iterator();
        boolean flagIf = false;
        boolean flagWhile = false;
        Object v2 = null;
        Object temp;
        Object v1 = graph.insertVertex(parent, null, "начало", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        topY += height + dist;
        while (it.hasNext()) {
            Map.Entry<Integer, Element> pair = it.next();
            Element element = pair.getValue();
            switch (element.getType()) {
                case "var":
                    if (flagIf) {
                        //рисуется элемент
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                        //соединяем предыдущий элемент (ромб) и новый элемент
                        graph.insertEdge(parent, null, "нет", v2, v3, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        if (!flagWhile) {
                            graph.insertEdge(parent, null, null, v1, v3);
                        }
                        v1 = v3;
                        flagIf = false;
                    } else {
                        v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                        graph.insertEdge(parent, null, null, v1, v2);
                        v1 = v2;
                    }
                    topY += height + dist;
                    break;
                case "function":
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                        graph.insertEdge(parent, null, "нет", v2, v3, "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1;exitY=0.5;exitPerimeter=1;"
                                + "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        if (!flagWhile) {
                            graph.insertEdge(parent, null, null, v1, v3);
                        }
                        v1 = v3;
                        flagIf = false;
                    } else {
                        v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                        graph.insertEdge(parent, null, null, v1, v2);
                        v1 = v2;
                    }
                    topY += height + dist;
                    break;
                case "branch":
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(),
                                leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                        graph.insertEdge(parent, null, "нет", v2, v3,
                                "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1;exitY=0.5;exitPerimeter=1;"
                                        + "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        if (!flagWhile) {
                            graph.insertEdge(parent, null, null, v1, v3);
                        }
                        v1 = v3;
                        flagIf = false;
                    } else {
                        flagIf = true;
                        v2 = graph.insertVertex(parent, null, element.getCondition(), leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                        graph.insertEdge(parent, null, null, v1, v2);
                        v1 = v2;
                    }
                    topY += height + dist;
                    java.util.List<Element> linkedList = element.getBody();
                    if (linkedList != null) {
                        v1 = listEntryObj(linkedList, graph, parent, v1);
                    }
                    break;
                case "while":
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                        graph.insertEdge(parent, null, "нет", v2, v3,
                                "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1;exitY=0.5;exitPerimeter=1;"
                                        + "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        if (!flagWhile) {
                            graph.insertEdge(parent, null, null, v1, v3);
                        }
                        v1 = v3;
                        temp = v3;
                        flagIf = false;
                        flagWhile = false;
                    } else {
                        flagIf = true;
                        flagWhile = true;
                        v2 = graph.insertVertex(parent, null, element.getCondition(), leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                        graph.insertEdge(parent, null, null, v1, v2);
                        v1 = v2;
                        temp = v2;
                    }
                    topY += height + dist;
                    java.util.List<Element> whileLinkedList = element.getBody();
                    if (whileLinkedList != null) {
                        v1 = listEntryObj(whileLinkedList, graph, parent, v1);
                    }
                    graph.insertEdge(parent, null, null, v1, temp, "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=0.5;exitY=1;exitPerimeter=1;"
                            + "entryX=0;entryY=0.5;entryPerimeter=1;");
                    v1 = temp;
                    break;
            }
        }
        Object end = graph.insertVertex(parent, null, "конец", leftX, topY, width, height, "shape=ellipse;perimeter=ellipsePerimeter");
        if (flagIf) {
            graph.insertEdge(parent, null, null, v1, end, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                    "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
        } else {
            graph.insertEdge(parent, null, null, v1, end);
        }
    }

    public Object listEntryObj(java.util.List<Element> linkedList, mxGraph graph, Object parent,
                               Object v1) {
        Object v = v1;
        Object v2 = null;
        boolean flagIf = false;
        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                        graph.insertEdge(parent, null, "нет", v2, v3, "exitX=1;exitY=0.5;exitPerimeter=1;" +
                                "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        graph.insertEdge(parent, null, null, v, v3);
                        v = v3;
                        flagIf = false;
                    } else {
                        v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                        if (i > 0) {
                            graph.insertEdge(parent, null, null, v, v2);
                        } else {
                            graph.insertEdge(parent, null, "да", v, v2);
                        }
                        v = v2;
                    }
                    topY += height + dist;
                    break;
                case "function":
                    String elType;
                    if (element.getNameFunction().equals("print") || element.getNameFunction().equals("printf")) {
                        elType = "shape=parallelogram;perimeter=parallelogramPerimeter";
                    } else {
                        elType = "shape=rectangle;perimeter=rectanglePerimeter";
                    }
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                        graph.insertEdge(parent, null, "нет", v2, v3, "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1;exitY=0.5;exitPerimeter=1;"
                                + "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        graph.insertEdge(parent, null, null, v, v3);
                        v = v3;
                        flagIf = false;
                    } else {
                        v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, elType);
                        if (i > 0) {
                            graph.insertEdge(parent, null, null, v, v2);
                        } else {
                            graph.insertEdge(parent, null, "да", v, v2);
                        }
                        v = v2;
                    }
                    topY += height + dist;
                    break;
                case "branch":
                    if (flagIf) {
                        Object v3 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height, "shape=parallelogram;perimeter=parallelogramPerimeter");
                        graph.insertEdge(parent, null, "нет", v2, v3,
                                "edgeStyle=elbowEdgeStyle;elbow=horizontal;exitX=1;exitY=0.5;exitPerimeter=1;"
                                        + "entryX=0.5;entryY=-0.5;entryPerimeter=1;");
                        graph.insertEdge(parent, null, null, v, v3);
                        v = v3;
                        flagIf = false;
                    } else {
                        flagIf = true;
                        v2 = graph.insertVertex(parent, null, element.getCondition(), leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                        if (i > 0) {
                            graph.insertEdge(parent, null, null, v, v2);
                        } else {
                            graph.insertEdge(parent, null, "да", v, v2);
                        }
                        v = v2;
                    }
                    topY += height + dist;
                    java.util.List<Element> innerlinkedList = element.getBody();
                    if (innerlinkedList != null) {
                        v = listEntryObj(innerlinkedList, graph, parent, v);
                    }
                    break;
            }
        }
        return v;
    }

    public void listEntry(java.util.List<Element> linkedList, mxGraph graph, Object parent, double leftX, double topY,
                          double width, double height, Object v1) {
        int leftXStr;
        int leftYStr;

        Object v = v1;
        Object v2 = null;
        //int j = 0;

        for (int i = 0; i < linkedList.size(); i++) {
            Element element = linkedList.get(i);
            switch (element.getType()) {
                case "var":
                    v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //graph.insertEdge(parent, null, null, v, v2);
                    if (i > 0) {
                        graph.insertEdge(parent, null, null, v, v2);
                    } else {
                        graph.insertEdge(parent, null, "да", v, v2);
                    }
                    v = v2;


//                    Rectangle2D rectV = new Rectangle2D.Double(leftX, topY, width, height);
//                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
//                    leftXStr = (int) leftX + (int) width / 3;
//                    leftYStr = (int) topY + (int) height / 2;
//                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "function":
                    v2 = graph.insertVertex(parent, null, element.getCommand(), leftX, topY, width, height);
                    //graph.insertEdge(parent, null, null, v1, v2);
                    if (i > 0) {
                        graph.insertEdge(parent, null, null, v, v2);
                    } else {
                        graph.insertEdge(parent, null, "да", v, v2);
                    }
                    v = v2;

//
//                    j++;
//                    Rectangle2D rectF = new Rectangle2D.Double(leftX, topY, width, height);
//                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
//                    leftXStr = (int) leftX + (int) width / 3;
//                    leftYStr = (int) topY + (int) height / 2;
//                    g2.drawString(element.getCommand(), leftXStr, leftYStr);
                    topY += height + dist;
                    break;
                case "branch":
                    v2 = graph.insertVertex(parent, null, element.getCondition(), leftX, topY, width, height, "shape=rhombus;perimeter=rhombusPerimeter");
                    //graph.insertEdge(parent, null, "да", v1, v2);
                    if (i > 0) {
                        graph.insertEdge(parent, null, null, v, v2);
                    } else {
                        graph.insertEdge(parent, null, "да", v, v2);
                    }
                    v = v2;
//
//                    j++;
//                    Rectangle2D rectB = new Rectangle2D.Double(leftX, topY, width, height);
//                    g2.drawRect((int) leftX, (int) topY, (int) width, (int) height);
//                    leftXStr = (int) leftX + (int) width / 3;
//                    leftYStr = (int) topY + (int) height / 2;
//                    g2.drawString(element.getCondition(), leftXStr, leftYStr);
                    topY += height + dist;
                    java.util.List<Element> innerlinkedList = element.getBody();
                    if (innerlinkedList != null) {
                        listEntry(innerlinkedList, graph, parent, leftX, topY, width, height, v2);
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
