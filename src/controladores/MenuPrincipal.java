package controladores;


import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {
    public MenuPrincipal() {
        setTitle("Juego de Pelota");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        JButton playButton = new JButton("Jugar");
        JButton exitButton = new JButton("Salir");

        playButton.addActionListener(e -> openMenuDificultades());
        exitButton.addActionListener(e -> System.exit(0));

        add(new JLabel("Juego de Pelota", SwingConstants.CENTER));
        add(playButton);
        add(exitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openMenuDificultades() {
        dispose();
        new MenuDificultades();
    }
}
