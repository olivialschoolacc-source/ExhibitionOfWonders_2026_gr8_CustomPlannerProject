package calendar.main;

// import events
import calendar.main.Events.Event;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;

public class MainPage extends JFrame {
    // Brightness Mode
    private enum BrightnessMode { LIGHT, DARK }
    private BrightnessMode brightnessMode = BrightnessMode.DARK;
    // Find Local Date
    private final LocalDate today;
    private YearMonth currentMonth;
    private Locale currentLocale;
    // Translator
    private final Translator translator;
    private final BulletJournal bulletJournal;
    // Basic Calendar Display
    private JPanel calendarPanel;
    private JLabel monthYearLabel;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    // Language Combo
    private JComboBox<String> languageCombo;
    private boolean updating = false;
    // Labels
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JLabel languageLabel;
    private JLabel todayLabel;
    // Bullet Journal Button
    private JButton bulletJournalButton;
    private JButton settingsButton;
    private JButton prevButton;
    private JButton nextButton;
    private BufferedImage buttonBaseImage;
    private BufferedImage buttonPressedBaseImage;
    private BufferedImage prevButtonImage;
    private BufferedImage nextButtonImage;
    private BufferedImage comboBoxBaseImage;
    private BufferedImage comboBoxTriggerImage;
    private BufferedImage comboPopupBgImage;
    private BufferedImage comboTrackImage;
    private BufferedImage comboThumbImage;
    private BufferedImage comboFocusImage;
    // JPanels
    @SuppressWarnings("FieldMayBeFinal")
    private JPanel mainContentPanel;
    private JPanel headerPanel;
    @SuppressWarnings("FieldMayBeFinal")
    private JPanel upcomingPanel;
    private Font customFont;
    private Font koreanFont;
    private Font chineseFont;
    private Font japaneseFont;
    // Dropdown color state for theme support
    private Color comboBoxTextColor = Color.WHITE;
    private Color comboBoxSelectionBgColor = new Color(34, 49, 63);
    private Color comboBoxSelectionFgColor = Color.WHITE;
    // Shared theme colors (set by applyBrightnessMode)
    private Color appBackground;
    private Color appForeground;
    private Color appPanelBackground;
    private Color appFieldBackground;
    private Color appBorderColor;
    private Color appSelectionBackground;
    private static final String CUSTOM_FONT_RESOURCE_PATH = "/calendar/main/assets/resources/fonts/Early GameBoy.ttf";
    private static final String KOREAN_FONT_RESOURCE_PATH = "/calendar/main/assets/resources/fonts/Korean.ttf";
    private static final String CHINESE_FONT_RESOURCE_PATH = "/calendar/main/assets/resources/fonts/Chinese.ttf";
    private static final String JAPANESE_FONT_RESOURCE_PATH = "/calendar/main/assets/resources/fonts/Japanese.ttf";
    // UpcomingEvents Label
    private JLabel upcomingTitleLabel;
    // Upcoming events container
    private JPanel upcomingEventsContentPanel;
    // Track currently expanded event detail for mutual exclusion
    private JTextArea currentlyExpandedDetail = null;
    // HashMap New
    private final Map<LocalDate, List<Event>> scheduleMap = new HashMap<>();
    // JComboBox countryCombo
    private JComboBox<String> countryCombo;
    // JLabel countryLabel
    private JLabel countryLabel;
    // Take current country info
    private String currentCountry = "Canada";
    private boolean saveScreenSize = false;
    private int savedWidth = 0;
    private int savedHeight = 0;
    
    @SuppressWarnings("FieldMayBeFinal")
    // Dimension ScreenSize Set
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public MainPage() {
        today = LocalDate.now();
        currentMonth = YearMonth.from(today);
        translator = new Translator();
        bulletJournal = new BulletJournal();
        loadState();
        loadBulletJournalState();
        if (currentLocale == null) {
            currentLocale = translator.getLocale("English");
        }
        translator.updateTranslations(translator.getLanguageName(currentLocale));

        loadCustomFont();
        loadButtonImages();
        loadDropdownImages();

        double widthRatio = 0.30;
        double heightRatio = 0.40;
        int windowWidth = (int) (screenSize.width * widthRatio);
        int windowHeight = (int) (screenSize.height * heightRatio);
        if (saveScreenSize && savedWidth > 0 && savedHeight > 0) {
            windowWidth = savedWidth;
            windowHeight = savedHeight;
        }

        setTitle(tr("Calendar Application"));
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        calendarPanel = createCalendarPanel(currentMonth);
        upcomingPanel = createUpcomingEventsPanel();
        mainContentPanel = new JPanel(new BorderLayout(10, 0));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        mainContentPanel.add(calendarPanel, BorderLayout.CENTER);
        mainContentPanel.add(upcomingPanel, BorderLayout.EAST);
        add(mainContentPanel, BorderLayout.CENTER);
        updateUpcomingEvents();

        // Update UI to reflect loaded state
        monthCombo.setSelectedItem(currentMonth.getMonth().getDisplayName(TextStyle.FULL, currentLocale));
        yearCombo.setSelectedItem(currentMonth.getYear());
        updateCalendar();

        // Apply UI defaults to existing components so buttons/labels use the custom font
        try {
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {
        }

        customizeComboBox(monthCombo);
        customizeComboBox(yearCombo);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (saveScreenSize) {
                    Dimension size = getSize();
                    savedWidth = size.width;
                    savedHeight = size.height;
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (saveScreenSize) {
                    Dimension size = getSize();
                    savedWidth = size.width;
                    savedHeight = size.height;
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveState();
            }
        });
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, tr("Settings"), true);
        dialog.setSize(550, 520);
        dialog.setLocationRelativeTo(this);

        Color background = getContentPane().getBackground();
        Color foreground = brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK;
        Color fieldBackground = brightnessMode == BrightnessMode.DARK ? background.brighter() : background;
        Color panelBackground = brightnessMode == BrightnessMode.DARK ? fieldBackground.brighter() : background.darker();

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBackground(background);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel headerLabel = new JLabel(tr("Settings"));
        headerLabel.setFont(uiFont(Font.BOLD, 18));
        headerLabel.setForeground(foreground);
        content.add(headerLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 1, 16, 16));
        center.setBackground(background);
        center.setOpaque(true);

        

        // Brightness Panel
        JPanel brightnessPanel = new JPanel(new BorderLayout(12, 8));
        brightnessPanel.setBackground(panelBackground);
        brightnessPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel brightnessLabel = new JLabel(tr("Brightness mode:"));
        brightnessLabel.setFont(uiFont(Font.PLAIN, 14));
        brightnessLabel.setForeground(foreground);
        JComboBox<String> brightnessCombo = new JComboBox<>(new String[] {tr("Dark"), tr("Light")});
        brightnessCombo.setFont(uiFont(Font.PLAIN, 14));
        brightnessCombo.setSelectedItem(brightnessMode == BrightnessMode.DARK ? tr("Dark") : tr("Light"));
        customizeComboBoxWithColors(brightnessCombo, fieldBackground, foreground);
        brightnessPanel.add(brightnessLabel, BorderLayout.WEST);
        brightnessPanel.add(brightnessCombo, BorderLayout.CENTER);
        center.add(brightnessPanel);
            // Brightness Panel
        // Language Panel
        JPanel languagePanel = new JPanel(new BorderLayout(12, 8));
        languagePanel.setBackground(panelBackground);
        languagePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel languageLabel = new JLabel(tr("Language:"));
        languageLabel.setFont(uiFont(Font.PLAIN, 14));
        languageLabel.setForeground(foreground);
        JComboBox<String> languageSelector = new JComboBox<>(translator.getLanguageNames().toArray(new String[0]));
        languageSelector.setFont(uiFont(Font.PLAIN, 14));
        languageSelector.setSelectedItem(translator.getLanguageName(currentLocale));
        customizeComboBoxWithColors(languageSelector, fieldBackground, foreground);
        languagePanel.add(languageLabel, BorderLayout.WEST);
        languagePanel.add(languageSelector, BorderLayout.CENTER);
        center.add(languagePanel);

        // Country Panel
        JPanel countryPanel = new JPanel(new BorderLayout(12, 8));
        countryPanel.setBackground(panelBackground);
        countryPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel countryLabel = new JLabel(tr("Country:"));
        countryLabel.setFont(uiFont(Font.PLAIN, 14));
        countryLabel.setForeground(foreground);
        JComboBox<String> countrySelector = new JComboBox<>(Holidays.getCountriesArray());
        countrySelector.setFont(uiFont(Font.PLAIN, 14));
        countrySelector.setSelectedItem(currentCountry);
        customizeComboBoxWithColors(countrySelector, fieldBackground, foreground);
        countryPanel.add(countryLabel, BorderLayout.WEST);
        countryPanel.add(countrySelector, BorderLayout.CENTER);
        center.add(countryPanel);

        // Screen Size Panel
        JPanel screenPanel = new JPanel(new BorderLayout(12, 8));
        screenPanel.setBackground(panelBackground);
        screenPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JCheckBox saveSizeCheck = new JCheckBox(tr("Save screen when resized"), saveScreenSize);
        saveSizeCheck.setFont(uiFont(Font.PLAIN, 14));
        saveSizeCheck.setBackground(panelBackground);
        saveSizeCheck.setForeground(foreground);
        saveSizeCheck.setOpaque(true);
        screenPanel.add(saveSizeCheck, BorderLayout.WEST);
        center.add(screenPanel);

        JLabel savedSizeLabel = new JLabel(tr("Saved size:") + " " + (savedWidth > 0 && savedHeight > 0 ? savedWidth + " x " + savedHeight : tr("Not set")));
        savedSizeLabel.setFont(uiFont(Font.PLAIN, 13));
        savedSizeLabel.setForeground(foreground);
        JPanel sizeDisplayPanel = new JPanel(new BorderLayout());
        sizeDisplayPanel.setBackground(panelBackground);
        sizeDisplayPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        sizeDisplayPanel.add(savedSizeLabel, BorderLayout.WEST);
        center.add(sizeDisplayPanel);

        content.add(center, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
            JPanel buttonWrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
            buttonWrapper.setOpaque(false);
            JButton cancelButton = createIconButton(tr("Cancel"));
            JButton saveButton = createIconButton(tr("Save"));
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            brightnessMode = tr("Dark").equals(brightnessCombo.getSelectedItem()) ? BrightnessMode.DARK : BrightnessMode.LIGHT;
            String selectedLanguage = (String) languageSelector.getSelectedItem();
            currentLocale = translator.getLocale(selectedLanguage);
            translator.updateTranslations(selectedLanguage);
            currentCountry = (String) countrySelector.getSelectedItem();
            saveScreenSize = saveSizeCheck.isSelected();
            if (saveScreenSize) {
                Dimension size = getSize();
                savedWidth = size.width;
                savedHeight = size.height;
            }
            loadButtonImages();
            loadDropdownImages();
            applyBrightnessMode();
            refreshUI();
            saveState();
            dialog.dispose();
        });
            buttonWrapper.add(cancelButton);
            buttonWrapper.add(saveButton);
            content.add(buttonWrapper, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        themeDialog(dialog);
        customizeComboBoxWithColors(brightnessCombo, fieldBackground, foreground);
        customizeComboBoxWithColors(languageSelector, fieldBackground, foreground);
        customizeComboBoxWithColors(countrySelector, fieldBackground, foreground);
        dialog.setVisible(true);
    }

    private void loadCustomFont() {
        customFont = loadFont("Early GameBoy.ttf", CUSTOM_FONT_RESOURCE_PATH);
        koreanFont = loadFont("Korean.ttf", KOREAN_FONT_RESOURCE_PATH);
        chineseFont = loadFont("Chinese.ttf", CHINESE_FONT_RESOURCE_PATH);
        japaneseFont = loadFont("Japanese.ttf", JAPANESE_FONT_RESOURCE_PATH);

        if (customFont != null) {
            customFont = customFont.deriveFont(8f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        }
        if (koreanFont != null) {
            koreanFont = koreanFont.deriveFont(Font.PLAIN, 16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(koreanFont);
        }
        if (chineseFont != null) {
            chineseFont = chineseFont.deriveFont(Font.PLAIN, 16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(chineseFont);
        }
        if (japaneseFont != null) {
            japaneseFont = japaneseFont.deriveFont(Font.PLAIN, 16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(japaneseFont);
        }

        if (customFont != null) {
            try {
                float uiSize = 8f;
                Font uiDerived = customFont.deriveFont(Font.PLAIN, uiSize);
                String sample = "A";
                if (uiDerived.canDisplayUpTo(sample) == -1) {
                    UIManager.put("Button.font", uiDerived);
                    UIManager.put("Label.font", uiDerived);
                    UIManager.put("ComboBox.font", uiDerived);
                    UIManager.put("TextField.font", uiDerived);
                    UIManager.put("TextArea.font", uiDerived);
                    UIManager.put("List.font", uiDerived);
                    UIManager.put("Table.font", uiDerived);
                    UIManager.put("ToggleButton.font", uiDerived);
                    UIManager.put("CheckBox.font", uiDerived);
                }
            } catch (Exception e) {
                System.err.println("Failed to apply custom font to UI defaults: " + e.getMessage());
            }
        }
    }

    private Font loadFont(String fileName, String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return Font.createFont(Font.TRUETYPE_FONT, is);
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load font from classpath " + resourcePath + ": " + e.getMessage());
        }

        Path[] searchPaths = new Path[] {
            Paths.get("calendar", "main", "assets", "resources", "fonts", fileName),
            Paths.get("assets", "resources", "fonts", fileName),
            Paths.get("resources", "fonts", fileName),
            Paths.get("fonts", fileName)
        };
        for (Path path : searchPaths) {
            if (Files.exists(path)) {
                try (InputStream fileStream = Files.newInputStream(path)) {
                    return Font.createFont(Font.TRUETYPE_FONT, fileStream);
                } catch (FontFormatException | IOException e) {
                    System.err.println("Could not load font from " + path + ": " + e.getMessage());
                }
            }
        }
        return null;
    }

    private BufferedImage loadImageResource(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Could not load image resource " + resourcePath + ": " + e.getMessage());
        }
        return null;
    }

    private BufferedImage loadImage(String directory, String fileName, boolean darkMode) {
        String darkFileName = null;
        int extIndex = fileName.lastIndexOf('.');
        if (darkMode && extIndex > 0) {
            darkFileName = fileName.substring(0, extIndex) + "_dark" + fileName.substring(extIndex);
        }

        String[] resourcePaths = darkFileName != null ? new String[] {
            "/calendar/main/assets/" + directory + "/" + darkFileName,
            "/assets/" + directory + "/" + darkFileName,
            "/calendar/main/assets/" + directory + "/" + fileName,
            "/assets/" + directory + "/" + fileName
        } : new String[] {
            "/calendar/main/assets/" + directory + "/" + fileName,
            "/assets/" + directory + "/" + fileName
        };

        for (String resourcePath : resourcePaths) {
            BufferedImage image = loadImageResource(resourcePath);
            if (image != null) {
                return image;
            }
        }

        Path[] searchRoots = new Path[] {
            Paths.get("calendar", "main", "assets"),
            Paths.get("assets"),
            Paths.get("app", "package-resources", "assets")
        };
        if (darkFileName != null) {
            for (Path root : searchRoots) {
                Path candidate = root.resolve(directory).resolve(darkFileName);
                if (Files.exists(candidate)) {
                    try {
                        return ImageIO.read(candidate.toFile());
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        for (Path root : searchRoots) {
            Path candidate = root.resolve(directory).resolve(fileName);
            if (Files.exists(candidate)) {
                try {
                    return ImageIO.read(candidate.toFile());
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    private Path findImagePath(String directory, String fileName, boolean darkMode) {
        String darkFileName = null;
        int extIndex = fileName.lastIndexOf('.');
        if (darkMode && extIndex > 0) {
            darkFileName = fileName.substring(0, extIndex) + "_dark" + fileName.substring(extIndex);
        }

        Path[] searchRoots = new Path[] {
            Paths.get("calendar", "main", "assets"),
            Paths.get("assets")
        };

        if (darkFileName != null) {
            for (Path root : searchRoots) {
                Path candidate = root.resolve(directory).resolve(darkFileName);
                if (Files.exists(candidate)) {
                    return candidate;
                }
            }
        }

        for (Path root : searchRoots) {
            Path candidate = root.resolve(directory).resolve(fileName);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    private void loadButtonImages() {
        try {
            buttonBaseImage = loadImage("buttons", "button_base.png", brightnessMode == BrightnessMode.DARK);
            buttonPressedBaseImage = loadImage("buttons", "button_pressed_base.png", brightnessMode == BrightnessMode.DARK);
            prevButtonImage = loadImage("other_UI", "previous.png", brightnessMode == BrightnessMode.DARK);
            nextButtonImage = loadImage("other_UI", "next.png", brightnessMode == BrightnessMode.DARK);
        } catch (Exception e) {
            System.err.println("Error loading button images: " + e.getMessage());
        }
    }

    private void loadDropdownImages() {
        try {
            comboBoxBaseImage = loadImage("dropdown", "ComboBox_base.png", brightnessMode == BrightnessMode.DARK);
            comboBoxTriggerImage = loadImage("dropdown", "DropDownTrigger.png", brightnessMode == BrightnessMode.DARK);
            comboPopupBgImage = loadImage("dropdown", "PopUpPanelBg_base.png", brightnessMode == BrightnessMode.DARK);
            comboTrackImage = loadImage("dropdown", "track.png", brightnessMode == BrightnessMode.DARK);
            comboThumbImage = loadImage("dropdown", "thumb.png", brightnessMode == BrightnessMode.DARK);
            comboFocusImage = loadImage("dropdown", "focus.png", brightnessMode == BrightnessMode.DARK);
        } catch (Exception e) {
            System.err.println("Error loading dropdown images: " + e.getMessage());
        }
    }

    private JButton createIconButton(String labelText) {
        return createIconButton(labelText, null);
    }

    private void refreshIconButton(JButton button, String labelText, BufferedImage overlayImage) {
        if (button == null) {
            return;
        }
        if (buttonBaseImage != null) {
            ImageIcon icon = createLabeledButtonIcon(buttonBaseImage, labelText, overlayImage);
            button.setIcon(icon);
            if (buttonPressedBaseImage != null) {
                ImageIcon pressedIcon = createLabeledButtonIcon(buttonPressedBaseImage, labelText, overlayImage);
                button.setPressedIcon(pressedIcon);
            }
            button.setToolTipText(labelText);
            button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        } else {
            button.setText(labelText);
            button.setFont(uiFont(Font.PLAIN, 8));
        }
    }

    private JButton createIconButton(String labelText, BufferedImage overlayImage) {
        JButton button = new JButton();
        if (buttonBaseImage != null) {
            ImageIcon icon = createLabeledButtonIcon(buttonBaseImage, labelText, overlayImage);
            button.setIcon(icon);
            // Use the same icon for disabled state so it doesn't turn gray
            button.setDisabledIcon(icon);
            button.setToolTipText(labelText);
            button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
            if (buttonPressedBaseImage != null) {
                ImageIcon pressedIcon = createLabeledButtonIcon(buttonPressedBaseImage, labelText, overlayImage);
                button.setPressedIcon(pressedIcon);
            }
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
        } else {
            button.setText(labelText);
            button.setFont(uiFont(Font.PLAIN, 8));
        }
        return button;
    }

    private ImageIcon createDisabledIcon(ImageIcon icon) {
        if (icon == null) return null;
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = src.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        RescaleOp op = new RescaleOp(new float[] {0.6f, 0.6f, 0.6f, 1f}, new float[] {0f, 0f, 0f, 0f}, null);
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        op.filter(src, dst);
        return new ImageIcon(dst);
    }

    private BufferedImage scaleOverlayImage(BufferedImage image, double scale) {
        if (image == null || scale == 1.0) {
            return image;
        }
        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }

    private ImageIcon createLabeledButtonIcon(BufferedImage image, String labelText) {
        return createLabeledButtonIcon(image, labelText, null);
    }

    private ImageIcon createLabeledButtonIcon(BufferedImage image, String labelText, BufferedImage overlayImage) {
        if (image == null) {
            return null;
        }
        Font font = null;
        if (currentLocale != null) {
            Font localeFont = getLocaleFont(currentLocale);
            if (localeFont != null) {
                font = localeFont.deriveFont(Font.PLAIN, 8f);
            }
        }
        if (font == null && customFont != null) {
            font = customFont.deriveFont(Font.PLAIN, 14f);
        }
        if (font == null) {
            font = uiFont(Font.PLAIN, 8);
        }
        BufferedImage measureImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureGraphics = measureImage.createGraphics();
        measureGraphics.setFont(font);
        FontMetrics fm = measureGraphics.getFontMetrics();
        int textWidth = fm.stringWidth(labelText != null ? labelText : "");
        int textHeight = fm.getHeight();
        measureGraphics.dispose();

        int overlayWidth = overlayImage != null ? overlayImage.getWidth() : 0;
        int targetWidth = Math.max(image.getWidth(), Math.max(textWidth + 40, overlayWidth + 24));
        int targetHeight = image.getHeight();
        BufferedImage buttonImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buttonImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw3Slice(g, image, targetWidth, targetHeight);

        if (overlayImage != null) {
            int overlayX = (targetWidth - overlayWidth) / 2;
            int overlayY = (targetHeight - overlayImage.getHeight()) / 2;
            g.drawImage(overlayImage, overlayX, overlayY, null);
        }

        if (labelText != null && !labelText.isEmpty()) {
            g.setFont(font);
            int x = (targetWidth - textWidth) / 2;
            int y = (targetHeight + textHeight) / 2 - fm.getDescent();
            g.setColor(new Color(0, 0, 0, 140));
            g.drawString(labelText, x + 1, y + 1);
            g.setColor(Color.WHITE);
            g.drawString(labelText, x, y);
        }
        g.dispose();
        return new ImageIcon(buttonImage);
    }

    public void draw3Slice(Graphics g, BufferedImage img, int targetWidth, int targetHeight) {
        int imgWidth = img.getWidth();
        int sliceWidth = imgWidth / 3;
        int leftWidth = sliceWidth;
        int rightWidth = sliceWidth;
        int centerWidth = targetWidth - leftWidth - rightWidth;

        if (centerWidth <= 0) {
            // Target too small to preserve three-slice; scale left and right proportionally to fit
            float scale = (float) targetWidth / (float) imgWidth;
            int newLeft = Math.max(1, Math.round(leftWidth * scale));
            int newRight = Math.max(1, targetWidth - newLeft);
            Image left = img.getSubimage(0, 0, leftWidth, img.getHeight());
            Image right = img.getSubimage(2 * sliceWidth, 0, rightWidth, img.getHeight());
            g.drawImage(left, 0, 0, newLeft, targetHeight, null);
            g.drawImage(right, newLeft, 0, newRight, targetHeight, null);
            return;
        }

        // 1. Left segment (fixed)
        Image left = img.getSubimage(0, 0, leftWidth, img.getHeight());
        g.drawImage(left, 0, 0, leftWidth, targetHeight, null);

        // 2. Center segment (stretched)
        Image center = img.getSubimage(sliceWidth, 0, sliceWidth, img.getHeight());
        g.drawImage(center, leftWidth, 0, centerWidth, targetHeight, null);

        // 3. Right segment (fixed)
        Image right = img.getSubimage(2 * sliceWidth, 0, rightWidth, img.getHeight());
        g.drawImage(right, leftWidth + centerWidth, 0, rightWidth, targetHeight, null);
    }

    private void customizeComboBox(JComboBox<?> comboBox) {
        comboBox.setOpaque(false);
        comboBox.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        comboBox.setBackground(new Color(0, 0, 0, 0));
        comboBox.setForeground(Color.WHITE);
        
        // Set colors for UI class
        comboBoxTextColor = Color.WHITE;
        comboBoxSelectionFgColor = Color.WHITE;
        comboBoxSelectionBgColor = new Color(34, 49, 63);
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(false);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });
        comboBox.setUI(new StyledComboBoxUI());
    }

    private void customizeComboBoxWithColors(JComboBox<?> comboBox, Color background, Color foreground) {
        comboBox.setOpaque(false);
        comboBox.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        comboBox.setBackground(new Color(0, 0, 0, 0));
        comboBox.setForeground(foreground);
        
        // Store colors for UI class to use
        comboBoxTextColor = foreground;
        comboBoxSelectionFgColor = foreground;
        comboBoxSelectionBgColor = brightnessMode == BrightnessMode.DARK ? new Color(60, 80, 100) : new Color(0x73, 0xA9, 0x42);
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(isSelected);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                label.setForeground(foreground);
                if (isSelected) {
                    label.setBackground(comboBoxSelectionBgColor);
                }
                return label;
            }
        });
        comboBox.setUI(new StyledComboBoxUI());
        comboBox.revalidate();
        comboBox.repaint();
    }

    private void reapplyDialogComboStyles(java.awt.Component comp, Color background, Color foreground) {
        if (comp instanceof javax.swing.JComboBox) {
            customizeComboBoxWithColors((JComboBox<?>) comp, background, foreground);
        }
        if (comp instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) comp).getComponents()) {
                reapplyDialogComboStyles(child, background, foreground);
            }
        }
    }

    private class StyledComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton arrowButton = new JButton() {
                @Override
                public Dimension getPreferredSize() {
                    if (comboBoxTriggerImage != null) {
                        return new Dimension(comboBoxTriggerImage.getWidth(), comboBoxTriggerImage.getHeight());
                    }
                    return super.getPreferredSize();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    if (comboBoxTriggerImage != null) {
                        int x = (getWidth() - comboBoxTriggerImage.getWidth()) / 2;
                        int y = (getHeight() - comboBoxTriggerImage.getHeight()) / 2;
                        g.drawImage(comboBoxTriggerImage, x, y, null);
                    } else {
                        super.paintComponent(g);
                    }
                }
            };
            arrowButton.setBorderPainted(false);
            arrowButton.setContentAreaFilled(false);
            arrowButton.setFocusPainted(false);
            arrowButton.setOpaque(false);
            return arrowButton;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            if (comboBoxBaseImage != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(bounds.x, bounds.y);
                draw3Slice(g2, comboBoxBaseImage, bounds.width, bounds.height);
                if (hasFocus && comboFocusImage != null) {
                    draw3Slice(g2, comboFocusImage, bounds.width, bounds.height);
                }
                g2.dispose();
            } else {
                super.paintCurrentValueBackground(g, bounds, hasFocus);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.setBorder(BorderFactory.createEmptyBorder());
                    scroller.setOpaque(false);
                    scroller.getViewport().setOpaque(false);
                    scroller.getVerticalScrollBar().setUI(new DropdownScrollBarUI());
                    scroller.setBackground(new Color(0, 0, 0, 0));
                    return scroller;
                }

                @Override
                protected void configurePopup() {
                    super.configurePopup();
                    setOpaque(false);
                    getList().setOpaque(false);
                    getList().setBackground(new Color(0, 0, 0, 0));
                    getList().setSelectionBackground(comboBoxSelectionBgColor);
                    getList().setSelectionForeground(comboBoxSelectionFgColor);
                }

                @Override
                public void paintComponent(Graphics g) {
                    if (comboPopupBgImage != null) {
                        draw9Slice(g, comboPopupBgImage, getWidth(), getHeight());
                    } else {
                        super.paintComponent(g);
                    }
                }
            };
            return popup;
        }
    }

    private class DropdownScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            if (comboTrackImage != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(trackBounds.x, trackBounds.y);
                draw3SliceVertical(g2, comboTrackImage, trackBounds.width, trackBounds.height);
                g2.dispose();
            } else {
                super.paintTrack(g, c, trackBounds);
            }
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (comboThumbImage != null) {
                g.drawImage(comboThumbImage, thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
            } else {
                super.paintThumb(g, c, thumbBounds);
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setOpaque(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusable(false);
            return button;
        }
    }

    private class UpcomingEventsScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(brightnessMode == BrightnessMode.DARK ? new Color(50, 50, 50) : new Color(240, 240, 240));
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color thumbColor = brightnessMode == BrightnessMode.DARK ? new Color(100, 100, 100) : new Color(180, 180, 180);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
            g2.setColor(brightnessMode == BrightnessMode.DARK ? new Color(130, 130, 130) : new Color(200, 200, 200));
            g2.setStroke(new java.awt.BasicStroke(1f));
            g2.drawRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setOpaque(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusable(false);
            return button;
        }
    }

    public void draw3SliceVertical(Graphics g, BufferedImage img, int targetWidth, int targetHeight) {
        int imgHeight = img.getHeight();
        int sliceHeight = imgHeight / 3;

        Image top = img.getSubimage(0, 0, img.getWidth(), sliceHeight);
        g.drawImage(top, 0, 0, targetWidth, sliceHeight, null);

        Image middle = img.getSubimage(0, sliceHeight, img.getWidth(), sliceHeight);
        g.drawImage(middle, 0, sliceHeight, targetWidth, targetHeight - (2 * sliceHeight), null);

        Image bottom = img.getSubimage(0, 2 * sliceHeight, img.getWidth(), sliceHeight);
        g.drawImage(bottom, 0, targetHeight - sliceHeight, targetWidth, sliceHeight, null);
    }

    public void draw9Slice(Graphics g, BufferedImage img, int targetWidth, int targetHeight) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        int sliceWidth = imgWidth / 3;
        int sliceHeight = imgHeight / 3;

        Image topLeft = img.getSubimage(0, 0, sliceWidth, sliceHeight);
        Image topCenter = img.getSubimage(sliceWidth, 0, sliceWidth, sliceHeight);
        Image topRight = img.getSubimage(2 * sliceWidth, 0, sliceWidth, sliceHeight);
        Image midLeft = img.getSubimage(0, sliceHeight, sliceWidth, sliceHeight);
        Image midCenter = img.getSubimage(sliceWidth, sliceHeight, sliceWidth, sliceHeight);
        Image midRight = img.getSubimage(2 * sliceWidth, sliceHeight, sliceWidth, sliceHeight);
        Image bottomLeft = img.getSubimage(0, 2 * sliceHeight, sliceWidth, sliceHeight);
        Image bottomCenter = img.getSubimage(sliceWidth, 2 * sliceHeight, sliceWidth, sliceHeight);
        Image bottomRight = img.getSubimage(2 * sliceWidth, 2 * sliceHeight, sliceWidth, sliceHeight);

        g.drawImage(topLeft, 0, 0, sliceWidth, sliceHeight, null);
        g.drawImage(topCenter, sliceWidth, 0, targetWidth - 2 * sliceWidth, sliceHeight, null);
        g.drawImage(topRight, targetWidth - sliceWidth, 0, sliceWidth, sliceHeight, null);
        g.drawImage(midLeft, 0, sliceHeight, sliceWidth, targetHeight - 2 * sliceHeight, null);
        g.drawImage(midCenter, sliceWidth, sliceHeight, targetWidth - 2 * sliceWidth, targetHeight - 2 * sliceHeight, null);
        g.drawImage(midRight, targetWidth - sliceWidth, sliceHeight, sliceWidth, targetHeight - 2 * sliceHeight, null);
        g.drawImage(bottomLeft, 0, targetHeight - sliceHeight, sliceWidth, sliceHeight, null);
        g.drawImage(bottomCenter, sliceWidth, targetHeight - sliceHeight, targetWidth - 2 * sliceWidth, sliceHeight, null);
        g.drawImage(bottomRight, targetWidth - sliceWidth, targetHeight - sliceHeight, sliceWidth, sliceHeight, null);
    }

    private ImageIcon createLabeledButtonIcon1(BufferedImage image, String labelText) {
        if (image == null) {
            return null;
        }
        Font font = customFont != null ? customFont.deriveFont(Font.PLAIN, 8f) : uiFont(Font.PLAIN, 8);
        BufferedImage measureImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureGraphics = measureImage.createGraphics();
        measureGraphics.setFont(font);
        FontMetrics fm = measureGraphics.getFontMetrics();
        int textWidth = fm.stringWidth(labelText);
        int textHeight = fm.getHeight();
        measureGraphics.dispose();

        int targetWidth = Math.max(image.getWidth(), textWidth + 40);
        int targetHeight = image.getHeight();
        BufferedImage buttonImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buttonImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw3Slice(g, image, targetWidth, targetHeight);

        g.setFont(font);
        int x = (targetWidth - textWidth) / 2;
        int y = (targetHeight + textHeight) / 2 - fm.getDescent();
        g.setColor(new Color(0, 0, 0, 140));
        g.drawString(labelText, x + 1, y + 1);
        g.setColor(Color.WHITE);
        g.drawString(labelText, x, y);
        g.dispose();
        return new ImageIcon(buttonImage);
    }

    public class SlicedPanel extends JPanel {
        private BufferedImage image;

        public SlicedPanel(String imagePath) {
            try {
                image = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                // Pass the Graphics object, the image, and the desired panel dimensions
                draw3Slice(g, image, this.getWidth(), this.getHeight());
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        prevButton = createIconButton("", scaleOverlayImage(prevButtonImage, 0.85));
        prevButton.setToolTipText(tr("Previous month"));
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        nextButton = createIconButton("", scaleOverlayImage(nextButtonImage, 0.85));
        nextButton.setToolTipText(tr("Next month"));
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        monthYearLabel = new JLabel(
                currentMonth.getMonth().getDisplayName(TextStyle.FULL, currentLocale)
                        + " " + currentMonth.getYear(),
                SwingConstants.CENTER);
        monthYearLabel.setFont(uiFont(Font.BOLD, 22));

        JPanel monthYearPanel = new JPanel(new BorderLayout());
        monthYearPanel.add(prevButton, BorderLayout.WEST);
        monthYearPanel.add(monthYearLabel, BorderLayout.CENTER);
        monthYearPanel.add(nextButton, BorderLayout.EAST);

        settingsButton = createIconButton(tr("Settings"));
        settingsButton.addActionListener(e -> showSettingsDialog());

        monthCombo = new JComboBox<>();
        for (Month month : Month.values()) {
            monthCombo.addItem(month.getDisplayName(TextStyle.FULL, currentLocale));
        }
        monthCombo.setSelectedItem(currentMonth.getMonth().getDisplayName(TextStyle.FULL, currentLocale));
        monthCombo.addActionListener(e -> {
            if (updating) return;
            String selectedMonthName = (String) monthCombo.getSelectedItem();
            Month selectedMonth = null;
            for (Month m : Month.values()) {
                if (m.getDisplayName(TextStyle.FULL, currentLocale).equals(selectedMonthName)) {
                    selectedMonth = m;
                    break;
                }
            }
            if (selectedMonth != null) {
                currentMonth = YearMonth.of(currentMonth.getYear(), selectedMonth);
                updateCalendar();
            }
        });

        yearCombo = new JComboBox<>();
        for (int year = 1950; year <= 2100; year++) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(currentMonth.getYear());
        yearCombo.addActionListener(e -> {
            int selectedYear = (Integer) yearCombo.getSelectedItem();
            currentMonth = YearMonth.of(selectedYear, currentMonth.getMonth());
            updateCalendar();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(monthYearPanel, BorderLayout.CENTER);
        topPanel.add(settingsButton, BorderLayout.EAST);

        JPanel dropdownPanel = new JPanel();
        monthLabel = new JLabel(tr("Month:"));
        monthLabel.setFont(uiFont(Font.PLAIN, 14));
        dropdownPanel.add(monthLabel);
        monthCombo.setFont(uiFont(Font.PLAIN, 14));
        dropdownPanel.add(monthCombo);
        yearLabel = new JLabel(tr("Year:"));
        yearLabel.setFont(uiFont(Font.PLAIN, 14));
        dropdownPanel.add(yearLabel);
        yearCombo.setFont(uiFont(Font.PLAIN, 14));
        dropdownPanel.add(yearCombo);

        customizeComboBox(monthCombo);
        customizeComboBox(yearCombo);

        controlsPanel.add(topPanel, BorderLayout.NORTH);
        controlsPanel.add(dropdownPanel, BorderLayout.SOUTH);

        header.add(controlsPanel, BorderLayout.CENTER);
        headerPanel = header;

        JPanel bottomPanel = new JPanel(new BorderLayout());
        todayLabel = new JLabel(tr("Today:") + " " + today.toString());
        todayLabel.setFont(uiFont(Font.PLAIN, 14));
        todayLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        todayLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        todayLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click: navigate to today's month
                    navigateToDate(today);
                }
            }
        });
        JPanel leftPanel = new JPanel(new BorderLayout(0, 6));
        leftPanel.setOpaque(false);

        bulletJournalButton = createIconButton(tr("Bullet Journal"));
        bulletJournalButton.setToolTipText(tr("Bullet Journal"));
        bulletJournalButton.addActionListener(e -> showBulletJournalDialog());

        JPanel buttonContainer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        buttonContainer.setOpaque(false);
        buttonContainer.add(bulletJournalButton);
        leftPanel.add(buttonContainer, BorderLayout.NORTH);
        leftPanel.add(todayLabel, BorderLayout.CENTER);
        bottomPanel.add(leftPanel, BorderLayout.WEST);

        header.add(bottomPanel, BorderLayout.SOUTH);

        return header;
        
    }


    private JPanel createCalendarPanel(YearMonth yearMonth) {
        Color panelBackground = brightnessMode == BrightnessMode.DARK ? new Color(0x2D, 0x6B, 0x0C) : new Color(0xAA, 0xD5, 0x76);
        Color headerBackground = brightnessMode == BrightnessMode.DARK ? new Color(0x36, 0x7A, 0x11) : new Color(0x73, 0xA9, 0x42);
        Color headerForeground = brightnessMode == BrightnessMode.DARK ? new Color(235, 240, 225) : new Color(0x53, 0x8D, 0x22);
        Color headerBorder = brightnessMode == BrightnessMode.DARK ? new Color(0x4B, 0x8D, 0x22) : new Color(0x53, 0x8D, 0x22);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(panelBackground);

        JPanel grid = new JPanel(new GridLayout(0, 7, 4, 4));
        grid.setBackground(panelBackground);
        DayOfWeek[] weekDays = {DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        for (DayOfWeek day : weekDays) {
            JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.SHORT, currentLocale), SwingConstants.CENTER);
            dayLabel.setFont(uiFont(Font.BOLD, 14));
            dayLabel.setOpaque(true);
            dayLabel.setBackground(headerBackground);
            dayLabel.setForeground(headerForeground);
            dayLabel.setBorder(BorderFactory.createLineBorder(headerBorder));
            grid.add(dayLabel);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int startOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < startOffset; i++) {
            JLabel emptyCell = new JLabel("");
            emptyCell.setOpaque(true);
            emptyCell.setBackground(panelBackground);
            grid.add(emptyCell);
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            grid.add(createDayButton(date));
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUpcomingEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, brightnessMode == BrightnessMode.DARK ? new Color(0x4B, 0x8D, 0x22) : new Color(0x73, 0xA9, 0x42)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setPreferredSize(new Dimension(260, 0));

        upcomingTitleLabel = new JLabel(tr("Upcoming events"));
        upcomingTitleLabel.setFont(uiFont(Font.BOLD, 16));
        panel.add(upcomingTitleLabel, BorderLayout.NORTH);

        upcomingEventsContentPanel = new JPanel();
        upcomingEventsContentPanel.setLayout(new javax.swing.BoxLayout(upcomingEventsContentPanel, javax.swing.BoxLayout.Y_AXIS));
        upcomingEventsContentPanel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(upcomingEventsContentPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(brightnessMode == BrightnessMode.DARK ? new Color(0x4B, 0x8D, 0x22) : new Color(0x73, 0xA9, 0x42)));
        scrollPane.setOpaque(false);
        scrollPane.setBackground(new Color(0,0,0,0));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0,0,0,0));
        javax.swing.JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setUnitIncrement(16);
        vBar.setUI(new UpcomingEventsScrollBarUI());
        vBar.setOpaque(false);
        javax.swing.JScrollBar hBar = scrollPane.getHorizontalScrollBar();
        hBar.setUI(new UpcomingEventsScrollBarUI());
        hBar.setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void updateUpcomingEvents() {
        Set<Event> upcomingSet = new HashSet<>();
        Date now = new Date();
        for (List<Event> list : scheduleMap.values()) {
            for (Event event : list) {
                if (!event.getEnd().before(now)) {
                    upcomingSet.add(event);
                }
            }
        }
        List<Event> upcoming = new ArrayList<>(upcomingSet);
        upcoming.sort(Comparator.comparing(Event::getStart));

        upcomingEventsContentPanel.removeAll();
        if (upcoming.isEmpty()) {
            JLabel noEventsLabel = new JLabel(tr("No upcoming events."));
            noEventsLabel.setFont(uiFont(Font.PLAIN, 13));
            noEventsLabel.setForeground(brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK);
            upcomingEventsContentPanel.add(noEventsLabel);
        } else {
            for (Event event : upcoming) {
                upcomingEventsContentPanel.add(createUpcomingEventPanel(event));
            }
        }
        upcomingEventsContentPanel.revalidate();
        upcomingEventsContentPanel.repaint();
    }

    private JPanel createUpcomingEventPanel(Event event) {
        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new javax.swing.BoxLayout(eventPanel, javax.swing.BoxLayout.Y_AXIS));
        eventPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, brightnessMode == BrightnessMode.DARK ? new Color(0x4B, 0x8D, 0x22) : new Color(0x73, 0xA9, 0x42)));
        eventPanel.setBackground(brightnessMode == BrightnessMode.DARK ? new Color(0x24, 0x55, 0x01) : new Color(0xAA, 0xD5, 0x76));
        eventPanel.setOpaque(true);

        JLabel titleLabel = new JLabel(event.getName());
        titleLabel.setFont(uiFont(Font.BOLD, 14));
        titleLabel.setForeground(brightnessMode == BrightnessMode.DARK ? new Color(0x65, 0xA8, 0x2A) : new Color(0x53, 0x8D, 0x22));
        titleLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(eventPanel.getBackground());
        // Ensure title and detail align left and take full width so toggling doesn't shift the title
        titleLabel.setAlignmentX(javax.swing.JComponent.LEFT_ALIGNMENT);
        titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleLabel.getPreferredSize().height));

        JTextArea detailArea = new JTextArea(event.formatDetails(
                tr("Start:"), tr("End:"), tr("Location:"), tr("Notes:")));
        detailArea.setFont(uiFont(Font.PLAIN, 13));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setEditable(false);
        detailArea.setFocusable(false);
        detailArea.setOpaque(true);
        detailArea.setBackground(brightnessMode == BrightnessMode.DARK ? new Color(40, 40, 40) : new Color(250, 250, 250));
        detailArea.setForeground(brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK);
        detailArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        detailArea.setVisible(false);
        detailArea.setAlignmentX(javax.swing.JComponent.LEFT_ALIGNMENT);
        detailArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        eventPanel.add(titleLabel);
        eventPanel.add(detailArea);

        eventPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        eventPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToDate(event.getStart());
                }
            }
        });

        detailArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToDate(event.getStart());
                }
            }
        });

        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click: navigate to event's month
                    navigateToDate(event.getStart());
                } else {
                    // Single click: toggle detail visibility
                    // Close the previously expanded detail, if any
                    if (currentlyExpandedDetail != null && currentlyExpandedDetail != detailArea) {
                        currentlyExpandedDetail.setVisible(false);
                    }
                    // Toggle current detail
                    detailArea.setVisible(!detailArea.isVisible());
                    // Update tracking
                    if (detailArea.isVisible()) {
                        currentlyExpandedDetail = detailArea;
                    } else {
                        currentlyExpandedDetail = null;
                    }
                    eventPanel.revalidate();
                    eventPanel.repaint();
                }
            }
        });

        return eventPanel;
    }

    private void navigateToDate(Date date) {
        java.time.LocalDate ld;
        try {
            ld = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (UnsupportedOperationException ex) {
            ld = java.time.Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        currentMonth = YearMonth.from(ld);
        updating = true;
        monthCombo.setSelectedIndex(ld.getMonthValue() - 1);
        yearCombo.setSelectedItem(ld.getYear());
        updating = false;
        updateCalendar();
    }

    private void navigateToDate(LocalDate date) {
        currentMonth = YearMonth.from(date);
        updating = true;
        monthCombo.setSelectedIndex(date.getMonthValue() - 1);
        yearCombo.setSelectedItem(date.getYear());
        updating = false;
        updateCalendar();
    }

    private void addEventAcrossDates(Event event) {
        for (LocalDate date : Events.getDateRange(event.getStart(), event.getEnd())) {
            scheduleMap.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
        }
    }

    private void removeEventAcrossDates(Event event) {
        for (LocalDate date : Events.getDateRange(event.getStart(), event.getEnd())) {
            List<Event> events = scheduleMap.get(date);
            if (events != null) {
                events.remove(event);
                if (events.isEmpty()) {
                    scheduleMap.remove(date);
                }
            }
        }
    }

    private boolean hasAdjacentEventAcross(LocalDate date, int offsetDays, Event event) {
        LocalDate adjacentDate = date.plusDays(offsetDays);
        List<Event> adjacentEvents = scheduleMap.get(adjacentDate);
        if (adjacentEvents == null) {
            return false;
        }
        return adjacentEvents.contains(event);
    }

    @SuppressWarnings("unused")
    private boolean hasAdjacentEventAcross(LocalDate date, int offsetDays) {
        LocalDate adjacentDate = date.plusDays(offsetDays);
        List<Event> events = scheduleMap.get(date);
        List<Event> adjacentEvents = scheduleMap.get(adjacentDate);
        if (events == null || adjacentEvents == null) {
            return false;
        }
        for (Event event : events) {
            if (adjacentEvents.contains(event)) {
                return true;
            }
        }
        return false;
    }

    private void refreshUI() {
        setTitle(tr("Calendar Application"));
        monthLabel.setText(tr("Month:"));
        yearLabel.setText(tr("Year:"));
        todayLabel.setText(tr("Today:") + " " + today.toString());
        upcomingTitleLabel.setText(tr("Upcoming events"));
        monthYearLabel.setFont(uiFont(Font.BOLD, 22));
        monthLabel.setFont(uiFont(Font.PLAIN, 10));
        yearLabel.setFont(uiFont(Font.PLAIN, 10));
        todayLabel.setFont(uiFont(Font.PLAIN, 10));
        if (settingsButton != null) {
            refreshIconButton(settingsButton, tr("Settings"), null);
        }
        if (bulletJournalButton != null) {
            refreshIconButton(bulletJournalButton, tr("Bullet Journal"), null);
        }
        if (prevButton != null) {
            refreshIconButton(prevButton, "", scaleOverlayImage(prevButtonImage, 0.85));
            prevButton.setToolTipText(tr("Previous month"));
        }
        if (nextButton != null) {
            refreshIconButton(nextButton, "", scaleOverlayImage(nextButtonImage, 0.85));
            nextButton.setToolTipText(tr("Next month"));
        }
        monthCombo.setFont(uiFont(Font.PLAIN, 10));
        yearCombo.setFont(uiFont(Font.PLAIN, 10));
        upcomingTitleLabel.setFont(uiFont(Font.BOLD, 16));
        applyBrightnessMode();
        updateCalendar();
    }

    private void applyBrightnessMode() {
        Color background = brightnessMode == BrightnessMode.DARK ? new Color(0x24, 0x55, 0x01) : new Color(0xAA, 0xD5, 0x76);
        Color foreground = brightnessMode == BrightnessMode.DARK ? new Color(235, 240, 225) : new Color(0x53, 0x8D, 0x22);
        Color panelBackground = brightnessMode == BrightnessMode.DARK ? new Color(0x2D, 0x6B, 0x0C) : new Color(0x73, 0xA9, 0x42);
        Color fieldBackground = brightnessMode == BrightnessMode.DARK ? new Color(0x3B, 0x7E, 0x11) : new Color(0xE8, 0xF2, 0xD4);
        Color selectionBackground = brightnessMode == BrightnessMode.DARK ? new Color(90, 120, 160) : new Color(184, 207, 229);

        // Set shared theme colors for use across dialogs/components
        appBackground = background;
        appForeground = foreground;
        appPanelBackground = panelBackground;
        appFieldBackground = fieldBackground;
        appBorderColor = brightnessMode == BrightnessMode.DARK ? new Color(0x65, 0xA8, 0x2A) : new Color(0x73, 0xA9, 0x42);
        appSelectionBackground = selectionBackground;
        getContentPane().setBackground(appBackground);
        if (calendarPanel != null) {
            calendarPanel.setBackground(background);
            // Don't apply componentTheme to calendarPanel - it has its own header colors
        }
        if (mainContentPanel != null) {
            mainContentPanel.setBackground(background);
        }
        if (upcomingPanel != null) {
            upcomingPanel.setBackground(appPanelBackground);
        }
        if (todayLabel != null) {
            todayLabel.setForeground(appForeground);
            todayLabel.setBackground(appBackground);
        }
        if (monthYearLabel != null) {
            monthYearLabel.setForeground(foreground);
            monthYearLabel.setBackground(background);
        }
        if (upcomingTitleLabel != null) {
            upcomingTitleLabel.setForeground(foreground);
            upcomingTitleLabel.setBackground(panelBackground);
        }
        if (upcomingEventsContentPanel != null) {
            upcomingEventsContentPanel.setBackground(panelBackground);
            for (java.awt.Component comp : upcomingEventsContentPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    comp.setBackground(panelBackground);
                    for (java.awt.Component child : ((JPanel) comp).getComponents()) {
                        if (child instanceof JLabel) {
                            child.setForeground(foreground);
                        }
                        if (child instanceof JTextArea) {
                            child.setBackground(brightnessMode == BrightnessMode.DARK ? new Color(50, 50, 50) : Color.WHITE);
                            child.setForeground(foreground);
                        }
                    }
                }
            }
        }
        if (headerPanel != null) {
            applyComponentTheme(headerPanel, panelBackground, foreground, fieldBackground);
        }
    }

    private void applyComponentTheme(java.awt.Component comp, Color background, Color foreground, Color fieldBackground) {
        if (comp instanceof javax.swing.JPanel) {
            comp.setBackground(background);
        }
        if (comp instanceof javax.swing.JLabel) {
            comp.setForeground(foreground);
        }
        if (comp instanceof javax.swing.JButton) {
            comp.setBackground(fieldBackground);
            comp.setForeground(foreground);
        }
        if (comp instanceof javax.swing.JComboBox) {
            comp.setForeground(foreground);
        }
        if (comp instanceof javax.swing.JTextArea) {
            comp.setBackground(background);
            comp.setForeground(foreground);
        }
        if (comp instanceof javax.swing.JTextField) {
            comp.setBackground(fieldBackground);
            comp.setForeground(foreground);
        }
        if (comp instanceof javax.swing.JScrollPane) {
            ((javax.swing.JScrollPane) comp).getViewport().setBackground(background);
            comp.setBackground(background);
        }
        if (comp instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) comp).getComponents()) {
                applyComponentTheme(child, background, foreground, fieldBackground);
            }
        }
    }

    private String tr(String text) {
        String selectedLanguage = translator.getLanguageName(currentLocale);
        if (selectedLanguage == null || selectedLanguage.isEmpty()) {
            selectedLanguage = "English";
        }
        return translator.getTranslated(text, selectedLanguage);
    }

    private Font uiFont(int style, int size) {
        int forcedSize = size;
        String language = currentLocale != null ? currentLocale.getLanguage() : "en";
        String sampleText = "A";
        if (language.equals("ko")) {
            sampleText = "\uC6D4";
        } else if (language.equals("zh")) {
            sampleText = "\u6708";
        } else if (language.equals("ja")) {
            sampleText = "\u65E5";
        }

        Font languageFont = getLocaleFont(currentLocale);
        if (languageFont != null) {
            Font derived = languageFont.deriveFont(style, (float) forcedSize);
            if (derived.canDisplayUpTo(sampleText) == -1) {
                return derived;
            }
        }

        if (customFont != null) {
            Font derived = customFont.deriveFont(style, (float) forcedSize);
            if (derived.canDisplayUpTo(sampleText) == -1) {
                return derived;
            }
        }

        String fontName = "Segoe UI";
        if (language.equals("ko")) {
            fontName = "Malgun Gothic";
        } else if (language.equals("zh")) {
            if (currentLocale != null && "TW".equals(currentLocale.getCountry())) {
                fontName = "Microsoft JhengHei";
            } else {
                fontName = "Microsoft YaHei";
            }
        } else if (language.equals("ja")) {
            fontName = "Yu Gothic";
        } else if (language.equals("th")) {
            fontName = "Tahoma";
        }
        Font font = new Font(fontName, style, forcedSize);
        if (font.canDisplayUpTo(sampleText) != -1) {
            return font;
        }
        return new Font("Dialog", style, forcedSize);
    }

    private Font getLocaleFont(Locale locale) {
        if (locale == null) {
            return null;
        }
        String language = locale.getLanguage();
        if ("ko".equals(language) && koreanFont != null) {
            return koreanFont;
        }
        if ("ja".equals(language) && japaneseFont != null) {
            return japaneseFont;
        }
        if ("zh".equals(language) && chineseFont != null) {
            return chineseFont;
        }
        return null;
    }

    private void updateCalendar() {
        monthYearLabel.setText(
                currentMonth.getMonth().getDisplayName(TextStyle.FULL, currentLocale)
                        + " " + currentMonth.getYear());
        // Update month combo
        updating = true;
        monthCombo.removeAllItems();
        for (Month month : Month.values()) {
            monthCombo.addItem(month.getDisplayName(TextStyle.FULL, currentLocale));
        }
        monthCombo.setSelectedItem(currentMonth.getMonth().getDisplayName(TextStyle.FULL, currentLocale));
        updating = false;
        yearCombo.setSelectedItem(currentMonth.getYear());

        mainContentPanel.remove(calendarPanel);
        calendarPanel = createCalendarPanel(currentMonth);
        mainContentPanel.add(calendarPanel, BorderLayout.CENTER);
        updateUpcomingEvents();
        applyBrightnessMode();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private String getEventColor(Event event) {
        int hash = Math.abs(event.getName().hashCode());
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E2"};
        return colors[hash % colors.length];
    }

    private String getHolidayColor() {
        return "#FFC107";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String truncateText(String text, int limit) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() <= limit) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(limit - 1, 0)) + "...";
    }

    private JButton createDayButton(LocalDate date) {
        String dayNumber = String.valueOf(date.getDayOfMonth());
        List<Event> dateEvents = scheduleMap.getOrDefault(date, new ArrayList<>());
        Holidays.Holiday holiday = Holidays.getHolidayForDate(currentCountry, date);
        boolean isHoliday = holiday != null;

        StringBuilder eventStrips = new StringBuilder();
        Set<Event> addedEvents = new HashSet<>();
        int maxMarkers = 3;
        int markerIndex = 0;

        if (isHoliday) {
            String holidayName = escapeHtml(tr(holiday.getName()));
            int stripHeight = 12;
            eventStrips.append("<div style='width:100%; height:").append(stripHeight).append("px; background-color:").append(getHolidayColor()).append("; color:#000; font-size:10px; font-weight:bold; overflow:hidden; white-space:nowrap; text-overflow:ellipsis; padding:1px 3px; box-sizing:border-box; border-radius:3px; margin:0;'>")
                    .append(holidayName)
                    .append("</div>");
            markerIndex++;
        }

        for (Event event : dateEvents) {
            if (!addedEvents.contains(event) && markerIndex < maxMarkers) {
                addedEvents.add(event);
                String eventColor = getEventColor(event);
                boolean spanLeft = hasAdjacentEventAcross(date, -1, event);
                boolean spanRight = hasAdjacentEventAcross(date, 1, event);
                String borderRadius = "3px";
                if (spanLeft && spanRight) {
                    borderRadius = "0";
                } else if (spanLeft) {
                    borderRadius = "0 3px 3px 0";
                } else if (spanRight) {
                    borderRadius = "3px 0 0 3px";
                }
                int stripHeight = markerIndex == 0 ? 12 : (markerIndex == 1 ? 10 : 8);
                String eventLabel = escapeHtml(truncateText(event.getName(), 16));
                eventStrips.append("<div style='width:100%; height:").append(stripHeight).append("px; background-color:").append(eventColor).append("; color:#fff; font-size:10px; overflow:hidden; white-space:nowrap; text-overflow:ellipsis; padding:1px 3px; box-sizing:border-box; border-radius:").append(borderRadius).append("; margin:0;'>")
                        .append(eventLabel)
                        .append("</div>");
                markerIndex++;
            }
        }

        if (eventStrips.length() == 0) {
            eventStrips.append("<div style='width:100%; height:2px; margin:0; padding:0;'></div>");
        }

        StringBuilder htmlText = new StringBuilder();
htmlText.append("<html><div style=\"margin:0; padding:0;\"><div style=\"padding:2px; text-align:left; line-height:1.0; font-size:10px;\"><b>")
                .append(dayNumber)
                .append("</b></div><div style=\"margin:0; padding:0; width:100%;\">")
                .append(eventStrips.toString())
                .append("</div></div></html>");
        Color dayButtonBackground = brightnessMode == BrightnessMode.DARK ? new Color(0x36, 0x7A, 0x11) : new Color(0xE8, 0xF2, 0xD4);
        Color dayButtonBorderColor = brightnessMode == BrightnessMode.DARK ? new Color(0x4B, 0x8D, 0x22) : new Color(0x73, 0xA9, 0x42);
        Color dayButtonForeground = brightnessMode == BrightnessMode.DARK ? new Color(235, 240, 225) : new Color(0x53, 0x8D, 0x22);

        JButton dayButton = new JButton(htmlText.toString());
        dayButton.setFont(uiFont(Font.PLAIN, 12));
        dayButton.setFocusPainted(false);
        dayButton.setOpaque(true);
        dayButton.setBackground(dayButtonBackground);
        dayButton.setBorder(BorderFactory.createLineBorder(dayButtonBorderColor));
        dayButton.setVerticalAlignment(JButton.TOP);
        dayButton.setHorizontalAlignment(JButton.LEFT);

        if ((isHoliday && holiday.isNoWorkDay()) || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayButton.setForeground(Color.RED);
        } else {
            dayButton.setForeground(dayButtonForeground);
        }

        if (date.equals(today)) {
            dayButton.setBackground(brightnessMode == BrightnessMode.DARK ? new Color(0x65, 0xA8, 0x2A) : new Color(0x73, 0xA9, 0x42));
        }
        dayButton.addActionListener(e -> showScheduleDialog(date));
        return dayButton;
    }

    private void showScheduleDialog(LocalDate selectedDate) {
        DefaultListModel<Event> eventListModel = new DefaultListModel<>();
        List<Event> events = scheduleMap.getOrDefault(selectedDate, new ArrayList<>());
        events.forEach(eventListModel::addElement);

        JDialog dialog = new JDialog(this, tr("Events for") + " " + selectedDate.toString(), true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel header = new JLabel(tr("Events on") + " " + selectedDate.toString());
        header.setFont(uiFont(Font.BOLD, 16));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(header, BorderLayout.NORTH);
        
        // Add holiday display if this date is a holiday
        Holidays.Holiday holiday = Holidays.getHolidayForDate(currentCountry, selectedDate);
        if (holiday != null) {
            JPanel holidayPanel = new JPanel(new BorderLayout());
            holidayPanel.setBackground(new Color(255, 200, 200));
            holidayPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            JLabel holidayLabel = new JLabel("<html><b>" + tr("Holiday") + ": " + tr(holiday.getName()) + "</b></html>");
            holidayLabel.setFont(uiFont(Font.BOLD, 13));
            holidayLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            holidayPanel.add(holidayLabel, BorderLayout.CENTER);
            headerPanel.add(holidayPanel, BorderLayout.SOUTH);
        }
        
        content.add(headerPanel, BorderLayout.NORTH);

        JList<Event> eventList = new JList<>(eventListModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.setFont(uiFont(Font.PLAIN, 14));
        eventList.setBackground(appFieldBackground != null ? appFieldBackground : (brightnessMode == BrightnessMode.DARK ? new Color(60, 60, 60) : Color.WHITE));
        eventList.setForeground(appForeground != null ? appForeground : (brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK));
        eventList.setSelectionBackground(appSelectionBackground != null ? appSelectionBackground : (brightnessMode == BrightnessMode.DARK ? new Color(90, 120, 160) : new Color(184, 207, 229)));
        eventList.setSelectionForeground(appForeground != null ? appForeground : (brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK));
        eventList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Event) {
                    Event event = (Event) value;
                    String formatted = event.formatDetails(tr("Start:"), tr("End:"), tr("Location:"), tr("Notes:")).replace("\n", "<br>");
                    label.setText("<html>" + formatted + "</html>");
                    label.setVerticalAlignment(SwingConstants.TOP);
                }
                label.setFont(uiFont(Font.PLAIN, 14));
                return label;
            }
        });
        eventList.clearSelection();
        JScrollPane listScroll = new JScrollPane(eventList);
        listScroll.setBorder(BorderFactory.createTitledBorder(tr("Events")));
        content.add(listScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(appBackground != null ? appBackground : (brightnessMode == BrightnessMode.DARK ? new Color(45, 45, 45) : Color.WHITE));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JButton addButton = createIconButton(tr("Add Event"));
        JButton editButton = createIconButton(tr("Edit"));
        JButton deleteButton = createIconButton(tr("Delete"));
        // Refresh pixel-art icons so labels are drawn into the button images
        refreshIconButton(addButton, tr("Add Event"), null);
        refreshIconButton(editButton, tr("Edit"), null);
        refreshIconButton(deleteButton, tr("Delete"), null);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        eventList.addListSelectionListener(e -> {
            boolean hasSelection = !eventList.isSelectionEmpty();
            deleteButton.setEnabled(hasSelection);
            editButton.setEnabled(hasSelection);
        });

        addButton.addActionListener(e -> showAddEventDialog(selectedDate, eventListModel));
        editButton.addActionListener(e -> {
            Event selectedEvent = eventList.getSelectedValue();
            if (selectedEvent != null) {
                showEditEventDialog(selectedDate, eventListModel, selectedEvent);
            }
        });
        deleteButton.addActionListener(e -> {
            Event selectedEvent = eventList.getSelectedValue();
            if (selectedEvent != null) {
                List<LocalDate> relatedDates = Events.getDateRange(selectedEvent.getStart(), selectedEvent.getEnd());
                if (relatedDates.size() > 1) {
                    Object[] options = {tr("Only this day"), tr("All related days"), tr("Cancel")};
                    int choice = showThemedOptionDialog(dialog,
                            tr("Delete only this day or the entire event?"),
                            tr("Delete event"),
                            options,
                            options[2],
                            JOptionPane.QUESTION_MESSAGE);
                    if (choice == 0) {
                        eventListModel.removeElement(selectedEvent);
                        removeEventFromDate(selectedEvent, selectedDate);
                        updateCalendar();
                        eventList.repaint();
                    } else if (choice == 1) {
                        eventListModel.removeElement(selectedEvent);
                        removeEventAcrossDates(selectedEvent);
                        updateCalendar();
                        eventList.repaint();
                    }
                } else {
                        int choice = showThemedConfirmDialog(dialog,
                            tr("Delete event?"),
                            tr("Confirm Delete"));
                    if (choice == 0) {
                        eventListModel.removeElement(selectedEvent);
                        removeEventAcrossDates(selectedEvent);
                        updateCalendar();
                    }
                }
            }
        });

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
    }

    private void showBulletJournalDialog () {
        DefaultListModel<String> journalListModel = new DefaultListModel<>();
        bulletJournal.getEntries().forEach(journalListModel::addElement);

        JDialog dialog = new JDialog(this, tr("Bullet Journal"), true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel header = new JLabel(tr("Bullet Journal"));
        header.setFont(uiFont(Font.BOLD, 16));
        content.add(header, BorderLayout.NORTH);

        JList<String> journalList = new JList<>(journalListModel);
        journalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        journalList.setFont(uiFont(Font.PLAIN, 14));
        journalList.setBackground(appFieldBackground != null ? appFieldBackground : (brightnessMode == BrightnessMode.DARK ? new Color(60, 60, 60) : Color.WHITE));
        journalList.setForeground(appForeground != null ? appForeground : (brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK));
        journalList.setSelectionBackground(appSelectionBackground != null ? appSelectionBackground : (brightnessMode == BrightnessMode.DARK ? new Color(90, 120, 160) : new Color(184, 207, 229)));
        journalList.setSelectionForeground(appForeground != null ? appForeground : (brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK));
        journalList.clearSelection();
        JScrollPane listScroll = new JScrollPane(journalList);
        listScroll.setBorder(BorderFactory.createTitledBorder(tr("Journal entries")));
        content.add(listScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(appBackground != null ? appBackground : (brightnessMode == BrightnessMode.DARK ? new Color(45, 45, 45) : Color.WHITE));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JButton addButton = createIconButton(tr("Add Entry"));
        JButton editButton = createIconButton(tr("Edit"));
        JButton deleteButton = createIconButton(tr("Delete"));
        refreshIconButton(addButton, tr("Add Entry"), null);
        refreshIconButton(editButton, tr("Edit"), null);
        refreshIconButton(deleteButton, tr("Delete"), null);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        journalList.addListSelectionListener(e -> {
            boolean hasSelection = !journalList.isSelectionEmpty();
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });

        journalList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && !journalList.isSelectionEmpty()) {
                    String selected = journalList.getSelectedValue();
                    if (selected != null) {
                        showBulletJournalEntryViewDialog(dialog, selected);
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            String entry = showBulletJournalEntryDialog(dialog, null);
            if (entry != null && !entry.trim().isEmpty()) {
                bulletJournal.addEntry(entry);
                journalListModel.addElement(entry);
                saveBulletJournalState();
            }
        });
        editButton.addActionListener(e -> {
            int index = journalList.getSelectedIndex();
            if (index >= 0) {
                String current = journalListModel.getElementAt(index);
                String updated = showBulletJournalEntryDialog(dialog, current);
                if (updated != null && !updated.trim().isEmpty()) {
                    bulletJournal.updateEntry(index, updated);
                    journalListModel.set(index, updated);
                    saveBulletJournalState();
                }
            }
        });
        deleteButton.addActionListener(e -> {
            int index = journalList.getSelectedIndex();
            if (index >= 0) {
                int choice = showThemedConfirmDialog(dialog, tr("Delete entry?"), tr("Confirm Delete"));
                if (choice == 0) {
                    bulletJournal.removeEntry(index);
                    journalListModel.remove(index);
                    saveBulletJournalState();
                    journalList.repaint();
                }
            }
        });

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
    }

    private void showBulletJournalEntryViewDialog(java.awt.Component parent, String entryText) {
        JDialog dialog = new JDialog(SwingUtilities.windowForComponent(parent), tr("View Entry"), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel prompt = new JLabel(tr("Entry text:"));
        prompt.setFont(uiFont(Font.PLAIN, 14));
        content.add(prompt, BorderLayout.NORTH);

        JTextArea entryArea = new JTextArea(entryText != null ? entryText : "", 8, 34);
        entryArea.setFont(uiFont(Font.PLAIN, 14));
        entryArea.setLineWrap(true);
        entryArea.setWrapStyleWord(true);
        entryArea.setEditable(false);
        entryArea.setFocusable(false);
        entryArea.setBackground(brightnessMode == BrightnessMode.DARK ? new Color(50, 50, 50) : Color.WHITE);
        entryArea.setForeground(brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK);
        JScrollPane entryScroll = new JScrollPane(entryArea);
        content.add(entryScroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        JButton closeButton = createIconButton(tr("Close"));
        if (closeButton.getIcon() == null) {
            closeButton.setFont(uiFont(Font.PLAIN, 13));
            closeButton.setOpaque(true);
            closeButton.setContentAreaFilled(true);
        }
        closeButton.addActionListener(e -> dialog.dispose());
        buttons.add(closeButton);
        content.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
    }

    private String showBulletJournalEntryDialog(java.awt.Component parent, String initialText) {
        JDialog dialog = new JDialog(SwingUtilities.windowForComponent(parent), initialText == null ? tr("Add Entry") : tr("Edit Entry"), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel prompt = new JLabel(tr("Entry text:"));
        prompt.setFont(uiFont(Font.PLAIN, 14));
        content.add(prompt, BorderLayout.NORTH);

        JTextArea entryArea = new JTextArea(initialText != null ? initialText : "", 8, 34);
        entryArea.setFont(uiFont(Font.PLAIN, 14));
        entryArea.setLineWrap(true);
        entryArea.setWrapStyleWord(true);
        JScrollPane entryScroll = new JScrollPane(entryArea);
        content.add(entryScroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        JButton saveButton = createIconButton(tr("Save"));
        JButton cancelButton = createIconButton(tr("Cancel"));
        if (saveButton.getIcon() == null) {
            saveButton.setFont(uiFont(Font.PLAIN, 13));
            saveButton.setOpaque(true);
            saveButton.setContentAreaFilled(true);
        }
        if (cancelButton.getIcon() == null) {
            cancelButton.setFont(uiFont(Font.PLAIN, 13));
            cancelButton.setOpaque(true);
            cancelButton.setContentAreaFilled(true);
        }
        buttons.add(saveButton);
        buttons.add(cancelButton);
        content.add(buttons, BorderLayout.SOUTH);

        final String[] result = {null};
        saveButton.addActionListener(e -> {
            result[0] = entryArea.getText().trim();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(saveButton);

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
        return result[0];
    }

    private void showAddEventDialog(LocalDate selectedDate, DefaultListModel<Event> eventListModel) {
        JDialog dialog = new JDialog(this, tr("Add event for") + " " + selectedDate.toString(), true);
        dialog.setSize(480, 460);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextArea notesArea = new JTextArea(4, 20);
        notesArea.setFont(uiFont(Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        Date defaultDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.setTime(defaultDate);
        defaultCalendar.set(Calendar.HOUR_OF_DAY, 9);
        defaultCalendar.set(Calendar.MINUTE, 0);
        defaultCalendar.set(Calendar.SECOND, 0);
        defaultCalendar.set(Calendar.MILLISECOND, 0);
        Date startDateTime = defaultCalendar.getTime();
        defaultCalendar.add(Calendar.HOUR_OF_DAY, 1);
        Date endDateTime = defaultCalendar.getTime();

        SpinnerDateModel startDateModel = new SpinnerDateModel(startDateTime, null, null, Calendar.DAY_OF_MONTH);
        JSpinner startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel startTimeModel = new SpinnerDateModel(startDateTime, null, null, Calendar.MINUTE);
        JSpinner startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        SpinnerDateModel endDateModel = new SpinnerDateModel(endDateTime, null, null, Calendar.DAY_OF_MONTH);
        JSpinner endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel endTimeModel = new SpinnerDateModel(endDateTime, null, null, Calendar.MINUTE);
        JSpinner endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        form.add(new JLabel(tr("Event name:")));
        form.add(nameField);
        form.add(new JLabel(tr("Start date:")));
        form.add(startDateSpinner);
        form.add(new JLabel(tr("Start time:")));
        form.add(startTimeSpinner);
        form.add(new JLabel(tr("End date:")));
        form.add(endDateSpinner);
        form.add(new JLabel(tr("End time:")));
        form.add(endTimeSpinner);
        form.add(new JLabel(tr("Location:")));
        form.add(locationField);
        form.add(new JLabel(tr("Notes:")));
        form.add(notesScroll);

        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        JButton saveButton = createIconButton(tr("Save"));
        JButton cancelButton = createIconButton(tr("Cancel"));
        if (saveButton.getIcon() == null) {
            saveButton.setFont(uiFont(Font.PLAIN, 13));
            saveButton.setOpaque(true);
            saveButton.setContentAreaFilled(true);
        }
        if (cancelButton.getIcon() == null) {
            cancelButton.setFont(uiFont(Font.PLAIN, 13));
            cancelButton.setOpaque(true);
            cancelButton.setContentAreaFilled(true);
        }
        buttons.add(saveButton);
        buttons.add(cancelButton);
        content.add(buttons, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String notes = notesArea.getText().trim();
            Date startDate = Events.combineDateAndTime((Date) startDateSpinner.getValue(), (Date) startTimeSpinner.getValue());
            Date endDate = Events.combineDateAndTime((Date) endDateSpinner.getValue(), (Date) endTimeSpinner.getValue());
            if (name.isEmpty()) {
                showThemedMessageDialog(dialog, tr("Please enter an event name."), tr("Missing name"), JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (endDate.before(startDate)) {
                showThemedMessageDialog(dialog, tr("End date/time must be after start date/time."), tr("Invalid time"), JOptionPane.WARNING_MESSAGE);
                return;
            }
            Event event = new Event(name, startDate, endDate, location, notes);
            addEventAcrossDates(event);
            eventListModel.addElement(event);
            updateCalendar();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
    }

    private void showEditEventDialog(LocalDate originalDate, DefaultListModel<Event> eventListModel, Event eventToEdit) {
        JDialog dialog = new JDialog(this, tr("Edit event") + " " + originalDate.toString(), true);
        dialog.setSize(480, 460);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField nameField = new JTextField(eventToEdit.getName());
        JTextField locationField = new JTextField(eventToEdit.getLocation());
        JTextArea notesArea = new JTextArea(4, 20);
        notesArea.setFont(uiFont(Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(eventToEdit.getNotes());
        JScrollPane notesScroll = new JScrollPane(notesArea);

        Date startDateTime = eventToEdit.getStart();
        Date endDateTime = eventToEdit.getEnd();

        SpinnerDateModel startDateModel = new SpinnerDateModel(startDateTime, null, null, Calendar.DAY_OF_MONTH);
        JSpinner startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel startTimeModel = new SpinnerDateModel(startDateTime, null, null, Calendar.MINUTE);
        JSpinner startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        SpinnerDateModel endDateModel = new SpinnerDateModel(endDateTime, null, null, Calendar.DAY_OF_MONTH);
        JSpinner endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel endTimeModel = new SpinnerDateModel(endDateTime, null, null, Calendar.MINUTE);
        JSpinner endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        form.add(new JLabel(tr("Event name:")));
        form.add(nameField);
        form.add(new JLabel(tr("Start date:")));
        form.add(startDateSpinner);
        form.add(new JLabel(tr("Start time:")));
        form.add(startTimeSpinner);
        form.add(new JLabel(tr("End date:")));
        form.add(endDateSpinner);
        form.add(new JLabel(tr("End time:")));
        form.add(endTimeSpinner);
        form.add(new JLabel(tr("Location:")));
        form.add(locationField);
        form.add(new JLabel(tr("Notes:")));
        form.add(notesScroll);

        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        JButton saveButton = createIconButton(tr("Save"));
        JButton cancelButton = createIconButton(tr("Cancel"));
        if (saveButton.getIcon() == null) {
            saveButton.setFont(uiFont(Font.PLAIN, 13));
            saveButton.setOpaque(true);
            saveButton.setContentAreaFilled(true);
        }
        if (cancelButton.getIcon() == null) {
            cancelButton.setFont(uiFont(Font.PLAIN, 13));
            cancelButton.setOpaque(true);
            cancelButton.setContentAreaFilled(true);
        }
        buttons.add(saveButton);
        buttons.add(cancelButton);
        content.add(buttons, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String notes = notesArea.getText().trim();
            Date startDate = Events.combineDateAndTime((Date) startDateSpinner.getValue(), (Date) startTimeSpinner.getValue());
            Date endDate = Events.combineDateAndTime((Date) endDateSpinner.getValue(), (Date) endTimeSpinner.getValue());
            if (name.isEmpty()) {
                showThemedMessageDialog(dialog, tr("Please enter an event name."), tr("Missing name"), JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (endDate.before(startDate)) {
                showThemedMessageDialog(dialog, tr("End date/time must be after start date/time."), tr("Invalid time"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            Event updatedEvent = new Event(name, startDate, endDate, location, notes);
            removeEventAcrossDates(eventToEdit);
            addEventAcrossDates(updatedEvent);

            int index = eventListModel.indexOf(eventToEdit);
            boolean stillOnSelectedDate = Events.getDateRange(updatedEvent.getStart(), updatedEvent.getEnd()).contains(originalDate);
            if (index >= 0) {
                if (stillOnSelectedDate) {
                    eventListModel.set(index, updatedEvent);
                } else {
                    eventListModel.remove(index);
                }
            } else if (stillOnSelectedDate) {
                eventListModel.addElement(updatedEvent);
            }

            updateCalendar();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(content);
        themeDialog(dialog);
        dialog.setVisible(true);
    }

    private int showThemedConfirmDialog(java.awt.Component parent, String message, String title) {
        Object[] options = {tr("Yes"), tr("No")};
        return showThemedOptionDialog(parent, message, title, options, options[1], JOptionPane.QUESTION_MESSAGE);
    }

    private int showThemedOptionDialog(java.awt.Component parent, String message, String title, Object[] options, Object initialValue, int messageType) {
        java.awt.Window owner = SwingUtilities.windowForComponent(parent);
        JDialog dialog = new JDialog(owner, title, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 180);
        dialog.setLocationRelativeTo(parent);

        Color background = brightnessMode == BrightnessMode.DARK ? new Color(45, 45, 45) : Color.WHITE;
        Color foreground = brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK;
        Color buttonBackground = brightnessMode == BrightnessMode.DARK ? new Color(60, 60, 60) : new Color(240, 240, 240);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel messageLabel = new JLabel("<html>" + escapeHtml(message).replace("\n", "<br>") + "</html>");
        messageLabel.setFont(uiFont(Font.PLAIN, 14));
        messageLabel.setForeground(foreground);
        messageLabel.setBackground(background);
        content.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(background);
        int defaultIndex = 0;
        final int[] result = {-1};
        for (int i = 0; i < options.length; i++) {
            Object option = options[i];
            JButton button = createIconButton(option.toString());
            if (button.getIcon() == null) {
                button.setFont(uiFont(Font.PLAIN, 13));
                button.setBackground(buttonBackground);
                button.setForeground(foreground);
                button.setOpaque(true);
                button.setContentAreaFilled(true);
            }
            int choiceIndex = i;
            button.addActionListener(e -> {
                result[0] = choiceIndex;
                dialog.dispose();
            });
            buttonPanel.add(button);
            if (option != null && option.equals(initialValue)) {
                defaultIndex = i;
            }
        }
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        themeDialog(dialog);
        if (buttonPanel.getComponentCount() > defaultIndex) {
            dialog.getRootPane().setDefaultButton((JButton) buttonPanel.getComponent(defaultIndex));
        }
        dialog.setVisible(true);

        return result[0];
    }

    private void removeEventFromDate(Event event, LocalDate date) {
        List<Event> events = scheduleMap.get(date);
        if (events != null) {
            events.remove(event);
            if (events.isEmpty()) {
                scheduleMap.remove(date);
            }
        }
    }

    private void showThemedMessageDialog(java.awt.Component parent, String message, String title, int messageType) {
        Object[] options = {tr("OK")};
        showThemedOptionDialog(parent, message, title, options, options[0], messageType);
    }

    private void themeDialog(JDialog dialog) {
        // Use shared app theme colors
        Color background = appBackground != null ? appBackground : (brightnessMode == BrightnessMode.DARK ? new Color(45, 45, 45) : Color.WHITE);
        Color foreground = appForeground != null ? appForeground : (brightnessMode == BrightnessMode.DARK ? Color.WHITE : Color.BLACK);
        Color fieldBackground = appFieldBackground != null ? appFieldBackground : (brightnessMode == BrightnessMode.DARK ? new Color(60, 60, 60) : Color.WHITE);
        dialog.getContentPane().setBackground(background);
        dialog.setBackground(background);
        applyComponentTheme(dialog.getContentPane(), background, foreground, fieldBackground);
        reapplyDialogComboStyles(dialog.getContentPane(), fieldBackground, foreground);
        dialog.getContentPane().revalidate();
        dialog.getContentPane().repaint();
    }
    
    private void saveState() {
        try {
            Path path = Paths.get("state.txt");
            List<String> lines = new ArrayList<>();
            lines.add(currentMonth.toString());
            lines.add(translator.getLanguageName(currentLocale));
            lines.add(currentCountry);
            lines.add(brightnessMode.name());
            lines.add(Boolean.toString(saveScreenSize));
            lines.add(Integer.toString(savedWidth));
            lines.add(Integer.toString(savedHeight));
            for (Map.Entry<LocalDate, List<Event>> entry : scheduleMap.entrySet()) {
                LocalDate date = entry.getKey();
                for (Event event : entry.getValue()) {
                    lines.add(date.toString() + "|" + event.getName() + "|" + event.getStart().getTime() + "|" + event.getEnd().getTime() + "|" + event.getLocation() + "|" + event.getNotes());
                }
            }
            Files.write(path, lines);
            saveBulletJournalState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBulletJournalState() {
        try {
            bulletJournal.loadFromFile(Paths.get("bullet_journal.txt"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveBulletJournalState() {
        try {
            bulletJournal.saveToFile(Paths.get("bullet_journal.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        try {
            Path path = Paths.get("state.txt");
            if (!Files.exists(path)) return;
            List<String> lines = Files.readAllLines(path);
            if (lines.size() < 3) return;
            currentMonth = YearMonth.parse(lines.get(0));
            currentLocale = translator.getLocale(lines.get(1));
            currentCountry = lines.get(2);
            if (lines.size() >= 4) {
                try {
                    brightnessMode = BrightnessMode.valueOf(lines.get(3).trim());
                } catch (IllegalArgumentException ignored) {
                    // preserve default if the saved value is invalid
                }
            }
            if (lines.size() >= 5) {
                saveScreenSize = Boolean.parseBoolean(lines.get(4).trim());
            }
            if (lines.size() >= 6) {
                try {
                    savedWidth = Integer.parseInt(lines.get(5).trim());
                } catch (NumberFormatException ignored) {
                }
            }
            if (lines.size() >= 7) {
                try {
                    savedHeight = Integer.parseInt(lines.get(6).trim());
                } catch (NumberFormatException ignored) {
                }
            }
            for (int i = 7; i < lines.size(); i++) {
                String[] parts = lines.get(i).split("\\|", -1);
                if (parts.length == 6) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    String name = parts[1];
                    Date start = new Date(Long.parseLong(parts[2]));
                    Date end = new Date(Long.parseLong(parts[3]));
                    String location = parts[4];
                    String notes = parts[5];
                    Event event = new Event(name, start, end, location, notes);
                    scheduleMap.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPage frame = new MainPage();
            frame.setVisible(true);
        });
    }
}
