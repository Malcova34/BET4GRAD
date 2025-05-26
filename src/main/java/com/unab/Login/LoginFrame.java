package com.unab.Login;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Admin.AdminDashboard;
import com.unab.Home.HomeFrame;
import com.unab.database.ActividadesDB;
import com.unab.database.PasswordRecovery;
import com.unab.database.UserAuth;
import com.unab.database.UserRole;

public class LoginFrame extends JFrame {
    private JPanel mainContainer;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel overlayPanel;
    private CardLayout cardLayout;
    private Timer slideTimer;
    private boolean isLoginView = true;
    private JPanel cardPanel;

    public LoginFrame() {
        setTitle("BET4GRAD Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        // Configurar el panel principal con bordes redondeados
        mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                g2.dispose();
            }
        };
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

        // Crear los paneles
        setupLoginPanel();
        setupRegisterPanel();
        setupOverlayPanel();

        // Configurar el layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(loginPanel, "login");
        cardPanel.add(registerPanel, "register");

        // Añadir los paneles al contenedor principal
        mainContainer.add(cardPanel, BorderLayout.CENTER);
        mainContainer.add(overlayPanel, BorderLayout.EAST);

        // Añadir el contenedor principal al frame
        setContentPane(mainContainer);
    }

    private void setupLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Título
        JLabel titleLabel = new JLabel("¡Loguéate Aquí!");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        loginPanel.add(titleLabel, gbc);

        // Campo de email
        JTextField emailField = new JTextField(20);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        emailField.setForeground(new Color(51, 51, 51));
        styleTextField(emailField);
        loginPanel.add(emailField, gbc);

        // Campo de contraseña
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        passwordField.setForeground(new Color(51, 51, 51));
        styleTextField(passwordField);
        loginPanel.add(passwordField, gbc);

        // Panel de opciones (Remember me y Forgot password)
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        optionsPanel.setBackground(Color.WHITE);
        
        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setForeground(new Color(51, 51, 51));
        
        JLabel forgotPassword = new JLabel("Forgot password?");
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.setForeground(new Color(255, 153, 0));
        forgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showPasswordRecoveryDialog();
            }
        });
        
        optionsPanel.add(rememberMe);
        optionsPanel.add(forgotPassword);
        loginPanel.add(optionsPanel, gbc);

        // Botón de login
        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (UserAuth.validateLogin(email, password)) {
                handleLoginSuccess(email);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Email o contraseña incorrectos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        loginPanel.add(loginButton, gbc);

        // Social media
        addSocialMediaButtons(loginPanel, gbc);
    }

    private void setupRegisterPanel() {
        registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Título
        JLabel titleLabel = new JLabel("¡Regístrate Aquí!");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        registerPanel.add(titleLabel, gbc);

        // Campos del registro
        JTextField nameField = new JTextField(20);
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nombre Completo");
        nameField.setForeground(new Color(51, 51, 51));
        styleTextField(nameField);
        registerPanel.add(nameField, gbc);

        JTextField emailField = new JTextField(20);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        emailField.setForeground(new Color(51, 51, 51));
        styleTextField(emailField);
        registerPanel.add(emailField, gbc);

        JTextField carreraField = new JTextField(20);
        carreraField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Carrera");
        carreraField.setForeground(new Color(51, 51, 51));
        styleTextField(carreraField);
        registerPanel.add(carreraField, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        passwordField.setForeground(new Color(51, 51, 51));
        styleTextField(passwordField);
        registerPanel.add(passwordField, gbc);

        // Botón de registro
        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> {
            String nombre = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String carrera = carreraField.getText();
            
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || carrera.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (UserAuth.emailExists(email)) {
            JOptionPane.showMessageDialog(this,
                    "Este email ya está registrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (UserAuth.registerUser(nombre, email, password, carrera)) {
                JOptionPane.showMessageDialog(this,
                    "Registro exitoso. Por favor inicie sesión",
                    "Registro Completado",
                    JOptionPane.INFORMATION_MESSAGE);
                switchView(); // Cambiar a la vista de login
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al registrar el usuario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        registerPanel.add(registerButton, gbc);

        // Social media
        addSocialMediaButtons(registerPanel, gbc);
    }

    private void setupOverlayPanel() {
        overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/com/unab/Login/icons/campusUnab.jpg"));
                    Image img = backgroundImage.getImage();
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    
                    // Agregar overlay semi-transparente
                    g2d.setColor(new Color(46, 94, 109, 180));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    setBackground(new Color(255, 153, 0));
                }
            }
        };
        
        overlayPanel.setPreferredSize(new Dimension(400, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 40, 10, 40);

        // Título
        JLabel titleLabel = new JLabel("BET4GRAD");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 45));
        titleLabel.setForeground(new Color(233, 164, 39));
        overlayPanel.add(titleLabel, gbc);

        // Botón para cambiar vista
        JButton switchButton = createGhostButton(isLoginView ? "Register" : "Login");
        switchButton.addActionListener(e -> switchView());
        overlayPanel.add(switchButton, gbc);
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(238, 238, 238)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        textField.setBackground(new Color(238, 238, 238));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(new Color(255, 153, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createGhostButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(new Color(255, 255, 255, 50));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addSocialMediaButtons(JPanel panel, GridBagConstraints gbc) {
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        socialPanel.setBackground(Color.WHITE);
        
        // Definir los enlaces y nombres de los iconos
        Object[][] socialLinks = {
            {"https://www.instagram.com/unab_online/", "instagram.png"},
            {"https://www.linkedin.com/school/universidad-autonoma-de-bucaramanga/", "linkedin.png"},
            {"https://x.com/unab_online?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor", "twitter.png"}
        };
        
        for (Object[] social : socialLinks) {
            String url = (String) social[0];
            String iconName = (String) social[1];
            
            JButton socialButton = new JButton();
            socialButton.setPreferredSize(new Dimension(40, 40));
            socialButton.setBorder(BorderFactory.createLineBorder(new Color(238, 238, 238)));
            socialButton.setBackground(Color.WHITE);
            socialButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            try {
                // Cargar y redimensionar el icono
                ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/unab/Login/icons/" + iconName));
                Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                socialButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                // Si no se encuentra el icono, usar texto como fallback
                socialButton.setText(iconName.substring(0, 1).toUpperCase());
            }
            
            // Agregar efecto hover
            socialButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    socialButton.setBorder(BorderFactory.createLineBorder(new Color(255, 153, 0)));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    socialButton.setBorder(BorderFactory.createLineBorder(new Color(238, 238, 238)));
                }
            });
            
            // Agregar acción de clic para abrir el enlace
            socialButton.addActionListener(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo abrir el enlace: " + url,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            socialPanel.add(socialButton);
        }
        
        panel.add(socialPanel, gbc);
    }

    private void switchView() {
        isLoginView = !isLoginView;
        cardLayout.show(cardPanel, isLoginView ? "login" : "register");
        
        // Remover el panel de overlay actual
        mainContainer.remove(overlayPanel);
        
        // Recrear y añadir el nuevo panel de overlay
        setupOverlayPanel();
        mainContainer.add(overlayPanel, BorderLayout.EAST);
        
        // Actualizar la interfaz
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void showPasswordRecoveryDialog() {
        JDialog dialog = new JDialog(this, "Recuperar Contraseña", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel instructionLabel = new JLabel("Ingresa tu correo electrónico:");
        instructionLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(51, 51, 51));
        dialog.add(instructionLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        emailField.setForeground(new Color(51, 51, 51));
        styleTextField(emailField);
        dialog.add(emailField, gbc);

        JButton sendButton = createStyledButton("Enviar");
        sendButton.addActionListener(e -> {
            String email = emailField.getText();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor ingresa tu correo electrónico",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (PasswordRecovery.requestPasswordRecovery(email)) {
                JOptionPane.showMessageDialog(dialog,
                    "Se ha enviado un correo con instrucciones para recuperar tu contraseña.\n" +
                    "Por favor revisa tu bandeja de entrada y carpeta de spam.",
                    "Correo Enviado",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "No se pudo procesar la solicitud. Por favor verifica tu correo electrónico o intenta más tarde.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(sendButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleLoginSuccess(String email) {
        // Verificar si el usuario es admin
        if (UserRole.isAdmin(email)) {
            // Si es admin, redirigir al AdminDashboard
            AdminDashboard adminDashboard = new AdminDashboard(email);
            adminDashboard.setVisible(true);
        } else {
            // Si no es admin, redirigir al HomeFrame
            HomeFrame homeFrame = new HomeFrame(email);
            homeFrame.setVisible(true);
        }
        
        // Cerrar el LoginFrame
        this.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Inicializar las tablas de la base de datos
            System.out.println("Inicializando sistema de base de datos...");
            ActividadesDB.initializeTables();
            ActividadesDB.initializeTutoriasTable();
            UserAuth.initializeApunabsForExistingUsers();
            System.out.println("Sistema inicializado correctamente!");
            
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
