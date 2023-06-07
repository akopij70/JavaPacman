import javax.swing.*;
public class GameFrame extends JFrame{
    private GamePanel panel;

    public GameFrame() {
        panel = new GamePanel();
        setTitle("PACMAN");
        setSize(414, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        add(panel);
        panel.requestFocus();
        revalidate();
        repaint();
    }
}
