/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unab.Ruleta;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.unab.Home.HomeFrame;
import com.unab.database.UserAuth;

/**
 * Frame para el juego de ruleta con puntos Apunab
 * @author danie
 */
public class RuletaFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(26, 42, 74);
    private static final Color HEADER_COLOR = new Color(13, 27, 51);
    private static final Color ACCENT_COLOR = new Color(255, 193, 7);
    private static final Color PANEL_BG = new Color(42, 60, 88);
    private static final Color CASINO_GREEN = new Color(0, 100, 0);
    
    // Configuración de la ruleta
    private static final int NUMEROS_RULETA = 37; // 0-36
    private static final Color[] COLORES_NUMEROS = new Color[NUMEROS_RULETA];
    private static final String[] NOMBRES_NUMEROS = new String[NUMEROS_RULETA];
    
    // Variables del juego
    private String currentUserEmail;
    private int apunabsJugador = 0;
    private int apuestaActual = 0;
    private int numeroGanador = -1;
    private boolean girando = false;
    
    // Componentes de la interfaz
    private JPanel panelRuleta;
    private JLabel labelApunabs;
    private JLabel labelApuesta;
    private JTextField campoApuesta;
    private JComboBox<TipoApuesta> comboTipoApuesta;
    private JComboBox<Integer> comboNumero;
    private JButton botonGirar;
    private JButton botonApostar;
    private JTextArea areaResultados;
    private Timer timerGiro;
    private double anguloActual = 0;
    
    // Tipos de apuesta
    private enum TipoApuesta {
        NUMERO("Número específico", 35),
        ROJO("Rojo", 1),
        NEGRO("Negro", 1),
        PAR("Par", 1),
        IMPAR("Impar", 1),
        PRIMERA_MITAD("1-18", 1),
        SEGUNDA_MITAD("19-36", 1);
        
        private final String nombre;
        private final int multiplicador;
        
        TipoApuesta(String nombre, int multiplicador) {
            this.nombre = nombre;
            this.multiplicador = multiplicador;
        }
        
        @Override
        public String toString() {
            return nombre + " (x" + multiplicador + ")";
        }
    }

    public RuletaFrame(String userEmail) {
        this.currentUserEmail = userEmail;
        this.apunabsJugador = UserAuth.getUserApunabs(userEmail);
        
        setTitle("BET4GRAD - Ruleta de Apunabs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 900));
        setLocationRelativeTo(null);

        inicializarColores();
        inicializarInterfaz();
        configurarEventos();

        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarColores() {
        // Orden real de una ruleta europea
        int[] ordenRuleta = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};
        
        for (int i = 0; i < NUMEROS_RULETA; i++) {
            int numero = ordenRuleta[i];
            NOMBRES_NUMEROS[i] = String.valueOf(numero);
            
            if (numero == 0) {
                COLORES_NUMEROS[i] = CASINO_GREEN; // Verde para el 0
            } else {
                // Números rojos en una ruleta europea
                int[] numerosRojos = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
                boolean esRojo = false;
                for (int numRojo : numerosRojos) {
                    if (numero == numRojo) {
                        esRojo = true;
                        break;
                    }
                }
                COLORES_NUMEROS[i] = esRojo ? new Color(200, 0, 0) : new Color(20, 20, 20);
            }
        }
    }

    private void inicializarInterfaz() {
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Crear header
        createHeader(mainPanel);

        // Panel de contenido
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de la ruleta
        panelRuleta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarRuleta(g);
            }
        };
        panelRuleta.setPreferredSize(new Dimension(600, 600));
        panelRuleta.setBackground(CASINO_GREEN);
        panelRuleta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Panel de controles
        JPanel panelControles = createControlPanel();

        contentPanel.add(panelRuleta, BorderLayout.CENTER);
        contentPanel.add(panelControles, BorderLayout.EAST);

        mainPanel.add(contentPanel);
        add(mainPanel);

        // Configurar timer para animación
        timerGiro = new Timer(30, e -> {
            anguloActual += 12;
            if (anguloActual >= 360) {
                anguloActual = 0;
            }
            panelRuleta.repaint();
        });

        // Mensaje inicial
        areaResultados.append("🎰 ¡Bienvenido a la Ruleta BET4GRAD!\n");
        areaResultados.append("💰 Tienes " + apunabsJugador + " Apunabs para jugar.\n");
        areaResultados.append("🎯 Realiza una apuesta y gira la ruleta.\n");
        areaResultados.append("🍀 ¡Buena suerte!\n\n");
    }

    private void createHeader(JPanel mainPanel) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Logo
        JLabel logo = new JLabel("BET4GRAD - Ruleta de Apunabs");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Colorear partes del logo
        String text = "BET4GRAD - Ruleta de Apunabs";
        StringBuilder html = new StringBuilder("<html><span style='color: #ffc107;'>");
        html.append(text.substring(0, 3));
        html.append("</span>");
        html.append(text.substring(3, 4));
        html.append("<span style='color: #ffc107;'>");
        html.append(text.substring(4, 8));
        html.append("</span>");
        html.append(" - Ruleta de Apunabs</html>");
        logo.setText(html.toString());

        // Panel derecho con puntos y botón volver
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(HEADER_COLOR);

        // Mostrar puntos Apunab
        labelApunabs = new JLabel("💰 Apunabs: " + apunabsJugador + " pts");
        labelApunabs.setFont(new Font("Arial", Font.BOLD, 16));
        labelApunabs.setForeground(ACCENT_COLOR);
        labelApunabs.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

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

        rightPanel.add(labelApunabs);
        rightPanel.add(backButton);

        header.add(logo, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(header);
    }

    private JPanel createControlPanel() {
        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBackground(PANEL_BG);
        panelControles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelControles.setPreferredSize(new Dimension(350, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("🎲 Panel de Apuestas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panelControles.add(titleLabel, gbc);

        // Tipo de apuesta
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel labelTipo = new JLabel("🎯 Tipo de apuesta:");
        labelTipo.setForeground(Color.WHITE);
        labelTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelControles.add(labelTipo, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        comboTipoApuesta = new JComboBox<>(TipoApuesta.values());
        comboTipoApuesta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelControles.add(comboTipoApuesta, gbc);

        // Número específico
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel labelNumero = new JLabel("🔢 Número (0-36):");
        labelNumero.setForeground(Color.WHITE);
        labelNumero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelControles.add(labelNumero, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        Integer[] numeros = new Integer[37];
        for (int i = 0; i < 37; i++) {
            numeros[i] = i;
        }
        comboNumero = new JComboBox<>(numeros);
        comboNumero.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelControles.add(comboNumero, gbc);

        // Cantidad de apuesta
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel labelCantidad = new JLabel("💰 Cantidad:");
        labelCantidad.setForeground(Color.WHITE);
        labelCantidad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelControles.add(labelCantidad, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        campoApuesta = new JTextField("10", 10);
        campoApuesta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelControles.add(campoApuesta, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        botonApostar = createStyledButton("💸 Realizar Apuesta", new Color(0, 150, 0));
        panelControles.add(botonApostar, gbc);

        gbc.gridy = 5;
        botonGirar = createStyledButton("🎰 GIRAR RULETA", new Color(200, 0, 0));
        botonGirar.setEnabled(false);
        panelControles.add(botonGirar, gbc);

        // Información de apuesta actual
        gbc.gridy = 6;
        labelApuesta = new JLabel("No hay apuesta activa");
        labelApuesta.setForeground(ACCENT_COLOR);
        labelApuesta.setHorizontalAlignment(SwingConstants.CENTER);
        labelApuesta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelApuesta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        panelControles.add(labelApuesta, gbc);

        // Área de resultados
        gbc.gridy = 7; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        areaResultados = new JTextArea(12, 25);
        areaResultados.setEditable(false);
        areaResultados.setBackground(new Color(40, 40, 40));
        areaResultados.setForeground(Color.WHITE);
        areaResultados.setFont(new Font("Courier", Font.PLAIN, 11));
        areaResultados.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR), "📊 Historial de Juegos", 
            0, 0, new Font("Segoe UI", Font.BOLD, 12), ACCENT_COLOR));
        panelControles.add(scrollResultados, gbc);

        return panelControles;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setPreferredSize(new Dimension(280, 45));
        return button;
    }

    private void configurarEventos() {
        // Evento para habilitar/deshabilitar selector de número
        comboTipoApuesta.addActionListener(e -> {
            TipoApuesta tipo = (TipoApuesta) comboTipoApuesta.getSelectedItem();
            comboNumero.setEnabled(tipo == TipoApuesta.NUMERO);
        });

        // Evento para realizar apuesta
        botonApostar.addActionListener(e -> realizarApuesta());

        // Evento para girar ruleta
        botonGirar.addActionListener(e -> girarRuleta());

        // Inicializar estado del selector de número
        comboNumero.setEnabled(false);
    }

    private void realizarApuesta() {
        try {
            int cantidad = Integer.parseInt(campoApuesta.getText());
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La apuesta debe ser mayor a 0", 
                    "Apuesta Inválida", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (cantidad > apunabsJugador) {
                JOptionPane.showMessageDialog(this, 
                    "No tienes suficientes Apunabs\nTienes: " + apunabsJugador + " Apunabs\nNecesitas: " + cantidad + " Apunabs", 
                    "Fondos Insuficientes", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            apuestaActual = cantidad;
            TipoApuesta tipo = (TipoApuesta) comboTipoApuesta.getSelectedItem();
            
            String detalleApuesta = "💸 Apuesta: " + cantidad + " Apunabs en " + tipo.nombre;
            if (tipo == TipoApuesta.NUMERO) {
                detalleApuesta += " (Número " + comboNumero.getSelectedItem() + ")";
            }
            
            labelApuesta.setText("<html><center>" + detalleApuesta + "</center></html>");
            botonGirar.setEnabled(true);
            botonApostar.setEnabled(false);
            
            areaResultados.append("🎯 " + detalleApuesta + "\n");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingresa una cantidad válida", 
                "Entrada Inválida", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void girarRuleta() {
        if (girando) return;
        
        girando = true;
        botonGirar.setEnabled(false);
        
        // Generar número ganador
        Random random = new Random();
        numeroGanador = random.nextInt(NUMEROS_RULETA);
        
        areaResultados.append("🎰 Girando la ruleta...\n");
        
        // Animación de giro
        Timer timerGiroCompleto = new Timer(4000, e -> {
            timerGiro.stop();
            mostrarResultado();
        });
        
        timerGiro.start();
        timerGiroCompleto.setRepeats(false);
        timerGiroCompleto.start();
    }

    private void mostrarResultado() {
        girando = false;
        
        // Obtener el número real que salió
        int[] ordenRuleta = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};
        int numeroReal = ordenRuleta[numeroGanador];
        
        // Mostrar resultado
        String colorTexto = COLORES_NUMEROS[numeroGanador].equals(new Color(200, 0, 0)) ? "ROJO" :
                           COLORES_NUMEROS[numeroGanador].equals(new Color(20, 20, 20)) ? "NEGRO" : "VERDE";
        
        areaResultados.append("🛑 ¡La ruleta se detiene!\n");
        areaResultados.append("🎯 Número ganador: " + numeroReal + " (" + colorTexto + ")\n");
        
        // Verificar si ganó
        TipoApuesta tipoApuesta = (TipoApuesta) comboTipoApuesta.getSelectedItem();
        boolean gano = verificarGanador(tipoApuesta, numeroReal);
        
        if (gano) {
            int ganancia = apuestaActual * tipoApuesta.multiplicador;
            apunabsJugador += ganancia;
            
            // Actualizar en la base de datos
            UserAuth.addApunabs(currentUserEmail, ganancia);
            
            areaResultados.append("🎉 ¡GANASTE! +" + ganancia + " Apunabs\n");
            
            // Mostrar mensaje de victoria
            JOptionPane.showMessageDialog(this,
                "🎉 ¡FELICITACIONES! 🎉\n\n" +
                "Has ganado " + ganancia + " Apunabs\n" +
                "Número ganador: " + numeroReal + " (" + colorTexto + ")",
                "¡GANASTE!",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            apunabsJugador -= apuestaActual;
            
            // Actualizar en la base de datos
            UserAuth.subtractApunabs(currentUserEmail, apuestaActual);
            
            areaResultados.append("😔 Perdiste " + apuestaActual + " Apunabs\n");
        }
        
        // Actualizar interfaz
        labelApunabs.setText("💰 Apunabs: " + apunabsJugador + " pts");
        labelApuesta.setText("No hay apuesta activa");
        areaResultados.append("💰 Apunabs restantes: " + apunabsJugador + "\n\n");
        
        // Verificar si puede seguir jugando
        if (apunabsJugador <= 0) {
            areaResultados.append("💸 ¡Te has quedado sin Apunabs! \n");
            areaResultados.append("🎯 Participa en actividades para ganar más puntos.\n");
            botonApostar.setEnabled(false);
            
            JOptionPane.showMessageDialog(this,
                "😔 Te has quedado sin Apunabs\n\n" +
                "Participa en actividades y tutorías\n" +
                "para ganar más puntos Apunab",
                "Sin Fondos",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            botonApostar.setEnabled(true);
        }
        
        apuestaActual = 0;
        panelRuleta.repaint();
    }

    private boolean verificarGanador(TipoApuesta tipo, int numero) {
        switch (tipo) {
            case NUMERO:
                return numero == ((Integer) comboNumero.getSelectedItem()).intValue();
            case ROJO:
                int[] numerosRojos = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
                for (int numRojo : numerosRojos) {
                    if (numero == numRojo) return true;
                }
                return false;
            case NEGRO:
                int[] numerosNegros = {2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35};
                for (int numNegro : numerosNegros) {
                    if (numero == numNegro) return true;
                }
                return false;
            case PAR:
                return numero != 0 && numero % 2 == 0;
            case IMPAR:
                return numero != 0 && numero % 2 == 1;
            case PRIMERA_MITAD:
                return numero >= 1 && numero <= 18;
            case SEGUNDA_MITAD:
                return numero >= 19 && numero <= 36;
            default:
                return false;
        }
    }

    private void dibujarRuleta(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int centerX = panelRuleta.getWidth() / 2;
        int centerY = panelRuleta.getHeight() / 2;
        int radio = Math.min(centerX, centerY) - 50;
        
        // Dibujar sombra
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(centerX - radio - 15, centerY - radio - 10, (radio + 15) * 2, (radio + 15) * 2);
        
        // Marco exterior con colores BET4GRAD
        GradientPaint gradienteMarco = new GradientPaint(
            centerX - radio - 20, centerY - radio - 20,
            ACCENT_COLOR,
            centerX + radio + 20, centerY + radio + 20,
            ACCENT_COLOR.darker()
        );
        g2d.setPaint(gradienteMarco);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawOval(centerX - radio - 15, centerY - radio - 15, (radio + 15) * 2, (radio + 15) * 2);
        
        // Círculo base de la ruleta
        GradientPaint gradienteBase = new GradientPaint(
            centerX - radio, centerY - radio,
            new Color(205, 205, 205),
            centerX + radio, centerY + radio,
            new Color(105, 105, 105)
        );
        g2d.setPaint(gradienteBase);
        g2d.fillOval(centerX - radio - 8, centerY - radio - 8, (radio + 8) * 2, (radio + 8) * 2);
        
        // Dibujar sectores de números
        double anguloSector = 360.0 / NUMEROS_RULETA;
        
        for (int i = 0; i < NUMEROS_RULETA; i++) {
            double anguloInicio = (i * anguloSector + anguloActual) % 360;
            
            Arc2D sector = new Arc2D.Double(
                centerX - radio, centerY - radio, radio * 2, radio * 2,
                anguloInicio, anguloSector, Arc2D.PIE
            );
            
            // Color del sector con gradiente
            Color colorBase = COLORES_NUMEROS[i];
            Color colorClaro = colorBase.brighter();
            
            GradientPaint gradienteSector = new GradientPaint(
                (float)(centerX + Math.cos(Math.toRadians(anguloInicio)) * radio * 0.3),
                (float)(centerY + Math.sin(Math.toRadians(anguloInicio)) * radio * 0.3),
                colorClaro,
                (float)(centerX + Math.cos(Math.toRadians(anguloInicio + anguloSector)) * radio * 0.8),
                (float)(centerY + Math.sin(Math.toRadians(anguloInicio + anguloSector)) * radio * 0.8),
                colorBase
            );
            
            g2d.setPaint(gradienteSector);
            g2d.fill(sector);
            
            // Borde dorado
            g2d.setColor(ACCENT_COLOR);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(sector);
            
            // Dibujar número
            double anguloMedio = Math.toRadians(anguloInicio + anguloSector / 2);
            int textX = (int) (centerX + Math.cos(anguloMedio) * radio * 0.75);
            int textY = (int) (centerY + Math.sin(anguloMedio) * radio * 0.75);
            
            // Sombra del texto
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String numeroStr = NOMBRES_NUMEROS[i];
            g2d.drawString(numeroStr, 
                textX - fm.stringWidth(numeroStr) / 2 + 1, 
                textY + fm.getAscent() / 2 + 1);
            
            // Texto principal
            g2d.setColor(Color.WHITE);
            g2d.drawString(numeroStr, 
                textX - fm.stringWidth(numeroStr) / 2, 
                textY + fm.getAscent() / 2);
        }
        
        // Centro de la ruleta con logo BET4GRAD
        RadialGradientPaint gradienteCentro = new RadialGradientPaint(
            centerX, centerY, 30,
            new float[]{0.0f, 0.3f, 0.7f, 1.0f},
            new Color[]{
                Color.WHITE,
                ACCENT_COLOR,
                ACCENT_COLOR.darker(),
                HEADER_COLOR
            }
        );
        g2d.setPaint(gradienteCentro);
        g2d.fillOval(centerX - 30, centerY - 30, 60, 60);
        
        // Borde del centro
        g2d.setColor(HEADER_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centerX - 30, centerY - 30, 60, 60);
        
        // Logo BET4GRAD en el centro
        g2d.setColor(HEADER_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String textoLogo = "BET4GRAD";
        g2d.drawString(textoLogo, 
            centerX - fm.stringWidth(textoLogo) / 2, 
            centerY + fm.getAscent() / 2);
        
        // Indicador (flecha)
        g2d.setColor(ACCENT_COLOR);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int[] xPoints = {centerX, centerX - 12, centerX + 12};
        int[] yPoints = {centerY - radio - 20, centerY - radio + 5, centerY - radio + 5};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Borde de la flecha
        g2d.setColor(ACCENT_COLOR.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 3);
        
        // Mostrar número ganador
        if (numeroGanador != -1 && !girando) {
            int[] ordenRuleta = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};
            int numeroReal = ordenRuleta[numeroGanador];
            
            String colorTexto = COLORES_NUMEROS[numeroGanador].equals(new Color(200, 0, 0)) ? "ROJO" :
                               COLORES_NUMEROS[numeroGanador].equals(new Color(20, 20, 20)) ? "NEGRO" : "VERDE";
            
            // Fondo del resultado
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRoundRect(centerX - 120, centerY + radio + 30, 240, 60, 15, 15);
            
            // Borde dorado
            g2d.setColor(ACCENT_COLOR);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(centerX - 120, centerY + radio + 30, 240, 60, 15, 15);
            
            // Texto del ganador
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fmGanador = g2d.getFontMetrics();
            String ganadorTexto = "🎯 GANADOR: " + numeroReal;
            g2d.drawString(ganadorTexto, 
                centerX - fmGanador.stringWidth(ganadorTexto) / 2, 
                centerY + radio + 50);
            
            // Color del número
            g2d.setColor(COLORES_NUMEROS[numeroGanador]);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fmColor = g2d.getFontMetrics();
            g2d.drawString(colorTexto, 
                centerX - fmColor.stringWidth(colorTexto) / 2, 
                centerY + radio + 70);
        }
    }
}
