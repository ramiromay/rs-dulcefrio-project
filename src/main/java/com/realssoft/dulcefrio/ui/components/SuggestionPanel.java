package com.realssoft.dulcefrio.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class SuggestionPanel extends JPanel
{

    public SuggestionPanel()
    {
        configureLayout();
        configureProperties();
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 438, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
    }

    private void configureProperties()
    {
        setBackground(UIManager.getColor("@background"));
        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0"));
    }

}
