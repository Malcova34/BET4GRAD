/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Home;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Actividades.ActividadesFrame;
import com.unab.Horario.HorarioFrame;
import com.unab.Mapa.MapaFrame;
import com.unab.Noticias.NoticiasFrame;
import com.unab.Profesores.ProfesoresFrame;
import com.unab.Profile.ProfileFrame;
import com.unab.Ranking.RankingFrame;
import com.unab.Ruleta.RuletaFrame;
import com.unab.Tutorias.TutoriasFrame;

/**
 *
 * @author danie
 */
public class HomeFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color GAME_ITEM_BG = new Color(13, 27, 51);
    private static final Color GAME_TITLE_BG = new Color(42, 60, 88);
    
    private JPanel mainPanel;
    private JPanel gamesGrid;
    private String currentUserEmail;

    public HomeFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        setTitle("BET4GRAD - Inicio");
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
        
        // Crear el grid de juegos
        createGamesGrid();

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
        JLabel logo = new JLabel("BET4GRAD");
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
        html.append(text.substring(4));
        html.append("</span></html>");
        logo.setText(html.toString());

        // Botón de perfil
        JButton profileButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/unab/Login/icons/profile.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            profileButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Si no se encuentra la imagen, usar el emoji como fallback
            profileButton.setText("👤");
            profileButton.setFont(new Font("Arial", Font.PLAIN, 24));
        }
        profileButton.setBackground(HEADER_COLOR);
        profileButton.setForeground(Color.WHITE);
        profileButton.setFocusPainted(false);
        profileButton.setBorderPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        profileButton.addActionListener(e -> {
            ProfileFrame profileFrame = new ProfileFrame(currentUserEmail);
            profileFrame.setVisible(true);
            this.dispose();
        });

        // Efecto hover
        profileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                profileButton.setContentAreaFilled(true);
                profileButton.setBackground(new Color(255, 255, 255, 20));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                profileButton.setContentAreaFilled(false);
            }
        });

        header.add(logo, BorderLayout.WEST);
        header.add(profileButton, BorderLayout.EAST);
        
        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(20));
    }

    private void createGamesGrid() {
        gamesGrid = new JPanel(new GridLayout(2, 4, 20, 20));
        gamesGrid.setBackground(BACKGROUND_COLOR);
        gamesGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Lista de juegos
        List<GameItem> allGames = new ArrayList<>();
        allGames.addAll(createSlotGames());
        allGames.addAll(createLiveCasinoGames());

        // Agregar solo los primeros 8 juegos (4x2 grid)
        for (int i = 0; i < Math.min(8, allGames.size()); i++) {
            JPanel gameItem = createGameItem(allGames.get(i));
            gamesGrid.add(gameItem);
        }

        mainPanel.add(gamesGrid);
    }

    private List<GameItem> createSlotGames() {
        List<GameItem> games = new ArrayList<>();
        games.add(new GameItem("Actividades", "/com/unab/Home/icons/Actividades.png"));
        games.add(new GameItem("Noticias", "/com/unab/Home/icons/Noticias.png"));
        games.add(new GameItem("Mapa Unab", "/com/unab/Home/icons/Mapa.png"));
        games.add(new GameItem("Tutorias", "/com/unab/Home/icons/Tutorias.png"));
        return games;
    }

    private List<GameItem> createLiveCasinoGames() {
        List<GameItem> games = new ArrayList<>();
        games.add(new GameItem("Profesores", "/com/unab/Home/icons/Profesores.png"));
        games.add(new GameItem("Ruleta", "/com/unab/Home/icons/Ruleta.png"));
        games.add(new GameItem("Mi Horario", "/com/unab/Home/icons/Horario.png"));
        games.add(new GameItem("Ranking Estudiantil", "/com/unab/Home/icons/Ranking.png"));
        return games;
    }

    // Clase interna para representar un juego
    private static class GameItem {
        private String title;
        private String imagePath;

        public GameItem(String title, String imagePath) {
            this.title = title;
            this.imagePath = imagePath;
        }
    }

    private JPanel createGameItem(GameItem game) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(BACKGROUND_COLOR);
        item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        item.setPreferredSize(new Dimension(250, 300));
        item.setMaximumSize(new Dimension(250, 300));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel de imagen simplificado
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(250, 250));
        imagePanel.setMaximumSize(new Dimension(250, 250));
        imagePanel.setBackground(BACKGROUND_COLOR);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(game.imagePath));
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setVerticalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar un mensaje
            JLabel errorLabel = new JLabel("Imagen no disponible");
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(errorLabel, BorderLayout.CENTER);
        }

        // Título del juego
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(GAME_TITLE_BG);
        titlePanel.setPreferredSize(new Dimension(250, 60));
        titlePanel.setMaximumSize(new Dimension(250, 60));
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'><b>" + game.title + "</b></div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalGlue());

        item.add(imagePanel);
        item.add(titlePanel);

        // Efecto hover simple
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Abrir la ventana correspondiente según el título del juego
                switch (game.title) {
                    case "Actividades":
                        ActividadesFrame actividadesFrame = new ActividadesFrame(currentUserEmail);
                        actividadesFrame.setVisible(true);
                        dispose();
                        break;
                    case "Noticias":
                        NoticiasFrame noticiasFrame = new NoticiasFrame(currentUserEmail);
                        noticiasFrame.setVisible(true);
                        dispose();
                        break;
                    case "Mapa Unab":
                        MapaFrame mapaFrame = new MapaFrame(currentUserEmail);
                        mapaFrame.setVisible(true);
                        dispose();
                        break;
                    case "Tutorias":
                        TutoriasFrame tutoriasFrame = new TutoriasFrame(currentUserEmail);
                        tutoriasFrame.setVisible(true);
                        dispose();
                        break;
                    case "Profesores":
                        ProfesoresFrame profesoresFrame = new ProfesoresFrame(currentUserEmail);
                        profesoresFrame.setVisible(true);
                        dispose();
                        break;
                    case "Mi Horario":
                        HorarioFrame horarioFrame = new HorarioFrame(currentUserEmail);
                        horarioFrame.setVisible(true);
                        dispose();
                        break;
                    case "Ranking Estudiantil":
                        RankingFrame rankingFrame = new RankingFrame(currentUserEmail);
                        rankingFrame.setVisible(true);
                        dispose();
                        break;
                    case "Ruleta":
                        RuletaFrame ruletaFrame = new RuletaFrame(currentUserEmail);
                        ruletaFrame.setVisible(true);
                        dispose();
                        break;
                    // TODO: Agregar casos para otros elementos cuando se implementen
                    default:
                        // Por ahora, solo mostrar un mensaje para elementos no implementados
                        System.out.println("Función " + game.title + " no implementada aún");
                        break;
                }
            }
        });

        return item;
    }

    
}

