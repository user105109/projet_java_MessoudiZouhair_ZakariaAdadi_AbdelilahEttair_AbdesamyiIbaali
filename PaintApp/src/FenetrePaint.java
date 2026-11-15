import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class FenetrePaint extends JFrame {
    private ResourceBundle rb;
    private ZoneDessin zoneDessin;
    private JButton btnCrayon, btnLigne, btnRectangle, btnCercle;
    private JButton btnCouleur, btnEpaisseur, btnOuvrir, btnSauvegarder, btnEffacer;

    public FenetrePaint() {
        rb = ResourceBundle.getBundle("Messages", new Locale("fr", "FR"));

        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Barre du haut (Fichiers & Langues)
        JToolBar topBar = new JToolBar();
        btnOuvrir = new JButton();
        btnSauvegarder = new JButton();
        btnEffacer = new JButton();
        JButton btnFR = new JButton("FR");
        JButton btnEN = new JButton("EN");

        topBar.add(btnOuvrir);
        topBar.add(btnSauvegarder);
        topBar.add(btnEffacer);
        topBar.addSeparator();
        topBar.add(btnFR);
        topBar.add(btnEN);
        add(topBar, BorderLayout.NORTH);

        // Panneau latéral gauche avec GridLayout (Outils de dessin)
        JPanel panelOutils = new JPanel(new GridLayout(6, 1, 5, 5));
        btnCrayon = new JButton();
        btnLigne = new JButton();
        btnRectangle = new JButton();
        btnCercle = new JButton();
        btnCouleur = new JButton();
        btnEpaisseur = new JButton();

        panelOutils.add(btnCrayon);
        panelOutils.add(btnLigne);
        panelOutils.add(btnRectangle);
        panelOutils.add(btnCercle);
        panelOutils.add(btnCouleur);
        panelOutils.add(btnEpaisseur);
        add(panelOutils, BorderLayout.WEST);

        // Zone de dessin au centre
        zoneDessin = new ZoneDessin();
        add(zoneDessin, BorderLayout.CENTER);

        // Événements des outils
        btnCrayon.addActionListener(e -> zoneDessin.setOutil("CRAYON"));
        btnLigne.addActionListener(e -> zoneDessin.setOutil("LIGNE"));
        btnRectangle.addActionListener(e -> zoneDessin.setOutil("RECTANGLE"));
        btnCercle.addActionListener(e -> zoneDessin.setOutil("CERCLE"));

        btnCouleur.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, rb.getString("btn.couleur"), Color.BLACK);
            zoneDessin.setCouleur(c);
        });

        btnEpaisseur.addActionListener(e -> {
            String saisie = JOptionPane.showInputDialog(this, rb.getString("msg.epaisseur"), "3");
            if (saisie != null) {
                try {
                    zoneDessin.setEpaisseur(Float.parseFloat(saisie));
                } catch (NumberFormatException ignored) {}
            }
        });

        btnEffacer.addActionListener(e -> {
            int rep = JOptionPane.showConfirmDialog(this, rb.getString("msg.effacer"), rb.getString("btn.effacer"), JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) zoneDessin.effacer();
        });

        btnOuvrir.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    zoneDessin.ouvrirImage(chooser.getSelectedFile());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSauvegarder.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    if (!f.getName().endsWith(".png")) f = new File(f + ".png");
                    zoneDessin.sauvegarderImage(f);
                    JOptionPane.showMessageDialog(this, rb.getString("msg.sauve"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnFR.addActionListener(e -> changerLangue(new Locale("fr", "FR")));
        btnEN.addActionListener(e -> changerLangue(new Locale("en", "US")));

        actualiserTextes();
    }

    private void changerLangue(Locale loc) {
        rb = ResourceBundle.getBundle("Messages", loc);
        actualiserTextes();
    }

    private void actualiserTextes() {
        setTitle(rb.getString("titre"));
        btnCrayon.setText(rb.getString("outil.crayon"));
        btnLigne.setText(rb.getString("outil.ligne"));
        btnRectangle.setText(rb.getString("outil.rectangle"));
        btnCercle.setText(rb.getString("outil.cercle"));
        btnCouleur.setText(rb.getString("btn.couleur"));
        btnEpaisseur.setText(rb.getString("btn.epaisseur"));
        btnOuvrir.setText(rb.getString("btn.ouvrir"));
        btnSauvegarder.setText(rb.getString("btn.sauvegarder"));
        btnEffacer.setText(rb.getString("btn.effacer"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FenetrePaint().setVisible(true));
    }
}
