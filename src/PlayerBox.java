import javax.swing.*;
import java.awt.*;

public class PlayerBox extends JPanel {
    private final String playerName;
    private JPanel PlayerBox;
    private boolean isCurrentPlayer;
    private int[] images = {21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};

    public PlayerBox(String playerName) {
        this.playerName = playerName;
        this.isCurrentPlayer = false;
        setBackground(Color.WHITE); // Set the default background color
    }

    public void removeImage() {
        removeAll();
    }

    public void updateImage(int score) {
        removeAll();

        // Assuming your images array contains image numbers 21 to 36
        int imageIndex = score - 21; // Adjust the score to correspond to the array index
        String imagePath = "resources/" + images[imageIndex] + ".jpg";

        ImageIcon icon = new ImageIcon(imagePath);
        Image originalImage = icon.getImage();
        // Resize the image
        Image resizedImage = originalImage.getScaledInstance(69, 118, Image.SCALE_DEFAULT);
        
        // Create a new ImageIcon with the resized image
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 70));

        JLabel imageLabel = new JLabel(resizedIcon);
        this.add(imageLabel);
    }

    public void setCurrentPlayer(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
        // Change the background color based on whether it's the current player or not
        if (isCurrentPlayer) {
            setBackground(Color.RED); // Change to red if it's the current player
        } else {
            setBackground(Color.WHITE); // Reset to default color if it's not the current player
        }
        repaint(); // Repaint to show the updated color
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // size of the player box
        int boxWidth = 120;
        int boxHeight = 200;

        // transparent rectangle
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, boxWidth, boxHeight);

        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));

        // outline of the box
        g2d.setColor(Color.BLACK); // color for the outline
        g2d.drawLine(0, 50, 200, 50);

        // bold style, increased font size
        Font originalFont = g2d.getFont();
        Font boldFont = originalFont.deriveFont(Font.BOLD, 18);
        g2d.setFont(boldFont);

        // draw the player name
        g2d.drawString(playerName, 1 + 50, 1 + 30); // adjust name's position

        g2d.setFont(originalFont);
        g2d.setStroke(originalStroke);
        g2d.dispose();
    }
}
