package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.api.model.dto.AccountDTO;
import com.realssoft.dulcefrio.api.model.dto.AccountResponse;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.prueba;
import com.realssoft.dulcefrio.ui.dialog.ProgressDialog;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;


import java.awt.Window;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.swing.JOptionPane;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDaoImpl
{

    private static final AccountDaoImpl INSTANCE = new AccountDaoImpl();
    private static final String CARACTERES_MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CARACTERES_MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String CARACTERES_NUMEROS = "0123456789";
    private static final String CARACTERES_ESPECIALES = "@#$%&*()-_=+[]{}|;:'\",.<>/?";

    private static final String emailFrom = "suppoort.dulcefrio@gmail.com";
    private static final String passwordFrom = "mabp gmky juvh cezf";

    private Session mSession;
    private MimeMessage mCorreo;

    private AccountDaoImpl()
    {}


    public static AccountDaoImpl getInstance()
    {
        return INSTANCE;
    }

    public AccountResponse login(boolean rememberMe,AccountDTO account)
    {
        String username = account.username();
        String password = hashPassword(account.password());

        try (EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT e FROM User e " +
                            "WHERE e.username = :username AND e.password = :password",
                    User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);

            User user = query.getSingleResult();
            entityManager.getTransaction().begin();
            if (rememberMe)
            {
                user.setToken(generateToken(username));
            }
            user.setActive(true);
            entityManager.getTransaction().commit();
            return new AccountResponse(user.getId(),user.getToken(), Objects.equals(user.getRole().getName(), "Administrador"));
        }
        catch (IllegalStateException | PersistenceException e)
        {
            return null;
        }
    }

    public Long getCountUsers()
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(e) FROM User e",
                    Long.class
            );
            return query.getSingleResult();
        }
        catch (IllegalStateException | PersistenceException e)
        {
            return null;
        }
    }

    public boolean isTokenValid(UUID id, String token)
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            User user = entityManager.find(User.class, id);
            if (user.getToken() == null) return false;
            return Objects.equals(user.getToken(), token);
        }
        catch (IllegalStateException | PersistenceException e)
        {
            return false;
        }
    }

    public boolean isAdmin(UUID id)
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            User user = entityManager.find(User.class, id);
            return Objects.equals(user.getRole().getName(), "Administrador");
        }
        catch (IllegalStateException | PersistenceException e)
        {
            return false;
        }
    }

    public void logout(UUID id)
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            entityManager.getTransaction().begin();
            User user = entityManager.find(User.class, id);
            user.setActive(false);
            user.setToken(null);
            entityManager.getTransaction().commit();
        }
        catch (IllegalStateException | PersistenceException e)
        {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().substring(0, 50);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateToken(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee isExistEmail(String email)
    {
        try(EntityManager  entityManager = HibernateUtils.getEntityManager())
        {
            TypedQuery<Employee> query = entityManager.createQuery(
                    "SELECT e FROM Employee e WHERE e.email = :email",
                    Employee.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private void updatePassword(UUID id, String newPassword)
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager()) {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT e FROM User e WHERE e.employee.id = :id",
                    User.class
            );
            query.setParameter("id", id);
            User user = query.getSingleResult();
            entityManager.getTransaction().begin();
            user.setPassword(newPassword);
            entityManager.getTransaction().commit();
        }
        catch (IllegalStateException | PersistenceException e) {
            e.printStackTrace();
        }
    }

    public void modifyPassword(String recipientEmail, Window w, ProgressDialog process)
    {
        if (validateEmailFormat(recipientEmail))
        {
            String newPassword = generadorContrasena();
            String newHashPassword = hashPassword(newPassword);
            Employee employee = isExistEmail(recipientEmail);

            if (employee != null)
            {
                UUID id = employee.getId();
                updatePassword(id, newHashPassword);
                createEmail(
                        recipientEmail,
                        employee.getName() + " " + employee.getLastName(),
                        newPassword
                );
                sendEmail();
                process.dispose();

                JOptionPane.showMessageDialog(
                        w,
                        "El correo con la nueva contraseña \nse envió correctamente.",
                        "Correo Enviado",
                        JOptionPane.INFORMATION_MESSAGE
                );
                w.dispose();
            }
            else
            {
                process.dispose();
                JOptionPane.showMessageDialog(
                        w,
                        "No se encontró ningún empleado con ese\ncorreo electrónico.",
                        StringRS.TITLE_DIALOG_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        else
        {
            process.dispose();
            JOptionPane.showMessageDialog(
                    w,
                    "El correo electrónico no es válido.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }

    private void createEmail(String recipientEmail, String name, String password) {
        String content = "<div style=\"\n" +
                "    width: 50%; \n" +
                "    margin: auto;\n" +
                "    box-sizing: border-box; \n" +
                "    font-family: Helvetica, sans-serif; \n" +
                "    line-height: 24px;\">\n" +
                "        <div style=\"\n" +
                "        display: flex;\n" +
                "        flex-direction: column-reverse;\n" +
                "        padding: 5px 5px 5px 5px;\n" +
                "        \">\n" +
                "        <img src=\"https://firebasestorage.googleapis.com/v0/b/gasfull-1a846.appspot.com/o/michoacana-logo.png?alt=media&token=7493aa36-0b3a-42fd-bb53-6da913bed508\" alt=\"\" style=\"\n" +
                "        width: 30px; \n" +
                "        height: 30px;\n" +
                "        margin-top: 10px;\n" +
                "        \">\n" +
                "        <p style=\" \n" +
                "        width: 200px;\n" +
                "        margin-left: 10px;\n" +
                "        font-size: 14px;\n" +
                "        \">Dulce Frío</p>\n" +
                "    </div>\n" +
                "    <div style=\"\n" +
                "    color: black;\n" +
                "    border: 1px solid #dfdfdf;\">\n" +
                "        <div style=\"\n" +
                "        background-color: #0085f7;\n" +
                "        color: white;\n" +
                "        font-size: 12px;\n" +
                "        padding: 15px 20px;\n" +
                "        padding-top: 70px;\">\n" +
                "            <h1>Recuperar contraseña para DulceFrío</h1>\n" +
                "        </div>\n" +
                "        <div style=\"\n" +
                "        padding: 10px 20px;\n" +
                "        background-color: #fafafa;\">\n" +
                "            <p>Hola, " + name + ":</p>\n" +
                "            <p>Hemos recibido una solicitud para restablecer la contraseña asociada a tu cuenta en DulceFrío. Tu nueva contraseña es:\n" +
                "            </p>\n" +
                "            <p style=\"\n" +
                "            text-align: center; \n" +
                "            font-size: 20px;\n" +
                "            \"><strong>"+ password +"</strong></p>\n" +
                "            <p><strong>No reenvíes este correo electrónico ni des tu contraseña a nadie.</strong> Has recibido este mensaje porque esta dirección de correo electrónico figura como dirección principal de su cuenta de DulceFrío.</p>\n" +
                "            <p>Atentamente,</p>\n" +
                "            <p>El equipo de DulceFrío</p>\n" +
                "            <br>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    </div>";
        String subject = "Recuperar contraseña para DulceFrío";
        Properties mProperties = new Properties();
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user",recipientEmail);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");

        mSession = Session.getDefaultInstance(mProperties);

        try {
            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            mCorreo.setSubject(subject);
            mCorreo.setText(content, "ISO-8859-1", "html");
        } catch (MessagingException ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendEmail() {
        try {
            Transport mTransport = mSession.getTransport("smtp");
            mTransport.connect(emailFrom, passwordFrom);
            mTransport.sendMessage(mCorreo, mCorreo.getRecipients(Message.RecipientType.TO));
            mTransport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String generadorContrasena(){
        StringBuilder contrasena = new StringBuilder();

        contrasena.append(obtenerCaracterAleatorio(CARACTERES_MAYUSCULAS));
        contrasena.append(obtenerCaracterAleatorio(CARACTERES_MINUSCULAS));
        contrasena.append(obtenerCaracterAleatorio(CARACTERES_NUMEROS));
        contrasena.append(obtenerCaracterAleatorio(CARACTERES_ESPECIALES));

        for (int i = 4; i < 8; i++) {
            String conjuntoCaracteres = CARACTERES_MAYUSCULAS + CARACTERES_MINUSCULAS + CARACTERES_NUMEROS + CARACTERES_ESPECIALES;
            contrasena.append(obtenerCaracterAleatorio(conjuntoCaracteres));
        }
        return mezclarContrasena(contrasena.toString());
    }

    private char obtenerCaracterAleatorio(String conjunto) {
        int indice = new SecureRandom().nextInt(conjunto.length());
        return conjunto.charAt(indice);
    }

    private String mezclarContrasena(String contrasena) {
        char[] caracteres = contrasena.toCharArray();
        for (int i = caracteres.length - 1; i > 0; i--) {
            int indiceAleatorio = new SecureRandom().nextInt(i + 1);

            char temp = caracteres[i];
            caracteres[i] = caracteres[indiceAleatorio];
            caracteres[indiceAleatorio] = temp;
        }
        return new String(caracteres);
    }


    public boolean validateEmailFormat(String correo) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

}
