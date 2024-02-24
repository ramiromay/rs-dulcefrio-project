package com.realssoft.dulcefrio.ui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.CategoryTypeDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.persistence.dao.ProductDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ProductDaoImpl;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegisterProductDialog extends JDialog
{

    private JPanel mainPanel;
    private JLabel lbTitle;
    private JLabel description;

    private JLabel lbName;
    private JTextField txtNameProduct;

    private JLabel lbPrice;
    private JSpinner txtPrice;
    private JLabel lbStock;
    private JSpinner txtStock;

    private JSeparator separator;

    private JLabel lbCategory;
    private JComboBox<String> cbCategory;

    private JLabel lbProductType;
    private JComboBox<String> cbProductType;
    private JLabel lbDescription;
    private JTextPane txtDescription;

    private JPanel activatePanel;
    private JLabel lbActivate;
    private JRadioButton jrActivate;
    private JRadioButton jrDeactivate;
    private ButtonGroup groupActivate;

    private JButton cmdRegister;
    private final ProductDao productDao = ProductDaoImpl.getInstance(this);
    private List<CategoryDTO> categories;

    @Setter
    private Action action;

    public RegisterProductDialog() {
        super(FrameInstance.getInstance(), true);
        configureComponents();
        configureListeners();
        configureLayout();
        configureProperties();
        addCategory();
    }

    private void addCategory() {
        cbCategory.addItem("Seleccione una categoría");
        categories = productDao.findAllCategory();
        for (CategoryDTO category : categories)
        {
            cbCategory.addItem(category.name());
        }
        cbCategory.setSelectedIndex(0);
    }

    private void configureProperties()
    {
        JFrame frame = FrameInstance.getInstance();
        int height = frame.getExtendedState() == Frame.MAXIMIZED_BOTH
                ? frame.getHeight() - 180
                : frame.getHeight() - 80;

        setSize(UIScale.scale(new Dimension(760, height)));
        setIconImage(((ImageIcon) SVGUtils.getSVGIcon(PathRS.SVG_MENU_ITEM.formatted("1"))).getImage());
        setTitle("Productos");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void configureComponents()
    {
        JScrollPane scrollPane;
        UIManager.put("TextField.placeholderForeground", UIManager.getColor("holder"));
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(25,40,25,40));
        mainPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(mainPanel);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new MigLayout("fill", "2[fill]2", "2[fill]2"));
        contentPane.setBorder(new EmptyBorder(2,20,15,20));
        contentPane.add(scrollPane);
        setContentPane(contentPane);

        lbTitle = new JLabel("Registrar Producto");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        mainPanel.add(lbTitle);

        description = new JLabel("¡Aventurémonos a mas sabores únicos!");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        mainPanel.add(description);

        lbName  = new JLabel("Nombre");
        mainPanel.add(lbName);

        txtNameProduct = new JTextField();
        txtNameProduct.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese el nombre del producto");
        mainPanel.add(txtNameProduct);

        lbPrice = new JLabel("Precio");
        mainPanel.add(lbPrice);

        lbStock = new JLabel("Stock");
        mainPanel.add(lbStock);

        txtPrice = ComponentsUtils.createSpinner();
        ((JSpinner.DefaultEditor)txtPrice.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
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
        mainPanel.add(txtPrice);

        txtStock = new JSpinner();
        txtStock.setValue(1);
        ((JSpinner.DefaultEditor)txtStock.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        mainPanel.add(txtStock);

        separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@foreground;" +
                "height:10;");
        mainPanel.add(separator);

        lbCategory = new JLabel("Categoría");
        mainPanel.add(lbCategory);

        cbCategory = new JComboBox<>();
        cbCategory.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                cbProductType.removeAllItems();
                txtDescription.setText("");
                String item = (String) e.getItem();
                if (item.equals("Seleccione una categoría"))
                {
                    return;
                }

                for (CategoryDTO category : categories)
                {
                    if (category.name().equals(item))
                    {
                        cbProductType.addItem(category.type().name());
                        txtDescription.setText(category.type().description());
                        return;
                    }
                }
            }
        });
        mainPanel.add(cbCategory);

        lbProductType = new JLabel("Tipo de Producto");
        mainPanel.add(lbProductType);

        cbProductType = new JComboBox<>();
        cbProductType.setEnabled(false);
        cbProductType.setEditable(false);
        mainPanel.add(cbProductType);

        lbDescription = new JLabel("Descripción");
        mainPanel.add(lbDescription);

        lbActivate = new JLabel("Habilitar Producto");
        mainPanel.add(lbActivate);

        activatePanel = new JPanel(new MigLayout("insets 0"));
        activatePanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        mainPanel.add(activatePanel);

        jrActivate = new JRadioButton("Habilitado");
        jrActivate.setSelected(true);
        activatePanel.add(jrActivate);

        jrDeactivate = new JRadioButton("Deshabilitado");
        activatePanel.add(jrDeactivate);

        groupActivate = new ButtonGroup();
        groupActivate.add(jrActivate);
        groupActivate.add(jrDeactivate);

        txtDescription = new JTextPane();
        txtDescription.setEnabled(false);
        txtDescription.setEditable(false);
        mainPanel.add(txtDescription);

        cmdRegister = new JButton("Registrar");
        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
    }

    private void configureLayout()
    {
        int space = 2;
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(lbTitle, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(description, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbName, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtNameProduct, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lbPrice, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(lbStock, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(txtPrice, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(txtStock, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
                .addComponent(separator, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbCategory, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(cbCategory, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbProductType, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(cbProductType, GroupLayout.DEFAULT_SIZE,
                        200, Short.MAX_VALUE)
                .addComponent(lbDescription, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtDescription, GroupLayout.DEFAULT_SIZE,
                        200, Short.MAX_VALUE)
                .addComponent(lbActivate, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(activatePanel, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(cmdRegister, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)

        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(lbTitle, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(description, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(lbName, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNameProduct, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(space)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbPrice, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbStock, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(txtPrice, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtStock, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(space)
                .addGap(10)
                .addComponent(separator, GroupLayout.DEFAULT_SIZE,
                        2, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addComponent(lbCategory, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(cbCategory, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addComponent(lbProductType, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(cbProductType, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(space)
                .addComponent(lbDescription, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtDescription, GroupLayout.DEFAULT_SIZE,
                        60, GroupLayout.PREFERRED_SIZE)
                .addComponent(lbActivate, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(activatePanel, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(15)
                .addComponent(cmdRegister, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
        );

    }

    private void configureListeners()
    {
        cmdRegister.addActionListener(e -> registerEmployee());
    }

    private void registerEmployee()
    {
        String name = txtNameProduct.getText().trim();
        double price = Double.parseDouble(txtPrice.getValue().toString());

        Integer stock = (Integer) txtStock.getValue();
        String category = String.valueOf(cbCategory.getSelectedItem()) ;
        String categoryType = String.valueOf(cbProductType.getSelectedItem());
        boolean available = jrActivate.isSelected();

        if (name.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del producto es requerida",
                    "Registrar Producto",
                    JOptionPane.ERROR_MESSAGE
            );
            txtNameProduct.requestFocus();
            return;
        }

        if (price <= 0)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El precio del producto debe ser mayor a 0",
                    "Registrar Producto",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPrice.requestFocus();
            return;
        }


        if (stock <= 0)
        {

            JOptionPane.showMessageDialog(
                    this,
                    "El stock del producto debe ser mayor a 0",
                    "Registrar Producto",
                    JOptionPane.ERROR_MESSAGE
            );
            txtStock.requestFocus();
            return;
        }

        if (category.equals("Seleccione una categoría"))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La categoría del producto es requerida",
                    "Registrar Producto",
                    JOptionPane.ERROR_MESSAGE
            );
            cbCategory.requestFocus();
            return;
        }

        if (productDao.isProductExist(null, name))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El producto ya existe",
                    "Registrar Producto",
                    JOptionPane.ERROR_MESSAGE
            );
            txtNameProduct.requestFocus();
            return;
        }

        ProductDTO product = ProductDTO.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .category(CategoryDTO.builder()
                        .name(category)
                        .type(CategoryTypeDTO.builder()
                                .name(categoryType)
                                .build())
                        .build())
                .isAvailable(available)
                .build();

        UUID id = productDao.save(product);
        if (id != null)
        {
            dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Producto registrado correctamente"
            );
            action.onAction();
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
        }

    }

}
