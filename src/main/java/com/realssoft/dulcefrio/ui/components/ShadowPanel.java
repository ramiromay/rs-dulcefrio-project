package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

@Setter
public class ShadowPanel extends JPanel
{

    private int arc = UIScale.scale(30);
    private float transparency = 0.2f;

    public ShadowPanel()
    {
        configureProperties();
    }

    public ShadowPanel(float  transparency)
    {
        this.transparency = transparency;
        configureProperties();
    }

    private void configureProperties()
    {
        setOpaque(false);
        setLayout(new MigLayout(
                "fillx", "5[fill]5", "5[fill]5")
        );
        putClientProperty(
            FlatClientProperties.STYLE,
            "background:$background.shade"
        );
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(transparency));
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }

}
