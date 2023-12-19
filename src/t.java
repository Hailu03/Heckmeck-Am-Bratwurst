import javax.swing.*;
import java.awt.*;

public class t {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setTitle("Verflixt Spiel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1200,800);
        frame.setVisible(true);
        frame.setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        // Set the bounds to cover the entire frame
        panel.setBounds(0, 0, frame.getWidth()*2/3, frame.getHeight()*2/3);

        for(int i = 0; i < 9; i++ ){
            ImageIcon img = new ImageIcon("resources/21.jpg");
            Image newImg = img.getImage().getScaledInstance(400*2/3, 300*2/3, Image.SCALE_SMOOTH);
            img = new ImageIcon(newImg);

            JLabel imageLabel = new JLabel(img);
            panel.add(imageLabel);
        }

        frame.add(panel,BorderLayout.NORTH);
        frame.revalidate();
        frame.repaint();
    }
}