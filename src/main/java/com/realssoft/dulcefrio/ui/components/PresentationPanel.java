package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.ui.page.PageManager;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

public class PresentationPanel extends JLayeredPane
{

    private Timer timer;
    private int index = 1;

    public PresentationPanel()
    {
        setLayout(new MigLayout("fill, inset 0"));

        ShadowPanel shadow = new ShadowPanel();
        shadow.setArc(0);
        shadow.putClientProperty(
                FlatClientProperties.STYLE,
                "background:$background.shade.black;"
        );
        shadow.setLayout(new MigLayout(
                "fill",
                "0[100%, fill]0[100]0[100%, fill]0",
                "0[center]0"));
        add(shadow, "pos 0 0 100% 100%");

        JLabel icon = new JLabel();
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setVerticalAlignment(SwingConstants.CENTER);
        icon.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA, 200,200));

        JLabel text = new JLabel();
        text.setForeground(Color.white);
        text.setBorder(new EmptyBorder(0,25,0,0));
        text.setText("100% NATURAL");
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +30");

        JPanel roundPanel = new JPanel();
        roundPanel.setLayout(new MigLayout(
                "fillx", "5[fill]5", "5[fill]5")
        );
        roundPanel.setOpaque(false);
        roundPanel.add(icon, "wrap");
        roundPanel.add(text);
        shadow.add(roundPanel, "cell 1 0");

        timer = new Timer(1400, ae -> startTimer());
        startAnimation();
    }

    public void startAnimation()
    {
        timer.start();
    }

    private void startTimer()
    {
        repaint();
        revalidate();
        if (index == 5)
        {
            index = 1;
            return;
        }
        index++;
    }

    @Override
    public void paint(@NotNull Graphics g)
    {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        Image backgroundImage = new ImageIcon(Objects.requireNonNull(Main.class
                .getResource(PathRS.JPG_BACKGROUND_LOGIN.formatted(index)))).getImage();
        double scale = Math.max((double) panelWidth / backgroundImage.getWidth(this),
                (double) panelHeight / backgroundImage.getHeight(this));
        int newImageWidth = (int) (backgroundImage.getWidth(this) * scale);
        int newImageHeight = (int) (backgroundImage.getHeight(this) * scale);
        int x = (panelWidth - newImageWidth) / 2;
        int y = (panelHeight - newImageHeight) / 2;
        g.drawImage(backgroundImage, x, y, newImageWidth, newImageHeight, this);
        super.paint(g);
    }

}
