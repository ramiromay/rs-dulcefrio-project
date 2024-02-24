package com.realssoft.dulcefrio.ui.page.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.dao.UserDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.EmployeeDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.UserDaoImpl;
import com.realssoft.dulcefrio.api.utils.DateUtils;
import com.realssoft.dulcefrio.ui.components.RoundPanel;
import com.realssoft.dulcefrio.ui.components.SearchBar;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.dialog.ProgressDialog;
import com.realssoft.dulcefrio.ui.dialog.UpdateUserDialog;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.page.PageManager;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserItem extends JPanel implements AncestorListener
{

    private static final UserItem INSTANCE = new UserItem();

    private SearchBar searchBar;
    private JButton editEmployeeButton;
    private JButton deleteEmployeeButton;
    private JButton refreshEmployee;
    private ShadowPanel shadowPanel;
    private RoundPanel roundPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private UserDao userDao;
    private EmployeeDao employeeDao;
    private List<UserDTO> users;

    @Setter
    private boolean isFirstBoot = true;

    public UserItem()
    {
        configureProperties();
        configureComponents();
        configureListeners();
        configureLayout();
        addAncestorListener(this);
    }

    public static UserItem getInstance()
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
        userDao = UserDaoImpl.getInstance(FrameInstance.getInstance());
        model = ComponentsUtils.getTableModel(String.class, String.class,
                String.class, String.class, String.class, String.class,
                Boolean.class, Boolean.class);
        model.addColumn("Id");
        model.addColumn("Usuario");
        model.addColumn("Empleado");
        model.addColumn("Fecha de Creación");
        model.addColumn("Fecha de Modificación");
        model.addColumn("Rol");
        model.addColumn("Habilitado");
        model.addColumn("Activo");

        table = new JTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE_CLASS, "table_style");
        setColumnStyle();

        searchBar = new SearchBar();
        searchBar.setTable(table);
        searchBar.setHoverText("Buscar usuario");
        searchBar.setOptionsFilter(List.of("Usuario", "Empleado", "Rol"));
        this.add(searchBar);

        editEmployeeButton = ComponentsUtils.createButton(PathRS.SVG_EDIT);
        this.add(editEmployeeButton);

        deleteEmployeeButton = ComponentsUtils.createButton(PathRS.SVG_DELETE);
        this.add(deleteEmployeeButton);

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

        editEmployeeButton.addActionListener(e -> modifyEmployee());
        deleteEmployeeButton.addActionListener(e -> deleteEmployee());
        refreshEmployee.addActionListener(e -> startProgress());
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
                        .addComponent(editEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addGap(3)
                        .addComponent(deleteEmployeeButton, GroupLayout.DEFAULT_SIZE,
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
                        .addComponent(editEmployeeButton, GroupLayout.DEFAULT_SIZE,
                                35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteEmployeeButton, GroupLayout.DEFAULT_SIZE,
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

        TableColumn columna1 = table.getColumnModel().getColumn(0);
        columna1.setPreferredWidth(0);
        columna1.setMinWidth(0);
        columna1.setMaxWidth(0);
        columna1.setResizable(false);

        TableColumn columna2 = table.getColumnModel().getColumn(1);
        columna2.setPreferredWidth(180);
        columna2.setResizable(false);

        TableColumn columna3 = table.getColumnModel().getColumn(2);
        columna3.setPreferredWidth(300);
        columna3.setResizable(false);

        TableColumn columna4 = table.getColumnModel().getColumn(3);
        columna4.setPreferredWidth(150);
        columna4.setResizable(false);

        TableColumn columna5 = table.getColumnModel().getColumn(4);
        columna5.setPreferredWidth(150);
        columna5.setResizable(false);

        TableColumn columna6 = table.getColumnModel().getColumn(5);
        columna6.setPreferredWidth(180);
        columna6.setResizable(false);

        TableColumn columna7 = table.getColumnModel().getColumn(6);
        columna7.setPreferredWidth(150);
        columna7.setResizable(false);

        TableColumn columna8 = table.getColumnModel().getColumn(6);
        columna8.setPreferredWidth(150);
        columna8.setResizable(false);

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
                    if ( column== 6 || column == 7 || column == 3 || column == 4)
                    {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    else if (column == 1 || column == 2 )
                    {
                        label.setHorizontalAlignment(SwingConstants.LEADING);

                    }
                    else {
                        label.setHorizontalAlignment(SwingConstants.TRAILING);
                    }
                }
                return component;
            }
        };
    }

    private int getUsers()
    {
        users = userDao.findAll();
        return users.size();
    }

    private void modifyEmployee()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1)
        {
            UUID employeeId = UUID.fromString(model.getValueAt(selectedRow, 0).toString());
            UpdateUserDialog dialog = new UpdateUserDialog(employeeId);
            dialog.setAction(this::refreshTable);
            dialog.setVisible(true);
        }
        else
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Seleccione un usuario para modificar",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteEmployee()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1)
        {

            int option = JOptionPane.showConfirmDialog(
                    PageManager.getInstance(),
                    "¿Desea eliminar al usuario?\nSi eliminas al usuario se eliminara\nel empleado asignado",
                    StringRS.TITLE_DIALOG_INFO,
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION)
            {
                UUID id = UUID.fromString(model.getValueAt(selectedRow, 0).toString());
                employeeDao.delete(id);
                Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        Notifications.Location.BOTTOM_RIGHT,
                        "Empleado y usuario eliminados correctamente"
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
                            "Lista de usuarios actualizada"
                    );
                });

            }
            else {
                table.clearSelection();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(
                    FrameInstance.getInstance(),
                    "Seleccione un empleado para eliminar",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }


    private void startProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setIconAndTitle("Usuarios", "4");
        Thread threadDialog = new Thread(() -> progressDialog.setVisible(true));
        Thread threadProcess = new Thread(() ->
        {
            Object[] row = new Object[8];
            progressDialog.setWaitingState("Cargando usuarios", "Espera...");

            int totalEmployees = getUsers();
            progressDialog.setMaximum(totalEmployees);

            model.setRowCount(0);
            for (int index = 0; index <= totalEmployees - 1; index++) {
                UserDTO user = users.get(index);
                EmployeeDTO  employee = user.employee();
                row[0] = employee.id();
                row[1] = user.username();
                row[2] = employee.name() + " " + employee.lastName();
                row[3] = DateUtils.formatDate(user.creationDate());
                row[4] = DateUtils.formatDate(user.modificationDate());
                row[5] = user.role();
                row[6] = user.available();
                row[7] = user.active();
                model.addRow(row);
                table.setModel(model);
                progressDialog.updateProgress(
                        "En proceso | " + (index + 1) + " / " + totalEmployees + " usuarios",
                        index + 1
                );

            }
            searchBar.setSearch();
            searchBar.updateData();
            progressDialog.setCompleteState("Usuarios cargados", "Listo!");
            progressDialog.dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Lista de usuarios actualizada"
            );
        });
        threadDialog.start();
        threadProcess.start();
    }

    private void refreshTable()
    {
        Object [] row = new Object[8];
        int totalUsers = getUsers();
        model.setRowCount(0);
        for (int index = 0; index <= totalUsers - 1; index++)
        {
            UserDTO user = users.get(index);
            EmployeeDTO  employee = user.employee();
            row[0] = employee.id();
            row[1] = user.username();
            row[2] = employee.name() + " " + employee.lastName();
            row[3] = DateUtils.formatDate(user.creationDate());
            row[4] = DateUtils.formatDate(user.modificationDate());
            row[5] = user.role();
            row[6] = user.available();
            row[7] = user.active();
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
