import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class CFrame extends JFrame {
    JPanel canvas;

    public CFrame(int panel_width, int panel_height) {
        super("Window");

        this.canvas = new JPanel();

        setSize(panel_width, panel_height);
        setContentPane(canvas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        setVisible(true);

        Graphics g = canvas.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(20, 20, 100, 200);
        pack();
    }

    public void draw_image(BufferedImage image, Vector2 position) {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(20, 20, 100, 200);
    }
}