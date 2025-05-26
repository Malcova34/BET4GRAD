package com.unab.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JLabel;

/**
 * Utilidad para manejar emojis en la aplicación (versión simplificada)
 */
public class EmojiUtils {
    
    private static Font emojiFont;
    private static boolean fontLoaded = false;
    
    /**
     * Inicializa la fuente de emojis
     */
    public static void initializeEmojiFont() {
        if (fontLoaded) return;
        
        try {
            // Buscar fuentes que soporten emojis en el sistema
            Font[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
            
            // Prioridad de fuentes para emojis
            String[] preferredFonts = {
                "Segoe UI Emoji",    // Windows
                "Apple Color Emoji", // macOS
                "Noto Color Emoji",  // Linux
                "Twemoji",          // Twitter
                "EmojiOne",         // EmojiOne
                "Segoe UI Symbol",   // Windows fallback
                "Symbola"           // Linux fallback
            };
            
            // Intentar usar fuente preferida
            for (String fontName : preferredFonts) {
                for (Font font : availableFonts) {
                    if (font.getFontName().equalsIgnoreCase(fontName)) {
                        emojiFont = font;
                        fontLoaded = true;
                        System.out.println("✓ Fuente de emoji encontrada: " + font.getFontName());
                        return;
                    }
                }
            }
            
            // Buscar cualquier fuente que contenga "emoji" en el nombre
            for (Font font : availableFonts) {
                String fontName = font.getFontName().toLowerCase();
                if (fontName.contains("emoji") || 
                    fontName.contains("symbol") ||
                    fontName.contains("noto")) {
                    emojiFont = font;
                    fontLoaded = true;
                    System.out.println("✓ Fuente de emoji encontrada: " + font.getFontName());
                    return;
                }
            }
            
            // Fallback: usar Segoe UI (Windows) o Dialog (multiplataforma)
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                emojiFont = new Font("Segoe UI", Font.PLAIN, 12);
                System.out.println("⚠ Usando Segoe UI como fallback para emojis");
            } else {
                emojiFont = new Font("Dialog", Font.PLAIN, 12);
                System.out.println("⚠ Usando Dialog como fallback para emojis");
            }
            fontLoaded = true;
            
        } catch (Exception e) {
            System.err.println("Error al cargar fuente de emoji: " + e.getMessage());
            emojiFont = new Font("Dialog", Font.PLAIN, 12);
            fontLoaded = true;
        }
    }
    
    /**
     * Configura un JLabel para mostrar emojis correctamente
     * @param label El JLabel a configurar
     * @param emojiText Texto con emojis
     * @param fontSize Tamaño de fuente
     */
    public static void setEmojiText(JLabel label, String emojiText, float fontSize) {
        initializeEmojiFont();
        
        label.setText(emojiText);
        
        if (emojiFont != null) {
            label.setFont(emojiFont.deriveFont(fontSize));
        } else {
            // Fallback simple
            label.setFont(new Font("Dialog", Font.PLAIN, (int)fontSize));
        }
    }
    
    /**
     * Crea un JLabel optimizado para mostrar emojis
     * @param emojiText Texto con emojis
     * @param fontSize Tamaño de fuente
     * @return JLabel configurado para emojis
     */
    public static JLabel createEmojiLabel(String emojiText, float fontSize) {
        JLabel label = new JLabel();
        setEmojiText(label, emojiText, fontSize);
        return label;
    }
    
    /**
     * Obtiene emojis comunes para usar en la aplicación
     */
    public static class CommonEmojis {
        public static final String USER = "👤";
        public static final String TROPHY = "🏆";
        public static final String CALENDAR = "📅";
        public static final String CHECK = "✅";
        public static final String EDIT = "✏️";
        public static final String LOCK = "🔒";
        public static final String DOOR = "🚪";
        public static final String CLIPBOARD = "📋";
        public static final String SECURITY = "🔐";
        public static final String FOOTBALL = "⚽";
        public static final String COMPUTER = "💻";
        public static final String CULTURE = "🎭";
        public static final String CODE = "💾";
        public static final String HEART = "❤️";
        public static final String ROBOT = "🤖";
        public static final String STAR = "⭐";
        public static final String FIRE = "🔥";
        public static final String THUMBS_UP = "👍";
        public static final String CLAP = "👏";
        public static final String GRADUATION = "🎓";
        public static final String BOOK = "📚";
        public static final String LIGHT_BULB = "💡";
        public static final String TARGET = "🎯";
        public static final String MONEY = "💰";
        public static final String GIFT = "🎁";
        public static final String MEDAL = "🏅";
        public static final String CROWN = "👑";
        public static final String ROCKET = "🚀";
    }
    
    /**
     * Verifica si el sistema soporta emojis básicos
     */
    public static boolean isEmojiSupported() {
        try {
            initializeEmojiFont();
            return emojiFont != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene información sobre las fuentes de emoji disponibles
     */
    public static String getAvailableEmojiFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        StringBuilder emojiFonts = new StringBuilder();
        
        for (Font font : fonts) {
            String fontName = font.getFontName().toLowerCase();
            if (fontName.contains("emoji") || 
                fontName.contains("symbol") ||
                fontName.contains("noto") ||
                fontName.contains("twemoji")) {
                if (emojiFonts.length() > 0) emojiFonts.append(", ");
                emojiFonts.append(font.getFontName());
            }
        }
        
        return emojiFonts.length() > 0 ? emojiFonts.toString() : "Ninguna fuente de emoji específica detectada";
    }
} 