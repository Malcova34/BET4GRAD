package com.unab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuth {
    
    public static boolean validateLogin(String email, String password) {
        String sql = "SELECT password_hash FROM Usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);
                return storedHash.equals(inputHash);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean registerUser(String nombreCompleto, String email, String password, String carrera) {
        String sql = "INSERT INTO Usuarios (nombre_completo, email, password_hash, carrera, rol_id, apunabs) VALUES (?, ?, ?, ?, 1, 0)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreCompleto);
            pstmt.setString(2, email);
            pstmt.setString(3, hashPassword(password));
            pstmt.setString(4, carrera);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
    
    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Métodos para manejar puntos Apunab
    public static int getUserApunabs(String email) {
        String sql = "SELECT COALESCE(apunabs, 0) as puntos FROM Usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("puntos");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static boolean addApunabs(String email, int puntos) {
        String sql = "UPDATE Usuarios SET apunabs = COALESCE(apunabs, 0) + ? WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, puntos);
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean subtractApunabs(String email, int puntos) {
        String sql = "UPDATE Usuarios SET apunabs = GREATEST(0, COALESCE(apunabs, 0) - ?) WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, puntos);
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean setApunabs(String email, int puntos) {
        String sql = "UPDATE Usuarios SET apunabs = ? WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Math.max(0, puntos)); // Asegurar que no sea negativo
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para inicializar la columna apunabs para usuarios existentes
    public static void initializeApunabsForExistingUsers() {
        String sql = "UPDATE Usuarios SET apunabs = 0 WHERE apunabs IS NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Inicializados puntos Apunab para " + rowsUpdated + " usuarios existentes.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para estadísticas de Apunabs
    public static double getApunabPromedioSemanal(String email) {
        // Calcular promedio basado en registros de eventos de la última semana
        String sql = "SELECT AVG(e.puntos_recompensa) as promedio " +
                    "FROM EventoRegistros er " +
                    "JOIN Eventos e ON er.evento_id = e.evento_id " +
                    "JOIN Usuarios u ON er.usuario_id = u.usuario_id " +
                    "WHERE u.email = ? AND er.fecha_registro >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                return rs.wasNull() ? 0.0 : promedio;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public static double getApunabPromedioMensual(String email) {
        // Calcular promedio basado en registros de eventos del último mes
        String sql = "SELECT AVG(e.puntos_recompensa) as promedio " +
                    "FROM EventoRegistros er " +
                    "JOIN Eventos e ON er.evento_id = e.evento_id " +
                    "JOIN Usuarios u ON er.usuario_id = u.usuario_id " +
                    "WHERE u.email = ? AND er.fecha_registro >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                return rs.wasNull() ? 0.0 : promedio;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public static double getApunabPromedioAnual(String email) {
        // Calcular promedio basado en registros de eventos del último año
        String sql = "SELECT AVG(e.puntos_recompensa) as promedio " +
                    "FROM EventoRegistros er " +
                    "JOIN Eventos e ON er.evento_id = e.evento_id " +
                    "JOIN Usuarios u ON er.usuario_id = u.usuario_id " +
                    "WHERE u.email = ? AND er.fecha_registro >= DATE_SUB(NOW(), INTERVAL 365 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                return rs.wasNull() ? 0.0 : promedio;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public static double getApunabPromedioSemestre(String email) {
        // Calcular promedio basado en registros de eventos de los últimos 6 meses (semestre)
        String sql = "SELECT AVG(e.puntos_recompensa) as promedio " +
                    "FROM EventoRegistros er " +
                    "JOIN Eventos e ON er.evento_id = e.evento_id " +
                    "JOIN Usuarios u ON er.usuario_id = u.usuario_id " +
                    "WHERE u.email = ? AND er.fecha_registro >= DATE_SUB(NOW(), INTERVAL 180 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                return rs.wasNull() ? 0.0 : promedio;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Método alternativo para calcular estadísticas basadas en el total actual
    public static class ApunabStats {
        public final double promedioSemanal;
        public final double promedioMensual;
        public final double promedioAnual;
        public final double promedioSemestre;
        
        public ApunabStats(double semanal, double mensual, double anual, double semestre) {
            this.promedioSemanal = semanal;
            this.promedioMensual = mensual;
            this.promedioAnual = anual;
            this.promedioSemestre = semestre;
        }
    }
    
    public static ApunabStats getApunabStatistics(String email) {
        int totalApunabs = getUserApunabs(email);
        
        // Si no hay datos históricos, calcular estimaciones basadas en el total actual
        // Asumiendo distribución uniforme en el tiempo
        double semanal = getApunabPromedioSemanal(email);
        double mensual = getApunabPromedioMensual(email);
        double anual = getApunabPromedioAnual(email);
        double semestre = getApunabPromedioSemestre(email);
        
        // Si no hay datos históricos, usar estimaciones simples
        if (semanal == 0.0 && mensual == 0.0 && anual == 0.0 && semestre == 0.0) {
            // Estimaciones basadas en el total actual (distribución uniforme)
            semanal = totalApunabs / 52.0; // Asumiendo 52 semanas de actividad
            mensual = totalApunabs / 12.0; // Asumiendo 12 meses de actividad
            anual = totalApunabs; // Total actual como promedio anual
            semestre = totalApunabs / 2.0; // Asumiendo 2 semestres
        }
        
        return new ApunabStats(semanal, mensual, anual, semestre);
    }
} 