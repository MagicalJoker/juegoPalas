package controladores;


import javax.swing.*;
import java.awt.*;

public class MenuDificultades extends JFrame {
    public MenuDificultades() {
        setTitle("Seleccionar Dificultad");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JButton easyButton = new JButton("Dificultad Baja");
        JButton mediumButton = new JButton("Dificultad Media");
        JButton backButton = new JButton("Volver");

        easyButton.addActionListener(e -> startGame("easy"));
        mediumButton.addActionListener(e -> startGame("medium"));
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal();
        });

        add(new JLabel("Selecciona una dificultad", SwingConstants.CENTER));
        add(easyButton);
        add(mediumButton);
        add(backButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(String difficulty) {
        dispose();
        new Juego(difficulty);
    }
}

