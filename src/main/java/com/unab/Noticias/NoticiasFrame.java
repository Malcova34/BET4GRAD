/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Noticias;

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
 * Frame para mostrar noticias de la universidad
 * @author danie
 */
public class NoticiasFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color NEWS_ITEM_BG = new Color(13, 27, 51);
    private static final Color NEWS_TITLE_BG = new Color(42, 60, 88);
    private static final Color READ_MORE_COLOR = new Color(52, 152, 219); // Azul para "Leer más"
    
    private JPanel mainPanel;
    private JPanel newsGrid;
    private String currentUserEmail;

    public NoticiasFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Noticias");
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
        
        // Crear el grid de noticias
        createNewsGrid();

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
        JLabel logo = new JLabel("BET4GRAD - Noticias");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Noticias";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Noticias</html>");
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

    private void createNewsGrid() {
        newsGrid = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas, filas dinámicas
        newsGrid.setBackground(BACKGROUND_COLOR);
        newsGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Obtener noticias estáticas
        List<NoticiaItem> noticias = getStaticNews();

        for (NoticiaItem noticia : noticias) {
            JPanel newsItem = createNewsItem(noticia);
            newsGrid.add(newsItem);
        }

        mainPanel.add(newsGrid);
    }

    private List<NoticiaItem> getStaticNews() {
        List<NoticiaItem> noticias = new ArrayList<>();
        
        noticias.add(new NoticiaItem(
            "Nueva Convocatoria de Becas 2024",
            "La UNAB abre convocatoria para becas de excelencia académica dirigidas a estudiantes de pregrado y posgrado.",
            "15 de Noviembre, 2024",
            "/com/unab/Noticias/images/becas.jpg", // Placeholder para imagen
            "Académico"
        ));
        
        noticias.add(new NoticiaItem(
            "Inauguración del Nuevo Laboratorio de IA",
            "Se inaugura el moderno laboratorio de Inteligencia Artificial con tecnología de última generación.",
            "12 de Noviembre, 2024",
            "/com/unab/Noticias/images/laboratorio_ia.jpg",
            "Tecnología"
        ));
        
        noticias.add(new NoticiaItem(
            "Festival Cultural UNAB 2024",
            "Gran festival cultural con participación de estudiantes de todas las carreras. Música, danza y teatro.",
            "10 de Noviembre, 2024",
            "/com/unab/Noticias/images/festival_cultural.jpg",
            "Cultura"
        ));
        
        noticias.add(new NoticiaItem(
            "Convenio Internacional con Universidad de Cambridge",
            "La UNAB firma importante convenio de intercambio académico con la prestigiosa Universidad de Cambridge.",
            "8 de Noviembre, 2024",
            "/com/unab/Noticias/images/convenio_cambridge.jpg",
            "Internacional"
        ));
        
        noticias.add(new NoticiaItem(
            "Ranking Nacional: UNAB entre las Top 10",
            "La Universidad se posiciona entre las 10 mejores universidades del país según ranking nacional 2024.",
            "5 de Noviembre, 2024",
            "/com/unab/Noticias/images/ranking_nacional.jpg",
            "Reconocimiento"
        ));
        
        noticias.add(new NoticiaItem(
            "Semana del Emprendimiento Universitario",
            "Estudiantes presentan proyectos innovadores durante la Semana del Emprendimiento con premios en efectivo.",
            "3 de Noviembre, 2024",
            "/com/unab/Noticias/images/emprendimiento.jpg",
            "Emprendimiento"
        ));
        
        noticias.add(new NoticiaItem(
            "Nueva Biblioteca Digital Interactiva",
            "Se lanza la nueva plataforma de biblioteca digital con más de 500,000 recursos académicos disponibles.",
            "1 de Noviembre, 2024",
            "/com/unab/Noticias/images/biblioteca_digital.jpg",
            "Tecnología"
        ));
        
        noticias.add(new NoticiaItem(
            "Graduación Extraordinaria 2024",
            "Ceremonia especial de graduación para estudiantes destacados con participación de autoridades nacionales.",
            "28 de Octubre, 2024",
            "/com/unab/Noticias/images/graduacion.jpg",
            "Académico"
        ));
        
        noticias.add(new NoticiaItem(
            "Conferencia Internacional de Sostenibilidad",
            "UNAB será sede de la Conferencia Internacional sobre Desarrollo Sostenible y Cambio Climático.",
            "25 de Octubre, 2024",
            "/com/unab/Noticias/images/sostenibilidad.jpg",
            "Sostenibilidad"
        ));

        return noticias;
    }

    private JPanel createNewsItem(NoticiaItem noticia) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(BACKGROUND_COLOR);
        item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        item.setPreferredSize(new Dimension(350, 450));
        item.setMaximumSize(new Dimension(350, 450));

        // Panel de imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(350, 200));
        imagePanel.setMaximumSize(new Dimension(350, 200));
        imagePanel.setBackground(BACKGROUND_COLOR);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(noticia.getImagePath()));
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(350, 200, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setVerticalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar un placeholder con emoji
            JLabel placeholderLabel = EmojiUtils.createEmojiLabel("📰", 80f);
            placeholderLabel.setForeground(ACCENT_COLOR);
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setVerticalAlignment(JLabel.CENTER);
            imagePanel.add(placeholderLabel, BorderLayout.CENTER);
        }

        // Panel de información de la noticia
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(NEWS_TITLE_BG);
        infoPanel.setPreferredSize(new Dimension(350, 250));
        infoPanel.setMaximumSize(new Dimension(350, 250));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Categoría de la noticia
        JLabel categoryLabel = new JLabel(noticia.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryLabel.setForeground(ACCENT_COLOR);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        // Título de la noticia
        JLabel titleLabel = new JLabel("<html><div style='text-align: left;'><b>" + noticia.getTitle() + "</b></div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descripción de la noticia
        JLabel descLabel = new JLabel("<html><div style='text-align: left; margin-top: 5px;'>" + noticia.getDescription() + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fecha de la noticia
        JLabel dateLabel = new JLabel(EmojiUtils.CommonEmojis.CALENDAR + " " + noticia.getDate());
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de botón
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        buttonPanel.setBackground(NEWS_TITLE_BG);

        // Botón "Leer más"
        JButton readMoreButton = createActionButton("📖 Leer Más", READ_MORE_COLOR);
        readMoreButton.addActionListener(e -> {
            showNewsDetail(noticia);
        });

        buttonPanel.add(readMoreButton);

        // Agregar espaciado dinámico
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalGlue()); // Empuja el contenido inferior hacia abajo
        infoPanel.add(dateLabel);
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

    private void showNewsDetail(NoticiaItem noticia) {
        String message = String.format(
            "📰 %s\n\n" +
            "📅 Fecha: %s\n" +
            "🏷️ Categoría: %s\n\n" +
            "📄 Descripción:\n%s\n\n" +
            "🔗 Para más información, visita el portal web de la universidad.",
            noticia.getTitle(),
            noticia.getDate(),
            noticia.getCategory(),
            noticia.getDescription()
        );

        JOptionPane.showMessageDialog(this,
            message,
            "Detalle de la Noticia",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Clase interna para representar una noticia
    private static class NoticiaItem {
        private final String title;
        private final String description;
        private final String date;
        private final String imagePath;
        private final String category;

        public NoticiaItem(String title, String description, String date, String imagePath, String category) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.imagePath = imagePath;
            this.category = category;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getImagePath() { return imagePath; }
        public String getCategory() { return category; }
    }
}
