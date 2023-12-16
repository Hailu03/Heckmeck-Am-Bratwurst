import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Image;

public class ImgService {
    public static ImageLabel loadImage(String filePath){
        BufferedImage image;
        ImageLabel imageLabel;
        try{
            InputStream inputStream = ImgService.class.getResourceAsStream(filePath);
            image = ImageIO.read(inputStream);

            ImageIcon originalIcon = new ImageIcon(image);
            Image originalImage = originalIcon.getImage();
            
            // Resize the image
            Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            
            // Create a new ImageIcon with the resized image
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            imageLabel = new ImageLabel(resizedIcon, filePath);
            return imageLabel;
        }catch(Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    public static void updateImage(ImageLabel imageLabel, String filePath){
        BufferedImage image;
        try{
            InputStream inputStream = ImgService.class.getResourceAsStream(filePath);
            image = ImageIO.read(inputStream);

            ImageIcon originalIcon = new ImageIcon(image);
            Image originalImage = originalIcon.getImage();
            
            // Resize the image
            Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            
            // Create a new ImageIcon with the resized image
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            
            imageLabel.setIcon(resizedIcon); // Set the icon for the ImageLabel
            imageLabel.setImageName(filePath);
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}
