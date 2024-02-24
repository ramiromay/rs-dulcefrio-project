package com.realssoft.dulcefrio.ui.components;

import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Getter;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SuggestionItem extends JPanel
{

    private JLabel icon;
    @Getter
    private JLabel suggestion;
    private final Color background = UIManager.getColor("@background");
    private final String item;
    private MouseClickedEvent mouseClickedEvent;

    public SuggestionItem(String item)
    {
        this.item = item;
        configureProperties();
        configureComponents();
        configureLayout();
    }

    private void configureProperties()
    {
        setBackground(background);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                setBackground(new Color(215, 216, 216));
            }

            @Override
            public void mouseExited(MouseEvent me) {
                setBackground(background);
            }
        });
    }

    private void configureComponents()
    {
        icon = new JLabel();
        icon.setHorizontalAlignment(JLabel.CENTER);
        icon.setVerticalAlignment(JLabel.CENTER);
        icon.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_SEARCH, 20, 20));

        suggestion = new JLabel(item);
        suggestion.setHorizontalAlignment(JLabel.LEADING);
        suggestion.setVerticalAlignment(JLabel.CENTER);
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(icon, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(5)
                        .addComponent(suggestion, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(icon, GroupLayout.DEFAULT_SIZE,
                                100, GroupLayout.PREFERRED_SIZE)
                        .addComponent(suggestion, GroupLayout.DEFAULT_SIZE,
                                100, GroupLayout.PREFERRED_SIZE)
                )
        );

    }

    public void setMouseListener(MouseClickedEvent mouseAdapter)
    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseAdapter.onClick(item);
            }
        });
    }


}
