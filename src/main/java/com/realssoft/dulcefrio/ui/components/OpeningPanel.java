package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.persistence.dao.impl.SaleDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ShoppingCartDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.TicketDaoImpl;
import com.realssoft.dulcefrio.ui.dialog.Action;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class OpeningPanel extends RoundPanel
{

    private JPanel mainPanel;
    private JScrollPane scrollPane;

    private JLabel lbTitle;
    private JLabel lbPay;

    private JSpinner txtPay;
    private JButton btnPay;
    private JButton btnClosed;

    @Setter
    private Action action;
    @Setter
    private List<ShoppingCartDTO> shoppingCart;
    private TicketDaoImpl ticketDao = TicketDaoImpl.getInstance(FrameInstance.getInstance());
    private SaleDaoImpl  saleDao = SaleDaoImpl.getInstance(FrameInstance.getInstance());
    private ShoppingCartDaoImpl shoppingCartDao = ShoppingCartDaoImpl.getInstance(FrameInstance.getInstance());

    double total = 0;
    double pay = 0;
    double change = 0;

    public OpeningPanel()
    {
        super();
        configureProperties();
        configureComponents();
        configureLayout();
        configureListeners();
    }

    private void configureProperties()
    {
        setLayout(new MigLayout("fill", "[fill]", "[fill]"));
    }

    private void configureComponents()
    {
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(2,4,2,4));
        mainPanel.setBackground(Color.white);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(mainPanel);
        add(scrollPane, "align center center");


        lbTitle = new JLabel("Apertura y Corte de Caja");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +5;");

        lbPay = new JLabel("PAGO CON");
        lbPay.setHorizontalAlignment(JLabel.RIGHT);
        mainPanel.add(lbPay);


        txtPay = ComponentsUtils.createSpinner();
        ((JSpinner.DefaultEditor)txtPay.getEditor()).getTextField().addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || c == '.' || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        mainPanel.add(txtPay);

        btnPay = new JButton("Abrir Caja");
        btnPay.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:$Component.accentColor;" +
                "foreground:#fff;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        mainPanel.add(btnPay);

        btnClosed = new JButton("Cerrar Caja");
        btnClosed.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:$Component.accentColor;" +
                "foreground:#fff;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        mainPanel.add(btnClosed);
    }


    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbPay, GroupLayout.PREFERRED_SIZE,
                                110, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(txtPay, GroupLayout.PREFERRED_SIZE,
                                22, Short.MAX_VALUE
                                )
                )
                .addComponent(btnPay, GroupLayout.PREFERRED_SIZE,
                        22, Short.MAX_VALUE)
                .addComponent(btnClosed, GroupLayout.PREFERRED_SIZE,
                        22, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbPay, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPay, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(5, 5, 10)
        );

    }

    private void configureListeners()
    {
       // btnPay.addActionListener(e -> generatePay());
    }



}
