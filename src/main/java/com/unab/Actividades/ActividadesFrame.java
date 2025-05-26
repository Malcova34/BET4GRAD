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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import com.unab.database.ActividadesDB.Evento;

/**
 *
 * @author danie
 */
public class ActividadesFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color EVENT_ITEM_BG = new Color(13, 27, 51);
    private static final Color EVENT_TITLE_BG = new Color(42, 60, 88);
    private static final Color REGISTER_COLOR = new Color(0, 123, 255); // Azul para registrarse
    private static final Color UNREGISTER_COLOR = new Color(220, 53, 69); // Rojo para desregistrarse
    
    private JPanel mainPanel;
    private JPanel eventsGrid;
    private String currentUserEmail;
    private JLabel apunabLabel;

    public ActividadesFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Actividades");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        // Inicializar las tablas de la base de datos
        ActividadesDB.initializeTables();

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear el grid de eventos
        createEventsGrid();

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
        JLabel logo = new JLabel("BET4GRAD - Actividades");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Actividades";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Actividades</html>");
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

    private void createEventsGrid() {
        eventsGrid = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas, filas dinámicas
        eventsGrid.setBackground(BACKGROUND_COLOR);
        eventsGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Obtener eventos de la base de datos
        List<Evento> eventos = ActividadesDB.getAllEventos();

        for (Evento evento : eventos) {
            JPanel eventItem = createEventItem(evento);
            eventsGrid.add(eventItem);
        }

        mainPanel.add(eventsGrid);
    }

    private JPanel createEventItem(Evento evento) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(BACKGROUND_COLOR);
        item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        item.setPreferredSize(new Dimension(350, 420));
        item.setMaximumSize(new Dimension(350, 420));

        // Panel de imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(350, 200));
        imagePanel.setMaximumSize(new Dimension(350, 200));
        imagePanel.setBackground(BACKGROUND_COLOR);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(evento.getImagenPath()));
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(350, 200, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setVerticalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar un placeholder
            JLabel placeholderLabel = new JLabel("📅", JLabel.CENTER);
            placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 80));
            placeholderLabel.setForeground(ACCENT_COLOR);
            imagePanel.add(placeholderLabel, BorderLayout.CENTER);
        }

        // Panel de información del evento
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(EVENT_TITLE_BG);
        infoPanel.setPreferredSize(new Dimension(350, 220));
        infoPanel.setMaximumSize(new Dimension(350, 220));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título del evento
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'><b>" + evento.getTitulo() + "</b></div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Descripción del evento
        JLabel descLabel = new JLabel("<html><div style='text-align: center; margin-top: 5px;'>" + evento.getDescripcion() + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.WHITE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(JLabel.CENTER);

        // Puntos de recompensa
        JLabel puntosLabel = new JLabel("Recompensa: " + evento.getPuntosRecompensa() + " Apunab");
        puntosLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        puntosLabel.setForeground(ACCENT_COLOR);
        puntosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        puntosLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(EVENT_TITLE_BG);

        // Verificar si el usuario ya está registrado
        boolean isRegistered = ActividadesDB.isUserRegistered(currentUserEmail, evento.getId());

        if (isRegistered) {
            // Botón para desregistrarse (rojo)
            JButton unregisterButton = createActionButton("Dejar de Participar", UNREGISTER_COLOR);
            unregisterButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas dejar de participar en este evento?\n" +
                    "Perderás " + evento.getPuntosRecompensa() + " puntos Apunab.",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (ActividadesDB.unregisterUserFromEvent(currentUserEmail, evento.getId())) {
                        JOptionPane.showMessageDialog(this,
                            "Te has desregistrado del evento exitosamente.\n" +
                            "Se han descontado " + evento.getPuntosRecompensa() + " puntos Apunab.",
                            "Desregistrado",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshEventsGrid();
                        updateApunabLabel();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Error al desregistrarse del evento",
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
                System.out.println("Evento ID: " + evento.getId());
                System.out.println("Título del evento: " + evento.getTitulo());
                
                boolean resultado = ActividadesDB.registerUserToEvent(currentUserEmail, evento.getId());
                System.out.println("Resultado del registro: " + resultado);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                        "¡Te has registrado exitosamente!\n" +
                        "Has ganado " + evento.getPuntosRecompensa() + " puntos Apunab.",
                        "Registrado",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshEventsGrid();
                    updateApunabLabel();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al registrarse en el evento.\nRevisa la consola para más detalles.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(registerButton);
        }

        // Agregar componentes al panel de información
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(puntosLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(buttonPanel);

        // Agregar paneles al item
        item.add(imagePanel);
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

    private void refreshEventsGrid() {
        eventsGrid.removeAll();
        
        List<Evento> eventos = ActividadesDB.getAllEventos();
        for (Evento evento : eventos) {
            JPanel eventItem = createEventItem(evento);
            eventsGrid.add(eventItem);
        }
        
        eventsGrid.revalidate();
        eventsGrid.repaint();
    }

    private void updateApunabLabel() {
        int puntos = ActividadesDB.getUserApunabPoints(currentUserEmail);
        apunabLabel.setText("Apunab: " + puntos + " pts");
    }
}
