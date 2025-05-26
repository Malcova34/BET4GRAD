/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Horario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Home.HomeFrame;

/**
 * Frame para mostrar el horario académico semanal del estudiante
 * @author danie
 */
public class HorarioFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color TABLE_HEADER_BG = new Color(42, 60, 88);
    private static final Color TABLE_CELL_BG = new Color(255, 255, 255);
    private static final Color TABLE_BORDER = new Color(200, 200, 200);
    
    // Colores para diferentes materias
    private static final Color[] SUBJECT_COLORS = {
        new Color(135, 206, 250), // Azul claro
        new Color(100, 149, 237), // Azul
        new Color(147, 112, 219), // Morado
        new Color(205, 133, 63),  // Marrón
        new Color(255, 165, 0),   // Naranja
        new Color(255, 182, 193), // Rosa
        new Color(144, 238, 144), // Verde claro
        new Color(64, 224, 208),  // Turquesa
        new Color(255, 218, 185), // Durazno
        new Color(176, 196, 222), // Azul gris
        new Color(238, 130, 238), // Violeta
        new Color(152, 251, 152)  // Verde pálido
    };
    
    private JPanel mainPanel;
    private String currentUserEmail;
    private Map<String, Color> subjectColorMap;

    public HorarioFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        this.subjectColorMap = new HashMap<>();
        
        setTitle("BET4GRAD - Mi Horario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 900));
        setLocationRelativeTo(null);

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear la tabla de horarios
        createScheduleTable();

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
        JLabel logo = new JLabel("BET4GRAD - Mi Horario Académico");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Mi Horario Académico";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Mi Horario Académico</html>");
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
        mainPanel.add(Box.createVerticalStrut(20));
    }

    private void createScheduleTable() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BACKGROUND_COLOR);
        tableContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título de la tabla
        JLabel tableTitle = new JLabel("📅 Horario de Clases - Semestre 2024-1", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(ACCENT_COLOR);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        // Panel principal de la tabla
        JPanel schedulePanel = new JPanel(new GridBagLayout());
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);

        // Crear los headers
        String[] days = {"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        String[] hours = {"06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", 
                         "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"};

        // Headers de días
        for (int col = 0; col < days.length; col++) {
            gbc.gridx = col;
            gbc.gridy = 0;
            gbc.weightx = col == 0 ? 0.5 : 1.0;
            gbc.weighty = 0.0;
            
            JLabel headerLabel = createHeaderCell(days[col]);
            schedulePanel.add(headerLabel, gbc);
        }

        // Definir las materias en el horario
        Map<String, ClassBlock> schedule = createSampleSchedule();

        // Crear las filas de horas y celdas
        for (int row = 0; row < hours.length; row++) {
            gbc.gridy = row + 1;
            gbc.weighty = 1.0;

            // Celda de hora
            gbc.gridx = 0;
            gbc.weightx = 0.5;
            JLabel hourLabel = createHourCell(hours[row]);
            schedulePanel.add(hourLabel, gbc);

            // Celdas de cada día
            for (int col = 1; col < days.length; col++) {
                gbc.gridx = col;
                gbc.weightx = 1.0;

                String key = days[col] + "-" + hours[row];
                ClassBlock classBlock = schedule.get(key);

                JPanel cell;
                if (classBlock != null) {
                    cell = createClassCell(classBlock);
                } else {
                    cell = createEmptyCell();
                }
                
                schedulePanel.add(cell, gbc);
            }
        }

        // Scroll pane para la tabla
        JScrollPane tableScrollPane = new JScrollPane(schedulePanel);
        tableScrollPane.setBorder(null);
        tableScrollPane.setBackground(Color.WHITE);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        // Crear leyenda de colores
        JPanel legendPanel = createLegendPanel();
        tableContainer.add(legendPanel, BorderLayout.SOUTH);

        mainPanel.add(tableContainer);
    }

    private JLabel createHeaderCell(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setBackground(TABLE_HEADER_BG);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TABLE_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        label.setPreferredSize(new Dimension(120, 40));
        return label;
    }

    private JLabel createHourCell(String hour) {
        JLabel label = new JLabel(hour, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(HEADER_COLOR);
        label.setBackground(new Color(245, 245, 245));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TABLE_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        label.setPreferredSize(new Dimension(80, 50));
        return label;
    }

    private JPanel createClassCell(ClassBlock classBlock) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        Color subjectColor = getSubjectColor(classBlock.getSubject());
        panel.setBackground(subjectColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(subjectColor.darker(), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Título de la materia
        JLabel subjectLabel = new JLabel(classBlock.getSubject());
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subjectLabel.setForeground(Color.BLACK);
        subjectLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // Información adicional
        if (classBlock.getRoom() != null && !classBlock.getRoom().isEmpty()) {
            JLabel roomLabel = new JLabel("📍 " + classBlock.getRoom());
            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            roomLabel.setForeground(Color.BLACK);
            roomLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            
            panel.add(subjectLabel);
            panel.add(Box.createVerticalStrut(2));
            panel.add(roomLabel);
        } else {
            panel.add(Box.createVerticalGlue());
            panel.add(subjectLabel);
            panel.add(Box.createVerticalGlue());
        }
        
        panel.setPreferredSize(new Dimension(120, 50));
        
        // Efecto hover
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 3),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(subjectColor.darker(), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
        
        return panel;
    }

    private JPanel createEmptyCell() {
        JPanel panel = new JPanel();
        panel.setBackground(TABLE_CELL_BG);
        panel.setBorder(BorderFactory.createLineBorder(TABLE_BORDER, 1));
        panel.setPreferredSize(new Dimension(120, 50));
        return panel;
    }

    private Color getSubjectColor(String subject) {
        return subjectColorMap.computeIfAbsent(subject, k -> {
            int index = subjectColorMap.size() % SUBJECT_COLORS.length;
            return SUBJECT_COLORS[index];
        });
    }

    private Map<String, ClassBlock> createSampleSchedule() {
        Map<String, ClassBlock> schedule = new HashMap<>();
        
        // Martes
        schedule.put("Martes-06:00", new ClassBlock("LABORATORIO DE MECÁNICA", "L201"));
        schedule.put("Martes-07:00", new ClassBlock("LABORATORIO DE MECÁNICA", "L201"));
        
        // Miércoles
        schedule.put("Miércoles-06:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        schedule.put("Miércoles-07:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        schedule.put("Miércoles-10:00", new ClassBlock("TALLER TENS DE CAMPO INTERMEDIO", "T102"));
        schedule.put("Miércoles-11:00", new ClassBlock("TALLER TENS DE CAMPO INTERMEDIO", "T102"));
        schedule.put("Miércoles-16:00", new ClassBlock("PROGRAMACIÓN DE COMPUTADORES", "C205"));
        schedule.put("Miércoles-17:00", new ClassBlock("PROGRAMACIÓN DE COMPUTADORES", "C205"));
        schedule.put("Miércoles-18:00", new ClassBlock("MECÁNICA Y LABORATORIO", "F301"));
        schedule.put("Miércoles-19:00", new ClassBlock("MECÁNICA Y LABORATORIO", "F301"));
        
        // Jueves
        schedule.put("Jueves-06:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        schedule.put("Jueves-10:00", new ClassBlock("SEMINARIO DE INGENIERÍA", "S401"));
        schedule.put("Jueves-11:00", new ClassBlock("SEMINARIO DE INGENIERÍA", "S401"));
        schedule.put("Jueves-14:00", new ClassBlock("PROGRAMACIÓN DE COMPUTADORES", "C205"));
        schedule.put("Jueves-15:00", new ClassBlock("PROGRAMACIÓN DE COMPUTADORES", "C205"));
        
        // Viernes
        schedule.put("Viernes-10:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        schedule.put("Viernes-11:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        schedule.put("Viernes-12:00", new ClassBlock("CÁLCULO INTEGRAL", "M301"));
        
        // Lunes
        schedule.put("Lunes-16:00", new ClassBlock("DISEÑO DE INTERFACES DE USUARIO", "D101"));
        schedule.put("Lunes-17:00", new ClassBlock("DISEÑO DE INTERFACES DE USUARIO", "D101"));
        schedule.put("Lunes-18:00", new ClassBlock("DISEÑO DE INTERFACES DE USUARIO", "D101"));
        
        // Miércoles (continuación)
        schedule.put("Miércoles-16:00", new ClassBlock("IDENTIDAD", "A305"));
        schedule.put("Miércoles-17:00", new ClassBlock("IDENTIDAD", "A305"));
        schedule.put("Miércoles-18:00", new ClassBlock("IDENTIDAD", "A305"));
        
        return schedule;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(BACKGROUND_COLOR);
        legendPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel legendTitle = new JLabel("📚 Leyenda de Materias", SwingConstants.CENTER);
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        legendTitle.setForeground(ACCENT_COLOR);
        legendTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        legendPanel.add(legendTitle);

        JPanel legendGrid = new JPanel(new GridLayout(0, 3, 10, 5));
        legendGrid.setBackground(BACKGROUND_COLOR);

        // Crear entradas de leyenda para cada materia única
        for (Map.Entry<String, Color> entry : subjectColorMap.entrySet()) {
            JPanel legendItem = new JPanel(new BorderLayout());
            legendItem.setBackground(entry.getValue());
            legendItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(entry.getValue().darker(), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            legendItem.setPreferredSize(new Dimension(200, 30));

            JLabel legendLabel = new JLabel(entry.getKey());
            legendLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            legendLabel.setForeground(Color.BLACK);
            legendLabel.setHorizontalAlignment(SwingConstants.CENTER);

            legendItem.add(legendLabel, BorderLayout.CENTER);
            legendGrid.add(legendItem);
        }

        legendPanel.add(legendGrid);
        return legendPanel;
    }

    // Clase interna para representar un bloque de clase
    private static class ClassBlock {
        private final String subject;
        private final String room;

        public ClassBlock(String subject, String room) {
            this.subject = subject;
            this.room = room;
        }

        public String getSubject() { return subject; }
        public String getRoom() { return room; }
    }
}
