package com.unab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordRecovery {
    private static final Logger logger = LoggerFactory.getLogger(PasswordRecovery.class);
    private static final String EMAIL_USERNAME = "bet4grad@gmail.com";
    private static final String EMAIL_PASSWORD = "fhgr awbv mijt doth";
    
    public static boolean requestPasswordRecovery(String email) {
        logger.info("Iniciando proceso de recuperación de contraseña para: {}", email);
        
        // Generar token único
        String token = UUID.randomUUID().toString();
        // Token expira en 1 hora
        Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 3600000);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si el usuario existe
            if (!UserAuth.emailExists(email)) {
                logger.warn("Intento de recuperación para email no registrado: {}", email);
                return false;
            }
            
            logger.debug("Usuario encontrado, procediendo con la recuperación");
            
            // Eliminar tokens anteriores para este usuario
            String deleteSql = "DELETE FROM PasswordRecoveryTokens WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, email);
                int deleted = pstmt.executeUpdate();
                logger.debug("Tokens anteriores eliminados: {}", deleted);
            }
            
            // Insertar nuevo token
            String insertSql = "INSERT INTO PasswordRecoveryTokens (email, token, expiry_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, token);
                pstmt.setTimestamp(3, expiryDate);
                int inserted = pstmt.executeUpdate();
                if (inserted == 0) {
                    logger.error("No se pudo insertar el token de recuperación");
                    return false;
                }
                logger.debug("Nuevo token insertado exitosamente");
            }
            
            // Enviar correo electrónico
            boolean emailSent = sendRecoveryEmail(email, token);
            if (!emailSent) {
                logger.error("No se pudo enviar el correo de recuperación");
                // Intentar limpiar el token si falló el envío
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setString(1, email);
                    pstmt.executeUpdate();
                }
                return false;
            }
            
            logger.info("Proceso de recuperación completado exitosamente para: {}", email);
            return true;
            
        } catch (SQLException e) {
            logger.error("Error en la base de datos durante la recuperación: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado durante la recuperación: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public static boolean resetPassword(String token, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar token y obtener email
            String selectSql = "SELECT email FROM PasswordRecoveryTokens WHERE token = ? AND expiry_date > ? AND used = false";
            String email = null;
            
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setString(1, token);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    email = rs.getString("email");
                } else {
                    return false; // Token inválido o expirado
                }
            }
            
            // Actualizar contraseña
            String updateSql = "UPDATE Usuarios SET password_hash = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, UserAuth.hashPassword(newPassword));
                pstmt.setString(2, email);
                pstmt.executeUpdate();
            }
            
            // Marcar token como usado
            String markUsedSql = "UPDATE PasswordRecoveryTokens SET used = true WHERE token = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(markUsedSql)) {
                pstmt.setString(1, token);
                pstmt.executeUpdate();
            }
            
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean sendRecoveryEmail(String email, String token) {
        logger.debug("Preparando envío de correo a: {}", email);
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        try {
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Habilitar debug para ver más detalles del proceso de envío
            session.setDebug(true);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "BET4GRAD"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Recuperación de Contraseña - BET4GRAD");
            
            String recoveryLink = "http://localhost:8080/reset-password?token=" + token;
            String emailContent = String.format(
                "Hola,\n\n" +
                "Has solicitado recuperar tu contraseña en BET4GRAD.\n\n" +
                "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" +
                "%s\n\n" +
                "Este enlace expirará en 1 hora.\n\n" +
                "Si no solicitaste este cambio, por favor ignora este correo.\n\n" +
                "Saludos,\n" +
                "Equipo BET4GRAD", recoveryLink);
            
            message.setText(emailContent);
            
            logger.debug("Intentando enviar correo...");
            Transport.send(message);
            logger.info("Correo enviado exitosamente a: {}", email);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Error al enviar el correo: {}", e.getMessage(), e);
            if (e.getMessage().contains("Authentication unsuccessful")) {
                logger.error("Error de autenticación. Por favor verifica que:\n" +
                           "1. La contraseña sea una 'Contraseña de aplicación' de Google\n" +
                           "2. La verificación en dos pasos esté activada en la cuenta de Google\n" +
                           "3. La cuenta de correo tenga habilitado el acceso de aplicaciones menos seguras");
            }
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al enviar el correo: {}", e.getMessage(), e);
            return false;
        }
    }
} 