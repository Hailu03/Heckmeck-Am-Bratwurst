import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.image.ImageObserver;

import java.util.Stack;
import java.util.Collections;

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

// KeyListener
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class MyFrame extends JFrame implements KeyListener {
    private int DICE_COUNT = 8;
    ImageIcon imgpanelbackground = new ImageIcon("resources/coins.png");
    Image ImgPanelBackground = imgpanelbackground.getImage();
    ImagePanel imagepanel = new ImagePanel(ImgPanelBackground);

    // Inside the creatingPlayer class constructor
    private JButton restartButton;
    int playerScore = 0;
    private JLabel gifLabel;

    ImageLabel[] diceImgs = new ImageLabel[DICE_COUNT];
    Map<Integer, Integer> DiceArray = new HashMap<Integer, Integer>();
    private List<PlayerBox> Boxes = new ArrayList<>();
    private int dice_score = 0;
    private boolean canClick = false;
    private boolean stopButtonClick = false;
    private ImagePanel DicePanel;

    JButton rollButton = new JButton("Roll All Dice!");

    List<Integer> images;

    JLabel playerName = new JLabel();

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

    private void restartGame() {
        int choose = JOptionPane.showConfirmDialog(null,"Do you want to restart game?", "choose one", JOptionPane.YES_NO_OPTION);
        if(choose == 0) {
            // Reinitialize playerNames list and reset the text field
            creatingPlayer newGame = new creatingPlayer();
            newGame.setVisible(true); // Show the player creation window
            this.dispose(); // Close the player creation window
        }
    }


    private void AddImageGuiComponents(JPanel imagepanel) {
        // create list of image and display it
        imagepanel.setBounds(0, 0, 1190, 120);

        for (int i = 0; i < images.size(); i++) {
            ImageIcon img = new ImageIcon("resources/"+String.valueOf(images.get(i))+".jpg");
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

    private void returnAndFlip() {
        if(!players.get(currentPlayerIndex % players.size()).getTiles().isEmpty()) {
            int diceValue = players.get(currentPlayerIndex % players.size()).getTiles().pop().getValue();
            images.add(diceValue);
            Collections.sort(images);

            // find the max value in images
            int max = images.get(0);
            int maxindex = 0;
            for(int i = 1; i < images.size(); i++) {
                if(images.get(i) > max && images.get(i) != 1000) {
                    max = images.get(i);
                    maxindex = i;
                }
            }

            // check whether dice_score is the last element in the images list
            if(diceValue != max) {
                images.set(maxindex, 1000);
            } 
            
            imagepanel.removeAll(); // Remove all components currently in the panel
            imagepanel.revalidate(); // Ensure the panel is updated before adding new components
            imagepanel.repaint();
    
            for(int i = 0; i < images.size(); i++) {
                ImageIcon img = new ImageIcon("resources/" + images.get(i) + ".jpg");
                Image newImg = img.getImage().getScaledInstance(69, 118, Image.SCALE_SMOOTH);
                img = new ImageIcon(newImg);
    
                JLabel imageLabel = new JLabel(img);
                imagepanel.add(imageLabel);
            }

            imagepanel.revalidate(); // Ensure the panel is updated before adding new components
            imagepanel.repaint();

            // Get the currentPlayerBox
            PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());
            currentPlayerBox.removeImage();

            if(!players.get(currentPlayerIndex % players.size()).getTiles().isEmpty()) {
                currentPlayerBox.updateImage(players.get(currentPlayerIndex % players.size()).getTiles().peek().getValue());
            }
        }
    }

    private void checkPlayerFail() {
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
            returnAndFlip();
            NextTurn();
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

        if(dice_score > 20 && dice_score < 37 && images.contains(dice_score)) {
            // Create a Tile object with the dice_score
            Tile tile = new Tile(dice_score);
            
            // Push the Tile onto the player's tiles stack
            players.get(currentPlayerIndex % players.size()).getTiles().push(tile);
            // print
            // System.out.println(players.get(currentPlayerIndex % players.size()).getTiles().peek().getValue());
            for(Tile p : players.get(currentPlayerIndex % players.size()).getTiles()) {
                System.out.println(p.getValue());
            }

            int temp = -1;
            for(int i = 0; i < images.size(); i++) {
                if(dice_score == images.get(i)) {
                    temp = i;
                }
            }

            // delete that tile on grill
            images.remove(temp);

            imagepanel.removeAll(); // Remove all components currently in the panel
            imagepanel.revalidate(); // Ensure the panel is updated before adding new components
            
            for (int i = 0; i < images.size(); i++) {
                ImageIcon img = new ImageIcon("resources/" + images.get(i) + ".jpg");
                Image newImg = img.getImage().getScaledInstance(69, 118, Image.SCALE_SMOOTH);
                img = new ImageIcon(newImg);

                JLabel imageLabel = new JLabel(img);
                imagepanel.add(imageLabel);
            }

            imagepanel.revalidate(); // Revalidate the panel after adding new components
            imagepanel.repaint();
            
            // Get the currentPlayerBox
            PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());

            // Update the image in the PlayerBox
            currentPlayerBox.updateImage(dice_score);
        } else {
            boolean check_fail = true;
            for (int i = 0; i < players.size(); i++) {
                Stack<Tile> playerTiles = players.get(i).getTiles();
                if (!playerTiles.isEmpty() && i != currentPlayerIndex % players.size()) {
                    Tile topTile = playerTiles.peek();
                    if(dice_score == topTile.getValue()) {
                        check_fail = false;
                        Tile steal = playerTiles.pop();
                        players.get(currentPlayerIndex%players.size()).getTiles().push(steal);

                        PlayerBox PlayerBoxBeingStealed = Boxes.get(i);
                        if(!players.get(i).getTiles().isEmpty()) {
                            PlayerBoxBeingStealed.updateImage(players.get(i).getTiles().peek().getValue());
                        } else {
                            PlayerBoxBeingStealed.removeImage();
                        }

                        System.out.println(steal.getValue());

                        PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());
                        currentPlayerBox.updateImage(steal.getValue());
                        break;
                    }
                } 
            }
            if(check_fail) {
                JOptionPane.showMessageDialog(MyFrame.this, "No score on grill and another player's stack.", "Player Failed", JOptionPane.INFORMATION_MESSAGE);
                
                boolean check = false;
                if(20 < dice_score && dice_score < 37) {
                    for(int i = dice_score-1; i > 20; i--) {
                        dice_score = i;
                        if(images.contains(i)) {
                            check = true;
                            break;
                        }
                    }
                }

                if(!check) {
                    returnAndFlip();
                } else {
                    // Create a Tile object with the dice_score
                    Tile tile = new Tile(dice_score);
                    
                    // Push the Tile onto the player's tiles stack
                    players.get(currentPlayerIndex % players.size()).getTiles().push(tile);
                    // print
                    // System.out.println(players.get(currentPlayerIndex % players.size()).getTiles().peek().getValue());
                    for(Tile p : players.get(currentPlayerIndex % players.size()).getTiles()) {
                        System.out.println(p.getValue());
                    }
        
                    int temp = -1;
                    for(int i = 0; i < images.size(); i++) {
                        if(dice_score == images.get(i)) {
                            temp = i;
                        }
                    }
        
                    // delete that tile on grill
                    images.remove(temp);
        
                    imagepanel.removeAll(); // Remove all components currently in the panel
                    imagepanel.revalidate(); // Ensure the panel is updated before adding new components
                    
                    for (int i = 0; i < images.size(); i++) {
                        ImageIcon img = new ImageIcon("resources/" + images.get(i) + ".jpg");
                        Image newImg = img.getImage().getScaledInstance(69, 118, Image.SCALE_SMOOTH);
                        img = new ImageIcon(newImg);
        
                        JLabel imageLabel = new JLabel(img);
                        imagepanel.add(imageLabel);
                    }
        
                    imagepanel.revalidate(); // Revalidate the panel after adding new components
                    imagepanel.repaint();
                    
                    // Get the currentPlayerBox
                    PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());
        
                    // Update the image in the PlayerBox
                    currentPlayerBox.updateImage(dice_score);
                }
            } else {
                JOptionPane.showMessageDialog(MyFrame.this, "You steal successfully.", "Congrats", JOptionPane.INFORMATION_MESSAGE);
            }
        }


    }
    // check whether the game is over or not
    private boolean checkGameOver() {
        for(int i = 0; i < images.size(); i++) {
            if(images.get(i) != 1000) {
                return false;
            }
        }

        return true;
    }

    // Fail function
    private void NextTurn() {
        if(checkGameOver()) {
            WinUI frame = new WinUI(players);
            frame.setVisible(true);
            dispose(); // Close the player creation window
        } else {
            currentPlayerIndex++; // Move to the next player
            // Update the label to show the current player
            Player currentPlayer = getCurrentPlayer();
            playerName.setText(currentPlayer.getName());
    
            // delete the remaining image from frame
            for(int i = 0; i < DICE_COUNT; i++) {
                DicePanel.remove(diceImgs[i]);
            }
            
            // Reset the dice count
            DICE_COUNT = 8;

            playerScore = 0;
            
            // Reset the dice array
            DiceArray.clear();
            
            // reset the click button
            canClick = false;
            
            // Reset the dice score
            dice_score = 0;
            
            // Update the color of the player boxes
            for (int i = 0; i < players.size(); i++) {
                if(i == currentPlayerIndex % players.size()) {
                    Boxes.get(i).setPlayerName(String.format("%s : %d",players.get(i).getName(),playerScore));
                } else {
                    Boxes.get(i).setPlayerName(players.get(i).getName());
                }
                Boxes.get(i).setCurrentPlayer(i == currentPlayerIndex % players.size());
            }
            
            MyFrame.this.revalidate();
            MyFrame.this.repaint();
    
            // Add the dice images back to the array
            AddDiceGuiComponents();
        }
    }

    private void AddDiceGuiComponents() {
        // Create JLabel elements for all 8 dices
        for (int i = 0; i < DICE_COUNT; i++) {
            int randomNumber = new Random().nextInt(6) + 1;
            diceImgs[i] = ImgService.loadImage(String.format("resources/dice%d.png",randomNumber));   
            diceImgs[i].setBounds(((i+1) * 120), 270, 110, 110);
            DicePanel.revalidate();
            DicePanel.repaint();

            // Adding a MouseListener to each JLabel
            final int index = i; // Create a final variable to use inside the MouseListener
            diceImgs[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // When the label is clicked, retrieve the name of the image
                    if(canClick){
                        ImageLabel clickedLabel = (ImageLabel) e.getSource();
                        // Create a list to store the remaining ImageLabels
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
                                    DicePanel.remove(diceImgs[i]); // Remove the image that matches the clicked one
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
    
                            DicePanel.revalidate();
                            DicePanel.repaint();
                            canClick = false;
                            stopButtonClick = true;
    
                            // Update the current player's dice score
                            DiceArray.put(Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4)), count);
                            
                            // if score is 6 then change it to 5 for playerscore
                            if(Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4)) == 6) {
                                playerScore += 5 * count;
                            } else {
                                playerScore += Integer.parseInt(imageName.substring(imageName.length() - 5, imageName.length() - 4)) * count;
                            }

                            playerName.setText(String.format("%s : %d",getCurrentPlayer().getName(),playerScore));

                            // Add remaining images to the frame
                            System.out.println("Clicked on: " + imageName.charAt(imageName.length() - 5) + " appears " + count + " times");
                        
                            PlayerBox currentPlayerBox = Boxes.get(currentPlayerIndex%players.size());
                            currentPlayerBox.setPlayerName(String.format("%s : %d",players.get(currentPlayerIndex%players.size()).getName(),playerScore));
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
                            returnAndFlip();
                        }

                        NextTurn();
                    } 
                }

                @Override
                public void mouseEntered(MouseEvent me) {
                    System.out.println("Mouse entered");
                    ImageLabel clickedLabel = (ImageLabel) me.getSource();
                    String imageName = clickedLabel.getImageName();

                    for(int i = 0; i < diceImgs.length; i++){
                        if (diceImgs[i].getImageName().equals(imageName)) {
                            ImgService.updateImage(diceImgs[i], "resources/dice" + diceImgs[i].getImageName().substring(diceImgs[i].getImageName().length() - 5, diceImgs[i].getImageName().length() - 4) + ".png",true);
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent me) {
                    System.out.println("Mouse exited");
                    ImageLabel clickedLabel = (ImageLabel) me.getSource();
                    String imageName = clickedLabel.getImageName();
                    
                    for(int i = 0; i < diceImgs.length; i++){
                        if (diceImgs[i].getImageName().equals(imageName)) {
                            ImgService.updateImage(diceImgs[i], "resources/dice" + diceImgs[i].getImageName().substring(diceImgs[i].getImageName().length() - 5, diceImgs[i].getImageName().length() - 4) + ".png",false);
                        }
                    }
                }
            });
            DicePanel.add(diceImgs[i]);
        }
    }

    private void AddStopButton(JButton stopButton) {
        stopButton.setBounds(540,470,120,30);
        this.add(stopButton);

        // Add action listener to stop button
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopButtonClick == true) {
                    rollButton.requestFocusInWindow(); // Replace rollButton with the appropriate button variable
                    if(checkWorm()) {
                        int choose = JOptionPane.showConfirmDialog(null,"Do you want to stop your turn?", "choose one", JOptionPane.YES_NO_OPTION);
                        if(choose == 0) {
                            System.out.println("Stop!!!");
                            calculateTotalDice();
                            stopButtonClick = false;
                            NextTurn();
                        }
                    } else {
                        JOptionPane.showMessageDialog(MyFrame.this, "No worm", "Invalid", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Display error message
                    JOptionPane.showMessageDialog(MyFrame.this, "Please finish your turn before stop", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Roll the dice when spacebar is pressed
            rollDice(); // Replace this with your dice rolling logic
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Your implementation here (if needed)
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Your implementation for keyTyped
    }

    private void rollDice() {
        Random rand = new Random();

        if(canClick == false) {
            canClick = true;
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
                            ImgService.updateImage(diceImgs[i], "resources/dice" + diceValue[i] + ".png",false);
                            repaint();
                            revalidate();
                        }
                    }

                    checkPlayerFail();
                }
            });
            rollThread.start();
        } else {
            // Display error message
            JOptionPane.showMessageDialog(MyFrame.this, "Please select dice before rolling.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add roll button
    private void AddRollButton(JButton rollButton) {
        rollButton.setBounds(400, 470, 120, 30);
        this.add(rollButton);

        // Add action listener to roll button
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDice();
            }
        });
    }
    

    MyFrame(List<String> playerNames) {
        setLayout(null); // Set layout to null
        this.addKeyListener(this); // Add the KeyListener to the frame
        this.setFocusable(true); // Ensure the frame can receive focus

        images = new ArrayList<>();
        for (int i = 21; i < 37; i++) {
            images.add(i);
        }

        //1. Add Players
        for(String name : playerNames) {
            addPlayers(name);
        }

        // Load the GIF from the resources folder
        ImageIcon gifIcon = new ImageIcon("resources/giphy.gif");
        // Create a JLabel to display the GIF
        gifLabel = new JLabel(gifIcon);
        gifLabel.setBounds(350, 25, gifIcon.getIconWidth(), gifIcon.getIconHeight());
        // resize the gif
        Image gifImage = gifIcon.getImage();
        int scale = 3;
        // get the width and height of the gif
        Image newGifImage = gifImage.getScaledInstance(gifIcon.getIconWidth()/scale, gifIcon.getIconHeight()/scale, Image.SCALE_DEFAULT);
        gifIcon = new ImageIcon(newGifImage);
        gifLabel.setIcon(gifIcon);
        this.add(gifLabel);

        // Set background image for backgroundPanel using custom ImagePanel class
        ImageIcon backgroundIcon = new ImageIcon("resources/woodtable.jpg");
        Image backgroundImage = backgroundIcon.getImage();
        // ImagePanel backgroundPanel = new ImagePanel(backgroundImage);
        // backgroundPanel.setLayout(null); // Set layout to null for manual positioning
        // backgroundPanel.setBounds(180, 300, 800, 120);

        // Create a JPanel to hold the dice images
        DicePanel = new ImagePanel(backgroundImage);
        DicePanel.setBounds(180, 300, 800, 120);
        DicePanel.setLayout(new BoxLayout(DicePanel, BoxLayout.X_AXIS));
        DicePanel.setBackground(new Color(51,255,153));

        // Add components
        // backgroundPanel.add(DicePanel);
        
        this.add(DicePanel);

        // Create a JPanel to hold multiple PlayerBoxes
        JPanel playerPanel = new JPanel();
        playerPanel.setBackground(new Color(51,255,153));

        // Add vertical spacing between PlayerBoxes
        int verticalSpacing = 10;

        //creating players box
        for(int i = 0; i < players.size(); i++) {
            PlayerBox playerBox;
            if(i == currentPlayerIndex % players.size()) {
                playerBox = new PlayerBox(String.format("%s : %d",players.get(i).getName(),playerScore));
            } else {
                playerBox = new PlayerBox(players.get(i).getName());
            }

            Boxes.add(playerBox);
            playerBox.setPreferredSize(new Dimension(200, 200)); // Set a fixed height, adjust as needed
            playerBox.setCurrentPlayer(i == currentPlayerIndex % players.size());
            playerPanel.add(playerBox);

            if (i < players.size() - 1) {
                playerPanel.add(Box.createRigidArea(new Dimension(verticalSpacing, 0)));
            }
        }

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
        AddImageGuiComponents(imagepanel);

        AddDiceGuiComponents();

        Font buttonFont = new Font("Arial", Font.BOLD, 12); // Change the 16 to adjust the font size
        
        //Roll Button
        rollButton.setFont(buttonFont);
        AddRollButton(rollButton);

        // Stop Button
        JButton stopButton = new JButton("Stop");
        stopButton.setFont(buttonFont);
        AddStopButton(stopButton);
        
        this.add(rollButton);

        // Add this snippet in your MyFrame constructor after setting up other buttons
        restartButton = new JButton("Restart Game");
        restartButton.setBounds(680, 470, 120, 30); // Set the button position and size
        this.add(restartButton);
        restartButton.addActionListener(e -> restartGame());

        this.setTitle("Heckmeck am Bratwurmeck");
        this.setSize(1200,800);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon logo = new ImageIcon("resources/logo.jpeg");
        this.setIconImage(logo.getImage());
        // this.getContentPane().setBackground(new Color(51,255,153));
        // background image to the screen
        JLabel background = new JLabel(new ImageIcon("resources/image.png"));
        background.setBounds(0, 0, 1200, 800);
        this.add(background);
        
        // Add a border to the panel
        imagepanel.setBorder(BorderFactory.createLineBorder(Color.black));

        this.repaint();
        this.revalidate();
    }
}