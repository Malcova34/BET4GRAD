package com.unab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRole {
    private static final String GET_USER_ROLE = "SELECT r.nombre_rol FROM Roles r JOIN Usuarios u ON u.rol_id = r.rol_id WHERE u.email = ?";
    
    private static final String GET_ALL_USERS = "SELECT u.usuario_id AS id, u.nombre_completo AS nombre, u.email, u.carrera, u.semestre, r.nombre_rol AS rol, COALESCE(u.apunabs, 0) AS apunabs FROM Usuarios u JOIN Roles r ON u.rol_id = r.rol_id ORDER BY u.usuario_id";
    
    private static final String UPDATE_USER_ROLE = "UPDATE Usuarios SET rol_id = (SELECT rol_id FROM Roles WHERE nombre_rol = ?) WHERE email = ?";
    
    private static final String UPDATE_USER_STATUS = "UPDATE Usuarios SET activo = ? WHERE email = ?";
    
    private static final String GET_USER_DETAILS = "SELECT u.usuario_id AS id, u.nombre_completo AS nombre, u.email, u.carrera, u.semestre, r.nombre_rol AS rol, COALESCE(u.apunabs, 0) AS apunabs FROM Usuarios u JOIN Roles r ON u.rol_id = r.rol_id WHERE u.email = ?";

    private static final String GET_USER_ROLE_ID = "SELECT rol_id FROM Usuarios WHERE email = ?";

    private static final String INSERT_USER = "INSERT INTO Usuarios (nombre_completo, email, password_hash, carrera, semestre, rol_id, apunabs) VALUES (?, ?, ?, ?, ?, ?, 0)";
    private static final String UPDATE_USER = "UPDATE Usuarios SET nombre_completo = ?, email = ?, carrera = ?, semestre = ?, rol_id = ? WHERE usuario_id = ?";
    private static final String DELETE_USER = "DELETE FROM Usuarios WHERE email = ?";
    private static final String UPDATE_PASSWORD = "UPDATE Usuarios SET password_hash = ? WHERE email = ?";

    // Agregamos la consulta para eliminar tokens
    private static final String DELETE_PASSWORD_TOKENS = "DELETE FROM PasswordRecoveryTokens WHERE email = ?";

    public static String getUserRole(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_ROLE)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nombre_rol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "USER"; // Rol por defecto
    }

    public static boolean isAdmin(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_ROLE_ID)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("rol_id") == 2; // 2 es el ID para el rol de admin
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<UserDetails> getAllUsers() {
        List<UserDetails> users = new ArrayList<>();
        System.out.println("Ejecutando getAllUsers...");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_USERS)) {
            
            System.out.println("Query: " + GET_ALL_USERS);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                try {
                    UserDetails user = new UserDetails(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("carrera"),
                        rs.getString("semestre"),
                        rs.getString("rol"),
                        rs.getInt("apunabs")
                    );
                    users.add(user);
                    count++;
                } catch (SQLException e) {
                    System.err.println("Error procesando fila de usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("✓ Se cargaron " + count + " usuarios");
            
        } catch (SQLException e) {
            System.err.println("Error en getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public static boolean updateUserRole(String email, String newRole) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_ROLE)) {
            
            stmt.setString(1, newRole);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserDetails getUserDetails(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_DETAILS)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new UserDetails(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("carrera"),
                    rs.getString("semestre"),
                    rs.getString("rol"),
                    rs.getInt("apunabs")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addUser(String nombre, String email, String password, String carrera, String semestre, int rolId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setString(3, UserAuth.hashPassword(password));
            pstmt.setString(4, carrera);
            pstmt.setString(5, semestre);
            pstmt.setInt(6, rolId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUser(int userId, String nombre, String email, String carrera, String semestre, int rolId) {
        System.out.println("Updating user: ID=" + userId + ", Nombre=" + nombre + ", Email=" + email + ", Carrera=" + carrera + ", Semestre=" + semestre + ", RolID=" + rolId); // Log para depuración
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setString(3, carrera);
            pstmt.setString(4, semestre);
            pstmt.setInt(5, rolId);
            pstmt.setInt(6, userId);
            
            int rowsAffected = pstmt.executeUpdate(); // Capturar filas afectadas
            System.out.println("Update user rows affected: " + rowsAffected); // Log filas afectadas
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserPassword(String email, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PASSWORD)) {
            
            pstmt.setString(1, UserAuth.hashPassword(newPassword));
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(String email) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar tokens de PasswordRecoveryTokens
            try (PreparedStatement pstmtTokens = conn.prepareStatement(DELETE_PASSWORD_TOKENS)) {
                pstmtTokens.setString(1, email);
                pstmtTokens.executeUpdate(); // No necesitamos verificar las filas afectadas aquí necesariamente
            }

            // 2. Eliminar el usuario de Usuarios
            try (PreparedStatement pstmtUser = conn.prepareStatement(DELETE_USER)) {
                pstmtUser.setString(1, email);
                int rowsAffected = pstmtUser.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Confirmar transacción si el usuario fue eliminado
                    return true;
                } else {
                    conn.rollback(); // Revertir si no se encontró el usuario (o si DELETE_USER falla por alguna otra razón)
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir en caso de error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close(); // Cerrar conexión
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        String countSql = "SELECT COUNT(*) FROM Roles";
        String insertSql = "INSERT INTO Roles (rol_id, nombre_rol) VALUES (?, ?)";
        String selectSql = "SELECT nombre_rol FROM Roles ORDER BY rol_id";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if roles exist
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                ResultSet rs = countStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // If no roles exist, insert default ones
                    System.out.println("No roles found, inserting default roles.");
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, 1); // Assuming 1 is the ID for USER
                        insertStmt.setString(2, "USER");
                        insertStmt.executeUpdate();

                        insertStmt.setInt(1, 2); // Assuming 2 is the ID for ADMIN
                        insertStmt.setString(2, "ADMIN");
                        insertStmt.executeUpdate();
                         System.out.println("Default roles inserted.");
                    }
                }
            }

            // Now retrieve all roles (including the newly inserted ones if applicable)
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                 ResultSet rs = selectStmt.executeQuery();
                 while (rs.next()) {
                     roles.add(rs.getString("nombre_rol"));
                 }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public static int getRoleId(String roleName) {
        String sql = "SELECT rol_id FROM Roles WHERE nombre_rol = ?"; // Usar nombre_rol y rol_id
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("rol_id"); // Leer rol_id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Por defecto retorna el ID del rol USER (asumiendo que 1 es USER)
    }

    // Clase para almacenar los detalles del usuario
    public static class UserDetails {
        private final int id;
        private final String nombre;
        private final String email;
        private final String carrera;
        private final String semestre;
        private final String rol;
        private final int apunabs;

        public UserDetails(int id, String nombre, String email, String carrera, String semestre, String rol, int apunabs) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.carrera = carrera;
            this.semestre = semestre;
            this.rol = rol;
            this.apunabs = apunabs;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public String getCarrera() { return carrera; }
        public String getSemestre() { return semestre; }
        public String getRol() { return rol; }
        public int getApunabs() { return apunabs; }
    }
} 