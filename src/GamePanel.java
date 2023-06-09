import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private Image pacman, ghost, coin;
    private int pacmanPosX, pacmanDesiredX, pacmanDesiredY, pacmanPosY, mapHeight, mapWidth;
    private int pacmanSpeed, ghostSpeed;
    private int score;

    private int[] ghostPosX;
    private int[] ghostPosY;

    public final static int SIZE = 20;
    public final static int GHOSTS = 3;
    String scoreDescription;
    List<String[]> boardData;
    private final Font customFont = new Font("sans-serif", Font.BOLD, 14);
    boolean gameOver = false;

    GamePanel() {
        score = 0;
        ghostPosX = new int[GHOSTS];
        ghostPosY = new int[GHOSTS];
        InitializeMap();
        setDefaultPositions();
        loadImages();
        addKeyListener(new PacmanKeyAdapter());
        setFocusable(true);
        moveGhostByNewThread();

    }

    class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
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
                case KeyEvent.VK_R:
                    if(gameOver) resetGame();
                    break;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());
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
                        g2d.fillRect((x * (SIZE)), (y * (SIZE)), (SIZE - 1), (SIZE - 1));
                        break;
                    case "P":
                    case "G":
                        g2d.setColor(Color.black);
                        g2d.fillRect((x * (SIZE)), (y * (SIZE)), (SIZE - 1), (SIZE - 1));
                        break;
                    case "C":
                        g2d.drawImage(coin, (x * (SIZE)), (y * (SIZE)), (SIZE - 1), (SIZE - 1), this);
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

        for (int i = 0; i < ghostPosX.length; i++) {
            if (Math.abs(pacmanPosX - ghostPosX[i]) == 0 && Math.abs(pacmanPosY - ghostPosY[i]) == 0) {
                GameOver();
            }
        }
        if (!gameOver) movePacman();
        else drawEnd(g2d);

        showPacman(g2d);
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
        for (int i = 0; i < ghostPosX.length; i++) {

            ghostSpeed = 1;
            int chaseZone = 5;
            int ghostDesiredX = ghostPosX[i];
            int ghostDesiredY = ghostPosY[i];
            int distanceX = pacmanPosX - ghostPosX[i];
            int distanceY = pacmanPosY - ghostPosY[i];

            int randomDirection = (int) (Math.random() * 4);
            switch (randomDirection) {
                case 0:
                    ghostDesiredX = ghostPosX[i] + ghostSpeed;
                    break;
                case 1:
                    ghostDesiredX = ghostPosX[i] - ghostSpeed;
                    break;
                case 2:
                    ghostDesiredY = ghostPosY[i] + ghostSpeed;
                    break;
                case 3:
                    ghostDesiredY = ghostPosY[i] - ghostSpeed;
                    break;
            }

            if ((ghostDesiredX < SIZE) && (ghostDesiredY < SIZE)) {

                if ((chaseZone > Math.abs(distanceX)) && (chaseZone > Math.abs(distanceY))) {
                    if (Math.abs(pacmanPosX - ghostPosX[i]) == 0 && Math.abs(pacmanPosY - ghostPosY[i]) == 0) {
                        //GameOver();
                        ghostDesiredX = pacmanPosX;
                        ghostDesiredY = pacmanPosY;
                    } else if (distanceX > 0 && (Math.abs(distanceX) > Math.abs(distanceY))) {
                        ghostDesiredX = ghostPosX[i] + ghostSpeed;
                    } else if (distanceX < 0 && (Math.abs(distanceX) > Math.abs(distanceY))) {
                        ghostDesiredX = ghostPosX[i] - ghostSpeed;
                    } else if (distanceY > 0) {
                        ghostDesiredY = ghostPosY[i] + ghostSpeed;
                    } else if (distanceY < 0) {
                        ghostDesiredY = ghostPosY[i] - ghostSpeed;
                    }
                }
                String positionChecker = boardData.get(ghostDesiredY)[ghostDesiredX];
                if (Objects.equals(positionChecker, "W") || Objects.equals(positionChecker, "G")) {
                    ghostDesiredX = ghostPosX[i];
                    ghostDesiredY = ghostPosY[i];
                }
                ghostPosX[i] = ghostDesiredX;
                ghostPosY[i] = ghostDesiredY;
                repaint();
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void moveGhostByNewThread() {
        new Thread(() -> {
            while (!gameOver) {
                moveGhosts();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void GameOver() {
        gameOver = true;
    }

    private void drawEnd(Graphics2D g2d) {
        int backgrounWidth = 250;
        int backgroundHeight = 60;
        int backgroundX = (mapWidth - backgrounWidth) / 2;
        int backgroundY = (mapHeight - backgroundHeight) / 2;
        g2d.setColor(Color.white);
        g2d.fillRect(backgroundX, backgroundY, backgrounWidth, backgroundHeight);

        scoreDescription = "Koniec Gry!";
        g2d.setFont(customFont);
        g2d.setColor(Color.black);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = backgroundX + (backgrounWidth - fm.stringWidth(scoreDescription)) / 2;
        int textY = backgroundY + (backgroundHeight - fm.getHeight()) / 2 ;
        g2d.drawString(scoreDescription, textX, textY);

        scoreDescription = " Nacisnij R aby zagrac ponownie!";
        g2d.drawString(scoreDescription, textX - 80, textY + 20);
    }

    private void resetGame() {
        score = 0;
        setDefaultPositions();
        gameOver = false;
        moveGhostByNewThread();
        timer.restart();
    }

}