package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.persistence.dao.ProductDao;
import com.realssoft.dulcefrio.api.persistence.dao.ShoppingCartDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ProductDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.ShoppingCartDaoImpl;
import com.realssoft.dulcefrio.ui.dialog.Action;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.rs.pattern.Observable;
import com.realssoft.dulcefrio.ui.rs.pattern.Observer;
import com.realssoft.dulcefrio.ui.utils.ComponentsUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchPanel extends RoundPanel
{

    private JFrame frame = FrameInstance.getInstance();
    private JPanel mainPanel;
    private JLabel lbTitle;
    private JLabel description;

    private SearchSuggestionBar searchBar;
    private SuggestionPanel suggestionPanel;
    private JPopupMenu popupMenu;
    private boolean isFirst = true;

    private JLabel lbName;
    private JTextField txtNameProduct;

    private JLabel lbPrice;
    private JTextField txtPrice;
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

    private JSeparator separatorAmount;
    private JLabel lbAmount;
    private JSpinner txtAmount;

    private JButton cmdRegister;
    private JButton cmdClear;
    private int productIndex = -1;

    @Setter
    private List<ProductDTO> products;
    @Setter
    private Action action;
    private ShoppingCartDao shoppingCartDao;

    public SearchPanel() {
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
    }

    public void setAddShoppingCart()
    {
        CashRegisterImpl cashRegister = CashRegisterImpl.getInstance();
        if (cashRegister.isOpeningToday() && cashRegister.isClosingToday())
        {
            cmdRegister.setEnabled(false);
            return;
        }
        cmdRegister.setEnabled(CashRegisterImpl.getInstance().isCashRegisterOpen());
    }

    private void configureProperties()
    {
        setBorder(new EmptyBorder(2, 5, 2, 5));
        setLayout(new MigLayout("fill", "[fill]", "[fill]"));
    }

    private void configureComponents()
    {
        shoppingCartDao = ShoppingCartDaoImpl.getInstance(frame);
        JScrollPane scrollPane;
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setBorder(new EmptyBorder(25,40,10,40));

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(mainPanel);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new MigLayout("fill", "0[fill]0", "0[fill]0"));
        contentPane.setBorder(new EmptyBorder(0,0,0,0));
        contentPane.add(scrollPane);
        add(contentPane);

        lbTitle = new JLabel("Asistente de Productos");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        mainPanel.add(lbTitle);

        description = new JLabel("¡Encontremos tu sabor ideal!");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        mainPanel.add(description);

//        --------------------------



        searchBar = new SearchSuggestionBar();
        searchBar.setHoverText("Buscar producto");
        searchBar.setMouseClicked(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               mouseClickedEvent();
            }
        });
        searchBar.setKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                keyEvent();
            }
        });
        add(searchBar);

        suggestionPanel = new SuggestionPanel();
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(164, 164, 164)));
        popupMenu.setFocusable(false);
        popupMenu.add(suggestionPanel);

//        --------------------------

        UIManager.put("TextField.placeholderForeground", UIManager.getColor("holder"));
        lbName  = new JLabel("Nombre");
        mainPanel.add(lbName);

        txtNameProduct = new JTextField();
        txtNameProduct.setEnabled(false);
        mainPanel.add(txtNameProduct);

        lbPrice = new JLabel("Precio");
        mainPanel.add(lbPrice);

        lbStock = new JLabel("Stock");
        mainPanel.add(lbStock);

        txtPrice = new JTextField();
        txtPrice.setEnabled(false);
        txtPrice.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || c == '.' ||(c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        mainPanel.add(txtPrice);

        txtStock = new JSpinner();
        txtStock.setEnabled(false);
        txtStock.setValue(0);
        txtStock.addKeyListener(new KeyAdapter() {
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
        txtStock.putClientProperty(FlatClientProperties.STYLE, "" +
                "arrowType:chevron;" +
                "arc:999;");
        mainPanel.add(txtStock);

        separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@foreground;" +
                "height:10;");
        mainPanel.add(separator);

        lbCategory = new JLabel("Categoría");
        mainPanel.add(lbCategory);

        cbCategory = new JComboBox<>();
        cbCategory.setEnabled(false);
        mainPanel.add(cbCategory);

        lbProductType = new JLabel("Tipo de Producto");
        mainPanel.add(lbProductType);

        cbProductType = new JComboBox<>();
        cbProductType.setEditable(false);
        cbProductType.setEnabled(false);
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
        jrActivate.setEnabled(false);
        activatePanel.add(jrActivate);

        jrDeactivate = new JRadioButton("Deshabilitado");
        jrDeactivate.setEnabled(false);
        activatePanel.add(jrDeactivate);

        groupActivate = new ButtonGroup();
        groupActivate.add(jrActivate);
        groupActivate.add(jrDeactivate);

        txtDescription = new JTextPane();
        txtDescription.setEditable(false);
        txtDescription.setEnabled(false);
        mainPanel.add(txtDescription);

        separatorAmount = new JSeparator();
        separatorAmount.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@foreground;" +
                "height:10;");
        mainPanel.add(separatorAmount);

        lbAmount = new JLabel("Cantidad de producto");
        mainPanel.add(lbAmount);

        txtAmount = new JSpinner();
        txtAmount.setValue(1);


        cmdRegister = new JButton("Agregar al carrito");
        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:$Component.accentColor;" +
                "foreground:#fff;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        cmdClear = ComponentsUtils.createButton(PathRS.SVG_RESTART);
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
                .addGroup(layout.createSequentialGroup()
                        .addComponent(searchBar, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(cmdClear, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
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
                .addComponent(separatorAmount, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbAmount, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtAmount, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(cmdRegister, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(lbTitle, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(description, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(searchBar, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdClear, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(10)
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
                .addComponent(separatorAmount, GroupLayout.DEFAULT_SIZE,
                        2, GroupLayout.PREFERRED_SIZE)
                .addComponent(lbAmount, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAmount, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(25)
                .addComponent(cmdRegister, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
        );

    }

    private void configureListeners()
    {
        cmdRegister.addActionListener(e -> addShoppingCart());
        cmdClear.addActionListener(e -> resetDataProduct());
    }

    private void addShoppingCart()
    {
        if (productIndex == -1) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Producto no valido",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (jrDeactivate.isSelected())
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "El producto se encuentra inactivo",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        ProductDTO product = products.get(productIndex);
        int amount = (int) txtAmount.getValue();
        if (amount > product.stock()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No hay suficiente stock",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        // agregar producto al carrito
        System.out.println(product.toString());
        ShoppingCartDTO newProduct = ShoppingCartDTO.builder()
                .product(product)
                .quantity(amount)
                .build();
        UUID id = shoppingCartDao.save(newProduct);
        if (id != null)
        {
            resetDataProduct();
            action.onAction();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Producto agregado al carrito"
            );
        }
    }

    private void resetDataProduct()
    {
        searchBar.setSearch();
        txtNameProduct.setText("");
        txtPrice.setText("");
        txtStock.setValue(0);
        cbCategory.removeAllItems();
        cbProductType.removeAllItems();
        txtDescription.setText("");
        txtAmount.setValue(1);
        groupActivate.clearSelection();
        productIndex = -1;
    }

    private void mouseClickedEvent()
    {
        if (isFirst)
        {
            popupMenu.setPopupSize(0, 0);
            popupMenu.show(searchBar, 0, searchBar.getHeight());
            popupMenu.setVisible(false);
            isFirst = false;
        }

        if (suggestionPanel.getComponentCount() > 0)
        {
            popupMenu.show(searchBar, 0, searchBar.getHeight());
            popupMenu.setPopupSize(searchBar.getWidth(), popupMenu.getHeight());
        }
    }

    private void searchData(String search)
    {
        suggestionPanel.removeAll();
        for (ProductDTO product : products)
        {
            String productName =  product.name();
            if (productName.toLowerCase().contains(search.toLowerCase()))
            {
                SuggestionItem item = new SuggestionItem(productName);
                item.setMouseListener(this::mouseClickedItem);
                suggestionPanel.add(item, "wrap");
                suggestionPanel.revalidate();
                suggestionPanel.repaint();
            }
            if (suggestionPanel.getComponentCount() == 10)
            {
                return;
            }
        }
    }

    private int getIndexItem(String item)
    {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).name().equals(item)) {
                return i;
            }
        }
        return -1;
    }

    private void showProduct(String item)
    {
        productIndex = getIndexItem(item);
        ProductDTO product = products.get(productIndex);
        if (product == null) return;
        CategoryDTO category = product.category();
        txtNameProduct.setText(product.name());
        txtPrice.setText(String.valueOf(product.price()));
        txtStock.setValue(product.stock());
        cbCategory.addItem(product.category().name());
        cbCategory.setSelectedIndex(0);
        cbProductType.addItem(category.type().name());
        txtDescription.setText(category.type().description());
        if (Boolean.TRUE.equals(product.isAvailable()))
        {
            jrActivate.setSelected(true);
        }
        else
        {
            jrDeactivate.setSelected(true);
        }
    }

    private void mouseClickedItem(String item)
    {
        resetDataProduct();
        showProduct(item);
        searchBar.setSearch();
        popupMenu.setPopupSize(0, 0);
        suggestionPanel.removeAll();
        popupMenu.setVisible(false);
        txtAmount.requestFocus();
    }

    private void keyEvent()
    {
        String search = searchBar.getSearch();
        if (search.isEmpty())
        {
            suggestionPanel.removeAll();
            popupMenu.setVisible(false);
            return;
        }
        searchData(search);
        if (suggestionPanel.getComponentCount() > 0)
        {
            popupMenu.show(searchBar, 0, searchBar.getHeight());
            popupMenu.setPopupSize(searchBar.getWidth(), (suggestionPanel.getComponentCount() * 35) + 5);
            return;
        }
        popupMenu.setVisible(false);
    }

}
