/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Profesores;

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
import java.util.ArrayList;
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
import com.unab.utils.EmojiUtils;

/**
 * Frame para mostrar información de profesores de la universidad
 * @author danie
 */
public class ProfesoresFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PROFESOR_ITEM_BG = new Color(13, 27, 51);
    private static final Color PROFESOR_TITLE_BG = new Color(42, 60, 88);
    private static final Color CONTACT_COLOR = new Color(52, 152, 219); // Azul para contacto
    
    private JPanel mainPanel;
    private JPanel profesoresGrid;
    private String currentUserEmail;

    public ProfesoresFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Profesores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
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
        
        // Crear el grid de profesores
        createProfesoresGrid();

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
        JLabel logo = new JLabel("BET4GRAD - Profesores");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Profesores";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Profesores</html>");
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

    private void createProfesoresGrid() {
        profesoresGrid = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas, filas dinámicas
        profesoresGrid.setBackground(BACKGROUND_COLOR);
        profesoresGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Obtener profesores estáticos
        List<ProfesorItem> profesores = getStaticProfesores();

        for (ProfesorItem profesor : profesores) {
            JPanel profesorItem = createProfesorItem(profesor);
            profesoresGrid.add(profesorItem);
        }

        mainPanel.add(profesoresGrid);
    }

    private List<ProfesorItem> getStaticProfesores() {
        List<ProfesorItem> profesores = new ArrayList<>();
        
        profesores.add(new ProfesorItem(
            "Dr. María González Rodríguez",
            "Doctora en Matemáticas",
            "Matemáticas y Cálculo",
            "maria.gonzalez@unab.edu.co",
            "/com/unab/Profesores/images/maria_gonzalez.jpg",
            "Matemáticas"
        ));
        
        profesores.add(new ProfesorItem(
            "Ing. Carlos Rodríguez López",
            "Magíster en Ingeniería de Software",
            "Programación y Desarrollo",
            "carlos.rodriguez@unab.edu.co",
            "/com/unab/Profesores/images/carlos_rodriguez.jpg",
            "Ingeniería"
        ));
        
        profesores.add(new ProfesorItem(
            "Dr. Ana Martínez Silva",
            "Doctora en Física",
            "Física y Mecánica",
            "ana.martinez@unab.edu.co",
            "/com/unab/Profesores/images/ana_martinez.jpg",
            "Ciencias"
        ));
        
        profesores.add(new ProfesorItem(
            "Dr. Luis Torres Mendoza",
            "Doctor en Química",
            "Química Orgánica e Inorgánica",
            "luis.torres@unab.edu.co",
            "/com/unab/Profesores/images/luis_torres.jpg",
            "Química"
        ));
        
        profesores.add(new ProfesorItem(
            "CPA. Sandra López Pérez",
            "Especialista en Finanzas",
            "Contabilidad y Finanzas",
            "sandra.lopez@unab.edu.co",
            "/com/unab/Profesores/images/sandra_lopez.jpg",
            "Económicas"
        ));
        
        profesores.add(new ProfesorItem(
            "Dr. Roberto Silva García",
            "Doctor en Medicina",
            "Anatomía y Fisiología",
            "roberto.silva@unab.edu.co",
            "/com/unab/Profesores/images/roberto_silva.jpg",
            "Medicina"
        ));
        
        profesores.add(new ProfesorItem(
            "Prof. Jennifer White Johnson",
            "Magíster en Lingüística Aplicada",
            "Idiomas e Inglés",
            "jennifer.white@unab.edu.co",
            "/com/unab/Profesores/images/jennifer_white.jpg",
            "Idiomas"
        ));
        
        profesores.add(new ProfesorItem(
            "Dr. Miguel Hernández Castro",
            "Doctor en Estadística",
            "Estadística y Análisis de Datos",
            "miguel.hernandez@unab.edu.co",
            "/com/unab/Profesores/images/miguel_hernandez.jpg",
            "Matemáticas"
        ));
        
        profesores.add(new ProfesorItem(
            "Abg. Patricia Ruiz Moreno",
            "Especialista en Derecho Constitucional",
            "Derecho y Jurisprudencia",
            "patricia.ruiz@unab.edu.co",
            "/com/unab/Profesores/images/patricia_ruiz.jpg",
            "Derecho"
        ));
        
        profesores.add(new ProfesorItem(
            "Dra. Carmen Vásquez Díaz",
            "Doctora en Psicología",
            "Psicología y Comportamiento",
            "carmen.vasquez@unab.edu.co",
            "/com/unab/Profesores/images/carmen_vasquez.jpg",
            "Psicología"
        ));
        
        profesores.add(new ProfesorItem(
            "Ing. Fernando Jiménez Ruiz",
            "Magíster en Ingeniería Civil",
            "Estructuras y Construcción",
            "fernando.jimenez@unab.edu.co",
            "/com/unab/Profesores/images/fernando_jimenez.jpg",
            "Ingeniería"
        ));
        
        profesores.add(new ProfesorItem(
            "Dra. Lucía Ramírez Torres",
            "Doctora en Biología",
            "Biología y Ecología",
            "lucia.ramirez@unab.edu.co",
            "/com/unab/Profesores/images/lucia_ramirez.jpg",
            "Ciencias"
        ));

        return profesores;
    }

    private JPanel createProfesorItem(ProfesorItem profesor) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(BACKGROUND_COLOR);
        item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        item.setPreferredSize(new Dimension(350, 480));
        item.setMaximumSize(new Dimension(350, 480));

        // Panel de imagen (foto del profesor)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(350, 200));
        imagePanel.setMaximumSize(new Dimension(350, 200));
        imagePanel.setBackground(BACKGROUND_COLOR);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(profesor.getImagePath()));
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(350, 200, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setVerticalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar un placeholder con emoji
            JLabel placeholderLabel = EmojiUtils.createEmojiLabel("👨‍🏫", 80f);
            placeholderLabel.setForeground(ACCENT_COLOR);
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setVerticalAlignment(JLabel.CENTER);
            imagePanel.add(placeholderLabel, BorderLayout.CENTER);
        }

        // Panel de información del profesor
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(PROFESOR_TITLE_BG);
        infoPanel.setPreferredSize(new Dimension(350, 280));
        infoPanel.setMaximumSize(new Dimension(350, 280));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Departamento/Área (tag superior)
        JLabel departamentoLabel = new JLabel(profesor.getDepartamento());
        departamentoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        departamentoLabel.setForeground(ACCENT_COLOR);
        departamentoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        departamentoLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        // Nombre del profesor
        JLabel nombreLabel = new JLabel("<html><div style='text-align: left;'><b>" + profesor.getNombre() + "</b></div></html>");
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nivel educativo
        JLabel nivelLabel = new JLabel("🎓 " + profesor.getNivelEducativo());
        nivelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nivelLabel.setForeground(Color.LIGHT_GRAY);
        nivelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Área o materia
        JLabel areaLabel = new JLabel("📚 " + profesor.getAreaMateria());
        areaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaLabel.setForeground(Color.LIGHT_GRAY);
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Correo electrónico
        JLabel correoLabel = new JLabel("📧 " + profesor.getCorreo());
        correoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        correoLabel.setForeground(CONTACT_COLOR);
        correoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        correoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hacer clic en el correo para copiarlo al portapapeles
        correoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                copyToClipboard(profesor.getCorreo());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                correoLabel.setForeground(CONTACT_COLOR.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                correoLabel.setForeground(CONTACT_COLOR);
            }
        });

        // Panel de información adicional
        JPanel extraInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        extraInfoPanel.setBackground(PROFESOR_TITLE_BG);
        
        JLabel infoLabel = new JLabel("💡 Clic en el email para copiar");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        extraInfoPanel.add(infoLabel);

        // Agregar espaciado dinámico
        infoPanel.add(departamentoLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(nombreLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(nivelLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(areaLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(correoLabel);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(extraInfoPanel);

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

    private void copyToClipboard(String email) {
        try {
            java.awt.datatransfer.StringSelection stringSelection = 
                new java.awt.datatransfer.StringSelection(email);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(stringSelection, null);
            
            JOptionPane.showMessageDialog(this,
                "Correo copiado al portapapeles:\n" + email,
                "Correo Copiado",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Correo del profesor: " + email + "\n\n" +
                "No se pudo copiar automáticamente, pero puedes copiarlo manualmente.",
                "Información de Contacto",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Clase interna para representar un profesor
    private static class ProfesorItem {
        private final String nombre;
        private final String nivelEducativo;
        private final String areaMateria;
        private final String correo;
        private final String imagePath;
        private final String departamento;

        public ProfesorItem(String nombre, String nivelEducativo, String areaMateria, 
                          String correo, String imagePath, String departamento) {
            this.nombre = nombre;
            this.nivelEducativo = nivelEducativo;
            this.areaMateria = areaMateria;
            this.correo = correo;
            this.imagePath = imagePath;
            this.departamento = departamento;
        }

        // Getters
        public String getNombre() { return nombre; }
        public String getNivelEducativo() { return nivelEducativo; }
        public String getAreaMateria() { return areaMateria; }
        public String getCorreo() { return correo; }
        public String getImagePath() { return imagePath; }
        public String getDepartamento() { return departamento; }
    }
}
