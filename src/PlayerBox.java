import javax.swing.*;
import java.awt.*;

public class PlayerBox extends JPanel {
    private final String playerName;
    private JPanel PlayerBox;
    public PlayerBox(String playerName) {
        this.playerName = playerName;
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
        g2d.fillRect(10, 100, boxWidth, boxHeight);

        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));

        // outline of the box
        g2d.setColor(Color.BLACK); // color for the outline
        g2d.drawLine( 0, 50, 200, 50);

        // bold style, increased font size
        Font originalFont = g2d.getFont();
        Font boldFont = originalFont.deriveFont(Font.BOLD, 18);
        g2d.setFont(boldFont);

        // draw the player name
        g2d.drawString(playerName, 1 + 20, 1 + 30); // adjust name's position

        g2d.setFont(originalFont);
        g2d.setStroke(originalStroke);
        g2d.dispose();
    }
}
