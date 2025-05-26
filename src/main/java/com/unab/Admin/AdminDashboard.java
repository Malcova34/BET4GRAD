package com.unab.Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Actividades.CrudActividadesFrame;
import com.unab.Home.HomeFrame;
import com.unab.database.UserAuth;
import com.unab.database.UserRole;
import com.unab.database.UserRole.UserDetails;

public class AdminDashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PANEL_BG = new Color(13, 27, 51);
    
    private JPanel mainPanel;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private String currentUserEmail;

    public AdminDashboard(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear el contenido del dashboard
        createDashboardContent();

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

        // Cargar datos iniciales
        refreshUserTable();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Logo
        JLabel logo = new JLabel("BET4GRAD - Admin");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = logo.getText();
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Admin</html>");
        logo.setText(html.toString());

        // Botón de volver
        JButton backButton = new JButton("← Volver");
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
        mainPanel.add(Box.createVerticalStrut(20));
    }

    private void createDashboardContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Panel de usuarios con botón de agregar
        JPanel usersHeaderPanel = new JPanel(new BorderLayout());
        usersHeaderPanel.setBackground(PANEL_BG);
        
        JLabel title = new JLabel("Gestión de Usuarios");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        // Panel para múltiples botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(PANEL_BG);
        
        JButton addUserButton = createActionButton("Agregar Usuario", new Color(39, 174, 96));
        addUserButton.addActionListener(e -> showAddUserDialog());
        
        JButton manageActivitiesButton = createActionButton("Gestionar Actividades", new Color(52, 152, 219));
        manageActivitiesButton.addActionListener(e -> {
            CrudActividadesFrame crudFrame = new CrudActividadesFrame(currentUserEmail);
            crudFrame.setVisible(true);
            this.dispose();
        });
        
        buttonsPanel.add(manageActivitiesButton);
        buttonsPanel.add(addUserButton);
        
        usersHeaderPanel.add(title, BorderLayout.WEST);
        usersHeaderPanel.add(buttonsPanel, BorderLayout.EAST);
        
        contentPanel.add(usersHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Panel de la tabla de usuarios
        JPanel usersPanel = createUsersPanel();
        contentPanel.add(usersPanel);

        mainPanel.add(contentPanel);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Obtener estadísticas
        List<UserDetails> users = UserRole.getAllUsers();
        int totalUsers = users.size();
        
        int adminUsers = (int) users.stream().filter(u -> "Admin".equals(u.getRol())).count();

        // Crear tarjetas de estadísticas
        panel.add(createStatCard("Total Usuarios", String.valueOf(totalUsers), ""));
        
        panel.add(createStatCard("Administradores", String.valueOf(adminUsers), ""));

        return panel;
    }

    private JPanel createStatCard(String title, String value, String icon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        iconLabel.setForeground(ACCENT_COLOR);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);

        return card;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Título
        JLabel title = new JLabel("Gestión de Usuarios");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        // Tabla de usuarios
        String[] columnNames = {"ID", "Nombre", "Email", "Carrera", "Semestre", "Rol", "Apunabs", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Solo la columna de acciones es editable (ahora es la columna 7)
            }
        };

        userTable = new JTable(tableModel);
        userTable.setBackground(PANEL_BG);
        userTable.setForeground(Color.WHITE);
        userTable.setGridColor(ACCENT_COLOR);
        userTable.setSelectionBackground(ACCENT_COLOR);
        userTable.setSelectionForeground(Color.BLACK);
        userTable.setRowHeight(40);
        userTable.getTableHeader().setBackground(HEADER_COLOR);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Configurar columnas
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(130); // Nombre
        userTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Carrera
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Semestre
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Rol
        userTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Apunabs
        userTable.getColumnModel().getColumn(7).setPreferredWidth(180); // Acciones

        // Agregar renderer/editor para la columna de acciones
        userTable.getColumn("Acciones").setCellRenderer(new ActionCellRenderer());
        userTable.getColumn("Acciones").setCellEditor(new ActionCellEditor());

        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(PANEL_BG);

        JButton refreshButton = createActionButton("Actualizar", ACCENT_COLOR);
        refreshButton.addActionListener(e -> refreshUserTable());

        actionPanel.add(refreshButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_BG);
        tablePanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // Renderer para la columna de acciones
    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Editar");
        private final JButton deleteButton = new JButton("Borrar");
        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            setBackground(BACKGROUND_COLOR); // Fondo azul de la ventana
            editButton.setBackground(BACKGROUND_COLOR); // Fondo azul de la ventana
            editButton.setForeground(new Color(0, 191, 255)); // Azul celeste
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Arial", Font.BOLD, 14));
            deleteButton.setBackground(BACKGROUND_COLOR); // Fondo azul de la ventana
            deleteButton.setForeground(new Color(220, 53, 69)); // Rojo
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
            add(editButton);
            add(deleteButton);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(BACKGROUND_COLOR);
            return this;
        }
    }

    // Editor para la columna de acciones
    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton editButton = new JButton("Editar");
        private final JButton deleteButton = new JButton("Borrar");
        private UserDetails userToEdit;
        private int editingRow;

        public ActionCellEditor() {
            panel.setBackground(BACKGROUND_COLOR);
            editButton.setBackground(BACKGROUND_COLOR);
            editButton.setForeground(new Color(0, 191, 255)); // Azul celeste
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Arial", Font.BOLD, 14));
            deleteButton.setBackground(BACKGROUND_COLOR);
            deleteButton.setForeground(new Color(220, 53, 69)); // Rojo
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(editButton);
            panel.add(deleteButton);

            // Listener para el botón Editar
            editButton.addActionListener(e -> {
                // Usar el userToEdit capturado cuando el editor se activó
                if (userToEdit != null) {
                    fireEditingStopped(); // Detener la edición ANTES de mostrar el diálogo
                    showEditDialog(userToEdit);
                }
            });

            // Listener para el botón Borrar
            deleteButton.addActionListener(e -> {
                // Usar el userToEdit capturado cuando el editor se activó
                if (userToEdit != null) {
                    fireEditingStopped(); // Detener la edición ANTES de mostrar la confirmación
                    int confirm = JOptionPane.showConfirmDialog(panel,
                        "¿Está seguro de que desea eliminar al usuario " + userToEdit.getNombre() + "?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (UserRole.deleteUser(userToEdit.getEmail())) {
                            JOptionPane.showMessageDialog(panel,
                                "Usuario eliminado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshUserTable();
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                "Error al eliminar el usuario",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Capturar el UserDetails para esta fila específica
            this.editingRow = row; // Guardar la fila por si acaso, aunque ahora usamos userToEdit
            this.userToEdit = getUserAtRow(row);

            panel.setBackground(BACKGROUND_COLOR);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null; // El editor no devuelve un valor de celda, solo maneja acciones
        }
    }

    // Este método sigue siendo útil para obtener el UserDetails basado en la fila actual de la tabla
    private UserDetails getUserAtRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return null;
        // Asumiendo que el orden de las columnas es: ID, Nombre, Email, Carrera, Semestre, Rol, Apunabs
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            String nombre = (String) tableModel.getValueAt(row, 1);
            String email = (String) tableModel.getValueAt(row, 2);
            String carrera = (String) tableModel.getValueAt(row, 3);
            String semestre = (String) tableModel.getValueAt(row, 4);
            String rol = (String) tableModel.getValueAt(row, 5);
            String apunabsStr = (String) tableModel.getValueAt(row, 6);
            int apunabs = Integer.parseInt(apunabsStr.replace(" pts", ""));
            return new UserDetails(id, nombre, email, carrera, semestre, rol, apunabs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void refreshUserTable() {
        tableModel.setRowCount(0);
        List<UserDetails> users = UserRole.getAllUsers();
        for (UserDetails user : users) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getCarrera(),
                user.getSemestre(),
                user.getRol(),
                user.getApunabs() + " pts",
                ""
            });
        }
    }

    private JButton createTableButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(PANEL_BG);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PANEL_BG);
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Usuario", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Campos del formulario
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField carreraField = new JTextField(20);
        JTextField semestreField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleCombo = new JComboBox<>(UserRole.getAllRoles().toArray(new String[0]));

        // Agregar campos al diálogo
        dialog.add(new JLabel("Nombre Completo:"), gbc);
        dialog.add(nameField, gbc);
        dialog.add(new JLabel("Email:"), gbc);
        dialog.add(emailField, gbc);
        dialog.add(new JLabel("Carrera:"), gbc);
        dialog.add(carreraField, gbc);
        dialog.add(new JLabel("Semestre:"), gbc);
        dialog.add(semestreField, gbc);
        dialog.add(new JLabel("Contraseña:"), gbc);
        dialog.add(passwordField, gbc);
        dialog.add(new JLabel("Rol:"), gbc);
        dialog.add(roleCombo, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createActionButton("Guardar", ACCENT_COLOR);
        JButton cancelButton = createActionButton("Cancelar", new Color(220, 53, 69));

        saveButton.addActionListener(e -> {
            String nombre = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String carrera = carreraField.getText();
            String semestre = semestreField.getText();
            String rol = (String) roleCombo.getSelectedItem();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || carrera.isEmpty() || semestre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserAuth.emailExists(email)) {
                JOptionPane.showMessageDialog(dialog,
                    "Este email ya está registrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rolId = UserRole.getRoleId(rol);
            if (UserRole.addUser(nombre, email, password, carrera, semestre, rolId)) {
                JOptionPane.showMessageDialog(dialog,
                    "Usuario agregado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Error al agregar el usuario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog(UserDetails user) {
        JDialog dialog = new JDialog(this, "Editar Usuario", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Campos de edición
        JTextField nameField = new JTextField(user.getNombre(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField carreraField = new JTextField(user.getCarrera(), 20);
        JTextField semestreField = new JTextField(user.getSemestre(), 20);
        JTextField apunabsField = new JTextField(String.valueOf(user.getApunabs()), 20);
        JComboBox<String> roleCombo = new JComboBox<>(UserRole.getAllRoles().toArray(new String[0]));
        roleCombo.setSelectedItem(user.getRol());

        // Panel de contraseña
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton changePasswordButton = createActionButton("Cambiar Contraseña", ACCENT_COLOR);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog(user.getEmail()));
        passwordPanel.add(changePasswordButton);

        dialog.add(new JLabel("Nombre:"), gbc);
        dialog.add(nameField, gbc);
        dialog.add(new JLabel("Email:"), gbc);
        dialog.add(emailField, gbc);
        dialog.add(new JLabel("Carrera:"), gbc);
        dialog.add(carreraField, gbc);
        dialog.add(new JLabel("Semestre:"), gbc);
        dialog.add(semestreField, gbc);
        dialog.add(new JLabel("Puntos Apunab:"), gbc);
        dialog.add(apunabsField, gbc);
        dialog.add(new JLabel("Rol:"), gbc);
        dialog.add(roleCombo, gbc);
        dialog.add(passwordPanel, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createActionButton("Guardar", ACCENT_COLOR);
        JButton deleteButton = createActionButton("Eliminar Usuario", new Color(220, 53, 69));
        JButton cancelButton = createActionButton("Cancelar", new Color(128, 128, 128));

        saveButton.addActionListener(e -> {
            String nombre = nameField.getText();
            String email = emailField.getText();
            String carrera = carreraField.getText();
            String semestre = semestreField.getText();
            String apunabsText = apunabsField.getText();
            String rol = (String) roleCombo.getSelectedItem();

            if (nombre.isEmpty() || email.isEmpty() || carrera.isEmpty() || semestre.isEmpty() || apunabsText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int apunabs = Integer.parseInt(apunabsText);
                if (apunabs < 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Los puntos Apunab no pueden ser negativos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rolId = UserRole.getRoleId(rol);
                if (UserRole.updateUser(user.getId(), nombre, email, carrera, semestre, rolId)) {
                    // Actualizar los puntos Apunab por separado
                    if (UserAuth.setApunabs(email, apunabs)) {
                        JOptionPane.showMessageDialog(dialog,
                            "Usuario actualizado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshUserTable();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Error al actualizar los puntos Apunab",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error al actualizar el usuario",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor ingrese un número válido para los puntos Apunab",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "¿Está seguro de que desea eliminar este usuario?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (UserRole.deleteUser(user.getEmail())) {
                    JOptionPane.showMessageDialog(dialog,
                        "Usuario eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshUserTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error al eliminar el usuario",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog(String email) {
        JDialog dialog = new JDialog(this, "Cambiar Contraseña", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        dialog.add(new JLabel("Nueva Contraseña:"), gbc);
        dialog.add(newPasswordField, gbc);
        dialog.add(new JLabel("Confirmar Contraseña:"), gbc);
        dialog.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createActionButton("Guardar", ACCENT_COLOR);
        JButton cancelButton = createActionButton("Cancelar", new Color(128, 128, 128));

        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Las contraseñas no coinciden",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserRole.updateUserPassword(email, newPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "Contraseña actualizada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Error al actualizar la contraseña",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 