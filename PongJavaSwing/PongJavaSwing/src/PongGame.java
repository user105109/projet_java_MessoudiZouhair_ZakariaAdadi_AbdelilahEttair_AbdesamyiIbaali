import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

public class PongGame extends JFrame {
    public PongGame() {
        super("Pong - Jeu à deux joueurs");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongGame::new);
    }

    private static class GamePanel extends JPanel {
        private static final int W=900, H=560, PW=14, PH=100, BS=18, WIN=7;
        private double y1=(H-PH)/2.0, y2=(H-PH)/2.0;
        private double bx=(W-BS)/2.0, by=(H-BS)/2.0, dx=5, dy=3;
        private int score1=0, score2=0;
        private boolean z,s,up,down,paused=false,over=false;
        private final Random random=new Random();
        private final Timer timer;

        GamePanel() {
            setPreferredSize(new Dimension(W,H));
            setBackground(new Color(18,24,38));
            setFocusable(true);
            bind("pressed Z","zOn",()->z=true);
            bind("released Z","zOff",()->z=false);
            bind("pressed S","sOn",()->s=true);
            bind("released S","sOff",()->s=false);
            bind("pressed UP","upOn",()->up=true);
            bind("released UP","upOff",()->up=false);
            bind("pressed DOWN","downOn",()->down=true);
            bind("released DOWN","downOff",()->down=false);
            bind("pressed SPACE","pause",()->{if(!over) paused=!paused; repaint();});
            bind("pressed R","restart",this::restart);
            timer=new Timer(16,e->updateGame());
            timer.start();
        }

        private void bind(String key,String name,Runnable action) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key),name);
            getActionMap().put(name,new AbstractAction() {
                public void actionPerformed(ActionEvent e){ action.run(); }
            });
        }

        private double clamp(double v,double min,double max){return Math.max(min,Math.min(max,v));}

        private void updateGame() {
            if(paused || over){repaint();return;}
            if(z)y1-=7; if(s)y1+=7; if(up)y2-=7; if(down)y2+=7;
            y1=clamp(y1,0,H-PH); y2=clamp(y2,0,H-PH);
            bx+=dx; by+=dy;
            if(by<=0 || by+BS>=H){dy=-dy;by=clamp(by,0,H-BS);}

            Rectangle2D left=new Rectangle2D.Double(28,y1,PW,PH);
            Rectangle2D right=new Rectangle2D.Double(W-28-PW,y2,PW,PH);
            Ellipse2D ball=new Ellipse2D.Double(bx,by,BS,BS);

            if(dx<0 && ball.intersects(left)){bx=28+PW; bounce(y1);}
            if(dx>0 && ball.intersects(right)){bx=W-28-PW-BS; bounce(y2);}
            if(bx+BS<0){score2++;checkWinner();resetBall(-1);}
            else if(bx>W){score1++;checkWinner();resetBall(1);}
            repaint();
        }

        private void bounce(double paddleY) {
            double relative=((by+BS/2.0)-(paddleY+PH/2.0))/(PH/2.0);
            dy=relative*5.5;
            dx=clamp(-dx*1.04,-10,10);
            dy=clamp(dy,-8,8);
        }

        private void resetBall(int direction) {
            bx=(W-BS)/2.0; by=(H-BS)/2.0;
            dx=direction*(4.5+random.nextDouble()*1.5);
            dy=random.nextDouble()*4-2;
        }

        private void checkWinner() {
            if(score1>=WIN || score2>=WIN){over=true;timer.stop();}
        }

        private void restart() {
            score1=score2=0; y1=y2=(H-PH)/2.0;
            paused=false;over=false;resetBall(random.nextBoolean()?1:-1);
            timer.start();repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(95,110,135));
            g2.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1,new float[]{10,12},0));
            g2.drawLine(W/2,20,W/2,H-20);

            g2.setColor(new Color(65,210,180));
            g2.fill(new Rectangle2D.Double(28,y1,PW,PH));
            g2.setColor(new Color(255,185,75));
            g2.fill(new Rectangle2D.Double(W-28-PW,y2,PW,PH));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(bx,by,BS,BS));

            g2.setFont(new Font("SansSerif",Font.BOLD,54));
            g2.setColor(new Color(65,210,180));g2.drawString(""+score1,W/2-100,85);
            g2.setColor(new Color(255,185,75));g2.drawString(""+score2,W/2+65,85);
            g2.setFont(new Font("SansSerif",Font.PLAIN,14));
            g2.setColor(new Color(190,200,215));
            String help="Joueur 1 : Z / S     Joueur 2 : ↑ / ↓     Espace : Pause     R : Recommencer";
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(help,(W-fm.stringWidth(help))/2,H-18);

            if(paused)overlay(g2,"PAUSE","Appuyez sur ESPACE pour reprendre");
            if(over)overlay(g2,score1>score2?"Joueur 1 gagne !":"Joueur 2 gagne !","Appuyez sur R pour rejouer");
            g2.dispose();
        }

        private void overlay(Graphics2D g,String title,String subtitle) {
            g.setColor(new Color(0,0,0,155));g.fillRoundRect(W/2-210,H/2-75,420,150,25,25);
            g.setColor(Color.WHITE);g.setFont(new Font("SansSerif",Font.BOLD,32));
            FontMetrics a=g.getFontMetrics();g.drawString(title,(W-a.stringWidth(title))/2,H/2-10);
            g.setFont(new Font("SansSerif",Font.PLAIN,16));
            FontMetrics b=g.getFontMetrics();g.drawString(subtitle,(W-b.stringWidth(subtitle))/2,H/2+28);
        }
    }
}
