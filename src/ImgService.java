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

    public static BufferedImage blurImage(BufferedImage image, int radius) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Perform simple box blur (replace this with an actual blur algorithm)
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;
                int count = 0;

                for (int k = -radius; k <= radius; k++) {
                    for (int l = -radius; l <= radius; l++) {
                        int x = i + k;
                        int y = j + l;

                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            int color = image.getRGB(x, y);
                            sumRed += (color >> 16) & 0xFF;
                            sumGreen += (color >> 8) & 0xFF;
                            sumBlue += color & 0xFF;
                            count++;
                        }
                    }
                }

                int avgRed = sumRed / count;
                int avgGreen = sumGreen / count;
                int avgBlue = sumBlue / count;

                int avgColor = (0xFF << 24) | (avgRed << 16) | (avgGreen << 8) | avgBlue;
                blurredImage.setRGB(i, j, avgColor);
            }
        }
        return blurredImage;
    }

    public static void updateImage(ImageLabel imageLabel, String filePath, boolean blur){
        BufferedImage image;
        try{
            InputStream inputStream = ImgService.class.getResourceAsStream(filePath);
            image = ImageIO.read(inputStream);

            ImageIcon resizedIcon;
            if(blur){
                BufferedImage blurredImage = blurImage(image, 1);
                ImageIcon originalIcon = new ImageIcon(blurredImage);
                Image originalImage = originalIcon.getImage();
            
                // Resize the image
                Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                resizedIcon = new ImageIcon(resizedImage);
            } else {
                ImageIcon originalIcon = new ImageIcon(image);
                Image originalImage = originalIcon.getImage();
                
                // Resize the image
                Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                resizedIcon = new ImageIcon(resizedImage);
            }
            
            imageLabel.setIcon(resizedIcon); // Set the icon for the ImageLabel
            imageLabel.setImageName(filePath);
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}
