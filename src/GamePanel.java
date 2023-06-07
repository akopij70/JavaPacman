import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private Image pacman, ghost, coin;
    private int pacmanPosX, pacmanDesiredX, pacmanDesiredY, pacmanPosY, mapHeight, mapWidth;
    private int pacmanSpeed, ghostSpeed;
    private int score;

    private int [] ghostPosX;
    private int [] ghostPosY;

    public final static int SIZE = 20;
    public final static int GHOSTS = 3;
    String scoreDescription;

    List<String[]> boardData;
    private final Font customFont = new Font("sans-serif", Font.BOLD, 14);
    GamePanel() {
        score = 0;
        ghostPosX = new int[GHOSTS];
        ghostPosY = new int[GHOSTS];
        InitializeMap();
        setDefaultPositions();
        loadImages();
        addKeyListener(new PacmanKeyAdapter());
        setFocusable(true);

    }

    class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key){
                case KeyEvent.VK_LEFT:
                    pacmanDesiredX--;
                    System.out.println("LEWO");
                    break;
                case KeyEvent.VK_RIGHT:
                    pacmanDesiredX++;
                    System.out.println("PRAWO");
                    break;
                case KeyEvent.VK_UP:
                    pacmanDesiredY--;
                    System.out.println("GORA");
                    break;
                case KeyEvent.VK_DOWN:
                    pacmanDesiredY++;
                    System.out.println("DOL");
                    break;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0,0, getWidth(), getHeight());
        drawBoard(g2d);
        drawScore(g2d);
        play(g2d);

        Toolkit.getDefaultToolkit();
        g2d.dispose();
    }

    private void InitializeMap() {
        String file = "resources/mapFile.csv";
        mapHeight = SIZE * SIZE;
        mapWidth = mapHeight;
        pacmanSpeed = 5;
        ghostSpeed = 4;
        boardData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String[] values = line.split(";");
                if (values.length != 20)
                    throw new IllegalStateException("Invalid size");
                boardData.add(values);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (boardData.size() != 20)
            throw new IllegalStateException("Invalid size");
        timer = new Timer(10, this);
        timer.start();
    }
    private void setDefaultPositions() {
        int ghostCounter = 0;
        for (int y = 0; y < SIZE; y++) {
            String[] line = boardData.get(y);
            for (int x = 0; x < SIZE; x++) {
                switch (line[x]) {
                    case "P":
                        pacmanPosX = x;
                        pacmanDesiredX = pacmanPosX;
                        pacmanPosY = y;
                        pacmanDesiredY = pacmanPosY;
                        break;
                    case "G":
                        if (ghostCounter < GHOSTS) {
                            ghostPosX[ghostCounter] = x;
                            ghostPosY[ghostCounter] = y;
                            ghostCounter++;
                        }
                        break;
                    case "":
                        boardData.get(y)[x] = "C";
                        break;
                }
            }
        }
    }
    private void loadImages() {
        pacman = new ImageIcon("resources/pacman.png").getImage();
        coin = new ImageIcon("resources/coin.png").getImage();
        ghost = new ImageIcon("resources/ghost.png").getImage();
    }

    private void drawBoard(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));
        for (int y = 0; y < SIZE; y++) {
            String[] line = boardData.get(y);
            for (int x = 0; x < SIZE; x++) {
                switch (line[x]) {
                    case "W":
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect((x * (SIZE)), (y * (SIZE)), (SIZE-1), (SIZE-1));
                        break;
                    case "P":
                    case "G":
                        g2d.setColor(Color.black);
                        g2d.fillRect((x * (SIZE)), (y * (SIZE)), (SIZE-1), (SIZE-1));
                        break;
                    case "C":
                        g2d.drawImage(coin, (x * (SIZE)), (y * (SIZE)),(SIZE-1), (SIZE-1), this);
                        break;
                }
            }
        }
    }
    private void drawScore(Graphics2D g2d) {
        scoreDescription = ("WYNIK: " + score);
        g2d.setColor(Color.white);
        g2d.drawString(scoreDescription, 15, 420);
    }
    private void play(Graphics2D g2d) {
        movePacman();
        showPacman(g2d);
        moveGhosts();
        showGhosts(g2d);
    }

    private void showPacman(Graphics2D g2d) {
        g2d.drawImage(pacman, (pacmanPosX * (SIZE)), (pacmanPosY * (SIZE)), SIZE, SIZE, this);
    }

    private void movePacman() {
        if ((pacmanDesiredX < SIZE) && (pacmanDesiredY < SIZE)) {
            String positionChecker = boardData.get(pacmanDesiredY)[pacmanDesiredX];
            System.out.println(positionChecker);
            switch (positionChecker) {
                case "W":
                    pacmanDesiredX = pacmanPosX;
                    pacmanDesiredY = pacmanPosY;
                    break;
                case "C":
                    boardData.get(pacmanDesiredY)[pacmanDesiredX] = "";
                    score += 10;
                    break;
                default:
                    pacmanPosX = pacmanDesiredX;
                    pacmanPosY = pacmanDesiredY;
                    break;
            }
        }
    }
    private void showGhosts(Graphics2D g2d) {
        for (int i = 0; i < GHOSTS; i++) {
            g2d.drawImage(ghost, (ghostPosX[i] * (SIZE)), (ghostPosY[i] * (SIZE)), SIZE, SIZE, this);
        }
    }

    private void moveGhosts() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
