import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.Dimension;

import java.util.Stack;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.Box;
import javax.swing.BoxLayout;


import java.util.Random;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Hash map
import java.util.HashMap;
import java.util.Map;

public class MyFrame extends JFrame {
    private int DICE_COUNT = 8;

    ImageLabel[] diceImgs = new ImageLabel[DICE_COUNT];
    Map<Integer, Integer> DiceArray = new HashMap<Integer, Integer>();
    private List<PlayerBox> Boxes = new ArrayList<>();
    private int dice_score = 0;
    private boolean canClick = false;
    private boolean stopButtonClick = false;

    int[] images = {21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};

    // add players
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    private void addPlayers(String... playerNames) {
        for (String name : playerNames) {
            players.add(new Player(name, "Human"));
        }
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex % players.size());
    }

    private void AddImageGuiComponents(JPanel imagepanel) {
        // create list of image and display it
        imagepanel.setBounds(0, 0, 1190, 120);

        for (int i = 0; i < images.length; i++) {
            ImageIcon img = new ImageIcon("resources/"+String.valueOf(images[i])+".jpg");
            Image newImg = img.getImage().getScaledInstance(69, 118, Image.SCALE_SMOOTH); 
            img = new ImageIcon(newImg);
            
            JLabel imageLabel = new JLabel(img);
            imagepanel.add(imageLabel);
        } 

        this.add(imagepanel);
    }

    private boolean checkWorm() {
        boolean haveWorm = false;
        // check whether the player is fail or not 
        for(Map.Entry<Integer,Integer> each : DiceArray.entrySet()) {
            if(each.getKey() == 6) {
                haveWorm = true;
            }
        }  
        return haveWorm;       
    }

    private void checkPlayerFail(JLabel playerName) {
        // check all of elements in diceImgs whether they are in DiceArray or not
        int remainder = 0;

        for(int j = 0; j < DICE_COUNT; j++) {
            for(Map.Entry<Integer,Integer> each : DiceArray.entrySet()) {
                if(each.getKey() == Integer.parseInt(diceImgs[j].getImageName().substring(diceImgs[j].getImageName().length() - 5, diceImgs[j].getImageName().length() - 4))) {
                    remainder++;
                }
            }
        }

        if(remainder == DICE_COUNT) {
            // Pop up a message to announce failure of the player
            JOptionPane.showMessageDialog(MyFrame.this, "You failed this round.", "Player Failed", JOptionPane.INFORMATION_MESSAGE);
            NextTurn(playerName);
        }
    } 

    private void calculateTotalDice() {
        for(Map.Entry<Integer,Integer> each : DiceArray.entrySet()) {
            int temp = each.getKey();
            if(temp == 6) {
                temp = 5;
            }
            System.out.println("Key = " + each.getKey() + ", Value = " + each.getValue());
            dice_score += each.getValue() * temp;
        }

        System.out.println(getCurrentPlayer().getName() + ": " + dice_score);

        if(dice_score > 20 && dice_score < 37) {
            // Create a Tile object with the dice_score
            Tile tile = new Tile(dice_score);
            
            // Push the Tile onto the player's tiles stack
            players.get(currentPlayerIndex % players.size()).getTiles().push(tile);

            Stack<Tile> tiles = new Stack<>();
            tiles.addAll(players.get(currentPlayerIndex % players.size()).getTiles());
            
            System.out.println("Tiles for Player " + players.get(currentPlayerIndex % players.size()).getName() + ":");
            for (Tile t : tiles) {
                System.out.println("Tile score: " + t.getValue());
            }
            
            // Get the currentPlayerBox
            PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());

            // Update the image in the PlayerBox
            currentPlayerBox.updateImage(dice_score);
        } else {
            JOptionPane.showMessageDialog(MyFrame.this, "No score on grill. You fail!!", "Player Failed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Fail function
    private void NextTurn(JLabel playerName) {
        currentPlayerIndex++; // Move to the next player
        // Update the label to show the current player
        Player currentPlayer = getCurrentPlayer();
        playerName.setText(currentPlayer.getName());

        // delete the remaining image from frame
        for(int i = 0; i < DICE_COUNT; i++) {
            MyFrame.this.remove(diceImgs[i]);
        }

        
        players.get(currentPlayerIndex % players.size()).getTiles().clear();
        
        // Reset the dice count
        DICE_COUNT = 8;
        
        // Reset the dice array
        DiceArray.clear();
        
        // reset the click button
        canClick = false;
        
        // Reset the dice score
        dice_score = 0;
        
        // Update the color of the player boxes
        for (int i = 0; i < players.size(); i++) {
            Boxes.get(i).setCurrentPlayer(i == currentPlayerIndex % players.size());
        }
        
        MyFrame.this.revalidate();
        MyFrame.this.repaint();

        // Add the dice images back to the array
        AddDiceGuiComponents(playerName);
    }

    private void AddDiceGuiComponents(JLabel playerName) {
        // Create JLabel elements for all 8 dices
        for (int i = 0; i < DICE_COUNT; i++) {
            int randomNumber = new Random().nextInt(6) + 1;
            diceImgs[i] = ImgService.loadImage(String.format("resources/dice%d.png",randomNumber));   
            diceImgs[i].setBounds(((i+1) * 120), 270, 110, 110);
            MyFrame.this.revalidate();
            MyFrame.this.repaint();

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
                        
                        boolean isAppeared = false;

                        for(Map.Entry<Integer,Integer> each : DiceArray.entrySet()) {
                            if(each.getKey() == Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4))) {
                                isAppeared = true;
                            }
                        }

                        if(isAppeared == true) {
                            JOptionPane.showMessageDialog(MyFrame.this, "Please select different dice.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        } else {
                            int count = 0;
                            // Remove the clicked image from the frame1
                            for(int i = 0; i < DICE_COUNT; i++){
                                if (diceImgs[i].getImageName().equals(imageName)) {
                                    count++;
                                    MyFrame.this.remove(diceImgs[i]); // Remove the image that matches the clicked one
                                }
                            }
    
                            // Remove the clicked image from the array
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
                            DICE_COUNT -= count; // Update the number of remaining dice
                            System.out.println(DICE_COUNT);
    
                            MyFrame.this.revalidate();
                            MyFrame.this.repaint();
                            canClick = false;
                            stopButtonClick = true;
    
                            // Update the current player's dice score
                            DiceArray.put(Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4)), count);
    
                            // Add remaining images to the frame
                            System.out.println("Clicked on: " + imageName.charAt(imageName.length() - 5) + " appears " + count + " times");
                        }

                    } else {
                        // Display error message
                        JOptionPane.showMessageDialog(MyFrame.this, "Please roll the dice before selecting.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (DICE_COUNT == 0) {
                        boolean haveWorm = checkWorm();

                        if(haveWorm) {
                            calculateTotalDice();
                        } else {
                            JOptionPane.showMessageDialog(MyFrame.this, "You failed this round.", "Player Failed", JOptionPane.INFORMATION_MESSAGE);
                        }

                        NextTurn(playerName);
                    } 
                }
            });
            this.add(diceImgs[i]);
        }
    }

    private void AddStopButton(JButton stopButton,JLabel playerName) {
        stopButton.setBounds(600,470,120,30);
        this.add(stopButton);

        // Add action listener to stop button
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopButtonClick == true) {
                    int choose = JOptionPane.showConfirmDialog(null,"Do you want to stop your turn?", "choose one", JOptionPane.YES_NO_OPTION);
                    if(choose == 0) {
                        System.out.println("Stop!!!");
                        calculateTotalDice();
                        stopButtonClick = false;
                        NextTurn(playerName);
                    }
                } else {
                    // Display error message
                    JOptionPane.showMessageDialog(MyFrame.this, "Please finish your turn before stop", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Add roll button
    private void AddRollButton(JButton rollButton,JLabel playerName) {
        rollButton.setBounds(460, 470, 120, 30);
        this.add(rollButton);
        Random rand = new Random();

        // Add action listener to roll button
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canClick == false) {
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

                            checkPlayerFail(playerName);
                        }
                    });
                    rollThread.start();
                } else {
                    // Display error message
                    JOptionPane.showMessageDialog(MyFrame.this, "Please select dice before rolling.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    MyFrame(List<String> playerNames) {
        setLayout(null); // Set layout to null

        //1. Add Players
        for(String name : playerNames) {
            addPlayers(name);
        }
        
        Player currentPlayer = getCurrentPlayer();
        JLabel playerName = new JLabel(currentPlayer.getName());
        playerName.setBounds(10, 160, 100, 30);
        this.add(playerName);

        // Create a JPanel to hold multiple PlayerBoxes
        JPanel playerPanel = new JPanel();
        playerPanel.setBackground(new Color(51,255,153));

        // Add vertical spacing between PlayerBoxes
        int verticalSpacing = 10;

        //creating players box
        for(int i = 0; i < players.size(); i++) {
            PlayerBox playerBox = new PlayerBox(players.get(i).getName());
            Boxes.add(playerBox);
            playerBox.setPreferredSize(new Dimension(200, 200)); // Set a fixed height, adjust as needed
            playerBox.setCurrentPlayer(i == currentPlayerIndex % players.size());
            playerPanel.add(playerBox);

            if (i < players.size() - 1) {
                playerPanel.add(Box.createRigidArea(new Dimension(verticalSpacing, 0)));
            }
        }

        // playerPanel.setBounds(1000, 130, 500, 1000);
        // this.add(playerPanel);

        // Set the preferred size for the playerPanel to enable proper scrolling
        // Dimension size = new Dimension(100, (players.size() * 220) + ((players.size() - 1) * verticalSpacing));
        Dimension size = new Dimension((players.size() * 220) + ((players.size() - 1) * verticalSpacing),100);
        playerPanel.setPreferredSize(size);

        // Add the playerPanel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        // scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane.setBounds(1000, 124, 190, 635);
        scrollPane.setBounds(0, 543, 1186, 220);
        this.add(scrollPane);

        // create list of image and display it
        JPanel imagepanel = new JPanel();
        AddImageGuiComponents(imagepanel);

        AddDiceGuiComponents(playerName);

        Font buttonFont = new Font("Arial", Font.BOLD, 12); // Change the 16 to adjust the font size
        
        //Roll Button
        JButton rollButton = new JButton("Roll All Dice!");
        rollButton.setFont(buttonFont);
        AddRollButton(rollButton,playerName);

        // Stop Button
        JButton stopButton = new JButton("Stop");
        stopButton.setFont(buttonFont);
        AddStopButton(stopButton,playerName);
        
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

        this.repaint();
        this.revalidate();
    }
}