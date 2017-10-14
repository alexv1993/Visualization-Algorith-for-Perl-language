package com.company.shape;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxBasicShape;
import com.mxgraph.view.mxCellState;


import java.awt.*;

/**
 * Created by ALEX on 01.05.2017.
 */

public class Parallelogram extends mxBasicShape {
    public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state) {
        mxCell cell = (mxCell) state.getCell();
        Polygon polygon = new Polygon();
        if (cell != null && cell.getGeometry() != null) {
            mxGeometry g = cell.getGeometry();
            int dx = (int) (cell.getGeometry().getHeight() / 4.0);
            polygon.addPoint((int) (g.getX() + dx), (int) g.getY());
            polygon.addPoint((int) (g.getX() + g.getWidth() + dx), (int) g.getY());
            polygon.addPoint((int) (g.getX() + g.getWidth() - dx), (int) (g.getY() + g.getHeight()));
            polygon.addPoint((int) ((int) g.getX() - dx), (int) (g.getY() + g.getHeight()));
        }
        return polygon;

    }
}
