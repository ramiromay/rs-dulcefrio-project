package com.realssoft.dulcefrio;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class prueba
{

    private static String emailFrom = "suppoort.dulcefrio@gmail.com";
    private static String passwordFrom = "mabp gmky juvh cezf";
    private String emailTo;
    private String subject;
    private String content;

    private Properties mProperties;
    private Session mSession;
    private MimeMessage mCorreo;

    public static void main(String[] args) {
        prueba prueba = new prueba();
        prueba.createEmail();
        prueba.sendEmail();

    }
    private void createEmail() {
        emailTo = "ramiromay1@gmail.com";
        content = "<div style=\"\n" +
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
                "            <p>Hola, Ramiro May :</p>\n" +
                "            <p>Hemos recibido una solicitud para restablecer la contraseña asociada a tu cuenta en DulceFrío. Tu nueva contraseña es:\n" +
                "            </p>\n" +
                "            <p style=\"\n" +
                "            text-align: center; \n" +
                "            font-size: 20px;\n" +
                "            \"><strong>Password</strong></p>\n" +
                "            <p><strong>No reenvíes este correo electrónico ni des tu contraseña a nadie.</strong> Has recibido este mensaje porque esta dirección de correo electrónico figura como dirección principal de su cuenta de DulceFrío.</p>\n" +
                "            <p>Atentamente,</p>\n" +
                "            <p>El equipo de DulceFrío</p>\n" +
                "            <br>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    </div>";
        subject = "Recuperación de Contraseña para DulceFrío";

        mProperties = new Properties();
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user",emailFrom);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");
        mSession = Session.getDefaultInstance(mProperties);

        try {
            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            mCorreo.setSubject(subject);
            mCorreo.setText(content, "ISO-8859-1", "html");
        } catch (AddressException ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
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

            JOptionPane.showMessageDialog(null, "Correo enviado");
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
