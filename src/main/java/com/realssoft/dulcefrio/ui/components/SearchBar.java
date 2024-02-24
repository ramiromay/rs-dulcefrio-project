package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SearchBar extends ShadowPanel
{

    private JPanel mainPanel;
    private JButton searchButton;
    private JTextField  searchField;
    private JComboBox<String> searchOptions;
    private JTable table;
    private DefaultTableModel model;
    private List<Vector<Object>> data;
    private StringBuilder filterOption;
    private StringBuilder toSearch;
    private int coincidences;

    @Setter
    private int arc = UIScale.scale(55);
    private final Color backgroundBar = UIManager.getColor("SearchBar");
    private final Color backgroundFilter = UIManager.getColor("SearchBar.filter");
    private final Color backgroundShadow = UIManager.getColor("background.shade");
    private final Color background = UIManager.getColor("@background");

    public SearchBar()
    {
        super(1.0f);
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
    }

    private void configureProperties()
    {
        setBackground(background);
        setLayout(new MigLayout(
                "fillx", "3[fill]3", "3[fill]3")
        );
        setArc(UIScale.scale(50));
        revalidate();
        repaint();
    }

    private void configureComponents()
    {
        data = new ArrayList<>();
        filterOption = new StringBuilder();
        toSearch = new StringBuilder();

        mainPanel = new RoundPanel();
        mainPanel.setBorder(new EmptyBorder(4, 7, 4, 7));
        mainPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;"
        );
        add(mainPanel);

        searchButton = new JButton();
        searchButton.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_SEARCH));
        searchButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "foreground:@foreground.white;" +
                "arc:999;"
        );
        mainPanel.add(searchButton);

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;" +
                "foreground:$blackColor-2;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:999;" +
                "font:15;"
        );
        mainPanel.add(searchField);

        searchOptions = new JComboBox<>();
        searchOptions.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar.filter;" +
                "foreground:#333;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "foreground:#000;" +
                "arc:25;" +
                "font:13;"
        );
        mainPanel.add(searchOptions);
    }

    private void configureListeners()
    {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                findByFilter();
            }
        });
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                table.clearSelection();
                updateData();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });


        searchButton.addActionListener(e -> {
            String search = searchField.getText();
            if (search.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "El campo de búsqueda está vacío",
                        "Error de Búsqueda",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            findByFilter();
            JOptionPane.showMessageDialog(
                    null,
                    "Se encontraron %s coincidencias por [%s]".formatted(coincidences, search),
                    "Resultado Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        searchOptions.addItemListener(e ->
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                searchField.setText("");
                findByFilter();
            }
        });
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(searchButton,  GroupLayout.DEFAULT_SIZE,
                        38, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        22, Short.MAX_VALUE)
                .addComponent(searchOptions, GroupLayout.DEFAULT_SIZE,
                        150, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(searchButton, GroupLayout.DEFAULT_SIZE,
                        38, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        20, Short.MAX_VALUE)
                .addComponent(searchOptions, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
        );
    }

    public void updateData()
    {
        int rowCount = model.getRowCount();
        data.clear();
        for (int i = 0; i < rowCount; i++)
        {
            Vector<Object> rowData = new Vector<>(model.getDataVector().get(i));
            data.add(rowData);
        }
    }

    public void setSearch()
    {
    	searchField.setText("");
    }

    public void setTable(JTable table)
    {
        this.table = table;
        model = (DefaultTableModel) table.getModel();
    }

    public void setHoverText(String text)
    {
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text);
    }

    public void setOptionsFilter(List<String> options)
    {
        searchOptions.removeAllItems();
        for (String option : options)
        {
            searchOptions.addItem(option);
        }
        searchOptions.setSelectedIndex(0);
        filterOption.setLength(0);
        filterOption.append(searchOptions.getSelectedItem());
    }


    private int getIndexColumn()
    {
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            if (table.getColumnName(i).contentEquals(filterOption))
            {
                return i;
            }
        }
        return -1;
    }

    private void searchData()
    {
        int indexColumn = getIndexColumn();
        String search  = toSearch.toString();
        coincidences = 0;
        model.setRowCount(0);
        if (search.isEmpty())
        {
            for (Vector<Object> vector : data)
            {
                model.addRow(vector);
            }
            table.setModel(model);
            return;
        }
        for (Vector<Object> vector : data)
        {
            String element = String.valueOf(vector.get(indexColumn));
            if (element.toLowerCase().contains(search.toLowerCase()))
            {
                model.addRow(vector);
                coincidences++;
            }

        }
        table.setModel(model);
    }

    private void findByFilter()
    {
        toSearch.setLength(0);
        filterOption.setLength(0);
        toSearch.append(searchField.getText());
        filterOption.append(searchOptions.getSelectedItem());
        searchData();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g)
    {
        if (searchField.isFocusOwner())
        {
            mainPanel.setBackground(Color.white);
            searchField.setBackground(Color.white);
            searchButton.setBackground(Color.white);
            searchOptions.setBackground(backgroundShadow);
            setBackground(backgroundShadow);
            setTransparency(0.05f);
        }
        else {
            mainPanel.setBackground(backgroundBar);
            searchField.setBackground(backgroundBar);
            searchButton.setBackground(backgroundBar);
            searchOptions.setBackground(backgroundFilter);
            setBackground(background);
            setTransparency(1.0f);
        }
        super.paint(g);
    }

}
