import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent; // Import KeyEvent
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

public class creatingPlayer extends JFrame {
    private JPanel addPanel;
    private JTextField addText;
    private JButton addButton;
    private JButton startGameButton;
    private int commandNum = 0;
    private List<String> playerNames = new ArrayList<>();

    public creatingPlayer() {
        setTitle("Player Creation Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600); // Window size
        setLocationRelativeTo(null);

        // Custom panel with background
        BackgroundPanel backgroundPanel = new BackgroundPanel("resources/heckmeck-am-bratwurmeck.jpeg");
        backgroundPanel.setLayout(new BorderLayout());

        // Initialize components
        addText = new JTextField(20);
        addButton = new JButton("Add Player");
        startGameButton = new JButton("Start Game");

        // Add panel
        addPanel = new JPanel();
        addPanel.setOpaque(false); // Make addPanel transparent
        addPanel.add(addText);
        addPanel.add(addButton);
        backgroundPanel.add(addPanel, BorderLayout.CENTER);

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
                g2d.drawString("START GAME", 202, 32);
                g2d.drawString("QUIT", 252, 72);

                g2d.setColor(Color.WHITE);
                String start = "START GAME";
                g2d.drawString(start, 200, 30);
                if (commandNum == 0) {
                    g2d.drawString(">", 170, 30);
                }

                String quit = "QUIT";
                g2d.drawString(quit, 250, 70);
                if (commandNum == 1) {
                    g2d.drawString(">", 220, 70);
                }

            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 80); // Set the preferred size here
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

        menuPanel.setOpaque(true); // Make menuPanel transparent
        menuPanel.setBackground(Color.BLACK);
        menuPanel.setFocusable(true);
        
        // Use Key Bindings for handling key events
        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "upPressed");
        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "downPressed");

        menuPanel.getActionMap().put("upPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandNum--;
                if(commandNum < 0) {
                    commandNum = 1;
                }
                repaint(); // Repaint to reflect changes
            }
        });

        menuPanel.getActionMap().put("downPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandNum++;
                if(commandNum > 1) {
                    commandNum = 0;
                }
                repaint(); // Repaint to reflect changes
            }
        });

        menuPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "performAction");

        menuPanel.getActionMap().put("performAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (commandNum == 0) {
                    startGame(); // Call startGame() when commandNum is 0
                } else if (commandNum == 1) {
                    dispose(); // Close the window when commandNum is 1
                }
            }
        });

        backgroundPanel.add(menuPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addPlayer());

        // enter key
        addText.addActionListener(e -> addPlayer());

        // Set the custom panel as content pane
        setContentPane(backgroundPanel);

        setVisible(true);
        requestFocus();
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    private void addPlayer() {
        String playerName = addText.getText().trim();
        if (!playerName.isEmpty()) {
            playerNames.add(playerName);
            addText.setText(""); // Clear the text field after adding the player name
        }
    }

    private void startGame() {
        if (playerNames.size() > 1) {
            MyFrame frame = new MyFrame(playerNames);
            frame.setVisible(true);
            dispose(); // Close the player creation window
        } else {
            JOptionPane.showMessageDialog(this, "The game needs at least two players", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new creatingPlayer();
    }
}
