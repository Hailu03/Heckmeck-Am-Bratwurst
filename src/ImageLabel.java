import javax.swing.*;

public class ImageLabel extends JLabel {
    private String imageName;

    public ImageLabel(Icon icon, String imageName) {
        super(icon);
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}