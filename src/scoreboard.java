import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent; // Import KeyEvent
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

public class scoreboard extends JFrame {
    private JPanel scoreboardPanel;

    public scoreboard(List<Player> players) {
        setTitle("Scoreboard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600); // Adjust size as needed
        // background color to green
        setLocationRelativeTo(null);

        // scoreboardPanel = new JPanel(new GridLayout(players.size() + 1, 3)); // +1 for header row
        // // color of the scoreboard (light green)
        // scoreboardPanel.setBackground(new Color(0, 250, 150));
        // addHeader();
        // populateScoreboard(players);

        // JScrollPane scrollPane = new JScrollPane(scoreboardPanel);
        // add(scrollPane);

        // setVisible(true);

        scoreboardPanel = new JPanel(new GridLayout(players.size() + 1, 3)); // +1 for header row
        scoreboardPanel.setBackground(new Color(0, 250, 150));
        addHeader();
        populateScoreboard(players);

        JScrollPane scrollPane = new JScrollPane(scoreboardPanel);
        add(scrollPane);

        // JPanel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false); // Hide the scoreboard
                new WinUI(players); // Show the win screen
                dispose(); // Close the scoreboard window
            }
        });
        buttonPanel.add(backButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addHeader() {
        JLabel nameLabel = new JLabel("Player Name");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreboardPanel.add(nameLabel);

        JLabel scoreLabel = new JLabel("Score");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreboardPanel.add(scoreLabel);

        JLabel tilesLabel = new JLabel("Tiles Acquired");
        tilesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreboardPanel.add(tilesLabel);
    }

    private void populateScoreboard(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            JLabel playerNameLabel = new JLabel(players.get(i).getName());
            playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreboardPanel.add(playerNameLabel);

            // change "0" to actual player score
            int score = 0;
            for (Tile tile : players.get(i).getTiles()) {
                score += Math.round((tile.getValue()-21)/4+1);
            }

            JLabel scoreLabel = new JLabel(String.format("%d",score)); // Replace "0" with actual player score
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreboardPanel.add(scoreLabel);

            JPanel tilesPanel = new JPanel(new FlowLayout());
            tilesPanel.setBackground(new Color(0, 250, 150));
            for (Tile tile : players.get(i).getTiles()) {
                JLabel tileLabel = new JLabel(readImg(String.format("resources/%d.jpg", tile.getValue())));
                tilesPanel.add(tileLabel);
            }
            scoreboardPanel.add(tilesPanel);
        }
    }

    // function to read image and resize it
    private static ImageIcon readImg(String imagePath) {
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(50, 80, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    // test this file
    // public static void main(String[] args) {
    //     List<Player> players = new ArrayList<>();
    //     players.add(new Player("Player 1"));
    //     // tile of player 1
    //     players.get(0).getTiles().add(new Tile(21));
    //     players.get(0).getTiles().add(new Tile(28));
    //     players.get(0).getTiles().add(new Tile(34));

    //     players.add(new Player("Player 2"));
    //     players.get(1).getTiles().add(new Tile(27));

    //     players.add(new Player("Player 3"));
    //     players.get(2).getTiles().add(new Tile(26));
    //     players.get(2).getTiles().add(new Tile(36));
    //     players.get(2).getTiles().add(new Tile(31));
    //     players.get(2).getTiles().add(new Tile(33));

    //     players.add(new Player("Player 4"));
    //     players.get(3).getTiles().add(new Tile(29));
    //     players.get(3).getTiles().add(new Tile(25));

    //     players.add(new Player("Player 5"));
    //     players.get(4).getTiles().add(new Tile(22));
    //     players.get(4).getTiles().add(new Tile(23));
    //     players.get(4).getTiles().add(new Tile(24));

    //     players.add(new Player("Player 6"));
    //     players.get(5).getTiles().add(new Tile(30));

    //     players.add(new Player("Player 7"));
    //     players.get(6).getTiles().add(new Tile(32));
    //     players.get(6).getTiles().add(new Tile(33));

    //     new scoreboard(players);
    // }
}