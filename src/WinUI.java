import javax.swing.*;
import java.awt.*;

public class WinUI extends JFrame {
    private JPanel blueScreen;
    private JLabel confettiBackground;
    private JButton startAgainButton;
    private boolean isBlueScreenVisible = true;

    public WinUI(String winner) {
        setTitle("Start Again");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        confettiBackground = new JLabel(new ImageIcon(getClass().getResource("/resources/confetti.jpg")));
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

        JLabel winnerLabel = new JLabel(String.format("Winner: %s", winner));
        Font largerFont = winnerLabel.getFont().deriveFont(Font.BOLD, 16f);
        winnerLabel.setFont(largerFont);
        winnerLabel.setBounds(90, 40, 150, 30);
        layeredPane.add(winnerLabel, JLayeredPane.PALETTE_LAYER);

        startAgainButton = new JButton("Start Again");
        startAgainButton.setBounds(100, 100, 100, 30);
        layeredPane.add(startAgainButton, JLayeredPane.PALETTE_LAYER);

        JPanel whiteBox = new JPanel();
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBounds(0, 40, 500, 30);
        winnerLabel.setOpaque(true);
        winnerLabel.setBackground(Color.WHITE);
        layeredPane.add(whiteBox, JLayeredPane.PALETTE_LAYER);

        startAgainButton.setOpaque(false);

        Timer timer = new Timer(1000, e -> toggleBlueScreenAnimation()); //1000 = 1s
        timer.setRepeats(false);
        timer.start();

        setVisible(true);

        startAgainButton.addActionListener(e -> {
            setVisible(false);
            new creatingPlayer();
        });
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
}
