package com.unab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActividadesDB {
    
    // Crear las tablas si no existen
    public static void initializeTables() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si la columna apunabs existe, si no, agregarla
            String checkColumnQuery = """
                SELECT COUNT(*) 
                FROM INFORMATION_SCHEMA.COLUMNS 
                WHERE TABLE_SCHEMA = DATABASE() 
                AND TABLE_NAME = 'Usuarios' 
                AND COLUMN_NAME = 'apunabs'
            """;
            
            String addApunabsColumn = "ALTER TABLE Usuarios ADD COLUMN apunabs INT DEFAULT 0";
            
            // Tabla de eventos
            String createEventosTable = """
                CREATE TABLE IF NOT EXISTS Eventos (
                    evento_id INT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(255) NOT NULL,
                    descripcion TEXT,
                    imagen_path VARCHAR(500),
                    puntos_recompensa INT DEFAULT 10,
                    fecha_evento DATE,
                    activo BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // Tabla de registros de usuarios en eventos
            String createRegistrosTable = """
                CREATE TABLE IF NOT EXISTS EventoRegistros (
                    registro_id INT AUTO_INCREMENT PRIMARY KEY,
                    usuario_email VARCHAR(255),
                    evento_id INT,
                    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_evento_id (evento_id),
                    UNIQUE KEY unique_registration (usuario_email, evento_id)
                )
            """;
            
            // Verificar y agregar columna apunabs si no existe
            try (PreparedStatement checkStmt = conn.prepareStatement(checkColumnQuery)) {
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // La columna no existe, agregarla
                    try (PreparedStatement alterStmt = conn.prepareStatement(addApunabsColumn)) {
                        alterStmt.executeUpdate();
                        System.out.println("✓ Columna 'apunabs' agregada a tabla Usuarios");
                    }
                } else {
                    System.out.println("✓ Columna 'apunabs' ya existe en tabla Usuarios");
                }
            } catch (SQLException e) {
                System.err.println("Error al verificar/agregar columna apunabs: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Crear tablas
            try (PreparedStatement stmt1 = conn.prepareStatement(createEventosTable);
                 PreparedStatement stmt2 = conn.prepareStatement(createRegistrosTable)) {
                
                stmt1.executeUpdate();
                System.out.println("✓ Tabla Eventos creada/verificada");
                
                stmt2.executeUpdate();
                System.out.println("✓ Tabla EventoRegistros creada/verificada");
                
                // Insertar eventos de ejemplo si la tabla está vacía
                insertSampleEventsIfEmpty(conn);
            }
            
        } catch (SQLException e) {
            System.err.println("Error en initializeTables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void insertSampleEventsIfEmpty(Connection conn) throws SQLException {
        String countQuery = "SELECT COUNT(*) FROM Eventos";
        try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
            ResultSet rs = countStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Insertar eventos de ejemplo
                String insertQuery = "INSERT INTO Eventos (titulo, descripcion, imagen_path, puntos_recompensa) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    // Evento 1
                    insertStmt.setString(1, "Torneo de Fútbol UNAB");
                    insertStmt.setString(2, "Participa en el torneo interuniversitario de fútbol");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/futbol.png");
                    insertStmt.setInt(4, 15);
                    insertStmt.executeUpdate();
                    
                    // Evento 2
                    insertStmt.setString(1, "Conferencia de Tecnología");
                    insertStmt.setString(2, "Aprende sobre las últimas tendencias en tecnología");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/conferencia.png");
                    insertStmt.setInt(4, 10);
                    insertStmt.executeUpdate();
                    
                    // Evento 3
                    insertStmt.setString(1, "Festival Cultural");
                    insertStmt.setString(2, "Celebra la diversidad cultural de nuestra universidad");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/cultura.png");
                    insertStmt.setInt(4, 12);
                    insertStmt.executeUpdate();
                    
                    // Evento 4
                    insertStmt.setString(1, "Hackathon UNAB 2024");
                    insertStmt.setString(2, "Demuestra tus habilidades de programación");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/hackathon.png");
                    insertStmt.setInt(4, 20);
                    insertStmt.executeUpdate();
                    
                    // Evento 5
                    insertStmt.setString(1, "Campaña de Voluntariado");
                    insertStmt.setString(2, "Ayuda a la comunidad con actividades de voluntariado");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/voluntariado.png");
                    insertStmt.setInt(4, 25);
                    insertStmt.executeUpdate();
                    
                    // Evento 6
                    insertStmt.setString(1, "Competencia de Robótica");
                    insertStmt.setString(2, "Diseña y programa robots para competir");
                    insertStmt.setString(3, "/com/unab/Actividades/icons/robotica.png");
                    insertStmt.setInt(4, 18);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
    
    public static List<Evento> getAllEventos() {
        List<Evento> eventos = new ArrayList<>();
        String query = "SELECT * FROM Eventos WHERE activo = TRUE ORDER BY evento_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento evento = new Evento(
                    rs.getInt("evento_id"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getString("imagen_path"),
                    rs.getInt("puntos_recompensa")
                );
                eventos.add(evento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }
    
    public static boolean isUserRegistered(String userEmail, int eventoId) {
        String query = "SELECT COUNT(*) FROM EventoRegistros WHERE usuario_email = ? AND evento_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, eventoId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean registerUserToEvent(String userEmail, int eventoId) {
        System.out.println("Intentando registrar usuario: " + userEmail + " en evento ID: " + eventoId);
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Verificar que el usuario existe
            String checkUserQuery = "SELECT COUNT(*) FROM Usuarios WHERE email = ?";
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery)) {
                checkUserStmt.setString(1, userEmail);
                ResultSet rs = checkUserStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Error: Usuario no encontrado: " + userEmail);
                    conn.rollback();
                    return false;
                }
            }
            
            // Verificar que el evento existe
            String checkEventQuery = "SELECT COUNT(*) FROM Eventos WHERE evento_id = ?";
            try (PreparedStatement checkEventStmt = conn.prepareStatement(checkEventQuery)) {
                checkEventStmt.setInt(1, eventoId);
                ResultSet rs = checkEventStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Error: Evento no encontrado: " + eventoId);
                    conn.rollback();
                    return false;
                }
            }
            
            // Verificar si ya está registrado
            if (isUserRegistered(userEmail, eventoId)) {
                System.err.println("Error: Usuario ya está registrado en este evento");
                conn.rollback();
                return false;
            }
            
            // Insertar registro
            String insertQuery = "INSERT INTO EventoRegistros (usuario_email, evento_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, userEmail);
                insertStmt.setInt(2, eventoId);
                int rowsInserted = insertStmt.executeUpdate();
                System.out.println("Registros insertados en EventoRegistros: " + rowsInserted);
            }
            
            // Obtener puntos del evento
            String getPuntosQuery = "SELECT puntos_recompensa FROM Eventos WHERE evento_id = ?";
            int puntos = 0;
            try (PreparedStatement getPuntosStmt = conn.prepareStatement(getPuntosQuery)) {
                getPuntosStmt.setInt(1, eventoId);
                ResultSet rs = getPuntosStmt.executeQuery();
                if (rs.next()) {
                    puntos = rs.getInt("puntos_recompensa");
                    System.out.println("Puntos a agregar: " + puntos);
                }
            }
            
            // Actualizar puntos Apunab del usuario en la tabla Usuarios
            String updatePuntosQuery = "UPDATE Usuarios SET apunabs = COALESCE(apunabs, 0) + ? WHERE email = ?";
            try (PreparedStatement updatePuntosStmt = conn.prepareStatement(updatePuntosQuery)) {
                updatePuntosStmt.setInt(1, puntos);
                updatePuntosStmt.setString(2, userEmail);
                int rowsUpdated = updatePuntosStmt.executeUpdate();
                System.out.println("Filas actualizadas en Usuarios: " + rowsUpdated);
                
                if (rowsUpdated > 0) {
                    conn.commit();
                    System.out.println("✓ Usuario registrado en evento. +" + puntos + " Apunabs agregados.");
                    return true;
                } else {
                    conn.rollback();
                    System.err.println("Error: No se pudo actualizar los puntos del usuario.");
                    return false;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error en registerUserToEvent: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static boolean unregisterUserFromEvent(String userEmail, int eventoId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Obtener puntos del evento antes de eliminar
            String getPuntosQuery = "SELECT puntos_recompensa FROM Eventos WHERE evento_id = ?";
            int puntos = 0;
            try (PreparedStatement getPuntosStmt = conn.prepareStatement(getPuntosQuery)) {
                getPuntosStmt.setInt(1, eventoId);
                ResultSet rs = getPuntosStmt.executeQuery();
                if (rs.next()) {
                    puntos = rs.getInt("puntos_recompensa");
                }
            }
            
            // Eliminar registro
            String deleteQuery = "DELETE FROM EventoRegistros WHERE usuario_email = ? AND evento_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, userEmail);
                deleteStmt.setInt(2, eventoId);
                int rowsAffected = deleteStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Restar puntos Apunab del usuario en la tabla Usuarios
                    String updatePuntosQuery = "UPDATE Usuarios SET apunabs = GREATEST(0, COALESCE(apunabs, 0) - ?) WHERE email = ?";
                    try (PreparedStatement updatePuntosStmt = conn.prepareStatement(updatePuntosQuery)) {
                        updatePuntosStmt.setInt(1, puntos);
                        updatePuntosStmt.setString(2, userEmail);
                        updatePuntosStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    System.out.println("Usuario desregistrado del evento. -" + puntos + " Apunabs descontados.");
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static int getUserApunabPoints(String userEmail) {
        String query = "SELECT COALESCE(apunabs, 0) as puntos FROM Usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int puntos = rs.getInt("puntos");
                System.out.println("Puntos Apunab para " + userEmail + ": " + puntos);
                return puntos;
            } else {
                System.out.println("Usuario no encontrado: " + userEmail + ", retornando 0 puntos");
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Error en getUserApunabPoints: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Método adicional para agregar/quitar puntos Apunab directamente
    public static boolean updateUserApunabs(String userEmail, int puntosAgregar) {
        String query = "UPDATE Usuarios SET apunabs = GREATEST(0, COALESCE(apunabs, 0) + ?) WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, puntosAgregar);
            stmt.setString(2, userEmail);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para obtener el ranking de usuarios por puntos Apunab
    public static List<UserApunabRanking> getUserApunabRanking(int limit) {
        List<UserApunabRanking> ranking = new ArrayList<>();
        String query = """
            SELECT nombre_completo, email, COALESCE(apunabs, 0) as puntos 
            FROM Usuarios 
            WHERE COALESCE(apunabs, 0) > 0 
            ORDER BY apunabs DESC, nombre_completo ASC 
            LIMIT ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            int posicion = 1;
            while (rs.next()) {
                UserApunabRanking user = new UserApunabRanking(
                    posicion++,
                    rs.getString("nombre_completo"),
                    rs.getString("email"),
                    rs.getInt("puntos")
                );
                ranking.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranking;
    }
    
    public static int getUserEventCount(String userEmail) {
        String sql = "SELECT COUNT(*) FROM EventoRegistros WHERE usuario_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en getUserEventCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Métodos CRUD para eventos
    public static boolean createEvento(String titulo, String descripcion, String imagenPath, int puntosRecompensa) {
        String sql = "INSERT INTO Eventos (titulo, descripcion, imagen_path, puntos_recompensa) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titulo);
            stmt.setString(2, descripcion);
            stmt.setString(3, imagenPath);
            stmt.setInt(4, puntosRecompensa);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Evento creado: " + titulo + " (filas afectadas: " + rowsAffected + ")");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creando evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateEvento(int eventoId, String titulo, String descripcion, String imagenPath, int puntosRecompensa) {
        String sql = "UPDATE Eventos SET titulo = ?, descripcion = ?, imagen_path = ?, puntos_recompensa = ? WHERE evento_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titulo);
            stmt.setString(2, descripcion);
            stmt.setString(3, imagenPath);
            stmt.setInt(4, puntosRecompensa);
            stmt.setInt(5, eventoId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Evento actualizado: ID " + eventoId + " (filas afectadas: " + rowsAffected + ")");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteEvento(int eventoId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Primero eliminar todos los registros de usuarios para este evento
            String deleteRegistrosSQL = "DELETE FROM EventoRegistros WHERE evento_id = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(deleteRegistrosSQL)) {
                stmt1.setInt(1, eventoId);
                int registrosEliminados = stmt1.executeUpdate();
                System.out.println("Registros de evento eliminados: " + registrosEliminados);
            }
            
            // 2. Luego eliminar el evento
            String deleteEventoSQL = "DELETE FROM Eventos WHERE evento_id = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(deleteEventoSQL)) {
                stmt2.setInt(1, eventoId);
                int eventosEliminados = stmt2.executeUpdate();
                
                if (eventosEliminados > 0) {
                    conn.commit(); // Confirmar transacción
                    System.out.println("Evento eliminado exitosamente: ID " + eventoId);
                    return true;
                } else {
                    conn.rollback(); // Revertir si no se encontró el evento
                    System.out.println("No se encontró el evento con ID: " + eventoId);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error eliminando evento: " + e.getMessage());
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
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static Evento getEventoById(int eventoId) {
        String sql = "SELECT evento_id, titulo, descripcion, imagen_path, puntos_recompensa FROM Eventos WHERE evento_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Evento(
                    rs.getInt("evento_id"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getString("imagen_path"),
                    rs.getInt("puntos_recompensa")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo evento por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Clase para representar un evento
    public static class Evento {
        private final int id;
        private final String titulo;
        private final String descripcion;
        private final String imagenPath;
        private final int puntosRecompensa;
        
        public Evento(int id, String titulo, String descripcion, String imagenPath, int puntosRecompensa) {
            this.id = id;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.imagenPath = imagenPath;
            this.puntosRecompensa = puntosRecompensa;
        }
        
        // Getters
        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getDescripcion() { return descripcion; }
        public String getImagenPath() { return imagenPath; }
        public int getPuntosRecompensa() { return puntosRecompensa; }
    }
    
    // Clase para representar el ranking de usuarios por Apunabs
    public static class UserApunabRanking {
        private final int posicion;
        private final String nombre;
        private final String email;
        private final int puntos;
        
        public UserApunabRanking(int posicion, String nombre, String email, int puntos) {
            this.posicion = posicion;
            this.nombre = nombre;
            this.email = email;
            this.puntos = puntos;
        }
        
        // Getters
        public int getPosicion() { return posicion; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public int getPuntos() { return puntos; }
    }
    
    // Métodos para el sistema de tutorías
    public static void initializeTutoriasTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Crear tabla de Tutorias
            String createTutoriasTable = """
                CREATE TABLE IF NOT EXISTS Tutorias (
                    tutoria_id INT AUTO_INCREMENT PRIMARY KEY,
                    materia VARCHAR(100) NOT NULL,
                    profesor VARCHAR(100) NOT NULL,
                    horario VARCHAR(50) NOT NULL,
                    salon VARCHAR(20) NOT NULL,
                    sede VARCHAR(50) NOT NULL,
                    descripcion TEXT,
                    puntos_recompensa INT DEFAULT 5,
                    cupos_disponibles INT DEFAULT 15,
                    imagen_path VARCHAR(255) DEFAULT '/com/unab/Tutorias/images/default.jpg'
                )""";
            
            try (PreparedStatement stmt = conn.prepareStatement(createTutoriasTable)) {
                stmt.executeUpdate();
                System.out.println("✓ Tabla Tutorias creada/verificada");
            }
            
            // Crear tabla de TutoriaRegistros
            String createTutoriaRegistrosTable = """
                CREATE TABLE IF NOT EXISTS TutoriaRegistros (
                    registro_id INT AUTO_INCREMENT PRIMARY KEY,
                    usuario_email VARCHAR(255) NOT NULL,
                    tutoria_id INT NOT NULL,
                    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_usuario_tutoria (usuario_email, tutoria_id)
                )""";
            
            try (PreparedStatement stmt = conn.prepareStatement(createTutoriaRegistrosTable)) {
                stmt.executeUpdate();
                System.out.println("✓ Tabla TutoriaRegistros creada/verificada");
            }
            
            // Insertar tutorías de ejemplo si no existen
            insertSampleTutorias();
            
        } catch (SQLException e) {
            System.err.println("Error inicializando tablas de tutorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void insertSampleTutorias() {
        String checkSql = "SELECT COUNT(*) FROM Tutorias";
        String insertSql = """
            INSERT INTO Tutorias (materia, profesor, horario, salon, sede, descripcion, puntos_recompensa, imagen_path) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si ya hay tutorías
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✓ Las tutorías de ejemplo ya existen");
                    return;
                }
            }
            
            // Tutorías de ejemplo
            Object[][] tutorias = {
                {"Cálculo I", "Dr. María González", "Lunes 2:00-4:00 PM", "A201", "Campus Principal", 
                 "Tutoría de refuerzo en límites, derivadas y aplicaciones del cálculo diferencial", 8, 
                 "/com/unab/Tutorias/images/calculo.jpg"},
                
                {"Programación Java", "Ing. Carlos Rodríguez", "Martes 10:00-12:00 PM", "L301", "Campus Principal", 
                 "Refuerzo en programación orientada a objetos, estructuras de datos y algoritmos", 10, 
                 "/com/unab/Tutorias/images/java.jpg"},
                
                {"Física Mecánica", "Dr. Ana Martínez", "Miércoles 3:00-5:00 PM", "F102", "Campus Principal", 
                 "Resolución de problemas de cinemática, dinámica y trabajo-energía", 8, 
                 "/com/unab/Tutorias/images/fisica.jpg"},
                
                {"Química Orgánica", "Dra. Luis Torres", "Jueves 8:00-10:00 AM", "Q205", "Campus Salud", 
                 "Mecanismos de reacción, síntesis orgánica y nomenclatura IUPAC", 9, 
                 "/com/unab/Tutorias/images/quimica.jpg"},
                
                {"Contabilidad Financiera", "CPA. Sandra López", "Viernes 1:00-3:00 PM", "E301", "Campus Principal", 
                 "Estados financieros, análisis de ratios y principios contables", 7, 
                 "/com/unab/Tutorias/images/contabilidad.jpg"},
                
                {"Anatomía Humana", "Dr. Roberto Silva", "Sábado 9:00-11:00 AM", "M101", "Campus Salud", 
                 "Estudio de sistemas corporales, anatomía macroscópica y microscópica", 9, 
                 "/com/unab/Tutorias/images/anatomia.jpg"},
                
                {"Inglés Intermedio", "Prof. Jennifer White", "Lunes 4:00-6:00 PM", "I202", "Campus Principal", 
                 "Conversación, gramática avanzada y preparación para exámenes internacionales", 6, 
                 "/com/unab/Tutorias/images/ingles.jpg"},
                
                {"Estadística Aplicada", "Dr. Miguel Hernández", "Miércoles 10:00-12:00 PM", "M205", "Campus Principal", 
                 "Análisis estadístico, probabilidad y uso de software estadístico", 8, 
                 "/com/unab/Tutorias/images/estadistica.jpg"},
                
                {"Derecho Constitucional", "Abg. Patricia Ruiz", "Martes 2:00-4:00 PM", "D101", "Campus Principal", 
                 "Análisis de la Constitución, derechos fundamentales y jurisprudencia", 7, 
                 "/com/unab/Tutorias/images/derecho.jpg"}
            };
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Object[] tutoria : tutorias) {
                    insertStmt.setString(1, (String) tutoria[0]); // materia
                    insertStmt.setString(2, (String) tutoria[1]); // profesor
                    insertStmt.setString(3, (String) tutoria[2]); // horario
                    insertStmt.setString(4, (String) tutoria[3]); // salon
                    insertStmt.setString(5, (String) tutoria[4]); // sede
                    insertStmt.setString(6, (String) tutoria[5]); // descripcion
                    insertStmt.setInt(7, (Integer) tutoria[6]);   // puntos_recompensa
                    insertStmt.setString(8, (String) tutoria[7]); // imagen_path
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                System.out.println("✓ Tutorías de ejemplo insertadas exitosamente");
            }
            
        } catch (SQLException e) {
            System.err.println("Error insertando tutorías de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Clase para representar una tutoría
    public static class Tutoria {
        private final int id;
        private final String materia;
        private final String profesor;
        private final String horario;
        private final String salon;
        private final String sede;
        private final String descripcion;
        private final int puntosRecompensa;
        private final int cuposDisponibles;
        private final String imagenPath;
        
        public Tutoria(int id, String materia, String profesor, String horario, String salon, 
                      String sede, String descripcion, int puntosRecompensa, int cuposDisponibles, String imagenPath) {
            this.id = id;
            this.materia = materia;
            this.profesor = profesor;
            this.horario = horario;
            this.salon = salon;
            this.sede = sede;
            this.descripcion = descripcion;
            this.puntosRecompensa = puntosRecompensa;
            this.cuposDisponibles = cuposDisponibles;
            this.imagenPath = imagenPath;
        }
        
        // Getters
        public int getId() { return id; }
        public String getMateria() { return materia; }
        public String getProfesor() { return profesor; }
        public String getHorario() { return horario; }
        public String getSalon() { return salon; }
        public String getSede() { return sede; }
        public String getDescripcion() { return descripcion; }
        public int getPuntosRecompensa() { return puntosRecompensa; }
        public int getCuposDisponibles() { return cuposDisponibles; }
        public String getImagenPath() { return imagenPath; }
    }
    
    // Obtener todas las tutorías
    public static List<Tutoria> getAllTutorias() {
        List<Tutoria> tutorias = new ArrayList<>();
        String sql = """
            SELECT tutoria_id, materia, profesor, horario, salon, sede, descripcion, 
                   puntos_recompensa, cupos_disponibles, imagen_path 
            FROM Tutorias 
            ORDER BY materia""";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tutorias.add(new Tutoria(
                    rs.getInt("tutoria_id"),
                    rs.getString("materia"),
                    rs.getString("profesor"),
                    rs.getString("horario"),
                    rs.getString("salon"),
                    rs.getString("sede"),
                    rs.getString("descripcion"),
                    rs.getInt("puntos_recompensa"),
                    rs.getInt("cupos_disponibles"),
                    rs.getString("imagen_path")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo tutorías: " + e.getMessage());
            e.printStackTrace();
        }
        return tutorias;
    }
    
    // Verificar si un usuario está registrado en una tutoría
    public static boolean isUserRegisteredToTutoria(String userEmail, int tutoriaId) {
        String sql = "SELECT COUNT(*) FROM TutoriaRegistros WHERE usuario_email = ? AND tutoria_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userEmail);
            stmt.setInt(2, tutoriaId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando registro de tutoría: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Registrar usuario en una tutoría
    public static boolean registerUserToTutoria(String userEmail, int tutoriaId) {
        System.out.println("Registrando usuario en tutoría: " + userEmail + " -> ID: " + tutoriaId);
        
        // Verificar si ya está registrado
        if (isUserRegisteredToTutoria(userEmail, tutoriaId)) {
            System.out.println("Usuario ya registrado en esta tutoría");
            return false;
        }
        
        String insertSql = "INSERT INTO TutoriaRegistros (usuario_email, tutoria_id) VALUES (?, ?)";
        String getTutoriaSql = "SELECT puntos_recompensa FROM Tutorias WHERE tutoria_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Obtener puntos de la tutoría
                int puntos = 0;
                try (PreparedStatement getTutoriaStmt = conn.prepareStatement(getTutoriaSql)) {
                    getTutoriaStmt.setInt(1, tutoriaId);
                    ResultSet rs = getTutoriaStmt.executeQuery();
                    if (rs.next()) {
                        puntos = rs.getInt("puntos_recompensa");
                    }
                }
                
                // Insertar registro
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, userEmail);
                    insertStmt.setInt(2, tutoriaId);
                    insertStmt.executeUpdate();
                }
                
                // Agregar puntos Apunab
                if (puntos > 0) {
                    UserAuth.addApunabs(userEmail, puntos);
                }
                
                conn.commit();
                System.out.println("✓ Usuario registrado exitosamente en tutoría. Puntos agregados: " + puntos);
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error registrando usuario en tutoría: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Desregistrar usuario de una tutoría
    public static boolean unregisterUserFromTutoria(String userEmail, int tutoriaId) {
        String deleteSql = "DELETE FROM TutoriaRegistros WHERE usuario_email = ? AND tutoria_id = ?";
        String getTutoriaSql = "SELECT puntos_recompensa FROM Tutorias WHERE tutoria_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Obtener puntos de la tutoría
                int puntos = 0;
                try (PreparedStatement getTutoriaStmt = conn.prepareStatement(getTutoriaSql)) {
                    getTutoriaStmt.setInt(1, tutoriaId);
                    ResultSet rs = getTutoriaStmt.executeQuery();
                    if (rs.next()) {
                        puntos = rs.getInt("puntos_recompensa");
                    }
                }
                
                // Eliminar registro
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, userEmail);
                    deleteStmt.setInt(2, tutoriaId);
                    int rowsAffected = deleteStmt.executeUpdate();
                    
                    if (rowsAffected == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Restar puntos Apunab
                if (puntos > 0) {
                    UserAuth.subtractApunabs(userEmail, puntos);
                }
                
                conn.commit();
                System.out.println("✓ Usuario desregistrado exitosamente de tutoría. Puntos descontados: " + puntos);
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("Error desregistrando usuario de tutoría: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 