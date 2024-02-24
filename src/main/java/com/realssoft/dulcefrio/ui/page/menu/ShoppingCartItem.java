package com.realssoft.dulcefrio.ui.page.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.dao.ProductDao;
import com.realssoft.dulcefrio.api.persistence.dao.ShoppingCartDao;
import com.realssoft.dulcefrio.api.persistence.dao.UserDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ProductDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ShoppingCartDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.UserDaoImpl;
import com.realssoft.dulcefrio.api.utils.MoneyUtils;
import com.realssoft.dulcefrio.ui.components.PaymentPanel;
import com.realssoft.dulcefrio.ui.components.RoundPanel;
import com.realssoft.dulcefrio.ui.components.SearchPanel;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import com.realssoft.dulcefrio.ui.components.ShoppingCartTitle;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.dialog.ProgressDialog;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.page.PageManager;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ShoppingCartItem extends JPanel implements AncestorListener
{

    private static final ShoppingCartItem  INSTANCE = new ShoppingCartItem();

    private ShoppingCartTitle panelEmployeeName;
    private JButton editEmployeeButton;
    private JButton deleteEmployeeButton;

    private JPanel auxPanel;
    private ShadowPanel shadowPanelTop;
    private RoundPanel roundPanelTop;
    private JTable table;
    private JScrollPane scrollPaneTop;
    private DefaultTableModel model;

    private ShadowPanel shadowPanelBottom;
    private PaymentPanel paymentPanel;

    private ShadowPanel shadowPanelRigth;
    private SearchPanel searchPanel;

    private UserDao  userDao;

    private ShoppingCartDao shoppingCartDao;
    private List<ShoppingCartDTO> shoppingCart;

    private ProductDao productDao;
    private List<ProductDTO> products;

    @Setter
    private boolean isFirstBoot = true;

    public ShoppingCartItem()
    {
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
        addAncestorListener(this);
    }

    public static ShoppingCartItem getInstance()
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
        userDao = UserDaoImpl.getInstance(FrameInstance.getInstance());
        shoppingCartDao = ShoppingCartDaoImpl.getInstance(FrameInstance.getInstance());
        productDao = ProductDaoImpl.getInstance(FrameInstance.getInstance());

        model = ComponentsUtils.getTableModel(String.class, String.class, String.class,
                String.class, String.class, String.class, Boolean.class);
        model.addColumn("Id");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Cantidad");
        model.addColumn("Subtotal");

        table = new JTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        setColumnStyle();

        panelEmployeeName = new ShoppingCartTitle();

        editEmployeeButton = ComponentsUtils.createButton(PathRS.SVG_EDIT);
        this.add(editEmployeeButton);

        deleteEmployeeButton = ComponentsUtils.createButton(PathRS.SVG_DELETE);
        this.add(deleteEmployeeButton);

        auxPanel = new JPanel();
        auxPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        auxPanel.setLayout(new MigLayout("fill, wrap", "0[fill]0", "0[fill]0"));

        shadowPanelTop = new ShadowPanel();
        shadowPanelTop.setArc(50);
        shadowPanelTop.setLayout(new MigLayout("fill", "2[fill]2", "2[fill]2"));
        auxPanel.add(shadowPanelTop, "h 70%");

        roundPanelTop = new RoundPanel();
        roundPanelTop.setLayout(new MigLayout("fill", "[fill]", "[fill]"));
        roundPanelTop.setBorder(new EmptyBorder(4, 7, 4, 7));
        shadowPanelTop.add(roundPanelTop);

        scrollPaneTop = new JScrollPane();
        scrollPaneTop.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneTop.setViewportView(table);
        roundPanelTop.add(scrollPaneTop);

        shadowPanelBottom = new ShadowPanel();
        shadowPanelBottom.setArc(50);
        shadowPanelBottom.setLayout(new MigLayout("fill", "2[fill]2", "2[fill]2"));
        auxPanel.add(shadowPanelBottom, "growx, gapy 5");

        paymentPanel = new PaymentPanel();
        paymentPanel.setAction(() -> refreshTable());
        shadowPanelBottom.add(paymentPanel);

        shadowPanelRigth = new ShadowPanel();
        shadowPanelRigth.setArc(50);
        shadowPanelRigth.setLayout(new MigLayout("fill", "2[fill]2", "2[fill]2"));
        add(shadowPanelRigth);

        searchPanel  = new SearchPanel();
        searchPanel.setAction(() ->
        {
            refreshTable();
        });
        shadowPanelRigth.add(searchPanel);

    }

    private void configureListeners()
    {
        editEmployeeButton.addActionListener(e -> modifyProduct());
        deleteEmployeeButton.addActionListener(e -> deleteProduct());
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panelEmployeeName, GroupLayout.DEFAULT_SIZE,
                                100, 800)
                        .addGap(7)
                        .addComponent(editEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(3)
                        .addComponent(deleteEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(3)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(auxPanel, GroupLayout.DEFAULT_SIZE,
                                650, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(shadowPanelRigth, GroupLayout.DEFAULT_SIZE,
                                500, 500)
                )
                .addGap(3)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(panelEmployeeName, GroupLayout.DEFAULT_SIZE,
                                100, GroupLayout.DEFAULT_SIZE)
                        .addComponent(editEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(8)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(auxPanel, GroupLayout.DEFAULT_SIZE,
                                1000, Short.MAX_VALUE)
                        .addComponent(shadowPanelRigth, GroupLayout.DEFAULT_SIZE,
                                1000, Short.MAX_VALUE)
                )

        );
    }

    private void setColumnStyle()
    {
        table.getTableHeader().setDefaultRenderer(getAlignmentCellRender(table.getTableHeader().getDefaultRenderer()));
        table.setDefaultRenderer(Object.class, getAlignmentCellRender(table.getDefaultRenderer(Object.class)));

        TableColumn columna1 = table.getColumnModel().getColumn(0);
        columna1.setPreferredWidth(0);
        columna1.setMinWidth(0);
        columna1.setMaxWidth(0);
        columna1.setResizable(false);

        TableColumn columna2 = table.getColumnModel().getColumn(1);
        columna2.setPreferredWidth(300);
        columna2.setResizable(false);

        TableColumn columna3 = table.getColumnModel().getColumn(2);
        columna3.setPreferredWidth(100);
        columna3.setResizable(false);

        TableColumn columna4 = table.getColumnModel().getColumn(3);
        columna4.setPreferredWidth(100);
        columna4.setResizable(false);

        TableColumn columna5 = table.getColumnModel().getColumn(4);
        columna5.setPreferredWidth(100);
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

    private int getProducts()
    {
        shoppingCart = shoppingCartDao.findAll();
        paymentPanel.setShoppingCart(shoppingCart);
        return shoppingCart.size();
    }

    private void startProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Productos", "2");
        Thread threadDialog = new Thread(() -> progressDialog.setVisible(true));
        Thread threadProcess = new Thread(() ->
        {
            Object[] row = new Object[5];
            progressDialog.setWaitingState("Cargando datos del empleado", "Espera...");
            setInformationEmployee();

            progressDialog.setWaitingState("Cargando datos de busqueda", "Espera...");
            products  = productDao.findAll();
            searchPanel.setProducts(products);

            progressDialog.setWaitingState("Cargando productos", "Espera...");
            int totalEmployees = getProducts();
            progressDialog.setMaximum(totalEmployees);
            double total = 0;
            model.setRowCount(0);
            for (int index = 0; index <= totalEmployees - 1; index++) {
                ShoppingCartDTO item = shoppingCart.get(index);
                ProductDTO product = item.product();
                row[0] = item.id();
                row[1] = product.name();
                row[2] = MoneyUtils.putFormat(product.price());
                row[3] = item.quantity();
                row[4] = MoneyUtils.putFormat(product.price() * item.quantity());
                total =  total + product.price() * item.quantity();
                model.addRow(row);
                table.setModel(model);
                progressDialog.updateProgress(
                        "En proceso | " + (index + 1) + " / " + totalEmployees + " productos",
                        index + 1
                );
            }
            paymentPanel.setTotal(total);
            progressDialog.setCompleteState("Productos cargados", "Listo!");
            progressDialog.dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Lista de productos cargada"
            );
        });
        threadDialog.start();
        threadProcess.start();
    }

    private void refreshTable()
    {
        Object [] row = new Object[5];
        double total = 0;
        int totalEmployees = getProducts();
        model.setRowCount(0);
        for (int index = 0; index <= totalEmployees - 1; index++)
        {
            ShoppingCartDTO item = shoppingCart.get(index);
            ProductDTO product = item.product();
            row[0] = item.id();
            row[1] = product.name();
            row[2] = MoneyUtils.putFormat(product.price());
            row[3] = item.quantity();
            row[4] = MoneyUtils.putFormat(product.price() * item.quantity());
            total =  total + product.price() * item.quantity();
            model.addRow(row);
        }
        table.setModel(model);
        paymentPanel.setTotal(total);
        products  = productDao.findAll();
        searchPanel.setProducts(products);
        paymentPanel.validatePay();
    }

    private void setInformationEmployee()
    {
        UserDTO user = userDao.findById(Main.ID_USER);
        EmployeeDTO employee =  user.employee();
        String fullName = employee.name() + " " + employee.lastName();
        String role = Boolean.TRUE.equals(Main.ADMIN) ? "Administrador" : "Empleado";
        System.out.println(role);
        System.out.println(Boolean.TRUE.equals(Main.ADMIN));
        panelEmployeeName.setHoverText(fullName, role);
    }

    private void modifyProduct()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1)
        {
            Integer quantity = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
            String name = model.getValueAt(selectedRow, 1).toString();
            Object value;
            int newQuantity = 0;
            do {
                value = JOptionPane.showInputDialog(
                        FrameInstance.getInstance(),
                        "Cantidad de producto:",
                        "Modificar Producto",
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        null,
                        quantity
                );
                try {
                    if (value == null) return;
                    newQuantity = Integer.parseInt(value.toString());
                    if (newQuantity <= 0)
                    {
                        JOptionPane.showMessageDialog(
                                FrameInstance.getInstance(),
                                "La cantidad debe ser mayor a 0.",
                                StringRS.TITLE_DIALOG_ERROR,
                                JOptionPane.ERROR_MESSAGE
                        );
                        value = -1;
                    }

                    if (!shoppingCartDao.isEnoughProduct(name, newQuantity))
                    {
                        JOptionPane.showMessageDialog(
                                FrameInstance.getInstance(),
                                "No hay suficiente producto.",
                                StringRS.TITLE_DIALOG_ERROR,
                                JOptionPane.ERROR_MESSAGE
                        );
                        value = -1;
                    }

                }
                catch (NumberFormatException e)
                {
                    JOptionPane.showMessageDialog(
                            FrameInstance.getInstance(),
                            "Numero no valido.",
                            StringRS.TITLE_DIALOG_ERROR,
                            JOptionPane.ERROR_MESSAGE
                    );
                    value = -1;
                }
            }
            while (Integer.parseInt(value.toString()) == -1);

            UUID id = UUID.fromString(model.getValueAt(selectedRow, 0).toString());
            shoppingCartDao.update(id, ShoppingCartDTO.builder()
                            .product(ProductDTO.builder()
                                    .name(name)
                                    .build())
                    .quantity(newQuantity)
                    .build()
            );
            refreshTable();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Se modifico la cantidad de producto correctamente"
            );
        }
        else
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Seleccione un producto para modificar.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int option = JOptionPane.showConfirmDialog(
                    PageManager.getInstance(),
                    "¿Desea eliminar el producto del carrito?",
                    StringRS.TITLE_DIALOG_INFO,
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                UUID id = UUID.fromString(model.getValueAt(selectedRow, 0).toString());
                shoppingCartDao.delete(id);
                Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        Notifications.Location.BOTTOM_RIGHT,
                        "Producto eliminado correctamente"
                );
                refreshTable();
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Notifications.getInstance().show(
                            Notifications.Type.SUCCESS,
                            Notifications.Location.BOTTOM_RIGHT,
                            "Lista de productos actualizada"
                    );
                });
                return;
            }
            table.clearSelection();

        } else {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Seleccione un producto para eliminar.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void ancestorAdded(AncestorEvent event)
    {
        paymentPanel.setEnablePay();
        searchPanel.setAddShoppingCart();
        if (isFirstBoot)
        {
            isFirstBoot = false;
            startProgress();
            return;
        }
        refreshTable();
    }

    @Override
    public void ancestorRemoved(AncestorEvent event)
    {

    }

    @Override
    public void ancestorMoved(AncestorEvent event) {}

}