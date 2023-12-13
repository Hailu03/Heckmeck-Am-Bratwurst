import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import javax.swing.BorderFactory;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.util.Random;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Hash map
import java.util.HashMap;
import java.util.Map;

public class MyFrame extends JFrame {
    private int DICE_COUNT = 8;
    Map<Integer, Integer> DiceValue = new HashMap<Integer, Integer>();
    private boolean canClick = false;
    String[] images = {"resources/21.jpg", "resources/22.jpg","resources/23.jpg","resources/24.jpg","resources/25.jpg","resources/26.jpg","resources/27.jpg","resources/28.jpg","resources/29.jpg","resources/30.jpg","resources/31.jpg","resources/32.jpg","resources/33.jpg","resources/34.jpg","resources/35.jpg","resources/36.jpg"};

    // add players
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    private void addPlayers(String... playerNames) {
        for (String name : playerNames) {
            players.add(new Player(name));
        }
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex % players.size());
    }

    private void AddImageGuiComponents(JPanel imagepanel) {
        // create list of image and display it
        imagepanel.setBounds(0, 0, 1190, 120);

        for (int i = 0; i < images.length; i++) {
            ImageIcon img = new ImageIcon(images[i]);
            Image newImg = img.getImage().getScaledInstance(69, 118, Image.SCALE_SMOOTH); 
            img = new ImageIcon(newImg);
            
            JLabel imageLabel = new JLabel(img);
            imagepanel.add(imageLabel);
        } 

        this.add(imagepanel);
    }

    private void AddDiceGuiComponents(ImageLabel[] diceImgs) {
        // Create JLabel elements for all 8 dices
        for (int i = 0; i < DICE_COUNT; i++) {
            int randomNumber = new Random().nextInt(6) + 1;
            diceImgs[i] = ImgService.loadImage(String.format("resources/dice%d.png",randomNumber));   
            diceImgs[i].setBounds((i * 80), 600, 80, 90);

            // Adding a MouseListener to each JLabel
            final int index = i; // Create a final variable to use inside the MouseListener
            diceImgs[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // When the label is clicked, retrieve the name of the image
                    if(canClick){
                        // Create a list to store the remaining ImageLabels
                        ImageLabel clickedLabel = (ImageLabel) e.getSource();
                        String imageName = clickedLabel.getImageName();
                        int count = 0;
                        for(int i = 0; i < DICE_COUNT; i++){
                            if (diceImgs[i].getImageName().equals(imageName)) {
                                count++;
                                MyFrame.this.remove(diceImgs[i]); // Remove the image that matches the clicked one
                            }
                        }

                        int temp = DICE_COUNT;
                        for(int i = 0; i < temp; i++){
                            if (diceImgs[i].getImageName().equals(imageName)) {
                                for(int k = i; k < DICE_COUNT-1; k++) {
                                        diceImgs[k] = diceImgs[k+1];
                                    }
                                temp--;
                                i--;
                            }
                        }
                        DICE_COUNT -= count;
                        DiceValue.put(Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4)), count);


                        // Add remaining images to the frame
                        System.out.println("Clicked on: " + imageName.charAt(imageName.length() - 5) + "appears " + count + " times");

                    }
                    MyFrame.this.revalidate();
                    MyFrame.this.repaint();
                    canClick = false;
                }
            });
            this.add(diceImgs[i]);
        }
    }

    private void AddRollButton(JButton rollButton, ImageLabel[] diceImgs) {
        rollButton.setBounds(230, 700, 120, 30);
        this.add(rollButton);
        Random rand = new Random();

        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollButton.setEnabled(false);

                // Roll all dice simultaneously
                long startTime = System.currentTimeMillis();
                Thread rollThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while ((System.currentTimeMillis() - startTime) / 1000F < 3) {
                            int[] diceValue = new int[DICE_COUNT];
                            // Roll each dice
                            for (int i = 0; i < DICE_COUNT; i++) {
                                diceValue[i] = rand.nextInt(1, 7);
                                // Sleep for a short period
                                try {
                                    Thread.sleep(60);
                                } catch (InterruptedException ex) {
                                    System.out.println("Threading Error: " + ex);
                                }
                            }
                            Arrays.sort(diceValue);
                            for(int i = 0; i < DICE_COUNT; i++){
                                ImgService.updateImage(diceImgs[i], "resources/dice" + diceValue[i] + ".png");
                                repaint();
                                revalidate();
                            }
                        }
                        
                        rollButton.setEnabled(true);
                        canClick = true;
                    }
                });

                if(DICE_COUNT == 0){
                    System.out.println("No dice");
                    currentPlayerIndex++;
                }
                else{
                    System.out.println("Dice");
                }

                rollThread.start();
            }
        });
    }

    MyFrame() {
        setLayout(null); // Set layout to null

        //1. Add Players
        addPlayers("Player 1", "Player 2", "Player 3", "Player 4");
        Player currentPlayer = getCurrentPlayer();
        JLabel label = new JLabel(currentPlayer.getName());
        label.setBounds(10, 160, 100, 30);
        this.add(label);

        // create list of image and display it
        JPanel imagepanel = new JPanel();
        AddImageGuiComponents(imagepanel);

        ImageLabel[] diceImgs = new ImageLabel[DICE_COUNT];
        AddDiceGuiComponents(diceImgs);

        //3. Roll Button
        Random rand = new Random();
        JButton rollButton = new JButton("Roll All Dice!");
        Font buttonFont = new Font("Arial", Font.BOLD, 12); // Change the 16 to adjust the font size
        rollButton.setFont(buttonFont);
        AddRollButton(rollButton,diceImgs);
        
        this.add(rollButton);


        this.setTitle("Heckmeck am Bratwurmeck");
        this.setSize(1200,800);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon logo = new ImageIcon("resources/logo.jpeg");
        this.setIconImage(logo.getImage());
        this.getContentPane().setBackground(new Color(51,255,153));

        // Add a border to the panel
        imagepanel.setBorder(BorderFactory.createLineBorder(Color.black));
    }
}