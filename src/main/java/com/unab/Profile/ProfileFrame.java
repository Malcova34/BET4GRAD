package com.unab.Profile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Admin.AdminDashboard;
import com.unab.Home.HomeFrame;
import com.unab.Login.LoginFrame;
import com.unab.database.ActividadesDB;
import com.unab.database.UserAuth;
import com.unab.database.UserRole;
import com.unab.utils.EmojiUtils;

public class ProfileFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PANEL_BG = new Color(13, 27, 51);
    private static final Color CARD_BG = new Color(42, 60, 88);
    
    private JPanel mainPanel;
    private String currentUserEmail = ""; 

    public ProfileFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Perfil de Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800)); // Mismo tamaño que HomeFrame
        setLocationRelativeTo(null);

        // Inicializar fuente de emojis
        EmojiUtils.initializeEmojiFont();

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear el contenido del perfil
        createProfileContent();

        // Agregar el panel principal al frame
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane);

        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Logo
        JLabel logo = new JLabel("BET4GRAD - Mi Perfil");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Mi Perfil";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span> - Mi Perfil</html>");
        logo.setText(html.toString());

        // Botón de volver
        JButton backButton = new JButton("← Volver al Inicio");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(ACCENT_COLOR);
        backButton.setForeground(HEADER_COLOR);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> {
            HomeFrame homeFrame = new HomeFrame(currentUserEmail);
            homeFrame.setVisible(true);
            this.dispose();
        });

        header.add(logo, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(30));
    }

    private void createProfileContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Obtener detalles del usuario
        UserRole.UserDetails userDetails = UserRole.getUserDetails(currentUserEmail);
        if (userDetails != null) {
            // Panel principal con información del usuario
            JPanel mainInfoPanel = createMainInfoPanel(userDetails);
            contentPanel.add(mainInfoPanel);
            contentPanel.add(Box.createVerticalStrut(30));

            // Panel de estadísticas
            JPanel statsPanel = createStatsPanel(userDetails);
            contentPanel.add(statsPanel);
            contentPanel.add(Box.createVerticalStrut(30));

            // Panel de acciones
            JPanel actionsPanel = createActionsPanel();
            contentPanel.add(actionsPanel);
        } else {
            // Mostrar mensaje de error si no se pueden obtener los datos
            JLabel errorLabel = new JLabel("Error al cargar los datos del usuario");
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(errorLabel);
        }

        mainPanel.add(contentPanel);
    }

    private JPanel createMainInfoPanel(UserRole.UserDetails userDetails) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título de la sección
        JLabel sectionTitle = EmojiUtils.createEmojiLabel(
            EmojiUtils.CommonEmojis.CLIPBOARD + " Información Personal", 24f);
        sectionTitle.setForeground(ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(sectionTitle, gbc);

        // Avatar placeholder
        JLabel avatarLabel = EmojiUtils.createEmojiLabel(EmojiUtils.CommonEmojis.USER, 80f);
        avatarLabel.setForeground(ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(avatarLabel, gbc);

        // Información del usuario
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createFieldLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(userDetails.getNombre()), gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createFieldLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(userDetails.getEmail()), gbc);

        // Carrera
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createFieldLabel("Carrera:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(userDetails.getCarrera()), gbc);

        // Semestre
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(createFieldLabel("Semestre:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(userDetails.getSemestre() != null ? userDetails.getSemestre() : "No especificado"), gbc);

        // Rol
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(createFieldLabel("Rol:"), gbc);
        gbc.gridx = 1;
        JLabel rolLabel = createFieldValue(userDetails.getRol());
        if ("ADMIN".equalsIgnoreCase(userDetails.getRol())) {
            rolLabel.setForeground(ACCENT_COLOR);
            rolLabel.setText(EmojiUtils.CommonEmojis.SECURITY + " " + userDetails.getRol());
        }
        panel.add(rolLabel, gbc);

        return panel;
    }

    private JPanel createStatsPanel(UserRole.UserDetails userDetails) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título de la sección
        JLabel sectionTitle = EmojiUtils.createEmojiLabel(
            EmojiUtils.CommonEmojis.TROPHY + " Estadísticas de Apunabs", 20f);
        sectionTitle.setForeground(ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(sectionTitle, gbc);

        // Obtener estadísticas de Apunabs
        UserAuth.ApunabStats stats = UserAuth.getApunabStatistics(currentUserEmail);

        // Crear tarjetas de estadísticas
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Estadísticas de Apunabs
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStatCard("📅", String.format("%.1f", stats.promedioSemanal), "Promedio Semanal", ACCENT_COLOR), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(createStatCard("📆", String.format("%.1f", stats.promedioMensual), "Promedio Mensual", new Color(52, 152, 219)), gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(createStatCard("📊", String.format("%.1f", stats.promedioSemestre), "Promedio Semestre", new Color(155, 89, 182)), gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        panel.add(createStatCard("📈", String.format("%.1f", stats.promedioAnual), "Promedio Anual", new Color(46, 204, 113)), gbc);

        // Estadísticas adicionales
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStatCard("💰", String.valueOf(userDetails.getApunabs()), "Total Apunabs", ACCENT_COLOR), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        int eventosRegistrados = getEventosRegistrados();
        panel.add(createStatCard("🎯", String.valueOf(eventosRegistrados), "Eventos Participados", new Color(230, 126, 34)), gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        // Calcular promedio por evento
        double promedioPorEvento = eventosRegistrados > 0 ? (double) userDetails.getApunabs() / eventosRegistrados : 0;
        panel.add(createStatCard("⭐", String.format("%.1f", promedioPorEvento), "Promedio por Evento", new Color(231, 76, 60)), gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        // Calcular Apunabs faltantes para graduación (mínimo 100,000)
        int apunabsActuales = userDetails.getApunabs();
        int apunabsMinimos = 100000;
        int apunabsFaltantes = Math.max(0, apunabsMinimos - apunabsActuales);
        
        String graduationText;
        Color graduationColor;
        String graduationIcon;
        
        if (apunabsFaltantes == 0) {
            graduationText = "¡LISTO!";
            graduationColor = new Color(46, 204, 113); // Verde
            graduationIcon = "🎓";
        } else {
            graduationText = String.format("%,d", apunabsFaltantes);
            graduationColor = new Color(241, 196, 15); // Amarillo
            graduationIcon = "📚";
        }
        
        panel.add(createStatCard(graduationIcon, graduationText, "Faltan para Graduación", graduationColor), gbc);

        // Tercera fila con información adicional
        gbc.gridx = 0; gbc.gridy = 3;
        // Calcular ranking aproximado (esto podría mejorarse con una consulta real)
        panel.add(createStatCard("🏆", "Top 10%", "Ranking Estimado", new Color(155, 89, 182)), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        // Mostrar progreso hacia graduación como porcentaje
        double progresoGraduacion = Math.min(100.0, (double) apunabsActuales / apunabsMinimos * 100);
        panel.add(createStatCard("📊", String.format("%.1f%%", progresoGraduacion), "Progreso Graduación", new Color(52, 152, 219)), gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        // Calcular días estimados para graduación (basado en promedio semanal)
        if (stats.promedioSemanal > 0 && apunabsFaltantes > 0) {
            double semanasEstimadas = apunabsFaltantes / stats.promedioSemanal;
            double diasEstimados = semanasEstimadas * 7;
            String tiempoEstimado;
            
            if (diasEstimados < 30) {
                tiempoEstimado = String.format("%.0f días", diasEstimados);
            } else if (diasEstimados < 365) {
                tiempoEstimado = String.format("%.1f meses", diasEstimados / 30);
            } else {
                tiempoEstimado = String.format("%.1f años", diasEstimados / 365);
            }
            
            panel.add(createStatCard("⏰", tiempoEstimado, "Tiempo Estimado", new Color(230, 126, 34)), gbc);
        } else if (apunabsFaltantes == 0) {
            panel.add(createStatCard("🎉", "¡YA!", "Tiempo Estimado", new Color(46, 204, 113)), gbc);
        } else {
            panel.add(createStatCard("❓", "N/A", "Tiempo Estimado", new Color(149, 165, 166)), gbc);
        }

        gbc.gridx = 3; gbc.gridy = 3;
        // Mostrar meta diaria recomendada
        if (apunabsFaltantes > 0) {
            // Asumiendo que quieren graduarse en 2 años (730 días)
            double metaDiaria = apunabsFaltantes / 730.0;
            panel.add(createStatCard("🎯", String.format("%.1f", metaDiaria), "Meta Diaria (2 años)", new Color(231, 76, 60)), gbc);
        } else {
            panel.add(createStatCard("✅", "Meta\nAlcanzada", "Meta Diaria", new Color(46, 204, 113)), gbc);
        }

        return panel;
    }

    private JPanel createStatCard(String icon, String value, String label, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel iconLabel = EmojiUtils.createEmojiLabel(icon, 32f);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComponent.setForeground(Color.WHITE);
        labelComponent.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(labelComponent);

        return card;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);

        // Botón de editar perfil (placeholder)
        JButton editButton = createActionButton(
            EmojiUtils.CommonEmojis.EDIT + " Editar Perfil", 
            new Color(52, 152, 219)
        );
        editButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Función de editar perfil próximamente disponible",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Botón de cambiar contraseña (placeholder)
        JButton changePasswordButton = createActionButton(
            EmojiUtils.CommonEmojis.LOCK + " Cambiar Contraseña", 
            ACCENT_COLOR
        );
        changePasswordButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Función de cambiar contraseña próximamente disponible",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Botón de panel de administración (solo para admins)
        JButton adminButton = createActionButton(
            "⚙️ Panel de Administración", 
            new Color(156, 39, 176) // Morado para distinguirlo
        );
        adminButton.addActionListener(e -> {
            // Verificar si el usuario es administrador
            if (UserRole.isAdmin(currentUserEmail)) {
                // Si es admin, abrir AdminDashboard
                AdminDashboard adminDashboard = new AdminDashboard(currentUserEmail);
                adminDashboard.setVisible(true);
                this.dispose();
            } else {
                // Si no es admin, mostrar mensaje de error
                JOptionPane.showMessageDialog(this,
                    "❌ Acceso Denegado\n\n" +
                    "No tienes permisos de administrador para acceder a esta función.\n" +
                    "Solo los usuarios con rol de administrador pueden acceder al panel de administración.",
                    "Error de Permisos",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botón de cerrar sesión
        JButton logoutButton = createActionButton(
            EmojiUtils.CommonEmojis.DOOR + " Cerrar Sesión", 
            new Color(220, 53, 69)
        );
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar Cierre de Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Cerrar la ventana actual y abrir LoginFrame
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                });
            }
        });

        panel.add(editButton);
        panel.add(changePasswordButton);
        panel.add(adminButton);
        panel.add(logoutButton);

        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(ACCENT_COLOR);
        return label;
    }

    private JLabel createFieldValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private int getEventosRegistrados() {
        // Obtener el número de eventos en los que está registrado el usuario
        // Esto requiere una consulta a la base de datos
        try {
            return ActividadesDB.getUserEventCount(currentUserEmail);
        } catch (Exception e) {
            System.err.println("Error al obtener eventos registrados: " + e.getMessage());
            return 0;
        }
    }
} 