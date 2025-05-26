/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Tutorias;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
import com.unab.Home.HomeFrame;
import com.unab.database.ActividadesDB;
import com.unab.database.ActividadesDB.Tutoria;

/**
 * Frame para el sistema de tutorías universitarias
 * @author danie
 */
public class TutoriasFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color TUTORIA_ITEM_BG = new Color(13, 27, 51);
    private static final Color TUTORIA_TITLE_BG = new Color(42, 60, 88);
    private static final Color REGISTER_COLOR = new Color(0, 123, 255); // Azul para registrarse
    private static final Color UNREGISTER_COLOR = new Color(220, 53, 69); // Rojo para desregistrarse
    
    private JPanel mainPanel;
    private JPanel tutoriasGrid;
    private String currentUserEmail;
    private JLabel apunabLabel;

    public TutoriasFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Tutorías Académicas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        // Inicializar las tablas de la base de datos
        ActividadesDB.initializeTutoriasTable();

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear el grid de tutorías
        createTutoriasGrid();

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
        JLabel logo = new JLabel("BET4GRAD - Tutorías Académicas");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Tutorías Académicas";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Tutorías Académicas</html>");
        logo.setText(html.toString());

        // Panel derecho con puntos Apunab y botón de volver
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(HEADER_COLOR);

        // Mostrar puntos Apunab
        int puntos = ActividadesDB.getUserApunabPoints(currentUserEmail);
        apunabLabel = new JLabel("Apunab: " + puntos + " pts");
        apunabLabel.setFont(new Font("Arial", Font.BOLD, 16));
        apunabLabel.setForeground(ACCENT_COLOR);
        apunabLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

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

        rightPanel.add(apunabLabel);
        rightPanel.add(backButton);

        header.add(logo, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(20));
    }

    private void createTutoriasGrid() {
        tutoriasGrid = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas, filas dinámicas
        tutoriasGrid.setBackground(BACKGROUND_COLOR);
        tutoriasGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Obtener tutorías de la base de datos
        List<Tutoria> tutorias = ActividadesDB.getAllTutorias();

        for (Tutoria tutoria : tutorias) {
            JPanel tutoriaItem = createTutoriaItem(tutoria);
            tutoriasGrid.add(tutoriaItem);
        }

        mainPanel.add(tutoriasGrid);
    }

    private JPanel createTutoriaItem(Tutoria tutoria) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(BACKGROUND_COLOR);
        item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        item.setPreferredSize(new Dimension(350, 300));
        item.setMaximumSize(new Dimension(350, 300));

        // Panel de información de la tutoría (sin imagen)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(TUTORIA_TITLE_BG);
        infoPanel.setPreferredSize(new Dimension(350, 300));
        infoPanel.setMaximumSize(new Dimension(350, 300));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Materia (título principal)
        JLabel materiaLabel = new JLabel("<html><div style='text-align: center;'><b>" + tutoria.getMateria() + "</b></div></html>");
        materiaLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        materiaLabel.setForeground(ACCENT_COLOR);
        materiaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        materiaLabel.setHorizontalAlignment(JLabel.CENTER);

        // Información del profesor
        JLabel profesorLabel = new JLabel("👨‍🏫 " + tutoria.getProfesor());
        profesorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profesorLabel.setForeground(Color.WHITE);
        profesorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Información del horario
        JLabel horarioLabel = new JLabel("🕐 " + tutoria.getHorario());
        horarioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        horarioLabel.setForeground(Color.LIGHT_GRAY);
        horarioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Información del salón y sede
        JLabel ubicacionLabel = new JLabel("📍 " + tutoria.getSalon() + " - " + tutoria.getSede());
        ubicacionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ubicacionLabel.setForeground(Color.LIGHT_GRAY);
        ubicacionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descripción
        JLabel descripcionLabel = new JLabel("<html><div style='text-align: left; margin-top: 5px;'>" + 
                                           tutoria.getDescripcion() + "</div></html>");
        descripcionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descripcionLabel.setForeground(Color.LIGHT_GRAY);
        descripcionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Puntos de recompensa
        JLabel puntosLabel = new JLabel("💰 Recompensa: " + tutoria.getPuntosRecompensa() + " Apunab");
        puntosLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        puntosLabel.setForeground(ACCENT_COLOR);
        puntosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(TUTORIA_TITLE_BG);

        // Verificar si el usuario ya está registrado
        boolean isRegistered = ActividadesDB.isUserRegisteredToTutoria(currentUserEmail, tutoria.getId());

        if (isRegistered) {
            // Botón para desregistrarse (rojo)
            JButton unregisterButton = createActionButton("Cancelar Tutoría", UNREGISTER_COLOR);
            unregisterButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas cancelar tu registro en esta tutoría?\n" +
                    "Perderás " + tutoria.getPuntosRecompensa() + " puntos Apunab.",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (ActividadesDB.unregisterUserFromTutoria(currentUserEmail, tutoria.getId())) {
                        JOptionPane.showMessageDialog(this,
                            "Te has desregistrado de la tutoría exitosamente.\n" +
                            "Se han descontado " + tutoria.getPuntosRecompensa() + " puntos Apunab.",
                            "Registro Cancelado",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshTutoriasGrid();
                        updateApunabLabel();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Error al cancelar el registro de la tutoría",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(unregisterButton);
        } else {
            // Botón para registrarse (azul)
            JButton registerButton = createActionButton("Registrarse", REGISTER_COLOR);
            registerButton.addActionListener(e -> {
                System.out.println("Botón 'Registrarse' presionado");
                System.out.println("Usuario: " + currentUserEmail);
                System.out.println("Tutoria ID: " + tutoria.getId());
                System.out.println("Materia: " + tutoria.getMateria());
                
                boolean resultado = ActividadesDB.registerUserToTutoria(currentUserEmail, tutoria.getId());
                System.out.println("Resultado del registro: " + resultado);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                        "¡Te has registrado exitosamente en la tutoría!\n" +
                        "Materia: " + tutoria.getMateria() + "\n" +
                        "Profesor: " + tutoria.getProfesor() + "\n" +
                        "Horario: " + tutoria.getHorario() + "\n" +
                        "Ubicación: " + tutoria.getSalon() + " - " + tutoria.getSede() + "\n\n" +
                        "Has ganado " + tutoria.getPuntosRecompensa() + " puntos Apunab.",
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshTutoriasGrid();
                    updateApunabLabel();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al registrarse en la tutoría.\nRevisa la consola para más detalles.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(registerButton);
        }

        // Agregar componentes al panel de información
        infoPanel.add(materiaLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(profesorLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(horarioLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(ubicacionLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descripcionLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(puntosLabel);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(buttonPanel);

        // Agregar solo el panel de información al item (sin imagen)
        item.add(infoPanel);

        // Efecto hover
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            }
        });

        return item;
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Efecto hover
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

    private void refreshTutoriasGrid() {
        tutoriasGrid.removeAll();
        
        List<Tutoria> tutorias = ActividadesDB.getAllTutorias();
        for (Tutoria tutoria : tutorias) {
            JPanel tutoriaItem = createTutoriaItem(tutoria);
            tutoriasGrid.add(tutoriaItem);
        }
        
        tutoriasGrid.revalidate();
        tutoriasGrid.repaint();
    }

    private void updateApunabLabel() {
        int puntos = ActividadesDB.getUserApunabPoints(currentUserEmail);
        apunabLabel.setText("Apunab: " + puntos + " pts");
    }
}
