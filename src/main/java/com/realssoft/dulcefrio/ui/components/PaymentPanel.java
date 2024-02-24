package com.realssoft.dulcefrio.ui.components;

import com.aspose.barcode.EncodeTypes;
import com.aspose.barcode.generation.BarcodeGenerator;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.SaleDTO;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.model.dto.UnitSalesDTO;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.SaleDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ShoppingCartDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.TicketDaoImpl;
import com.realssoft.dulcefrio.api.persistence.entity.CashRegisterOpening;
import com.realssoft.dulcefrio.api.persistence.entity.Ticket;
import com.realssoft.dulcefrio.api.utils.DateUtils;
import com.realssoft.dulcefrio.api.utils.MoneyUtils;
import com.realssoft.dulcefrio.ui.dialog.Action;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.dialog.ProgressDialog;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PaymentPanel extends RoundPanel
{

    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JLabel lbTotalPrice;
    private JLabel lbPay;
    private JLabel lbChange;
    private JSpinner txtTotalPrice;
    private JSpinner txtPay;
    private JFormattedTextField textField;
    private JSpinner txtChange;
    private JComboBox<String> cbMoney;
    private JLabel lbMoney;
    private JLabel lbMoney2;
    private JButton btnPay;

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

    public PaymentPanel()
    {
        super();
        configureProperties();
        configureComponents();
        configureLayout();
        configureListeners();
    }

    public void setEnablePay()
    {
        CashRegisterImpl cashRegister = CashRegisterImpl.getInstance();
        if (cashRegister.isOpeningToday() && cashRegister.isClosingToday())
        {
            btnPay.setEnabled(false);
            return;
        }
        btnPay.setEnabled(CashRegisterImpl.getInstance().isCashRegisterOpen());
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

        lbTotalPrice = new JLabel("TOTAL A PAGAR");
        lbTotalPrice.setHorizontalAlignment(JLabel.RIGHT);
        mainPanel.add(lbTotalPrice);

        txtTotalPrice = ComponentsUtils.createSpinner();
        txtTotalPrice.setEnabled(false);
        mainPanel.add(txtTotalPrice);

        cbMoney = new JComboBox<>();
        cbMoney.setEditable(false);
        cbMoney.addItem("MXN");
        mainPanel.add(cbMoney);

        lbPay = new JLabel("PAGO CON");
        lbPay.setHorizontalAlignment(JLabel.RIGHT);
        mainPanel.add(lbPay);

        lbMoney = new JLabel("MXN");
        mainPanel.add(lbMoney);

        txtPay = ComponentsUtils.createSpinner();
        textField =  ((JSpinner.DefaultEditor)txtPay.getEditor()).getTextField();;
        txtPay.addChangeListener(e -> {
            validatePay();
        });
        textField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || c == '.' || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    getToolkit().beep();
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                validatePayForKey();
            }
        });
        mainPanel.add(txtPay);

        lbChange = new JLabel("CAMBIO");
        lbChange.setHorizontalAlignment(JLabel.RIGHT);
        mainPanel.add(lbChange);


        lbMoney2 = new JLabel("MXN");
        mainPanel.add(lbMoney2);

        txtChange = ComponentsUtils.createSpinner();
        txtChange.setEnabled(false);
        mainPanel.add(txtChange);

        btnPay = new JButton("Cobrar");
        btnPay.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:$Component.accentColor;" +
                "foreground:#fff;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        mainPanel.add(btnPay);
    }


    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbTotalPrice, GroupLayout.PREFERRED_SIZE,
                                110, GroupLayout.PREFERRED_SIZE)
                        .addGap(60)
                        .addComponent(txtTotalPrice, GroupLayout.PREFERRED_SIZE,
                                22, Short.MAX_VALUE)
                        .addGap(3)
                        .addComponent(cbMoney, GroupLayout.PREFERRED_SIZE,
                                70, GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbPay, GroupLayout.PREFERRED_SIZE,
                                110, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(lbMoney, GroupLayout.PREFERRED_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addGap(10)
                        .addComponent(txtPay, GroupLayout.PREFERRED_SIZE,
                                22, Short.MAX_VALUE
                                )
                )
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbChange, GroupLayout.PREFERRED_SIZE,
                                110, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(lbMoney2, GroupLayout.PREFERRED_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addGap(10)
                        .addComponent(txtChange, GroupLayout.PREFERRED_SIZE,
                                22, Short.MAX_VALUE
                                )
                )
                .addComponent(btnPay, GroupLayout.PREFERRED_SIZE,
                        22, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(0, 0, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbTotalPrice, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTotalPrice, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbMoney, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbPay, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPay, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbMoney, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbChange, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtChange, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbMoney2, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(5, 5, 10)
                .addComponent(btnPay, GroupLayout.PREFERRED_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
        );

    }

    private void configureListeners()
    {
        btnPay.addActionListener(e -> generatePay());
    }

    public void setTotal(double total)
    {
        txtTotalPrice.setValue(total);
    }

    public void validatePay()
    {
        try {
            total = Double.parseDouble(txtTotalPrice.getValue().toString());
            pay = Double.parseDouble(txtPay.getValue().toString());
        }
        catch (NumberFormatException ex)
        {
            txtChange.setValue(0);;
            return;
        }
        if (pay >= total)
        {
            change = pay - total;
            txtChange.setValue(change);
            return;
        }
        txtChange.setValue(0);
    }

    private void validatePayForKey()
    {
        try {
            String money = textField.getText().replaceAll(",", "");
            total = Double.parseDouble(txtTotalPrice.getValue().toString());
            pay = Double.parseDouble(money);
        }
        catch (NumberFormatException ex)
        {
            txtChange.setValue(txtChange.getValue());
            return;
        }
        if (pay >= total)
        {
            change = pay - total;
            txtChange.setValue(change);
            return;
        }
        txtChange.setValue(0);
    }


    private void generatePay()
    {
        total = Double.parseDouble(txtTotalPrice.getValue().toString());
        pay = Double.parseDouble(txtPay.getValue().toString());
        change = Double.parseDouble(txtChange.getValue().toString());

        if (pay < total)
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "El pago no puede ser menor al total a pagar",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                FrameInstance.getInstance(),
                "El pago es de: " + MoneyUtils.putFormat(pay) + " MXN" + "\nEl cambio es de: " + MoneyUtils.putFormat(change) + " MXN",
                "Confirmar Venta",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION)
        {
            int amount = MoneyUtils.getCents(change);
            if (amount > 0)
            {
                int optionAmount = JOptionPane.showConfirmDialog(
                        FrameInstance.getInstance(),
                        "¿Desea redondear " + amount + " cts?",
                        "Confirmar Redondeo",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (optionAmount == JOptionPane.OK_OPTION)
                {
                    total = total + (amount / 100.0);
                    change = change - (amount  / 100.0);
                    JOptionPane.showMessageDialog(
                            FrameInstance.getInstance(),
                            "El cambio es de: " + MoneyUtils.putFormat(change) + " MXN",
                            StringRS.TITLE_DIALOG_ERROR,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            //generar empleado, producto, ticket y venta
            TicketDTO ticket = TicketDTO.builder()
                    .amount(pay)
                    .totalPrice(total)
                    .build();
            UUID ticketId = ticketDao.save(ticket);
            UUID saleId = null;
            if (ticketId != null)
            {

                for (ShoppingCartDTO cartDTO : shoppingCart)
                {
                    SaleDTO sale = SaleDTO.builder()
                            .numberProduct(cartDTO.quantity())
                            .ticket(TicketDTO.builder()
                                    .id(ticketId)
                                    .build())
                            .product(ProductDTO.builder()
                                    .id(cartDTO.product().id())
                                    .build())

                            .build();

                    saleId = saleDao.save(sale);
                    if (saleId != null)
                    {
                        shoppingCartDao.delete(cartDTO.id());
                    }
                }
            }
            if (saleId != null)
            {
                txtPay.setValue(0);
                txtChange.setValue(0);
                txtTotalPrice.setValue(0);
                action.onAction();
                Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        Notifications.Location.BOTTOM_RIGHT,
                        "Venta registrada correctamente"
                );
                createTicket(ticketId);
            }
        }

    }

    private void createTicket(UUID id)
    {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Carrito", "5");

        Thread thread = new Thread(() ->
        {
            progressDialog.setVisible(true);
        });

        Thread process = new Thread(() ->
        {
            progressDialog.setUndefined("Generando Ticket de Venta");
            List<SaleDTO> sales = saleDao.findByTicket(id);
            List<UnitSalesDTO> unitSales = new ArrayList<>();
            unitSales.add(UnitSalesDTO.builder()
                    .fullName("")
                    .symbol("")
                    .price("")
                    .build());
            double total = 0;
            int numberProducts = 0;
            double pay = sales.get(0).ticket().amount();

            for (SaleDTO sale : sales)
            {
                ProductDTO product = sale.product();
                double price = product.price() * sale.numberProduct();
                unitSales.add(new UnitSalesDTO(
                        product.name().toUpperCase() + " " + sale.numberProduct() + " X $" + product.price()
                        ,
                        "$",
                        String.valueOf(price)));
                total += sale.product().price() * sale.numberProduct();
                numberProducts = numberProducts + sale.numberProduct();;
            }
            unitSales.add(UnitSalesDTO.builder()
                    .fullName("TOTAL")
                    .symbol("$")
                    .price(String.valueOf(total))
                    .build());
            unitSales.add(UnitSalesDTO.builder()
                    .fullName("EFECTIVO")
                    .symbol("$")
                    .price(String.valueOf(pay))
                    .build());
            unitSales.add(UnitSalesDTO.builder()
                    .fullName("CAMBIO")
                    .symbol("$")
                    .price(String.valueOf(pay - total))
                    .build());

            Image logoCompany = new ImageIcon(Objects.requireNonNull(Main.class
                    .getResource("/image/michoacana-logo.jpg"))).getImage();

            BarcodeGenerator generator = new BarcodeGenerator(EncodeTypes.CODE_128, id.toString());
            generator.getParameters().setResolution(1200);
            Image logoQr = generator.generateBarCodeImage();

            Path path = Paths.get(PathRS.FORMAT);
            InputStream file;
            try {
                file = Files.newInputStream(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                JasperReport report = (JasperReport) JRLoader.loadObject(file);
                JRBeanArrayDataSource ds = new JRBeanArrayDataSource(unitSales.toArray());

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("ds", ds);
                parameters.put("logoCompany", logoCompany);
                parameters.put("logoQr", logoQr);
                parameters.put("currentDate", DateUtils.formatDate(sales.get(0).ticket().timestamp()));
                parameters.put("currentTime", DateUtils.getTime(sales.get(0).ticket().timestamp()));
                parameters.put("money", MoneyUtils.descomponerNum(total));
                parameters.put("numberProducts", "CANTIDAD DE PRODUCTOS: " + numberProducts);
                parameters.put("id", String.valueOf(sales.get(0).ticket().id()));

                JasperPrint print = JasperFillManager.fillReport(report, parameters, ds);
                JasperViewer view = new JasperViewer(print, false);
                view.setTitle("Reporte de Venta");
                view.setSize(UIScale.scale(new Dimension(380, 600)));
                view.setLocationRelativeTo(FrameInstance.getInstance());
                view.setZoomRatio(.47f);
                view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                progressDialog.dispose();
                view.setVisible(true);
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        process.start();
    }

}
