package com.realssoft.dulcefrio.ui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.api.persistence.dao.impl.AccountDaoImpl;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;

public class RecoverPassword extends JDialog
{

    private final AccountDaoImpl accountDaoImpl = AccountDaoImpl.getInstance();
    private final JTextField textField;
    private final Window w = this;

    public RecoverPassword() {
        super(FrameInstance.getInstance(), true);
        setResizable(false);
        setSize(new Dimension(450, 600));
        setLocationRelativeTo(FrameInstance.getInstance());

        JPanel panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(new MigLayout("wrap,fillx,insets 10 45 30 45", "[fill]"));

        JLabel icon = new JLabel();
        icon.setHorizontalAlignment(SwingConstants.RIGHT);
        icon.setVerticalAlignment(SwingConstants.CENTER);
        icon.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA, 25,25));

        JLabel text  = new JLabel();
        text.setBorder(new EmptyBorder(0,5,0,0));
        text.setText("DulceFrío");

        JPanel roundPanel = new JPanel();
        roundPanel.setLayout(new MigLayout(
                "fillx", "5[fill]5", "5[fill]5")
        );
        roundPanel.setOpaque(false);
        roundPanel.add(icon);
        roundPanel.add(text);
        panel.add(roundPanel,"align center center");

        JLabel  lblNewLabel = new JLabel();
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setVerticalAlignment(SwingConstants.CENTER);
        lblNewLabel.setIcon(SVGUtils.getSVGIcon("svg/reset-password.svg", 250,250));
        panel.add(lblNewLabel, "gapy 10");

        JLabel lblRecuperarCuenta = new JLabel("Recuperación de la cuenta");
        lblRecuperarCuenta.putClientProperty(
                FlatClientProperties.STYLE,
                "font:bold +7;"
        );
        lblRecuperarCuenta.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblRecuperarCuenta, "gapy 10");

        JLabel lblIngresaTCorreo = new JLabel("Ingresa t\u00FA correo electr\u00F3nico.");
        panel.add(lblIngresaTCorreo, "gapy 10");

        textField = new JTextField();
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Correo electrónico");
        panel.add(textField, "gapy 10");

        JButton btnNewButton = new JButton("Recuperar");
        btnNewButton.putClientProperty(
                FlatClientProperties.STYLE,
                "background:$Component.accentColor;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "foreground:@foreground.white;"
        );
        btnNewButton.addActionListener(arg0 -> {
            ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.setIconAndTitle("Cuenta", "3");
            Thread thread = new Thread(() -> {
                progressDialog.setUndefined("Recuperando la cuenta");
                progressDialog.setVisible(true);
            });
            Thread process = new Thread(() -> {
                accountDaoImpl.modifyPassword(textField.getText(), w, progressDialog);
            });
            thread.start();
            process.start();

        });
        panel.add(btnNewButton, "gapy 12");

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.putClientProperty(
                FlatClientProperties.STYLE,
                "background:$Component.accentColor;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "foreground:@foreground.white;"
        );
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar, "gapy 5");


        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

}
