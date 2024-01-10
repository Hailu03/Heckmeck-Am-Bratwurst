import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent; // Import KeyEvent
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List
// stack
import java.util.Stack;

public class WinUI extends JFrame {
    private JPanel blueScreen;
    private JLabel confettiBackground;
    private JButton startAgainButton;
    private boolean isBlueScreenVisible = true;
    private int commandNum = 0;

    public WinUI(List<Player> players) {
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

        ImageIcon confetti = new ImageIcon(getClass().getResource("/resources/winbackground.jpg"));
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
                g.drawString(text, layeredPane.getWidth()/2-110, 60);

                g.setColor(Color.BLACK);
                g.drawString(text, layeredPane.getWidth()/2+2-110, 62);
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

    // test main
    // public static void main(String[] args) {
    //     List<Player> players = new ArrayList<>();
    //     players.add(new Player("Hai"));
    //     players.get(0).getTiles().add(new Tile(29));
    //     players.get(0).getTiles().add(new Tile(32));
    //     players.get(0).getTiles().add(new Tile(27));

    //     players.add(new Player("Hieu"));
    //     players.get(1).getTiles().add(new Tile(21));

    //     players.add(new Player("Tri"));
    //     players.get(2).getTiles().add(new Tile(31));
    //     players.get(2).getTiles().add(new Tile(30));
    //     players.get(2).getTiles().add(new Tile(23));
    //     players.get(2).getTiles().add(new Tile(24));

    //     players.add(new Player("Player 4"));
    //     players.get(3).getTiles().add(new Tile(29));
    //     players.get(3).getTiles().add(new Tile(25));

    //     new WinUI(players);
    // }
}
