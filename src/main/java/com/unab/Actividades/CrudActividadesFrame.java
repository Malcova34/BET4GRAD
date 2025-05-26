/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Actividades;

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
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Admin.AdminDashboard;
import com.unab.database.ActividadesDB;
import com.unab.database.ActividadesDB.Evento;

/**
 * Frame para gestionar actividades (CRUD)
 * @author danie
 */
public class CrudActividadesFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PANEL_BG = new Color(13, 27, 51);
    
    private JPanel mainPanel;
    private JTable eventosTable;
    private DefaultTableModel tableModel;
    private String currentUserEmail;

    public CrudActividadesFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Gestión de Actividades");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 800));
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
        refreshEventosTable();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Logo
        JLabel logo = new JLabel("BET4GRAD - Gestión de Actividades");
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
        html.append(" - Gestión de Actividades</html>");
        logo.setText(html.toString());

        // Botón de volver
        JButton backButton = new JButton("← Volver al Admin");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(ACCENT_COLOR);
        backButton.setForeground(HEADER_COLOR);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> {
            AdminDashboard adminDashboard = new AdminDashboard(currentUserEmail);
            adminDashboard.setVisible(true);
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

        // Panel de actividades con botón de agregar
        JPanel actividadesHeaderPanel = new JPanel(new BorderLayout());
        actividadesHeaderPanel.setBackground(PANEL_BG);
        
        JLabel title = new JLabel("Gestión de Actividades");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        JButton addEventButton = createActionButton("Nueva Actividad", new Color(39, 174, 96));
        addEventButton.addActionListener(e -> showAddEventDialog());
        
        actividadesHeaderPanel.add(title, BorderLayout.WEST);
        actividadesHeaderPanel.add(addEventButton, BorderLayout.EAST);
        
        contentPanel.add(actividadesHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Panel de la tabla de actividades
        JPanel eventosPanel = createEventosPanel();
        contentPanel.add(eventosPanel);

        mainPanel.add(contentPanel);
    }

    private JPanel createEventosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Tabla de eventos
        String[] columnNames = {"ID", "Título", "Descripción", "Puntos", "Imagen", "Acciones"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Solo la columna de acciones es editable
            }
        };

        eventosTable = new JTable(tableModel);
        eventosTable.setBackground(PANEL_BG);
        eventosTable.setForeground(Color.WHITE);
        eventosTable.setGridColor(ACCENT_COLOR);
        eventosTable.setSelectionBackground(ACCENT_COLOR);
        eventosTable.setSelectionForeground(Color.BLACK);
        eventosTable.setRowHeight(50);
        eventosTable.getTableHeader().setBackground(HEADER_COLOR);
        eventosTable.getTableHeader().setForeground(Color.WHITE);
        eventosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Configurar columnas
        eventosTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        eventosTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
        eventosTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Descripción
        eventosTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Puntos
        eventosTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Imagen
        eventosTable.getColumnModel().getColumn(5).setPreferredWidth(180); // Acciones

        // Agregar renderer/editor para la columna de acciones
        eventosTable.getColumn("Acciones").setCellRenderer(new ActionCellRenderer());
        eventosTable.getColumn("Acciones").setCellEditor(new ActionCellEditor());

        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(PANEL_BG);

        JButton refreshButton = createActionButton("Actualizar", ACCENT_COLOR);
        refreshButton.addActionListener(e -> refreshEventosTable());

        actionPanel.add(refreshButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_BG);
        tablePanel.add(new JScrollPane(eventosTable), BorderLayout.CENTER);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // Renderer para la columna de acciones
    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Editar");
        private final JButton deleteButton = new JButton("Eliminar");
        
        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            setBackground(BACKGROUND_COLOR);
            
            editButton.setBackground(BACKGROUND_COLOR);
            editButton.setForeground(new Color(0, 191, 255));
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Arial", Font.BOLD, 12));
            
            deleteButton.setBackground(BACKGROUND_COLOR);
            deleteButton.setForeground(new Color(220, 53, 69));
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
            
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
        private final JButton deleteButton = new JButton("Eliminar");
        private Evento eventoToEdit;

        public ActionCellEditor() {
            panel.setBackground(BACKGROUND_COLOR);
            
            editButton.setBackground(BACKGROUND_COLOR);
            editButton.setForeground(new Color(0, 191, 255));
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setFont(new Font("Arial", Font.BOLD, 12));
            
            deleteButton.setBackground(BACKGROUND_COLOR);
            deleteButton.setForeground(new Color(220, 53, 69));
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
            
            panel.add(editButton);
            panel.add(deleteButton);

            // Listener para el botón Editar
            editButton.addActionListener(e -> {
                if (eventoToEdit != null) {
                    fireEditingStopped();
                    showEditEventDialog(eventoToEdit);
                }
            });

            // Listener para el botón Eliminar
            deleteButton.addActionListener(e -> {
                if (eventoToEdit != null) {
                    fireEditingStopped();
                    int confirm = JOptionPane.showConfirmDialog(panel,
                        "¿Está seguro de que desea eliminar la actividad '" + eventoToEdit.getTitulo() + "'?\n" +
                        "Esta acción no se puede deshacer y eliminará todos los registros asociados.",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (ActividadesDB.deleteEvento(eventoToEdit.getId())) {
                            JOptionPane.showMessageDialog(panel,
                                "Actividad eliminada exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshEventosTable();
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                "Error al eliminar la actividad",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.eventoToEdit = getEventoAtRow(row);
            panel.setBackground(BACKGROUND_COLOR);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private Evento getEventoAtRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return null;
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            String titulo = (String) tableModel.getValueAt(row, 1);
            String descripcion = (String) tableModel.getValueAt(row, 2);
            String puntosStr = (String) tableModel.getValueAt(row, 3);
            int puntos = Integer.parseInt(puntosStr.replace(" pts", ""));
            String imagenPath = (String) tableModel.getValueAt(row, 4);
            return new Evento(id, titulo, descripcion, imagenPath, puntos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void refreshEventosTable() {
        tableModel.setRowCount(0);
        List<Evento> eventos = ActividadesDB.getAllEventos();
        for (Evento evento : eventos) {
            tableModel.addRow(new Object[]{
                evento.getId(),
                evento.getTitulo(),
                truncateText(evento.getDescripcion(), 50),
                evento.getPuntosRecompensa() + " pts",
                evento.getImagenPath(),
                ""
            });
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
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

    private void showAddEventDialog() {
        showEventDialog(null, "Agregar Nueva Actividad");
    }

    private void showEditEventDialog(Evento evento) {
        showEventDialog(evento, "Editar Actividad");
    }

    private void showEventDialog(Evento evento, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Campos del formulario
        JTextField tituloField = new JTextField(evento != null ? evento.getTitulo() : "", 30);
        JTextArea descripcionArea = new JTextArea(evento != null ? evento.getDescripcion() : "", 4, 30);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        JScrollPane descripcionScroll = new JScrollPane(descripcionArea);
        
        JTextField imagenField = new JTextField(evento != null ? evento.getImagenPath() : "/com/unab/Actividades/images/", 30);
        JTextField puntosField = new JTextField(evento != null ? String.valueOf(evento.getPuntosRecompensa()) : "5", 10);

        // Agregar campos al diálogo
        dialog.add(new JLabel("Título de la Actividad:"), gbc);
        dialog.add(tituloField, gbc);
        dialog.add(new JLabel("Descripción:"), gbc);
        dialog.add(descripcionScroll, gbc);
        dialog.add(new JLabel("Ruta de la Imagen:"), gbc);
        dialog.add(imagenField, gbc);
        dialog.add(new JLabel("Puntos de Recompensa:"), gbc);
        dialog.add(puntosField, gbc);

        // Información sobre imágenes
        JLabel infoLabel = new JLabel("<html><i>Recomendación: Usar imágenes de 350x200 píxeles en formato JPG/PNG</i></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        dialog.add(infoLabel, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createActionButton("Guardar", ACCENT_COLOR);
        JButton cancelButton = createActionButton("Cancelar", new Color(220, 53, 69));

        saveButton.addActionListener(e -> {
            String titulo = tituloField.getText().trim();
            String descripcion = descripcionArea.getText().trim();
            String imagenPath = imagenField.getText().trim();
            String puntosText = puntosField.getText().trim();

            if (titulo.isEmpty() || descripcion.isEmpty() || imagenPath.isEmpty() || puntosText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int puntos = Integer.parseInt(puntosText);
                if (puntos < 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Los puntos de recompensa deben ser un número positivo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success;
                if (evento == null) {
                    // Crear nuevo evento
                    success = ActividadesDB.createEvento(titulo, descripcion, imagenPath, puntos);
                } else {
                    // Actualizar evento existente
                    success = ActividadesDB.updateEvento(evento.getId(), titulo, descripcion, imagenPath, puntos);
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        evento == null ? "Actividad creada exitosamente" : "Actividad actualizada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshEventosTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error al " + (evento == null ? "crear" : "actualizar") + " la actividad",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor ingrese un número válido para los puntos de recompensa",
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
