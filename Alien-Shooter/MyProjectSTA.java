/*
	Author	:	Tianshu Bao
	Project	:	Alien Shooter
	Purpose	:	A java game I built at graduate school
	Desc		:	Implementation using awt and swing
	Date		:	June 25, 2015
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;

import java.io.File;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.Timer;

class GameEnv extends JApplet implements ActionListener {
    int frameNumber = -1, speed = 5;
    Timer timer;
    boolean frozen = false;
    final int dist = 30, no_alien = 6, verSpace = 4, LimitX =800,
            maxWpn = 30, scReward = 15, scPenalty = 5, scoreX = 870, scoreY= 370,
            scoreWidth= 110, scoreHeight = 100, maxLevel = 8;
    
    ImageIcon fireIcon;
    
    Container refresher;
    JFrame jf;
    JLayeredPane layeredPane;
    JLabel bgLabel, shipLabel, scoreLabel;
    JLabel fgLabel[] = new JLabel[no_alien];
    JLabel wpnLabel[] = new JLabel[50];
    JLabel fireLabel;
    JButton startButton, levelButton;


    byte Level = 1;
    
    int fgHeight, fgWidth;
    int bgHeight, bgWidth;
    int shipHeight, shipWidth;
    int wpnWidth, wpnHeight;
    int barWidth, barHeight;
    int fireWidth, fireHeight;
    int wpnPosX[] = new int[maxWpn];
    int wpnPosY[] = new int[maxWpn];
    int minIndex = 0;
    int maxIndex = 0;
    int score = 0;
    int fgPosY[] = new int[no_alien];
    int shipX;

    boolean wpnF[] = new boolean[50];
    boolean alienF[] = new boolean[no_alien];
    boolean status = false;
    
    Random rn = new Random();
    
/*
    //Invoked only when run as an applet.
    public void init() {
        Image bgImage = getImage(getCodeBase(), bgFile);
        Image fgImage = getImage(getCodeBase(), fgFile);
        Image shipImage = getImage(getCodeBase(), shipFile);
        Image wpnImage = getImage(getCodeBase(), wpnFile);
        buildUI(getContentPane(), bgImage, fgImage, shipImage, );
    }*/
    

    void buildUI(JFrame f, final Container container, Image bgImage, Image fgImage, Image shipImage, Image wpnImage, Image fireImage) {
        refresher = container;
        jf = f;
        
        for (int i=0; i<no_alien; i++) {
            alienF[i] = true;
            int ranNum = rn.nextInt(205);
            fgPosY[i] = -(ranNum * speed);
        }

        final ImageIcon bgIcon = new ImageIcon(bgImage);
        final ImageIcon fgIcon = new ImageIcon(fgImage);
        final ImageIcon shipIcon = new ImageIcon(shipImage);
        final ImageIcon wpnIcon = new ImageIcon(wpnImage);
        fireIcon = new ImageIcon(fireImage);
        //final ImageIcon barIcon = new ImageIcon(barImage);

        //java.util.r
        
        bgWidth = bgIcon.getIconWidth();
        bgHeight = bgIcon.getIconHeight();
        fgWidth = fgIcon.getIconWidth();
        fgHeight = fgIcon.getIconHeight();
        shipWidth = shipIcon.getIconWidth();
        shipHeight = shipIcon.getIconHeight();
        wpnWidth = wpnIcon.getIconWidth();
        wpnHeight = wpnIcon.getIconHeight();
        fireWidth = fireIcon.getIconWidth();
        fireHeight = fireIcon.getIconHeight();
        
        //Set up a timer that calls this object's action handler
        timer = new Timer(100, this); //delay = 100 ms
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        //Create a label to display the background image.
        startButton= new JButton("Click to start");
        bgLabel = new JLabel(bgIcon);
        bgLabel.setOpaque(true);
        bgLabel.setBounds(0, 0, bgWidth, bgHeight);

        //Create a label to display the foreground image.
        for (int i=0; i<no_alien; i++) {
            fgLabel[i] = new JLabel(fgIcon);
            fgLabel[i].setBounds(i*(dist +fgWidth), fgPosY[i], fgWidth, fgHeight);
        }

        // Create Label to display player's ship
        shipLabel = new JLabel(shipIcon);
        shipLabel.setBounds((LimitX - shipWidth)/2, bgHeight - verSpace - shipHeight, shipWidth, shipHeight);
        
        scoreLabel = new JLabel();
        scoreLabel.setText(" "+score);
        Font curFont = scoreLabel.getFont();
        //curFont.getS
        scoreLabel.setFont(new Font(curFont.getName(), curFont.getStyle(), 30));
        scoreLabel.setForeground(Color.orange);
        scoreLabel.setBounds(scoreX, scoreY, scoreWidth, scoreHeight);
        
        fireLabel = new JLabel(fireIcon);
        fireLabel.setBounds(-200, -200, fireWidth, fireHeight);
        
        levelButton = new JButton("Level "+Level);
        levelButton.setBounds(LimitX + 40, 670, 170, 50);
        //startButton.setBounds(LimitX + 40, 600, 170, 50);

        //Create the layered pane to hold the labels.
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(bgWidth, bgHeight));
        layeredPane.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                shipX = (e.getX() + shipWidth/2) > LimitX? LimitX - shipWidth/2: (e.getX() - shipWidth/2 <0? e.getX()+shipWidth/2:e.getX());
                //System.out.println("shipx for listener: "+shipX);
                if (status) {
                    shipLabel.setLocation(shipX - shipWidth/2, bgHeight - verSpace - shipHeight);

                    for (int i=0; i<no_alien; i++) {
                        int mx = i*(dist + fgWidth);
                        if (alienF[i] && shipX -shipWidth/2 >= mx - shipWidth && shipX <= mx + fgWidth +shipWidth/2 && (fgPosY[i] + fgHeight>=bgHeight-shipHeight-verSpace) && (fgPosY[i]<= 1024)) {
                            //System.out.println("Player loses.");
                            firstExitStage(false);
                        }
                    }

                }
            }
        });
        layeredPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (status) {
                if (score>0) {
                    score -= scPenalty;
                    scoreLabel.setText(" "+score);
                    scoreLabel.setLocation(scoreX, scoreY);
                }
                wpnLabel[maxIndex] = new JLabel(wpnIcon);
                int maxCheck = (e.getX() + shipWidth/2) > LimitX? LimitX - (shipWidth+wpnWidth)/2: (e.getX() - shipWidth/2 <0? e.getX()+shipWidth/2-wpnWidth/2:e.getX()-wpnWidth/2);
                wpnPosX[maxIndex] = maxCheck;
                wpnPosY[maxIndex] = bgHeight -verSpace - shipHeight - wpnHeight;
                wpnLabel[maxIndex].setBounds(wpnPosX[maxIndex], wpnPosY[maxIndex], wpnWidth, wpnHeight);
                layeredPane.add(wpnLabel[maxIndex], new Integer(2));
                wpnPosY[maxIndex] += frameNumber * speed;
                wpnF[maxIndex++] = true;
              //  container.repaint();
                }
            }
        });
        // Creating the bar
        //barLabel = new JLabel(barIcon);
        //barLabel.setBounds(LimitX, 0, barWidth, barHeight);
        //testButton.setBounds(0,0, 200, 200);

        layeredPane.add(bgLabel, new Integer(0));  //low layer
        layeredPane.add(shipLabel, new Integer(1));  //high layer
        layeredPane.add(scoreLabel, new Integer(1));
        layeredPane.add(fireLabel, new Integer(2));
        for (int i=0; i<no_alien; i++)
            layeredPane.add(fgLabel[i], new Integer(1));  //high layer
        layeredPane.add(levelButton, new Integer(1));
        //layeredPane.add(barLabel, new Integer(2));
        //layeredPane.add(testButton, new Integer(1));
        container.add(layeredPane, BorderLayout.CENTER);
        
        waitToStart(layeredPane, f);
    }
                
/*    //Invoked by the applet browser only.
    public void start() {
        startAnimation();
    }

    //Invoked by the applet browser only.
    public void stop() {
        stopAnimation();
    }
*/
    public synchronized void startAnimation() {
        if (frozen) {
            //Do nothing.  The user has requested that we
            //stop changing the image.
        } else {
            //Start animating!
            if (!timer.isRunning()) {
                timer.start();
            }
        }
    }
            
    public synchronized void stopAnimation() {
        //Stop the animating thread.
        if (timer.isRunning()) {
            timer.stop();
        } 
    }

    public void actionPerformed(ActionEvent e) {
        //Advance animation frame.
        frameNumber++;
        //Display it.
        if (minIndex> 0 && minIndex == maxIndex)
            minIndex = maxIndex = 0;
            
        for (int i=minIndex; i<maxIndex; i++)
            if (wpnF[i]) {
                if (wpnPosY[i]<0) {
                    wpnF[i] = false;
                    layeredPane.remove(wpnLabel[i]);
                    if (minIndex == i)
                        minIndex++;
                    else if (maxIndex -1 == i)
                        maxIndex--;
                }
                else
                    wpnLabel[i].setLocation(wpnPosX[i], wpnPosY[i] - frameNumber * speed);
            }

       
        for (int i=0; i<no_alien; i++) {
            final int x = i*(dist + fgWidth);
            fgPosY[i] += speed;
            fgPosY[i] %= (fgHeight + bgHeight);

            if (alienF[i]) {

                for (int j=minIndex; j<maxIndex; j++) {
                    if (wpnF[j] && x-wpnWidth <= wpnPosX[j] && wpnPosX[j] <= x+fgWidth-wpnWidth/2 && fgPosY[i]+fgHeight >= wpnPosY[j] - frameNumber * speed) {
                        fireLabel.setLocation(x, fgPosY[i]);
                        score += scReward;
                        scoreLabel.setText(" "+score);
                        scoreLabel.setLocation(scoreX, scoreY);
                        wpnF[j] = false;
                        alienF[i] = false;
                        layeredPane.remove(wpnLabel[j]);
                        //layeredPane.remove(fgLabel[i]);
                        fgLabel[i].setVisible(false);
                        if (minIndex == j)
                            minIndex++;
                        else if (maxIndex -1 == j)
                            maxIndex--;
                    }
                }
            }
            if (alienF[i]) {
                fgLabel[i].setVisible(true);
                fgLabel[i].setLocation(x, fgPosY[i]);
            }
        }
        fireLabel.setLocation(-200, -200);
        refresher.repaint();

        int i;
        for (i=0; i<no_alien; i++)
            if (alienF[i])
                break;
        if (i == no_alien) {
            if (Level == maxLevel)
                firstExitStage(true);
            Level++;
            for (int j=0; j<no_alien; j++) {
                levelButton.setText("Level "+Level);
                alienF[j] = true;
                int ranNum = rn.nextInt(205);
                fgPosY[j] = -(ranNum * speed);
                speed += 2;
            }
        }
    }   
    
    public void waitToStart(JLayeredPane curPane, final JFrame f) {
        pause();
        startButton.setBounds(LimitX + 40, 600, 170, 50);
        curPane.add(startButton, new Integer(1));
        refresher.repaint();
        startButton.addMouseListener(new MouseAdapter() {
            boolean clicked = false;
            public void mousePressed(MouseEvent e) {
                startButton.setText("Exit Game");
                if (clicked) {
                    onExit(f);
                }
                clicked = true;
                pause();
                status = true;
            }
        });
        startButton.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 32)
                        pause();
            }
        });

    }

    
    public void firstExitStage(boolean win) {
        timer.stop();
        if (win)
            JOptionPane.showMessageDialog(jf, "Human player wins. Numerous thanks for saving the earth\n from evil aliens.", "Aliens Defeated",JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(jf, "You lose the game & earth is left vulnerable.", "Aliens invincible",JOptionPane.WARNING_MESSAGE);

        onExit(jf);
    }
    
    //Invoked only when run as an application.

    public void onExit(JFrame f) {
        //immediately hide the window (no falling apart windows)
        f.setVisible(false);
        //cleanup and destroy the window threads
        f.dispose();
        //exit the application without an error
        System.exit(0);
    }

    public void pause() {
        if (frozen) {
            frozen = false;
            startAnimation();
        } else {
            frozen = true;
            stopAnimation();
        }
        if (status) status = false;
        else status = true;
    }
}

public class MyProjectSTA {
    static String fgFile = "images/Alien.gif";  
    static String bgFile = "images/The_space.jpg";
    static String shipFile = "images/Player_ship.gif";
    static String wpnFile = "images/msl.gif";
    static String fireFile = "images/Fire.jpg";
    //static String vertBarFile = "images/TransBar.jpg";

    public static void main(String[] args) {
        File nF = new File(bgFile);
        if (nF.exists() == false) {
            System.out.println("File"+bgFile+" does not exist. Please reconfigure image source.");
        }

        nF = new File (fgFile);
        if (nF.exists() == false) {
            System.out.println("File"+fgFile+" does not exist. Please reconfigure image source.");
        }
        
        Image bgImage = Toolkit.getDefaultToolkit().getImage(bgFile);
        Image fgImage = Toolkit.getDefaultToolkit().getImage(fgFile);
        Image shipImage = Toolkit.getDefaultToolkit().getImage(shipFile);
        Image wpnImage = Toolkit.getDefaultToolkit().getImage(wpnFile);
        Image fireImage = Toolkit.getDefaultToolkit().getImage(fireFile);
//        Image barImage = Toolkit.getDefaultToolkit().getImage(vertBarFile);

        final GameEnv movingLabels = new GameEnv();
        final JFrame f = new JFrame();
        // remove window title & border
        f.setUndecorated(true);
        // set FullScreen
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(f);
        
        /* Unnecessary if resolution is same
        //create a display mode 800x600x32 with refresh of 60hz
        DisplayMode dm = new DisplayMode(1024, 768, 32, 60);

        //attempt to change the screen resolution.
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setDisplayMode(dm);
         * */

         /*
        f.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == 27)
                            movingLabels.onExit(f);
                        else if (e.getKeyCode() == 32)
                            movingLabels.pause();
                }
        });*/

        /*// For minimize, maximize & closing
        f.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                movingLabels.stopAnimation();
            }
            public void windowDeiconified(WindowEvent e) {
                movingLabels.startAnimation();
            }
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });*/
        movingLabels.buildUI(f, f.getContentPane(), bgImage, fgImage, shipImage, wpnImage, fireImage);
        f.setSize(1024, 768);
        f.setVisible(true);
        movingLabels.startAnimation();
    }
}

