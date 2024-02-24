package com.realssoft.dulcefrio.ui.components.calendar.utils;

import java.awt.event.MouseEvent;

public interface CalendarCellListener {

    public void cellSelected(MouseEvent evet, int index);

    public void scrollChanged();
}
