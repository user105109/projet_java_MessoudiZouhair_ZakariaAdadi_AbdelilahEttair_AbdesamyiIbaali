import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PaintFrame extends JFrame {
    private DrawingPanel drawingPanel;
    private ResourceBundle messages;
    private Locale currentLocale = Locale.FRENCH;
    private JLabel statusLabel;
    private JToolBar toolBar;
    private JMenuBar menuBar;
    private JButton pencilButton, eraserButton, lineButton, rectangleButton, ellipseButton;
    private JButton colorButton, undoButton, redoButton, clearButton;
    private JComboBox<String> languageCombo;
    private JSlider widthSlider;

    public PaintFrame() {
        setTitle("Paint Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        setLayout(new BorderLayout(5, 5));
        add(createToolBar(), BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        statusLabel = new JLabel("Prêt");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);

        rebuildTexts();
        setVisible(true);
    }

    private JToolBar createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        pencilButton = toolButton("✎", Tool.PENCIL);
        eraserButton = toolButton("⌫", Tool.ERASER);
        lineButton = toolButton("╱", Tool.LINE);
        rectangleButton = toolButton("▭", Tool.RECTANGLE);
        ellipseButton = toolButton("○", Tool.ELLIPSE);

        colorButton = new JButton();
        colorButton.setBackground(drawingPanel.getCurrentColor());
        colorButton.setToolTipText("Couleur");
        colorButton.addActionListener(e -> chooseColor());

        undoButton = new JButton("↶");
        undoButton.addActionListener(e -> drawingPanel.undo());
        redoButton = new JButton("↷");
        redoButton.addActionListener(e -> drawingPanel.redo());
        clearButton = new JButton("Effacer tout");
        clearButton.addActionListener(e -> confirmClear());

        widthSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 3);
        widthSlider.setToolTipText("Épaisseur du trait");
        widthSlider.setMaximumSize(new java.awt.Dimension(100, 35));
        widthSlider.addChangeListener(e -> drawingPanel.setStrokeWidth(widthSlider.getValue()));

        languageCombo = new JComboBox<>(new String[]{"Français", "English"});
        languageCombo.addActionListener(e -> {
            if (languageCombo.getSelectedIndex() == 0) {
                currentLocale = Locale.FRENCH;
            } else {
                currentLocale = Locale.ENGLISH;
            }
            rebuildTexts();
        });

        toolBar.add(pencilButton);
        toolBar.add(eraserButton);
        toolBar.add(lineButton);
        toolBar.add(rectangleButton);
        toolBar.add(ellipseButton);
        toolBar.addSeparator();
        toolBar.add(colorButton);
        toolBar.add(new JLabel("  Épaisseur "));
        toolBar.add(widthSlider);
        toolBar.addSeparator();
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.add(clearButton);
        toolBar.addSeparator();
        toolBar.add(languageCombo);

        return toolBar;
    }

    private JButton toolButton(String text, Tool tool) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> {
            drawingPanel.setCurrentTool(tool);
            statusLabel.setText(messages.getString("tool") + " : " + messages.getString(tool.name().toLowerCase()));
        });
        return button;
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu(messages.getString("file"));
        JMenuItem openItem = new JMenuItem(messages.getString("open"));
        JMenuItem saveItem = new JMenuItem(messages.getString("save"));
        JMenuItem exitItem = new JMenuItem(messages.getString("exit"));

        openItem.addActionListener(this::openImage);
        saveItem.addActionListener(this::saveImage);
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu(messages.getString("edit"));
        JMenuItem undoItem = new JMenuItem(messages.getString("undo"));
        JMenuItem redoItem = new JMenuItem(messages.getString("redo"));
        JMenuItem clearItem = new JMenuItem(messages.getString("clear"));
        undoItem.addActionListener(e -> drawingPanel.undo());
        redoItem.addActionListener(e -> drawingPanel.redo());
        clearItem.addActionListener(e -> confirmClear());
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(clearItem);

        JMenu helpMenu = new JMenu(messages.getString("help"));
        JMenuItem aboutItem = new JMenuItem(messages.getString("about"));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                messages.getString("aboutText"), messages.getString("about"),
                JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(aboutItem);
        bar.add(fileMenu);
        bar.add(editMenu);
        bar.add(helpMenu);
        return bar;
    }

    private void rebuildTexts() {
        messages = ResourceBundle.getBundle("Messages", currentLocale);
        setTitle(messages.getString("title"));
        menuBar = createMenuBar();
        setJMenuBar(menuBar);

        if (pencilButton != null) {
            pencilButton.setToolTipText(messages.getString("pencil"));
            eraserButton.setToolTipText(messages.getString("eraser"));
            lineButton.setToolTipText(messages.getString("line"));
            rectangleButton.setToolTipText(messages.getString("rectangle"));
            ellipseButton.setToolTipText(messages.getString("ellipse"));
            colorButton.setToolTipText(messages.getString("color"));
            undoButton.setToolTipText(messages.getString("undo"));
            redoButton.setToolTipText(messages.getString("redo"));
            clearButton.setText(messages.getString("clear"));
            languageCombo.setToolTipText(messages.getString("language"));
            statusLabel.setText(messages.getString("ready"));
        }
        revalidate();
        repaint();
    }

    private void chooseColor() {
        Color chosen = javax.swing.JColorChooser.showDialog(
                this, messages.getString("chooseColor"), drawingPanel.getCurrentColor());
        if (chosen != null) {
            drawingPanel.setCurrentColor(chosen);
            colorButton.setBackground(chosen);
        }
    }

    private void confirmClear() {
        int result = JOptionPane.showConfirmDialog(this,
                messages.getString("confirmClear"),
                messages.getString("clear"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            drawingPanel.clearCanvas();
            statusLabel.setText(messages.getString("ready"));
        }
    }

    private void openImage(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(messages.getString("open"));
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images PNG, JPG, JPEG, GIF, BMP", "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                drawingPanel.openImage(chooser.getSelectedFile());
                statusLabel.setText(messages.getString("opened"));
            } catch (IOException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void saveImage(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(messages.getString("save"));
        chooser.setSelectedFile(new File("dessin.png"));
        chooser.setFileFilter(new FileNameExtensionFilter("Image PNG", "png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")
                    && !file.getName().toLowerCase().endsWith(".jpg")
                    && !file.getName().toLowerCase().endsWith(".jpeg")) {
                file = new File(file.getParentFile(), file.getName() + ".png");
            }
            try {
                drawingPanel.saveImage(file);
                statusLabel.setText(messages.getString("saved"));
            } catch (IOException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                messages.getString("error"), JOptionPane.ERROR_MESSAGE);
    }
}
