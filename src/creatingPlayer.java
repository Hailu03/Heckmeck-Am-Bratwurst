import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class creatingPlayer extends JFrame {
    private JPanel addPanel;
    private JTextField addText;
    private JButton addButton;
    private JButton startGameButton;

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

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false); // Make buttonPanel transparent
        buttonPanel.add(startGameButton);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addPlayer());
        startGameButton.addActionListener(e -> startGame());

        // Set the custom panel as content pane
        setContentPane(backgroundPanel);

        setVisible(true);
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
        if(playerNames.size() > 1) {
            MyFrame frame = new MyFrame(playerNames);
            frame.setVisible(true);
            dispose(); // Close the player creation window
        } else {
            JOptionPane.showMessageDialog(this, "The game need at least two players", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new creatingPlayer();
    }
}
