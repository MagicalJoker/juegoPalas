package controladores;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Juego extends JPanel {
    public int timeRemaining; // Tiempo restante en segundos
    public Timer gameTimer;   // Temporizador para la cuenta regresiva
    public Timer brickTimer;  // Temporizador para generar ladrillos en dificultad media
    public Timer brickGenerationTimer; // Temporizador para dificultad baja

    public final int WIDTH = 800, HEIGHT = 600;
    public final int BALL_SIZE = 20, PADDLE_WIDTH = 100, PADDLE_HEIGHT = 10, BRICK_WIDTH = 60, BRICK_HEIGHT = 20;
    public int ballSpeedX, ballSpeedY, paddleSpeed, score = 0, lives = 3;
    public Rectangle ball, paddleBottom, paddleTop;
    public List<Rectangle> bricks;
    public Timer timer;
    public boolean isGameRunning = true;

    public Juego(String difficulty) {
        JFrame frame = new JFrame("Juego de Pelota - Dificultad: " + difficulty);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setLocationRelativeTo(null);

        setFocusable(true);
        setBackground(Color.GRAY);

        initGame(difficulty);
        setupKeyListener();
        startTimer();

        timer = new Timer(1000 / 60, e -> {
            updateGame();
            repaint();
        });
        timer.start();

        frame.setVisible(true);
    }

    public void initGame(String difficulty) {
        ball = new Rectangle(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);
        paddleBottom = new Rectangle(WIDTH / 2 - PADDLE_WIDTH / 2, HEIGHT - 40, PADDLE_WIDTH, PADDLE_HEIGHT);

        if (difficulty.equals("medium")) {
            paddleTop = new Rectangle(WIDTH / 2 - PADDLE_WIDTH / 2, 30, PADDLE_WIDTH, PADDLE_HEIGHT);
            ballSpeedX = 6;
            ballSpeedY = 6;
            startBrickGeneration();
        } else {
            ballSpeedX = 4;
            ballSpeedY = 4;
            startBrickGenerationForEasy();
        }

        paddleSpeed = 15;
        bricks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            bricks.add(new Rectangle(i % 10 * BRICK_WIDTH + 50, i / 10 * BRICK_HEIGHT + 100, BRICK_WIDTH, BRICK_HEIGHT));
        }
    }

    public void startTimer() {
        timeRemaining = (paddleTop == null) ? 600 : 300; // 600 segundos para dificultad baja, 300 para media

        gameTimer = new Timer(1000, e -> {
            timeRemaining--;
            if (timeRemaining <= 0) {
                gameTimer.stop();
                endGame(false); // Termina el juego si el tiempo llega a 0
            }
            repaint(); // Actualiza la pantalla para mostrar el tiempo restante
        });
        gameTimer.start();
    }

    public void startBrickGeneration() {
        brickTimer = new Timer(15000, e -> {
            if (bricks.size() < 50) {
                for (int i = 0; i < 5; i++) {
                    bricks.add(new Rectangle(i % 10 * BRICK_WIDTH + 50, (bricks.size() / 10) * BRICK_HEIGHT + 100, BRICK_WIDTH, BRICK_HEIGHT));
                }
            }
        });
        brickTimer.start();
    }

    public void startBrickGenerationForEasy() {
        brickGenerationTimer = new Timer(120000, e -> { // 2 minutos en milisegundos
            if (bricks.size() < 30) {
                for (int i = 0; i < 5; i++) {
                    bricks.add(new Rectangle(i % 10 * BRICK_WIDTH + 50, (bricks.size() / 10) * BRICK_HEIGHT + 100, BRICK_WIDTH, BRICK_HEIGHT));
                }
            }
        });
        brickGenerationTimer.start();
    }

    public void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT && paddleBottom.x > 0) {
                    paddleBottom.x -= paddleSpeed;
                    if (paddleTop != null) paddleTop.x -= paddleSpeed;
                } else if (key == KeyEvent.VK_RIGHT && paddleBottom.x < WIDTH - PADDLE_WIDTH) {
                    paddleBottom.x += paddleSpeed;
                    if (paddleTop != null) paddleTop.x += paddleSpeed;
                }
            }
        });
    }

    public void updateGame() {
        if (!isGameRunning) return;

        ball.x += ballSpeedX;
        ball.y += ballSpeedY;

        // Rebote en paredes laterales
        if (ball.x <= 0 || ball.x >= WIDTH - BALL_SIZE) {
            ballSpeedX *= -1;
        }

        // Rebote en la pared superior solo en dificultad baja
        if (ball.y <= 0) {
            if (paddleTop == null) { // En dificultad baja, rebota
                ballSpeedY *= -1;
            } else { // En dificultad media, se pierde una vida
                lives--;
                score = Math.max(0, score - 5); // Resta 5 puntos
                if (lives == 0) {
                    endGame(false);
                } else {
                    resetBall();
                }
            }
        }

        // Rebote en palas
        if (ball.intersects(paddleBottom) || (paddleTop != null && ball.intersects(paddleTop))) {
            ballSpeedY *= -1;
        }

        // Pierde una vida si toca la pared inferior (en ambas dificultades)
        if (ball.y > HEIGHT) {
            lives--;
            score = Math.max(0, score - 5); // Resta 5 puntos
            if (lives == 0) {
                endGame(false);
            } else {
                resetBall();
            }
        }

        // Colisión con ladrillos
        bricks.removeIf(brick -> {
            if (ball.intersects(brick)) {
                ballSpeedY *= -1;
                score += 10;
                checkVictory(); // Verificar si el jugador ha ganado
                return true;
            }
            return false;
        });
    }

    public void checkVictory() {
        int targetScore = (paddleTop == null) ? 300 : 500; // Baja: 300, Media: 500
        if (score >= targetScore) {
            endGame(true); // Finaliza el juego con victoria
        }
    }

    public void resetBall() {
        ball.setLocation(WIDTH / 2 - BALL_SIZE / 2, HEIGHT / 2 - BALL_SIZE / 2);
    }

    public void endGame(boolean hasWon) {
        isGameRunning = false;
        timer.stop();
        if (gameTimer != null) gameTimer.stop();
        if (brickTimer != null) brickTimer.stop();
        if (brickGenerationTimer != null) brickGenerationTimer.stop();

        String message = hasWon ? "¡Has ganado! Puntuación final: " + score : "Juego Terminado. Puntuación final: " + score;
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Doble búfer
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();

        drawGame(g2d);
        g.drawImage(buffer, 0, 0, null);
        g2d.dispose();
    }

    public void drawGame(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Puntos: " + score, 20, 50);
        g.drawString("Vidas: " + lives, 20, 70);
        g.drawString("Tiempo: " + formatTime(timeRemaining), WIDTH - 150, 50);

        g.setColor(Color.RED);
        g.fillOval(ball.x, ball.y, ball.width, ball.height);

        g.setColor(Color.BLUE);
        g.fillRect(paddleBottom.x, paddleBottom.y, paddleBottom.width, paddleBottom.height);
        if (paddleTop != null) {
            g.fillRect(paddleTop.x, paddleTop.y, paddleTop.width, paddleTop.height);
        }

        g.setColor(Color.GREEN); // Color para el interior del ladrillo
        for (Rectangle brick : bricks) {
            g.fillRect(brick.x, brick.y, brick.width, brick.height); // Rellenar el ladrillo

            g.setColor(Color.RED); // Color para el borde del ladrillo
            g.drawRect(brick.x, brick.y, brick.width, brick.height); // Dibujar el borde
            g.setColor(Color.GREEN); // Volver a poner el color del interior
        }
    }

    public String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}


