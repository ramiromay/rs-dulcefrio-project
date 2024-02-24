package com.realssoft.dulcefrio;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.api.config.PropertiesConfiguration;
import com.realssoft.dulcefrio.api.persistence.dao.impl.AccountDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.EmployeeDaoImpl;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.page.LoginPage;
import com.realssoft.dulcefrio.ui.page.MenuPage;
import com.realssoft.dulcefrio.ui.page.PageManager;
import com.realssoft.dulcefrio.ui.page.SplashScreen;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Menu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.UUID;

public class Main extends JFrame
{

    public static boolean ADMIN = false;
    public static UUID ID_USER = null;

    public Main ()
    {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PropertiesConfiguration.getInstance().close();
            }
        });
        this.setIconImage(((ImageIcon) SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA)).getImage());;
        setTitle("DulceFrío");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(UIScale.scale(new Dimension(1000, 650)));
        this.setLocationRelativeTo(null);

        HibernateUtils.getEntityManager();
        FrameInstance.setInstance(this);
        Notifications.getInstance().setJFrame(this);
        PageManager pageManager = PageManager.getInstance();
        PropertiesConfiguration config = PropertiesConfiguration.getInstance();
        config.load();

        if (config.getToken().equals(""))
        {
            pageManager.showActivity(LoginPage.getInstance());
            this.setContentPane(pageManager);
        }
        else {
           try {
               Main.ID_USER  = UUID.fromString(config.getUserId());
               String token = config.getToken();
               if (AccountDaoImpl.getInstance().isTokenValid(ID_USER, token))
               {
                   Main.ADMIN = AccountDaoImpl.getInstance().isAdmin(ID_USER);
                   pageManager.showActivity(MenuPage.getInstance());
                   this.setContentPane(pageManager);
                   return;
               }
               pageManager.showActivity(LoginPage.getInstance());
               this.setContentPane(pageManager);
           }
           catch (Exception e)
           {
               pageManager.showActivity(LoginPage.getInstance());
               this.setContentPane(pageManager);
           }
        }
    }

    public static void main(String[] args)
    {
        FlatInterFont.install();
        FlatLaf.registerCustomDefaultsSource("themes" );
        FlatMacLightLaf.setup();
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 13));
        EventQueue.invokeLater(() -> {
            SplashScreen splashScreen = new SplashScreen();
            Thread processSplash = new Thread(() -> {
                splashScreen.setVisible(true);
            });
            Thread processMain = new Thread(() -> {
                Main main = new Main();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                splashScreen.dispose();
                main.setVisible(true);
            });
            processSplash.start();
            processMain.start();

        });
    }

}