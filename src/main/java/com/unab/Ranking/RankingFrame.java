/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Ranking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

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
import com.unab.database.ActividadesDB;
import com.unab.database.ActividadesDB.UserApunabRanking;

/**
 * Frame para mostrar el ranking de estudiantes por puntos Apunab
 * @author danie
 */
public class RankingFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color RANKING_PANEL_BG = new Color(42, 60, 88);
    private static final Color RANKING_ITEM_BG = new Color(255, 255, 255);
    
    // Colores para el podio
    private static final Color GOLD_COLOR = new Color(255, 215, 0);      // Oro - 1er lugar
    private static final Color SILVER_COLOR = new Color(192, 192, 192);  // Plata - 2do lugar
    private static final Color BRONZE_COLOR = new Color(205, 127, 50);   // Bronce - 3er lugar
    private static final Color REGULAR_COLOR = new Color(100, 149, 237); // Otros lugares
    
    private JPanel mainPanel;
    private String currentUserEmail;

    public RankingFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Ranking Estudiantil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);

        // Configurar el panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Crear el header
        createHeader();
        
        // Crear el ranking
        createRankingPanel();

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
        JLabel logo = new JLabel("BET4GRAD - Ranking Estudiantil");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Ranking Estudiantil";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Ranking Estudiantil</html>");
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

    private void createRankingPanel() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Título del ranking
        JLabel titleLabel = new JLabel("🏆 Top 15 Estudiantes con Más Puntos Apunab", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        containerPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal del ranking
        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BoxLayout(rankingPanel, BoxLayout.Y_AXIS));
        rankingPanel.setBackground(RANKING_PANEL_BG);
        rankingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Obtener datos del ranking
        List<UserApunabRanking> rankings = ActividadesDB.getUserApunabRanking(15);

        if (rankings.isEmpty()) {
            // Mensaje cuando no hay datos
            JLabel noDataLabel = new JLabel("📊 No hay datos de ranking disponibles", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noDataLabel.setForeground(Color.WHITE);
            noDataLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            rankingPanel.add(noDataLabel);
        } else {
            // Crear header de la tabla
            JPanel headerPanel = createRankingHeader();
            rankingPanel.add(headerPanel);
            rankingPanel.add(Box.createVerticalStrut(10));

            // Crear filas del ranking
            for (UserApunabRanking user : rankings) {
                JPanel userPanel = createUserRankingPanel(user);
                rankingPanel.add(userPanel);
                rankingPanel.add(Box.createVerticalStrut(8));
            }
        }

        // Scroll pane para el ranking
        JScrollPane rankingScrollPane = new JScrollPane(rankingPanel);
        rankingScrollPane.setBorder(null);
        rankingScrollPane.setBackground(RANKING_PANEL_BG);
        rankingScrollPane.getViewport().setBackground(RANKING_PANEL_BG);
        rankingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rankingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        containerPanel.add(rankingScrollPane, BorderLayout.CENTER);

        // Panel de información adicional
        JPanel infoPanel = createInfoPanel();
        containerPanel.add(infoPanel, BorderLayout.SOUTH);

        mainPanel.add(containerPanel);
    }

    private JPanel createRankingHeader() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);

        // Posición
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        JLabel posHeader = new JLabel("🏆 Posición", SwingConstants.CENTER);
        posHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        posHeader.setForeground(ACCENT_COLOR);
        headerPanel.add(posHeader, gbc);

        // Nombre
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JLabel nameHeader = new JLabel("👤 Nombre Completo", SwingConstants.LEFT);
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameHeader.setForeground(ACCENT_COLOR);
        headerPanel.add(nameHeader, gbc);

        // Puntos
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JLabel pointsHeader = new JLabel("💰 Apunabs", SwingConstants.CENTER);
        pointsHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pointsHeader.setForeground(ACCENT_COLOR);
        headerPanel.add(pointsHeader, gbc);

        return headerPanel;
    }

    private JPanel createUserRankingPanel(UserApunabRanking user) {
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBackground(RANKING_ITEM_BG);
        
        // Determinar color según posición
        Color positionColor = getPositionColor(user.getPosicion());
        
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(positionColor, 2),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);

        // Posición con emoji especial para top 3
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        String positionText = getPositionText(user.getPosicion());
        JLabel positionLabel = new JLabel(positionText, SwingConstants.CENTER);
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        positionLabel.setForeground(positionColor);
        userPanel.add(positionLabel, gbc);

        // Nombre completo
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JLabel nameLabel = new JLabel(user.getNombre());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(Color.BLACK);
        userPanel.add(nameLabel, gbc);

        // Puntos Apunab
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JLabel pointsLabel = new JLabel(user.getPuntos() + " pts", SwingConstants.CENTER);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pointsLabel.setForeground(positionColor);
        userPanel.add(pointsLabel, gbc);

        // Resaltar al usuario actual
        if (user.getEmail().equals(currentUserEmail)) {
            userPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 3),
                BorderFactory.createEmptyBorder(11, 19, 11, 19)
            ));
            userPanel.setBackground(new Color(255, 255, 200)); // Amarillo claro
        }

        return userPanel;
    }

    private Color getPositionColor(int position) {
        switch (position) {
            case 1: return GOLD_COLOR;
            case 2: return SILVER_COLOR;
            case 3: return BRONZE_COLOR;
            default: return REGULAR_COLOR;
        }
    }

    private String getPositionText(int position) {
        switch (position) {
            case 1: return "🥇 1°";
            case 2: return "🥈 2°";
            case 3: return "🥉 3°";
            default: return "#" + position;
        }
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Información sobre cómo ganar puntos
        JLabel infoTitle = new JLabel("💡 ¿Cómo ganar puntos Apunab?", SwingConstants.CENTER);
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoTitle.setForeground(ACCENT_COLOR);
        infoPanel.add(infoTitle);

        infoPanel.add(Box.createVerticalStrut(10));

        String[] tips = {
            "🎯 Participa en actividades extracurriculares",
            "📚 Regístrate en tutorías académicas",
            "🏆 Completa eventos especiales de la universidad",
            "⭐ Mantente activo en la comunidad estudiantil"
        };

        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));
        tipsPanel.setBackground(new Color(42, 60, 88));
        tipsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tipLabel.setForeground(Color.WHITE);
            tipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            tipsPanel.add(tipLabel);
            tipsPanel.add(Box.createVerticalStrut(5));
        }

        infoPanel.add(tipsPanel);
        return infoPanel;
    }
}
