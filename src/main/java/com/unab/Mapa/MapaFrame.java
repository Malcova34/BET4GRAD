/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Mapa;

import java.awt.BorderLayout;
import java.awt.Color;
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
import com.unab.Home.HomeFrame;
import com.unab.utils.EmojiUtils;

/**
 * Frame para mostrar el mapa con la ubicación de la UNAB
 * @author danie
 */
public class MapaFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PANEL_BG = new Color(13, 27, 51);
    private static final Color CARD_BG = new Color(42, 60, 88);
    
    // Coordenadas de la UNAB - Universidad Autónoma de Bucaramanga
    private static final double UNAB_LATITUDE = 7.1193;
    private static final double UNAB_LONGITUDE = -73.1227;
    private static final String UNAB_ADDRESS = "Avenida 42 No. 48-11, Bucaramanga, Santander, Colombia";
    private static final String UNAB_PLUS_CODE = "4V8V+QW Bucaramanga, Santander";
    
    private JPanel mainPanel;
    private String currentUserEmail;

    public MapaFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Mapa UNAB");
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
        
        // Crear el contenido del mapa
        createMapContent();

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
        JLabel logo = new JLabel("BET4GRAD - Mapa UNAB");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Mapa UNAB";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Mapa UNAB</html>");
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

    private void createMapContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de información
        JPanel infoPanel = createInfoPanel();
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Panel visual del mapa
        JPanel mapPanel = createMapDisplayPanel();
        contentPanel.add(mapPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Panel de acciones
        JPanel actionsPanel = createActionsPanel();
        contentPanel.add(actionsPanel);

        mainPanel.add(contentPanel);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Título
        JLabel titleLabel = EmojiUtils.createEmojiLabel(
            EmojiUtils.CommonEmojis.TARGET + " Ubicación UNAB", 24f);
        titleLabel.setForeground(ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Información de ubicación
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Dirección
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createFieldLabel("📍 Dirección:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(UNAB_ADDRESS), gbc);

        // Coordenadas
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createFieldLabel("🌐 Coordenadas:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue(UNAB_PLUS_CODE), gbc);

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createFieldLabel("📞 Teléfono:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue("+57 (7) 643 6111"), gbc);

        // Sitio web
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createFieldLabel("🌐 Sitio Web:"), gbc);
        gbc.gridx = 1;
        panel.add(createFieldValue("www.unab.edu.co"), gbc);

        return panel;
    }

    private JPanel createMapDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(20, 10, 20, 10)
        ));
        panel.setPreferredSize(new Dimension(1160, 400));

        // Título del mapa
        JLabel mapTitle = EmojiUtils.createEmojiLabel(
            "🗺️ Ubicación de la Universidad Autónoma de Bucaramanga", 20f);
        mapTitle.setForeground(ACCENT_COLOR);
        mapTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        mapTitle.setHorizontalAlignment(JLabel.CENTER);
        panel.add(mapTitle, BorderLayout.NORTH);

        // Contenido visual del mapa
        JPanel mapContent = new JPanel();
        mapContent.setLayout(new BoxLayout(mapContent, BoxLayout.Y_AXIS));
        mapContent.setBackground(CARD_BG);

        // Emoji de mapa grande
        JLabel mapEmoji = EmojiUtils.createEmojiLabel("🏛️", 120f);
        mapEmoji.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        mapEmoji.setHorizontalAlignment(JLabel.CENTER);

        // Texto descriptivo
        JLabel descriptionLabel = new JLabel(
            "<html><div style='text-align: center; width: 100%;'>" +
            "<b>Universidad Autónoma de Bucaramanga</b><br><br>" +
            "📍 " + UNAB_ADDRESS + "<br><br>" +
            "🌐 Código Plus: " + UNAB_PLUS_CODE + "<br><br>" +
            "🗺️ Haz clic en 'Ver en Google Maps' para abrir el mapa interactivo<br>" +
            "📱 Compatible con GPS y navegación en tiempo real" +
            "</div></html>"
        );
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);

        mapContent.add(Box.createVerticalStrut(70));
        mapContent.add(mapEmoji);
        mapContent.add(Box.createVerticalStrut(40));
        mapContent.add(descriptionLabel);
        mapContent.add(Box.createVerticalStrut(70));

        panel.add(mapContent, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);

        // Botón para abrir en Google Maps
        JButton googleMapsButton = createActionButton(
            "🗺️ Ver en Google Maps", 
            new Color(52, 152, 219)
        );
        googleMapsButton.addActionListener(e -> openGoogleMaps());

        panel.add(googleMapsButton);

        return panel;
    }

    private void openGoogleMaps() {
        try {
            String googleMapsUrl = "https://maps.app.goo.gl/qgihMoUmTEeTL7VC8";
            java.awt.Desktop.getDesktop().browse(new java.net.URI(googleMapsUrl));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir Google Maps.\n\n" +
                "Código Plus: " + UNAB_PLUS_CODE + "\n\n" +
                "Puedes buscar este código en Google Maps manualmente.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ACCENT_COLOR);
        return label;
    }

    private JLabel createFieldValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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
}
