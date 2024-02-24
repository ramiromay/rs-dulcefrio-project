package com.realssoft.dulcefrio.ui.components.calendar.utils;

import com.realssoft.dulcefrio.ui.components.calendar.model.ModelDate;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface PanelDateListener {

    public boolean cellPaint(Graphics2D g2, Rectangle2D rectangle, ModelDate e);
}
