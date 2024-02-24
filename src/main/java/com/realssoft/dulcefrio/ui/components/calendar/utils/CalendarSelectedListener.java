package com.realssoft.dulcefrio.ui.components.calendar.utils;

import com.realssoft.dulcefrio.ui.components.calendar.model.ModelDate;

import java.awt.event.MouseEvent;


public interface CalendarSelectedListener {

    public void selected(MouseEvent evt, ModelDate date);
}
