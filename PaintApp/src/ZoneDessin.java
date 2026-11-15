import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZoneDessin extends JPanel {
    private static class FormeColoree {
        Shape forme;
        Color couleur;
        float epaisseur;
        boolean remplir;

        FormeColoree(Shape forme, Color couleur, float epaisseur, boolean remplir) {
            this.forme = forme;
            this.couleur = couleur;
            this.epaisseur = epaisseur;
            this.remplir = remplir;
        }
    }

    private final List<FormeColoree> formes = new ArrayList<>();
    private BufferedImage imageFond = null;

    private String outilActuel = "CRAYON";
    private Color couleurActuelle = Color.BLACK;
    private float epaisseurActuelle = 3.0f;

    private int xDebut, yDebut, xFin, yFin;
    private Shape formeEnCours = null;

    public ZoneDessin() {
        setBackground(Color.WHITE);

        MouseAdapter souris = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xDebut = e.getX();
                yDebut = e.getY();
                if (outilActuel.equals("CRAYON")) {
                    formes.add(new FormeColoree(
                            new Ellipse2D.Double(xDebut - epaisseurActuelle / 2, yDebut - epaisseurActuelle / 2, epaisseurActuelle, epaisseurActuelle),
                            couleurActuelle, epaisseurActuelle, true
                    ));
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                xFin = e.getX();
                yFin = e.getY();

                if (outilActuel.equals("CRAYON")) {
                    formes.add(new FormeColoree(new Line2D.Double(xDebut, yDebut, xFin, yFin), couleurActuelle, epaisseurActuelle, false));
                    xDebut = xFin;
                    yDebut = yFin;
                } else {
                    formeEnCours = creerForme(xDebut, yDebut, xFin, yFin);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!outilActuel.equals("CRAYON") && formeEnCours != null) {
                    formes.add(new FormeColoree(formeEnCours, couleurActuelle, epaisseurActuelle, false));
                    formeEnCours = null;
                    repaint();
                }
            }
        };

        addMouseListener(souris);
        addMouseMotionListener(souris);
    }

    private Shape creerForme(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int largeur = Math.abs(x1 - x2);
        int hauteur = Math.abs(y1 - y2);

        switch (outilActuel) {
            case "LIGNE": return new Line2D.Double(x1, y1, x2, y2);
            case "RECTANGLE": return new Rectangle2D.Double(x, y, largeur, hauteur);
            case "CERCLE": return new Ellipse2D.Double(x, y, largeur, hauteur);
            default: return null;
        }
    }

    public void setOutil(String outil) { this.outilActuel = outil; }
    public void setCouleur(Color c) { if (c != null) this.couleurActuelle = c; }
    public void setEpaisseur(float e) { this.epaisseurActuelle = e; }

    public void effacer() {
        formes.clear();
        imageFond = null;
        repaint();
    }

    public void ouvrirImage(File fichier) throws Exception {
        imageFond = ImageIO.read(fichier);
        formes.clear();
        repaint();
    }

    public void sauvegarderImage(File fichier) throws Exception {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        paint(g2);
        g2.dispose();
        ImageIO.write(img, "png", fichier);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (imageFond != null) {
            g2.drawImage(imageFond, 0, 0, this);
        }

        for (FormeColoree fc : formes) {
            g2.setColor(fc.couleur);
            g2.setStroke(new BasicStroke(fc.epaisseur, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (fc.remplir) g2.fill(fc.forme);
            else g2.draw(fc.forme);
        }

        if (formeEnCours != null) {
            g2.setColor(couleurActuelle);
            g2.setStroke(new BasicStroke(epaisseurActuelle));
            g2.draw(formeEnCours);
        }
    }
}
