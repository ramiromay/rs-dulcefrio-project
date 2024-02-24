package com.realssoft.dulcefrio.ui.page;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.ui.components.Login;
import com.realssoft.dulcefrio.ui.components.PresentationPanel;
import com.realssoft.dulcefrio.ui.components.RoundPanel;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

public class SplashScreen extends JFrame
{

    public SplashScreen ()
    {
        setUndecorated(true);
        this.setIconImage(((ImageIcon) SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA)).getImage());
        setTitle("DulceFrío");
        setSize(new Dimension(750, 500));

        JPanel  panel = new JPanel();
        panel.setLayout(new MigLayout(
                "fill",
                "0[100%,fill]0",
                "0[100%,fill]0"));

        PresentationPanel  presentationPanel = new PresentationPanel();
        panel.add(presentationPanel);

        this.setContentPane(panel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

}
