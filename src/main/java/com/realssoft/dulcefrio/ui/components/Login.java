package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.config.PropertiesConfiguration;
import com.realssoft.dulcefrio.api.model.dto.AccountDTO;
import com.realssoft.dulcefrio.api.model.dto.AccountResponse;
import com.realssoft.dulcefrio.api.persistence.dao.impl.AccountDaoImpl;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.dialog.RecoverPassword;
import com.realssoft.dulcefrio.ui.dialog.RegisterEmployeeDialog;
import com.realssoft.dulcefrio.ui.page.MenuPage;
import com.realssoft.dulcefrio.ui.page.PageManager;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class Login extends JPanel
{

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chRememberMe;
    private JButton cmdForgotPassword;
    private JButton cmdLogin;
    private AccountDaoImpl  accountDao = AccountDaoImpl.getInstance();

    public Login()
    {
        configureProperties();
        configureComponents();
        configureEvents();
    }

    private void configureProperties()
    {
        setOpaque(false);
        setLayout(new MigLayout("wrap,fillx,insets 60 45 30 45, align center 40%", "[fill]"));
    }

    private @NotNull Component createForgotPasswordLabel()
    {
        JPanel panelForgotPassword = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelForgotPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");

        cmdForgotPassword = new JButton("<html><a href=\"#\">¿Olvidaste tu contraseña?</a></html>");
        cmdForgotPassword.addActionListener(e -> {
            RecoverPassword recoverPassword = new RecoverPassword();
            recoverPassword.setVisible(true);
        });
        cmdForgotPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3;" +
                "foreground:$Component.accentColor;");
        cmdForgotPassword.setContentAreaFilled(false);
        cmdForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelForgotPassword.add(cmdForgotPassword);
        return panelForgotPassword;
    }

    private void configureComponents()
    {
        JLabel profileIcon = new JLabel();
        profileIcon.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA, 105, 105));
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);
        profileIcon.setVerticalAlignment(SwingConstants.CENTER);
        add(profileIcon);

        JLabel title = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        title.putClientProperty(
                FlatClientProperties.STYLE,
                "font:bold +10"
        );
        add(title, "w 280!, gapy 20");

        JLabel lblUsername = new JLabel("Usuario");
        add(lblUsername, "gapy 20");

        txtUsername = new JTextField();
        txtUsername.putClientProperty(
                FlatClientProperties.STYLE,
                "margin:5,10,5,10;" +
                        "focusWidth:1;" +
                        "innerFocusWidth:0"
        );
        txtUsername.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Ingrese su usuario"
        );
        add(txtUsername);

        JLabel lblPassword = new JLabel("Contraseña");
        add(lblPassword ,"gapy 10");

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(
                FlatClientProperties.STYLE,
                "margin:5,10,5,10;" +
                "focusWidth:1;" +
                "innerFocusWidth:0;" +
                "showRevealButton:true"
        );
        txtPassword.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Ingrese su contraseña"
        );
        add(txtPassword);

        chRememberMe = new JCheckBox("Recuérdame");
        add(chRememberMe, "gapy 5");
        add(createForgotPasswordLabel(), "gapy 10");

        cmdLogin = new JButton("Acceso");
        if (accountDao.getCountUsers() == 0)
        {
            cmdLogin.setText("Registrate");
        }
        cmdLogin.putClientProperty(
                FlatClientProperties.STYLE,
                "background:$Component.accentColor;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "foreground:@foreground.white;"
        );
        add(cmdLogin, "gapy 20");
        add(new JLabel(), "gapy 50");
    }

    private void configureEvents()
    {
        cmdLogin.addActionListener(e -> {

            if (accountDao.getCountUsers() == 0)
            {
                RegisterEmployeeDialog dialog = new RegisterEmployeeDialog();
                dialog.setAction(() -> cmdLogin.setText("Acceso"));
                dialog.setVisible(true);
            }

            if (txtUsername.getText().isEmpty() || String.valueOf(txtPassword.getPassword()).isEmpty())
            {
                JOptionPane.showMessageDialog(
                        FrameInstance.getInstance(),
                        "Por favor, complete los campos",
                        StringRS.TITLE_DIALOG_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean  rememberMe = chRememberMe.isSelected();
            String username = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            AccountDTO account = new AccountDTO(username, password);
            AccountResponse response = accountDao.login(rememberMe,account);
            if (response == null)
            {
                JOptionPane.showMessageDialog(
                        FrameInstance.getInstance(),
                        "Usuario o contraseña incorrectos",
                        StringRS.TITLE_DIALOG_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            if (rememberMe)
            {
                PropertiesConfiguration.getInstance().setToken(response.token());
            }
            PropertiesConfiguration.getInstance().setUserId(response.userId().toString());
            Main.ID_USER = response.userId();
            Main.ADMIN = response.isAdmin();

            txtPassword.setText("");
            txtUsername.setText("");
            PageManager.getInstance().showActivity(new MenuPage());
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = UIScale.scale(30);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }

}
