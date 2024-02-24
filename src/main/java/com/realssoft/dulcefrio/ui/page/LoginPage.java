package com.realssoft.dulcefrio.ui.page;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.ui.components.Login;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

public class LoginPage extends JLayeredPane
{

    private static final LoginPage  INSTANCE = new LoginPage();
    private PageManager pageManager;
    private Timer timer;
    private int index = 1;

    public LoginPage()
    {
        configureProperties();
        configureComponents();
    }

    public static LoginPage getInstance()
    {
        return INSTANCE;
    }

    private void configureProperties()
    {
        UIManager.put("TextField.placeholderForeground", UIManager.getColor("holder"));
        setLayout(new MigLayout("fill, inset 0"));
    }

    private void configureComponents()
    {
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

        Login login = new Login();
        login.putClientProperty(
                FlatClientProperties.STYLE,
                "background:$background.card"
        );
        shadow.add(login, "cell 1 0");

        timer = new Timer(3000, ae -> startTimer());
        pageManager = PageManager.getInstance();
        startAnimation();
    }

    public void startAnimation()
    {
        timer.start();
    }

    private void startTimer()
    {
        if (!pageManager.isAncestor(this))
        {
            timer.stop();
            return;
        }
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
