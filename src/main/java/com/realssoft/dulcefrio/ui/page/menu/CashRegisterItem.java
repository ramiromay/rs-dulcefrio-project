package com.realssoft.dulcefrio.ui.page.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.CashRegisterImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.EmployeeDaoImpl;
import com.realssoft.dulcefrio.api.persistence.entity.CashRegisterOpening;
import com.realssoft.dulcefrio.api.persistence.entity.InterruptCashRegister;
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
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
import java.util.Date;
import java.util.List;

public class CashRegisterItem extends JPanel implements AncestorListener
{

    private static final CashRegisterItem INSTANCE = new CashRegisterItem();

    private SearchBar searchBar;
    private JButton refreshEmployee;
    private JButton openCashRegister;
    private JButton closeCashRegister;
    private ShadowPanel shadowPanel;
    private RoundPanel roundPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private EmployeeDao employeeDao;
    private List<InterruptCashRegister> employees;
    private CashRegisterImpl  cashRegisterDao = CashRegisterImpl.getInstance();

    @Setter
    private boolean isFirstBoot = true;

    public CashRegisterItem()
    {
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
        addAncestorListener(this);
    }

    public static CashRegisterItem getInstance()
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
        employeeDao = EmployeeDaoImpl.getInstance(FrameInstance.getInstance());
        model = ComponentsUtils.getTableModel(String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class);
        model.addColumn("Id");
        model.addColumn("Empleado");
        model.addColumn("Monto Inicial");
        model.addColumn("Fecha");
        model.addColumn("Hora Apertura");
        model.addColumn("Hora Corte");
        model.addColumn("Tipo Corte");
        model.addColumn("Ventas Totales");

        table = new JTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        setColumnStyle();

        searchBar = new SearchBar();
        searchBar.setTable(table);
        searchBar.setHoverText("Buscar empleado");
        searchBar.setOptionsFilter(List.of("Empleado", "Fecha", "Tipo Corte"));
        this.add(searchBar);

        refreshEmployee = ComponentsUtils.createButton(PathRS.SVG_REFRESH);
        this.add(refreshEmployee);

        openCashRegister = ComponentsUtils.createButton("svg/open-cash.svg");
        if (cashRegisterDao.isOpeningToday())
        {
            openCashRegister.setEnabled(false);
        }

        closeCashRegister = ComponentsUtils.createButton("svg/closed-cash.svg");
        if (cashRegisterDao.isClosingToday())
        {
            closeCashRegister.setEnabled(false);
        }

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
        openCashRegister.addActionListener(e -> openCashRegister());
        closeCashRegister.addActionListener(e -> closeCashRegister());
       refreshEmployee.addActionListener(e -> refreshTable());
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
                        .addComponent(openCashRegister, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(3)
                        .addComponent(closeCashRegister, GroupLayout.DEFAULT_SIZE,
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
                        .addComponent(openCashRegister, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(closeCashRegister, GroupLayout.DEFAULT_SIZE,
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

        TableColumn columna = table.getColumnModel().getColumn(0);
        columna.setPreferredWidth(0);
        columna.setMinWidth(0);
        columna.setMaxWidth(0);
        columna.setResizable(false);

        TableColumn columna1 = table.getColumnModel().getColumn(1);
        columna1.setPreferredWidth(300);
        columna1.setResizable(false);

        TableColumn columna2 = table.getColumnModel().getColumn(2);
        columna2.setPreferredWidth(140);
        columna2.setResizable(false);

        TableColumn columna3 = table.getColumnModel().getColumn(3);
        columna3.setPreferredWidth(150);
        columna3.setResizable(false);

        TableColumn columna4 = table.getColumnModel().getColumn(4);
        columna4.setPreferredWidth(150);
        columna4.setResizable(false);

        TableColumn columna5 = table.getColumnModel().getColumn(5);
        columna.setPreferredWidth(150);
        columna.setResizable(false);

        TableColumn columna6 = table.getColumnModel().getColumn(6);
        columna6.setPreferredWidth(100);
        columna6.setResizable(false);

        TableColumn columna7 = table.getColumnModel().getColumn(7);
        columna7.setPreferredWidth(150);
        columna7.setResizable(false);

        table.getTableHeader().setReorderingAllowed(false) ;
    }

    @Contract("_ -> new")
    private @NotNull TableCellRenderer getAlignmentCellRender(TableCellRenderer oldRender)
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


    private void openCashRegister()
    {

        if (!DateUtils.isAfter8AM(new Date()))
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "No se puede abrir la caja antes de las 8:00 AM.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (cashRegisterDao.isCashRegisterOpen())
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Ya existe una caja abierta.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Object value;
        double newQuantity = 0;
        do {
            value = JOptionPane.showInputDialog(
                    FrameInstance.getInstance(),
                    "Monto Inicial:",
                    "Apertura de Caja",
                    JOptionPane.INFORMATION_MESSAGE
            );
            try {
                if (value == null) return;
                newQuantity = Double.parseDouble(value.toString());
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
        CashRegisterOpening cashRegisterOpening = new CashRegisterOpening();
        cashRegisterOpening.setInitialAmount(newQuantity);
        cashRegisterDao.opening(cashRegisterOpening);
        Notifications.getInstance().show(
                Notifications.Type.SUCCESS,
                Notifications.Location.BOTTOM_RIGHT,
                "Caja abierta exitosamente"
        );
        refreshTable();
        openCashRegister.setEnabled(false);
    }

    private void closeCashRegister()
    {
        if (!cashRegisterDao.isCashRegisterOpen())
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "No se encontró ninguna caja abierta.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                FrameInstance.getInstance(),
                "¿Desea cerrar la caja?",
                StringRS.TITLE_DIALOG_INFO,
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
        {

            cashRegisterDao.closing();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Caja cerrada exitosamente"
            );
            refreshTable();
            closeCashRegister.setEnabled(false);
        }
    }

    private int getEmployees()
    {
        employees = cashRegisterDao.getInterruptCashRegister();
        return employees.size();
    }

    private void startProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Caja Registradora", "6");
        Thread threadDialog = new Thread(() -> progressDialog.setVisible(true));
        Thread threadProcess = new Thread(() ->
        {
            Object[] row = new Object[8];
            progressDialog.setWaitingState("Cargando aperturas y cortes de caja", "Espera...");

            int totalEmployees = getEmployees();
            progressDialog.setMaximum(totalEmployees);

            model.setRowCount(0);
            for (int index = 0; index <= totalEmployees - 1; index++) {
                InterruptCashRegister employee = employees.get(index);
                row[0] = employee.getId();
                row[1] = employee.getCashRegisterOpening().getEmployee().getName() + " " + employee.getCashRegisterOpening().getEmployee().getLastName();
                row[2] = MoneyUtils.putFormat(employee.getCashRegisterOpening().getInitialAmount());
                row[3] = DateUtils.formatDate(employee.getCashRegisterOpening().getTimestamp());
                row[4] = DateUtils.getTime(employee.getCashRegisterOpening().getTimestamp());
                row[5] = employee.getTimestamp() == null
                        ? "Pendiente"
                        : DateUtils.getTime(employee.getTimestamp());
                row[6] = employee.getInterruptType() == null
                        ? "Pendiente"
                        : employee.getInterruptType().getName();
                row[7] = employee.getSalesTotal() == null
                        ? "Pendiente"
                        : employee.getSalesTotal();
                model.addRow(row);
                table.setModel(model);
                progressDialog.updateProgress(
                        "En proceso | " + (index + 1) + " / " + totalEmployees + " datos",
                        index + 1
                );

            }
            searchBar.setSearch();
            searchBar.updateData();
            progressDialog.setCompleteState("Aperturas y cortes de caja cargados", "Listo!");
            progressDialog.dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Lista actualizada"
            );
        });
        threadDialog.start();
        threadProcess.start();
    }

    private void refreshTable()
    {
        Object [] row = new Object[8];
        int totalEmployees = getEmployees();
        model.setRowCount(0);
        for (int index = 0; index <= totalEmployees - 1; index++)
        {
            InterruptCashRegister employee = employees.get(index);
            row[0] = employee.getId();
            row[1] = employee.getCashRegisterOpening().getEmployee().getName() + " " + employee.getCashRegisterOpening().getEmployee().getLastName();
            row[2] = MoneyUtils.putFormat(employee.getCashRegisterOpening().getInitialAmount());
            row[3] = DateUtils.formatDate(employee.getCashRegisterOpening().getTimestamp());
            row[4] = DateUtils.getTime(employee.getCashRegisterOpening().getTimestamp());
            row[5] = employee.getTimestamp() == null
                    ? "Pendiente"
                    : DateUtils.getTime(employee.getTimestamp());
            row[6] = employee.getInterruptType() == null
                    ? "Pendiente"
                    : employee.getInterruptType().getName();
            row[7] = employee.getSalesTotal() == null
                    ? "Pendiente"
                    : employee.getSalesTotal();
            model.addRow(row);
        }
        table.setModel(model);
        searchBar.setSearch();
        searchBar.updateData();
    }


    @Override
    public void ancestorAdded(AncestorEvent event) {
        if (!cashRegisterDao.isClosingToday())
        {
            closeCashRegister.setEnabled(true);
        }

        if (!cashRegisterDao.isOpeningToday())
        {
            openCashRegister.setEnabled(true);
        }

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
