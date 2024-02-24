package com.realssoft.dulcefrio.ui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.dao.UserDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.AccountDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.EmployeeDaoImpl;
import com.realssoft.dulcefrio.api.persistence.dao.impl.UserDaoImpl;
import com.realssoft.dulcefrio.api.utils.DateUtils;
import com.realssoft.dulcefrio.ui.components.PasswordStrengthStatus;
import com.realssoft.dulcefrio.ui.components.calendar.Calendar;
import com.realssoft.dulcefrio.ui.components.calendar.model.ModelDate;
import com.realssoft.dulcefrio.ui.components.calendar.model.ModelMonth;
import com.realssoft.dulcefrio.ui.components.calendar.utils.CalendarEventCellRender;
import com.realssoft.dulcefrio.ui.components.calendar.utils.CalendarSelectedListener;
import com.realssoft.dulcefrio.ui.notification.Notifications;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics2D;
import java.awt.PopupMenu;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegisterEmployeeDialog extends JDialog
{

    private JPanel mainPanel;
    private JLabel lbTitle;
    private JLabel description;

    private JLabel lbName;
    private JTextField txtName;
    private JTextField txtLastName;

    private JLabel lbDate;
    private JTextField txtDate;
    private JButton cmdDate;
    private JPopupMenu panelDate;
    private Calendar calendar;

    private JLabel lbPhone;
    private JTextField txtPhone;

    private JLabel lbEmail;
    private JTextField txtEmail;

    private JSeparator separator;

    private JLabel lbUsername;
    private JTextField txtUsername;

    private JLabel lbRol;
    private JPanel rolPanel;
    private JRadioButton jrEmployee;
    private JRadioButton jrAdmin;

    private JLabel lbPassword;
    private JPasswordField txtPassword;
    private JLabel lbConfirmPassword;
    private JPasswordField txtConfirmPassword;
    private ButtonGroup groupRol;

    private JPanel activatePanel;
    private JLabel lbActivate;
    private JRadioButton jrActivate;
    private JRadioButton jrDeactivate;
    private ButtonGroup groupActivate;

    private JButton cmdRegister;
    private PasswordStrengthStatus passwordStrengthStatus;
    private final EmployeeDao employeeDao = EmployeeDaoImpl.getInstance(this);
    private final UserDao userDao = UserDaoImpl.getInstance(this);

    @Setter
    private Action action;

    public RegisterEmployeeDialog() {
        super(FrameInstance.getInstance(), true);
        configureComponents();
        configureListeners();
        configureLayout();
        configureProperties();
    }

    private void configureProperties()
    {
        int height = FrameInstance.getInstance().getHeight() - 80;
        setSize(UIScale.scale(new Dimension(760, height)));
        setIconImage(((ImageIcon) SVGUtils.getSVGIcon(PathRS.SVG_MENU_ITEM.formatted("2"))).getImage());
        setTitle("Empleados");
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

        lbTitle = new JLabel("Registrando Empleado");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        mainPanel.add(lbTitle);

        description = new JLabel("¡Iniciando este viaje de sabores y experiencias únicas!");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        mainPanel.add(description);

        lbName  = new JLabel("Nombre Completo");
        mainPanel.add(lbName);

        txtName = new JTextField();
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isLetter(c) && (Character.isUpperCase(c) || Character.isLowerCase(c))) ||
                        c == KeyEvent.VK_BACK_SPACE ||
                        c == KeyEvent.VK_SPACE ||
                        c == KeyEvent.VK_DELETE)) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nombre(s)");
        mainPanel.add(txtName);

        txtLastName = new JTextField();
        txtLastName.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!((Character.isLetter(c) && (Character.isUpperCase(c) || Character.isLowerCase(c))) ||
                    c == KeyEvent.VK_BACK_SPACE ||
                    c == KeyEvent.VK_SPACE ||
                    c == KeyEvent.VK_DELETE)) {
                getToolkit().beep();
                e.consume();
            }
        }
    });
        txtLastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Apellidos");
        mainPanel.add(txtLastName);

        lbDate = new JLabel("Fecha de Ingreso");
        mainPanel.add(lbDate);

        lbPhone = new JLabel("Teléfono");
        mainPanel.add(lbPhone);

        txtDate = new JTextField(DateUtils.formatDate(new Date()));
        txtDate.setEditable(false);
        mainPanel.add(txtDate);

        calendar = new Calendar();
        calendar.addCalendarSelectedListener((evt, date) -> {
            txtDate.setText(DateUtils.formatDate(date.toDate()));
            panelDate.setVisible(false);
        });

        panelDate = new JPopupMenu();
        panelDate.setBorder(BorderFactory.createLineBorder(new Color(164, 164, 164)));
        panelDate.setFocusable(false);
        panelDate.add(calendar);

        cmdDate = new JButton();
        cmdDate.setHorizontalAlignment(SwingConstants.CENTER);
        cmdDate.setVerticalAlignment(SwingConstants.CENTER);
        cmdDate.setIcon(SVGUtils.getSVGIcon(PathRS.SVG_CALENDAR, 20, 20));
        cmdDate.setToolTipText("Abrir calendario");
        cmdDate.addActionListener(e -> {
            panelDate.show(txtDate, 0, txtDate.getHeight());
            panelDate.setPopupSize(txtDate.getWidth() + cmdDate.getWidth(), 280);
        });

        txtPhone = new JTextField();
        txtPhone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (txtPhone.getText().length() == 10)
                {
                    getToolkit().beep();
                    e.consume();
                }
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    getToolkit().beep();
                    e.consume();
                }
            }

        });
        txtPhone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su número de teléfono");
        mainPanel.add(txtPhone);

        lbEmail = new JLabel("Correo");
        mainPanel.add(lbEmail);

        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su correo");
        mainPanel.add(txtEmail);

        separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@foreground;" +
                "height:10;");
        mainPanel.add(separator);

        lbUsername = new JLabel("Usuario");
        mainPanel.add(lbUsername);

        lbRol = new JLabel("Rol");
        mainPanel.add(lbRol);

        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su usuario");
        mainPanel.add(txtUsername);

        rolPanel = new JPanel(new MigLayout("insets 0"));
        rolPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        mainPanel.add(rolPanel);

        jrEmployee = new JRadioButton("Empleado");
        jrEmployee.setSelected(true);
        rolPanel.add(jrEmployee);

        jrAdmin = new JRadioButton("Administrador");
        rolPanel.add(jrAdmin);

        groupRol = new ButtonGroup();
        groupRol.add(jrEmployee);
        groupRol.add(jrAdmin);

        lbPassword = new JLabel("Contraseña");
        mainPanel.add(lbPassword);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingrese su contraseña");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        mainPanel.add(txtPassword);

        passwordStrengthStatus = new PasswordStrengthStatus();
        passwordStrengthStatus.initPasswordField(txtPassword);
        mainPanel.add(passwordStrengthStatus);

        lbConfirmPassword = new JLabel("Confirmar Contraseña");
        mainPanel.add(lbConfirmPassword);

        lbActivate = new JLabel("Habilitar Usuario");
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

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirme su contraseña");
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        mainPanel.add(txtConfirmPassword);

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
                .addGroup(layout.createSequentialGroup()
                        .addComponent(txtName, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(txtLastName, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lbDate, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(lbPhone, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDate, GroupLayout.DEFAULT_SIZE,
                                50, Short.MAX_VALUE)
                        .addComponent(cmdDate, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addGap(5)
                        .addComponent(txtPhone, GroupLayout.DEFAULT_SIZE,
                                100, Short.MAX_VALUE)
                )
                .addComponent(lbEmail, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtEmail, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(separator, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbUsername, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbPassword, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE,
                        200, Short.MAX_VALUE)
                .addComponent(passwordStrengthStatus, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(lbConfirmPassword, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(txtConfirmPassword, GroupLayout.DEFAULT_SIZE,
                        200, Short.MAX_VALUE)
                .addComponent(lbRol, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(rolPanel, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(txtName, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtLastName, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(space)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lbDate, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbPhone, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(txtDate, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdDate, GroupLayout.DEFAULT_SIZE,
                                20, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPhone, GroupLayout.DEFAULT_SIZE,
                                30, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(space)
                .addComponent(lbEmail, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtEmail, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(10)
                .addComponent(separator, GroupLayout.DEFAULT_SIZE,
                        2, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addComponent(lbUsername, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
                .addComponent(lbPassword, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addGap(space)
                .addComponent(passwordStrengthStatus, GroupLayout.DEFAULT_SIZE,
                        10, GroupLayout.PREFERRED_SIZE)
                .addComponent(lbConfirmPassword, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtConfirmPassword, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(lbRol, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(rolPanel, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
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
        String name = txtName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String date = txtDate.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword()).trim();
        String confirmPassword = String.valueOf(txtConfirmPassword.getPassword()).trim();
        String role = jrEmployee.isSelected() ? "Empleado" : "Administrador";
        boolean available = jrActivate.isSelected();

        if (name.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre es obligatorio",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtName.requestFocus();
            return;
        }

        if (lastName.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El apellido es obligatorio",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtLastName.requestFocus();
            return;
        }

        if (date.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha es obligatoria",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtDate.requestFocus();
            return;
        }

        if (!DateUtils.isDateValid(date))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha es inválida debe estar\nen formato dia/mes/año",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtDate.requestFocus();
            return;
        }

        if (DateUtils.isDateBeforeCurrent(date))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha es anterior a la actual",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtDate.requestFocus();
            return;
        }

        if (phone.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono es obligatorio",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPhone.requestFocus();
            return;
        }

        if (phone.length() < 10)
        {

            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono es inválido",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPhone.requestFocus();
            return;

        }

        if (email.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El correo electrónico es obligatorio",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtEmail.requestFocus();
            return;
        }

        if (!AccountDaoImpl.getInstance().validateEmailFormat(email))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El formato del correo electrónico es inválido",
                    StringRS.TITLE_DIALOG_INFO,
                    JOptionPane.ERROR_MESSAGE
            );
            txtEmail.requestFocus();
            return;
        }

        if (username.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El usuario es obligatorio",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtUsername.requestFocus();
            return;
        }

        if (password.isBlank())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña es obligatoria",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Las contraseñas no coinciden",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtConfirmPassword.requestFocus();
            return;
        }

        if (passwordStrengthStatus.getType() == 1)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña debe contener al menos\n10 caracteres, minúsculas y mayúsculas",
                    StringRS.TITLE_DIALOG_INFO,
                    JOptionPane.ERROR_MESSAGE
            );
            txtPassword.requestFocus();
            return;
        }

        if (employeeDao.isEmployeeExist(null, name, lastName))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del empleado ya esta registrado",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtName.requestFocus();
            return;
        }


        if (employeeDao.isEmailExist(null, email))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El correo electrónico ya esta registrado",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtEmail.requestFocus();
            return;
        }

        if (userDao.isUsernameExist(null,username))
        {
            JOptionPane.showMessageDialog(
                    this,
                    "El usuario ya esta registrado",
                    "Registro Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtUsername.requestFocus();
            return;
        }

        Date currentDate = new Date();
        EmployeeDTO employee = EmployeeDTO.builder()
                .name(name)
                .lastName(lastName)
                .admissionDate(DateUtils.convertedDateMySqlFormat(date))
                .phone(Long.parseLong(phone))
                .email(email)
                .build();

        UserDTO user = UserDTO.builder()
                .username(username)
                .password(password)
                .employee(employee)
                .role(role)
                .creationDate(currentDate)
                .modificationDate(currentDate)
                .available(available)
                .active(false)
                .build();

        UUID employeeId = employeeDao.save(employee);
        UUID userId = userDao.save(user);

        if (employeeId != null && userId != null)
        {
            dispose();
            Notifications.getInstance().show(
                    Notifications.Type.SUCCESS,
                    Notifications.Location.BOTTOM_RIGHT,
                    "Empleado registrado correctamente"
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
                        "Lista de empleados actualizada"
                );
            });
        }
    }

}
