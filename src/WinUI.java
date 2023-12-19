import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WinUI extends JFrame {
    private JLabel victoryLabel;
    private Timer timer;
    private float alpha = 0f;

    public WinUI(String winnerName) {
        setTitle("Victory!");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 200); // Adjust the size as needed
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        victoryLabel = new JLabel(winnerName + " wins!");
        victoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        victoryLabel.setHorizontalAlignment(JLabel.CENTER);
        victoryLabel.setForeground(new Color(0, 0, 0, 0)); // Transparent text initially
        panel.add(victoryLabel, BorderLayout.CENTER);

        setContentPane(panel);
        setVisible(true);

        startAnimation();
    }

    private void startAnimation() {
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha < 1.0f) {
                    alpha += 0.05f; // Increment alpha for fading effect
                    if (alpha > 1.0f) {
                        alpha = 1.0f; // Ensure alpha doesn't exceed 1.0
                    }
                    victoryLabel.setForeground(new Color(0, 0, 0, alpha));
                } else {
                    timer.stop(); // Stop the timer once fully faded in
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        new WinUI("Tom");
    }
}
