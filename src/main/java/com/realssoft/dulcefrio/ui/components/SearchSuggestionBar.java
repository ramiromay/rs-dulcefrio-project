package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class SearchSuggestionBar extends ShadowPanel
{

    private JPanel mainPanel;
    private JLabel searchButton;
    private JTextField  searchField;
    private int coincidences;

    @Setter
    private int arc = UIScale.scale(55);
    private final Color backgroundBar = UIManager.getColor("SearchBar");
    private final Color backgroundShadow = UIManager.getColor("background.shade");
    private final Color background = UIManager.getColor("@background");

    public SearchSuggestionBar()
    {
        super(1.0f);
        configureProperties();
        configureComponents();

        configureLayout();
    }

    private void configureProperties()
    {
        setBackground(background);
        setLayout(new MigLayout(
                "fillx", "3[fill]3", "3[fill]3")
        );
        setArc(UIScale.scale(50));
        revalidate();
        repaint();
    }

    private void configureComponents()
    {
        mainPanel = new RoundPanel();
        mainPanel.setBorder(new EmptyBorder(4, 7, 4, 7));
        mainPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;"
        );
        add(mainPanel);

        searchButton = new JLabel();
        searchButton.setBorder(new EmptyBorder(4,2,4,2));
        searchButton.setHorizontalAlignment(JLabel.CENTER);
        searchButton.setVerticalAlignment(JLabel.CENTER);
        searchButton.setOpaque(false);
        searchButton.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_SEARCH));
        mainPanel.add(searchButton);

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;" +
                "foreground:$blackColor-2;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:999;" +
                "font:15;"
        );
        mainPanel.add(searchField);
    }



    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(searchButton,  GroupLayout.DEFAULT_SIZE,
                        38, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        22, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(searchButton, GroupLayout.DEFAULT_SIZE,
                        38, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        20, Short.MAX_VALUE)
        );
    }

    public void setSearch()
    {
    	searchField.setText("");
    }

    public void setHoverText(String text)
    {
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text);
    }

    public void setMouseClicked(MouseAdapter actionListener)
    {
        searchField.addMouseListener(actionListener);
    }

    public void setKeyListener(KeyAdapter keyAdapter)
    {
        searchField.addKeyListener(keyAdapter);
    }


    public String getSearch()
    {
        return searchField.getText().trim().toLowerCase();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g)
    {
        if (searchField.isFocusOwner())
        {
            mainPanel.setBackground(Color.white);
            searchField.setBackground(Color.white);
            searchButton.setBackground(Color.white);
            setBackground(backgroundShadow);
            setTransparency(0.05f);
        }
        else {
            mainPanel.setBackground(backgroundBar);
            searchField.setBackground(backgroundBar);
            searchButton.setBackground(backgroundBar);
            setBackground(background);
            setTransparency(1.0f);
        }
        super.paint(g);
    }

}
