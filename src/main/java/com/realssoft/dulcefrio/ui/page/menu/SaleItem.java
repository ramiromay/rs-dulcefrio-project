package com.realssoft.dulcefrio.ui.page.menu;

import com.aspose.barcode.EncodeTypes;
import com.aspose.barcode.generation.BarcodeGenerator;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.SaleDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.model.dto.UnitSalesDTO;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.SaleDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.TicketDaoImpl;
import com.realssoft.dulcefrio.api.utils.DateUtils;
import com.realssoft.dulcefrio.api.utils.MoneyUtils;
import com.realssoft.dulcefrio.ui.components.RoundPanel;
import com.realssoft.dulcefrio.ui.components.SearchBar;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.dialog.ProgressDialog;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SaleItem extends JPanel implements AncestorListener
{

    private static final SaleItem INSTANCE = new SaleItem();

    private SearchBar searchBar;
    private JButton viewEmployeeButton;
    private JButton refreshEmployee;
    private ShadowPanel shadowPanel;
    private RoundPanel roundPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private List<TicketDTO> products;
    private TicketDaoImpl ticketDao = TicketDaoImpl.getInstance(FrameInstance.getInstance());
    private SaleDaoImpl  saleDao = SaleDaoImpl.getInstance(FrameInstance.getInstance());

    @Setter
    private boolean isFirstBoot = true;

    public SaleItem()
    {
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
        addAncestorListener(this);
    }

    public static SaleItem getInstance()
    {
        return INSTANCE;
    }

    private void configureProperties()
    {
        UIManager.put("TextField.placeholderForeground", new Color(0,0,0));
        setLayout(new MigLayout("wrap, fill", "[fill]"));
    }

    private void configureComponents()
    {
        model = ComponentsUtils.getTableModel(String.class,String.class, String.class,
                String.class, String.class, String.class);
        model.addColumn("Id");
        model.addColumn("Empleado");
        model.addColumn("Fecha");
        model.addColumn("Hora");
        model.addColumn("Precio Total");
        model.addColumn("Abono");

        table = new JTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        setColumnStyle();

        searchBar = new SearchBar();
        searchBar.setTable(table);
        searchBar.setHoverText("Buscar venta");
        searchBar.setOptionsFilter(List.of("Empleado", "Fecha"));
        this.add(searchBar);

        viewEmployeeButton = ComponentsUtils.createButton(PathRS.SVG_VIEW);
        this.add(viewEmployeeButton);

        refreshEmployee = ComponentsUtils.createButton(PathRS.SVG_REFRESH);
        this.add(refreshEmployee);

        roundPanel = new RoundPanel();
        roundPanel.setLayout(new MigLayout("fill", "[fill]", "[fill]"));
        roundPanel.setBorder(new EmptyBorder(4, 7, 4, 7));

        shadowPanel  = new ShadowPanel();
        shadowPanel.setLayout(new MigLayout("fill", "2[fill]2", "2[fill]2"));
        shadowPanel.add(roundPanel);
        add(shadowPanel);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(table);
        roundPanel.add(scrollPane);
    }

    private void configureListeners()
    {

        viewEmployeeButton.addActionListener(e ->
        {
            createTicket();
        });

        refreshEmployee.addActionListener(e ->
        {
           startProgress();
        });
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(searchBar, GroupLayout.DEFAULT_SIZE,
                                100, 800)
                        .addGap(7)
                        .addComponent(viewEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(3)
                        .addComponent(refreshEmployee, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(shadowPanel, GroupLayout.DEFAULT_SIZE,
                        1000, Short.MAX_VALUE)
                .addGap(3)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(searchBar, GroupLayout.DEFAULT_SIZE,
                                100, GroupLayout.DEFAULT_SIZE)
                        .addComponent(viewEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshEmployee, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(10)
                .addComponent(shadowPanel, GroupLayout.DEFAULT_SIZE,
                        1000, Short.MAX_VALUE)
        );
    }

    private void setColumnStyle()
    {
        table.getTableHeader().setDefaultRenderer(getAlignmentCellRender(table.getTableHeader().getDefaultRenderer()));
        table.setDefaultRenderer(Object.class, getAlignmentCellRender(table.getDefaultRenderer(Object.class)));

        TableColumn columna0 = table.getColumnModel().getColumn(0);
        columna0.setPreferredWidth(0);
        columna0.setMinWidth(0);
        columna0.setMaxWidth(0);
        columna0.setResizable(false);

        TableColumn columna1 = table.getColumnModel().getColumn(1);
        columna1.setPreferredWidth(300);
        columna1.setResizable(false);

        TableColumn columna2 = table.getColumnModel().getColumn(2);
        columna2.setPreferredWidth(150);
        columna2.setResizable(false);

        TableColumn columna3 = table.getColumnModel().getColumn(3);
        columna3.setPreferredWidth(150);
        columna3.setResizable(false);

        TableColumn columna4 = table.getColumnModel().getColumn(4);
        columna4.setPreferredWidth(150);
        columna4.setResizable(false);

        TableColumn columna5 = table.getColumnModel().getColumn(5);
        columna5.setPreferredWidth(150);
        columna5.setResizable(false);

        table.getTableHeader().setReorderingAllowed(false) ;
    }

    public TableCellRenderer getAlignmentCellRender(TableCellRenderer oldRender)
    {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component  component = oldRender.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (component instanceof JLabel label)
                {
                    if (column == 1)
                    {
                        label.setHorizontalAlignment(SwingConstants.LEADING);
                    }
                    else {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                }
                return component;
            }
        };
    }

    private void createTicket()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Seleccione una venta para generar\nsu ticket",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Venta", "4");

        Thread thread = new Thread(() ->
        {
            progressDialog.setVisible(true);
        });

        Thread process = new Thread(() ->
        {
            progressDialog.setUndefined("Generando Ticket de Venta");
            UUID id = (UUID) table.getValueAt(selectedRow, 0);
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

    private int getProducts()
    {
        products = ticketDao.findAll();
        return products.size();
    }

    private void startProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Ventas", "4");
        Thread threadDialog = new Thread(() -> progressDialog.setVisible(true));
        Thread threadProcess = new Thread(() ->
        {
            Object[] row = new Object[6];
            progressDialog.setWaitingState("Cargando ventas", "Espera...");

            int totalEmployees = getProducts();
            progressDialog.setMaximum(totalEmployees);

            model.setRowCount(0);
            for (int index = 0; index <= totalEmployees - 1; index++) {
                TicketDTO product = products.get(index);
                EmployeeDTO employee = product.employee();
                row[0] = product.id();
                row[1] = employee.name() + " " + employee.lastName();
                row[2] = DateUtils.formatDate(product.timestamp());
                row[3] = DateUtils.getTime(product.timestamp());
                row[4] = MoneyUtils.putFormat(product.totalPrice());
                row[5] = MoneyUtils.putFormat(product.amount());
                model.addRow(row);
                table.setModel(model);
                progressDialog.updateProgress(
                        "En proceso | " + (index + 1) + " / " + totalEmployees + " ventas",
                        index + 1
                );

            }
            searchBar.setSearch();
            searchBar.updateData();
            progressDialog.setCompleteState("Ventas cargadas", "Listo!");
            progressDialog.dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Lista de ventas actualizada"
            );
        });
        threadDialog.start();
        threadProcess.start();
    }

    private void refreshTable()
    {
        Object [] row = new Object[6];
        int totalEmployees = getProducts();
        model.setRowCount(0);
        for (int index = 0; index <= totalEmployees - 1; index++)
        {
            TicketDTO product = products.get(index);
            EmployeeDTO employee = product.employee();
            row[0] = product.id();
            row[1] = employee.name() + " " + employee.lastName();
            row[2] = DateUtils.formatDate(product.timestamp());
            row[3] = DateUtils.getTime(product.timestamp());
            row[4] = MoneyUtils.putFormat(product.totalPrice());
            row[5] = MoneyUtils.putFormat(product.amount());
            model.addRow(row);
        }
        table.setModel(model);
        searchBar.setSearch();
        searchBar.updateData();
    }


    @Override
    public void ancestorAdded(AncestorEvent event) {
        if (isFirstBoot)
        {
            isFirstBoot = false;
            startProgress();
            return;
        }
        refreshTable();
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {

    }

    @Override
    public void ancestorMoved(AncestorEvent event) {

    }
}
