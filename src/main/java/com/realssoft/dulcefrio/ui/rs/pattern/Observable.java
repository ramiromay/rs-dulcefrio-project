package com.realssoft.dulcefrio.ui.rs.pattern;

public interface Observable
{

    void addObserver(Observer observer);
    void notifyEvent(Object object);

}
