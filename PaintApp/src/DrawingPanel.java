import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {
    private final List<DrawingShape> shapes = new ArrayList<>();
    private final List<DrawingShape> undoHistory = new ArrayList<>();

    private Tool currentTool = Tool.PENCIL;
    private Color currentColor = Color.BLACK;
    private float strokeWidth = 3.0f;

    private int startX;
    private int startY;
    private int lastX;
    private int lastY;
    private DrawingShape previewShape;
    private List<Line2D.Double> currentSegments;

    private BufferedImage openedImage;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setOpaque(true);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = lastX = e.getX();
                startY = lastY = e.getY();

                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    currentSegments = new ArrayList<>();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    currentSegments.add(new Line2D.Double(lastX, lastY, x, y));
                    Color drawColor = currentTool == Tool.ERASER ? Color.WHITE : currentColor;
                    previewShape = DrawingShape.freehand(currentSegments, drawColor,
                            currentTool == Tool.ERASER ? Math.max(12.0f, strokeWidth * 4) : strokeWidth,
                            currentTool);
                } else {
                    previewShape = DrawingShape.geometric(currentTool, currentColor,
                            strokeWidth, DrawingShape.createShape(currentTool, startX, startY, x, y));
                }

                lastX = x;
                lastY = y;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    if (currentSegments != null) {
                        if (currentSegments.isEmpty()) {
                            currentSegments.add(new Line2D.Double(startX, startY, x + 0.1, y + 0.1));
                        }
                        Color drawColor = currentTool == Tool.ERASER ? Color.WHITE : currentColor;
                        float width = currentTool == Tool.ERASER
                                ? Math.max(12.0f, strokeWidth * 4) : strokeWidth;
                        addShape(DrawingShape.freehand(currentSegments, drawColor, width, currentTool));
                    }
                } else {
                    java.awt.Shape shape = DrawingShape.createShape(currentTool, startX, startY, x, y);
                    if (shape != null) {
                        addShape(DrawingShape.geometric(currentTool, currentColor, strokeWidth, shape));
                    }
                }

                currentSegments = null;
                previewShape = null;
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void addShape(DrawingShape shape) {
        shapes.add(shape);
        undoHistory.clear();
    }

    public void setCurrentTool(Tool tool) {
        currentTool = tool;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void setCurrentColor(Color color) {
        if (color != null) {
            currentColor = color;
            if (currentTool == Tool.ERASER) {
                currentTool = Tool.PENCIL;
            }
        }
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setStrokeWidth(float width) {
        strokeWidth = Math.max(1.0f, Math.min(30.0f, width));
    }

    public void clearCanvas() {
        if (!shapes.isEmpty()) {
            undoHistory.clear();
            undoHistory.addAll(shapes);
            shapes.clear();
        }
        openedImage = null;
        repaint();
    }

    public void undo() {
        if (!shapes.isEmpty()) {
            undoHistory.add(shapes.remove(shapes.size() - 1));
            repaint();
        }
    }

    public void redo() {
        if (!undoHistory.isEmpty()) {
            shapes.add(undoHistory.remove(undoHistory.size() - 1));
            repaint();
        }
    }

    public void openImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("Format d'image non pris en charge.");
        }
        openedImage = image;
        shapes.clear();
        undoHistory.clear();
        repaint();
    }

    public void saveImage(File file) throws IOException {
        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        drawContent(g2);
        g2.dispose();

        String name = file.getName().toLowerCase();
        String format = name.endsWith(".jpg") || name.endsWith(".jpeg") ? "jpg" : "png";
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Impossible d'enregistrer l'image.");
        }
    }

    private void drawContent(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (openedImage != null) {
            g2.drawImage(openedImage, 0, 0, this);
        }

        for (DrawingShape shape : shapes) {
            shape.draw(g2);
        }

        if (previewShape != null) {
            previewShape.draw(g2);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        drawContent(g2);
        g2.dispose();
    }
}
