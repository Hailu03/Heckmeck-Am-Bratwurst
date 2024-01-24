// timer for gif
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent; // Import KeyEvent
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class WinUI extends JFrame {
    private Clip backgroundMusic;
    private JPanel blueScreen;
    private JLabel confettiBackground;
    private boolean isBlueScreenVisible = true;
    private int commandNum = 0;

    private Timer gifTimer;
    private int currentFrame = 0;
    private AnimatedGifLabel winLabel;

    private AnimatedGifLabel createAnimatedGifLabel(String filePath) {
        ImageIcon gifIcon = new ImageIcon(getClass().getResource(filePath));
        return new AnimatedGifLabel(gifIcon,200,200);
    }

    private class AnimatedGifLabel extends JLabel {
        private ImageIcon animatedGif;
    
        public AnimatedGifLabel(ImageIcon gifIcon, int width, int height) {
            Image gifImage = gifIcon.getImage();
            Image resizedImage = gifImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            this.animatedGif = new ImageIcon(resizedImage);
            setIcon(animatedGif);
        }
    
        public void startAnimation() {
            gifTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentFrame = (currentFrame + 1) % animatedGif.getIconWidth();
    
                    int subimageWidth = Math.min(200, animatedGif.getIconWidth() - currentFrame); // Ensure the subimage width is within bounds
    
                    BufferedImage bufferedImage = new BufferedImage(subimageWidth, animatedGif.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bufferedImage.getGraphics();
                    animatedGif.paintIcon(null, g, 0, 0);
                    g.dispose();
    
                    ImageIcon frameIcon = new ImageIcon(bufferedImage);
                    setIcon(frameIcon);
                    repaint();
                }
            });
            gifTimer.start();
        }
    
        public void stopAnimation() {
            if (gifTimer != null) {
                gifTimer.stop();
            }
        }
    }

    // ... existing code ...

    private void playBackgroundMusic() {
        try {
            File musicFile = new File("resources/success.wav");
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);
            backgroundMusic.loop(1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playSoundEffect(String filePath) {
        try {
            File soundEffectFile = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundEffectFile);
            Clip soundEffect = AudioSystem.getClip();
            soundEffect.open(audioInput);
            soundEffect.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public WinUI(List<Player> players) {
        playBackgroundMusic();
        List<Integer> scores = new ArrayList<>();
        int max = 0;
        for (Player player : players) {
            int score = 0;
            for (Tile tile : player.getTiles()) {
                score += Math.round((tile.getValue()-21)/4+1);
            }
            if(score > max) {
                max = score;
            }
            scores.add(score);
        }

        List<Integer> tie = new ArrayList<>();
        for(int i = 0; i < scores.size(); i++) {
            if(scores.get(i) == max) {
                tie.add(i);
            }
        }

        int highestTile = 0;
        int winnerIndex = -1;

        if(tie.size() > 1) {
            for(int i = 0; i < tie.size(); i++) {
                int playerIndex = tie.get(i);
                for(Tile tile : players.get(playerIndex).getTiles()) {
                    if(tile.getValue() > highestTile) {
                        winnerIndex = playerIndex;
                        highestTile = tile.getValue();
                    }
                }
            }
        } else {
            winnerIndex = tie.get(0);
        }

        setTitle("Start Again");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600); // Window size
        setResizable(false);
        setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        ImageIcon confetti = new ImageIcon(getClass().getResource("/resources/wing.png"));
        //resize image to fit the window
        Image confettiImage = confetti.getImage();
        Image resizedConfettiImage = confettiImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        confetti = new ImageIcon(resizedConfettiImage);


        confettiBackground = new JLabel(confetti);
        
        confettiBackground.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(confettiBackground, JLayeredPane.DEFAULT_LAYER);

        blueScreen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                Font font = new Font("Arial", Font.BOLD, 20);
                g.setFont(font);

                String text = "And the winner is...";
                int textX = (getWidth() - g.getFontMetrics().stringWidth(text)) / 2;
                int textY = getHeight() / 2;

                if (isBlueScreenVisible) {
                    g.drawString(text, textX, textY);
                }
            }
        };
        blueScreen.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(blueScreen, JLayeredPane.MODAL_LAYER);

        // final int idx = winnerIndex;
        winLabel = createAnimatedGifLabel("/resources/celeb.gif");
        winLabel.setBounds((getWidth() - 200) / 2, (getHeight() - 200) / 2, 200, 200);
        layeredPane.add(winLabel, JLayeredPane.PALETTE_LAYER);
        winLabel.startAnimation();

        final int idx = winnerIndex;

        JLabel winnerLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
        
                Graphics2D g2 = (Graphics2D) g.create();
                g.setFont(g.getFont().deriveFont(Font.BOLD, 40F));
        
                // Shadow
                String winner = players.get(idx).getName();
                g.setColor(Color.GRAY);
                String text = String.format("Winner: %s", winner);
                g.drawString(text, layeredPane.getWidth()/2-100, 65);

                g.setColor(Color.BLACK);
                g.drawString(text, layeredPane.getWidth()/2+2-100, 67);

            }
        };
        // change background color
        Font largerFont = winnerLabel.getFont().deriveFont(Font.BOLD, 30f);
        // change font position
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setFont(largerFont);
        winnerLabel.setBounds(0, 0, 600, 100);
        winnerLabel.setOpaque(false);

        layeredPane.add(winnerLabel, JLayeredPane.PALETTE_LAYER);

        
        // MENU graphics2D panel
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 30F));
                
                // Shadow
                g2d.setColor(Color.GRAY);
                g2d.drawString("Again", 32, 32);
                g2d.drawString("Scoreboard", 212, 32);
                g2d.drawString("QUIT", 502, 32);
                
                String start = "Again";
                if (commandNum == 0) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawString(start, 30, 30);

                String scoreboard = "Scoreboard";
                if (commandNum == 1) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawString(scoreboard, 210, 32);
                
                String quit = "QUIT";
                if (commandNum == 2) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawString(quit, 500, 30);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 100); // Set the preferred size here
            }
            
            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize(); // Return the preferred size
            }
            
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize(); // Return the preferred size
            }
        };
        
        menuPanel.setOpaque(false); // Make menuPanel transparent
        menuPanel.setBackground(Color.BLACK);
        menuPanel.setFocusable(true);
        
        menuPanel.setBounds(0, 500, 600, 100);

        // Use Key Bindings for handling key events
        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");

        menuPanel.getActionMap().put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandNum++;
                if(commandNum > 2) {
                    commandNum = 0;
                }
                repaint(); // Repaint to reflect changes
            }
        });

        menuPanel.getActionMap().put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandNum--;
                if(commandNum < 0) {
                    commandNum = 2;
                }
                repaint(); // Repaint to reflect changes
            }
        });

        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        
        menuPanel.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (commandNum == 0) {
                    setVisible(false);
                    new creatingPlayer();
                } else if (commandNum == 1) {
                    setVisible(false);
                    new scoreboard(players);
                } else if (commandNum == 2) {
                    System.exit(0);
                }
            }
        });
        
        layeredPane.add(menuPanel, JLayeredPane.PALETTE_LAYER);

        Timer timer = new Timer(1000, e -> toggleBlueScreenAnimation()); //1000 = 1s
        timer.setRepeats(false);
        timer.start();

        setVisible(true);
    }

    private void toggleBlueScreenAnimation() {
        isBlueScreenVisible = true;

        animateBlueScreen();
    }

    private void animateBlueScreen() {
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            int x = blueScreen.getX();
            if (x < getWidth()) {
                x += 5;
                blueScreen.setLocation(x, 0);
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Hai"));
        players.get(0).getTiles().add(new Tile(29));
        players.get(0).getTiles().add(new Tile(32));
        players.get(0).getTiles().add(new Tile(27));

        players.add(new Player("Hieu"));
        players.get(1).getTiles().add(new Tile(21));

        players.add(new Player("Tri"));
        players.get(2).getTiles().add(new Tile(31));
        players.get(2).getTiles().add(new Tile(30));
        players.get(2).getTiles().add(new Tile(23));
        players.get(2).getTiles().add(new Tile(24));

        players.add(new Player("Player 4"));
        players.get(3).getTiles().add(new Tile(29));
        players.get(3).getTiles().add(new Tile(25));

        new WinUI(players);
    }
}
