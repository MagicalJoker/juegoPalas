package controladores;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * NO REFACTORIZADO.
 * jmormez | 23-01-2025
 * Clase que contenedra todo el funcionamiento del juego.
 */
public class Juego extends JPanel {
    public int tiempoRestante; // Tiempo restante en segundos
    public Timer temporizador;   // Temporizador para la cuenta regresiva
    public Timer generarLadrillosDificultadMedia;  // Temporizador para generar ladrillos en dificultad media
    public Timer temporizadorGenerarLadrillo; // Temporizador para dificultad baja

    public final int anchoVentana = 800, altoVentana = 600; // Dimensiones de la ventana del juego
    public final int tamanoPelota = 20, anchoPala = 100, altoPala = 10, anchoLadrillo = 60, altoLadrillo = 20; // Dimensiones de los elementos del juego
    public int velocidadPelotaX, velocidadPelotaY, velocidadPala, puntuacion = 0, vidas = 3; // Velocidades, puntuación y vidas
    public Rectangle pelota, palaInferior, palaSuperior; // Rectángulos que representan la pelota y las palas
    public List<Rectangle> ladrillos; // Lista de ladrillos
    public Timer temporizadorJuego; // Temporizador principal del juego
    public boolean juegoEjecutandose = true; // Estado del juego

    /**
     * jmormez | 23-01-2025
     * Constructor de la clase Juego.
     * Inicializa la ventana del juego y configura los elementos iniciales.
     *
     * @param dificultad Dificultad del juego (baja o media).
     */
    public Juego(String dificultad) {
        JFrame frame = new JFrame("Juego Palas - Dificultad: " + dificultad);
        frame.setSize(anchoVentana, altoVentana); // Establece el tamaño de la ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        frame.add(this); // Añade el panel del juego a la ventana
        frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla

        setFocusable(true); // Permite que el panel reciba eventos de teclado
        setBackground(Color.DARK_GRAY); // Establece el color de fondo del panel

        iniciarJuego(dificultad); // Inicializa el juego según la dificultad
        setupKeyListener(); // Configura el listener de teclado
        startTimer(); // Inicia el temporizador del juego

        // Temporizador para actualizar el juego y repintar la pantalla
        temporizadorJuego = new Timer(1000 / 60, e -> {
            actualizarJuego(); // Actualiza la lógica del juego
            repaint(); // Repinta el panel
        });
        temporizadorJuego.start(); // Inicia el temporizador

        frame.setVisible(true); // Hace visible la ventana
    }

    /**
     * jmormez | 23-01-2025
     * Inicializa el juego configurando la pelota, las palas y los ladrillos.
     *
     * @param dificultadJuego Dificultad del juego (baja o media).
     */
    public void iniciarJuego(String dificultadJuego) {
        // Inicializa la pelota en el centro de la pantalla
        pelota = new Rectangle(anchoVentana / 2 - tamanoPelota / 2, altoVentana / 2 - tamanoPelota / 2, tamanoPelota, tamanoPelota);
        // Inicializa la pala inferior
        palaInferior = new Rectangle(anchoVentana / 2 - anchoPala / 2, altoVentana - 40, anchoPala, altoPala);

        // Configura la dificultad del juego
        if (dificultadJuego.equals("medium")) {
            // Dificultad media: añade pala superior y establece velocidades
            palaSuperior = new Rectangle(anchoVentana / 2 - anchoPala / 2, 30, anchoPala, altoPala);
            velocidadPelotaX = 6; // Velocidad de la pelota en el eje X
            velocidadPelotaY = 6; // Velocidad de la pelota en el eje Y
            generarLadrillosNormal(); // Inicia la generación de ladrillos
        } else {
            // Dificultad baja: establece velocidades más lentas
            velocidadPelotaX = 4; // Velocidad de la pelota en el eje X
            velocidadPelotaY = 4; // Velocidad de la pelota en el eje Y
            generarLadrillosFacil(); // Inicia la generación de ladrillos para dificultad fácil
        }

        velocidadPala = 15; // Velocidad de movimiento de la pala
        ladrillos = new ArrayList<>(); // Inicializa la lista de ladrillos
        // Genera ladrillos en la parte superior de la pantalla
        for (int i = 0; i < 20; i++) {
            ladrillos.add(new Rectangle(i % 10 * anchoLadrillo + 50, i / 10 * altoLadrillo + 100, anchoLadrillo, altoLadrillo));
        }
    }

    /**
     * jmormez | 23-01-2025
     * Inicia el temporizador del juego que cuenta el tiempo restante.
     */
    public void startTimer() {
        // Establece el tiempo restante según la dificultad
        tiempoRestante = (palaSuperior == null) ? 600 : 300; // 600 segundos para dificultad baja, 300 para media

        // Temporizador que decrementa el tiempo restante cada segundo
        temporizador = new Timer(1000, e -> {
            tiempoRestante--; // Decrementa el tiempo restante
            if (tiempoRestante <= 0) {
                temporizador.stop(); // Detiene el temporizador
                terminarJuego(false); // Termina el juego si el tiempo llega a 0
            }
            repaint(); // Actualiza la pantalla para mostrar el tiempo restante
        });
        temporizador.start(); // Inicia el temporizador
    }

    /**
     * jmormez | 23-01-2025
     * Inicia la generación de ladrillos en dificultad media.
     */
    public void generarLadrillosNormal() {
        // Temporizador que genera ladrillos cada 15 segundos
        generarLadrillosDificultadMedia = new Timer(15000, e -> {
            if (ladrillos.size() < 50) { // Limita la cantidad de ladrillos
                for (int i = 0; i < 5; i++) {
                    // Añade nuevos ladrillos a la lista
                    ladrillos.add(new Rectangle(i % 10 * anchoLadrillo + 50, (ladrillos.size() / 10) * altoLadrillo + 100, anchoLadrillo, altoLadrillo));
                }
            }
        });
        generarLadrillosDificultadMedia.start(); // Inicia el temporizador de ladrillos
    }

    /**
     * jmormez | 23-01-2025
     * Inicia la generación de ladrillos en dificultad baja.
     */
    public void generarLadrillosFacil() {
        // Temporizador que genera ladrillos cada 2 minutos
        temporizadorGenerarLadrillo = new Timer(120000, e -> { // 2 minutos en milisegundos
            if (ladrillos.size() < 30) { // Limita la cantidad de ladrillos
                for (int i = 0; i < 5; i++) {
                    // Añade nuevos ladrillos a la lista
                    ladrillos.add(new Rectangle(i % 10 * anchoLadrillo + 50, (ladrillos.size() / 10) * altoLadrillo + 100, anchoLadrillo, altoLadrillo));
                }
            }
        });
        temporizadorGenerarLadrillo.start(); // Inicia el temporizador de generación de ladrillos
    }

    /**
     * jmormez | 23-01-2025
     * Configura el listener de teclado para controlar las palas.
     */
    public void setupKeyListener() {
        // Añade un KeyListener para manejar las teclas presionadas
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode(); // Obtiene el código de la tecla presionada
                // Mueve la pala hacia la izquierda
                if (tecla == KeyEvent.VK_LEFT && palaInferior.x > 0) {
                    palaInferior.x -= velocidadPala; // Mueve la pala hacia la izquierda
                    if (palaSuperior != null) palaSuperior.x -= velocidadPala; // Mueve la pala superior si existe
                } 
                // Mueve la pala hacia la derecha
                else if (tecla == KeyEvent.VK_RIGHT && palaInferior.x < anchoVentana - anchoPala) {
                    palaInferior.x += velocidadPala; // Mueve la pala hacia la derecha
                    if (palaSuperior != null) palaSuperior.x += velocidadPala; // Mueve la pala superior si existe
                }
            }
        });
    }

    /**
     * jmormez | 23-01-2025
     * Actualiza la lógica del juego, incluyendo el movimiento de la pelota y las colisiones.
     */
    public void actualizarJuego() {
        if (!juegoEjecutandose) return; // Si el juego no está en ejecución, no hace nada

        // Actualiza la posición de la pelota
        pelota.x += velocidadPelotaX;
        pelota.y += velocidadPelotaY;

        // Rebote en paredes laterales
        if (pelota.x <= 0 || pelota.x >= anchoVentana - tamanoPelota) {
            velocidadPelotaX *= -1; // Invierte la dirección en el eje X
        }

        // Rebote en la pared superior solo en dificultad baja
        if (pelota.y <= 0) {
            if (palaSuperior == null) { // En dificultad baja, rebota
                velocidadPelotaY *= -1; // Invierte la dirección en el eje Y
            } else { // En dificultad media, se pierde una vida
                vidas--; // Resta una vida
                puntuacion = Math.max(0, puntuacion - 5); // Resta 5 puntos por cada vida perdida
                if (vidas == 0) {
                    terminarJuego(false); // Termina el juego si no quedan vidas
                } else {
                    reiniciarPelota(); // Reinicia la posición de la pelota
                }
            }
        }

        // Rebote en palas
        if (pelota.intersects(palaInferior) || (palaSuperior != null && pelota.intersects(palaSuperior))) {
            velocidadPelotaY *= -1; // Invierte la dirección en el eje Y
        }

        // Pierde una vida si toca la pared inferior (en ambas dificultades)
        if (pelota.y > altoVentana) {
            vidas--; // Resta una vida
            puntuacion = Math.max(0, puntuacion - 5); // Resta 5 puntos por cada vida perdida
            if (vidas == 0) {
                terminarJuego(false); // Termina el juego si no quedan vidas
            } else {
                reiniciarPelota(); // Reinicia la posición de la pelota
            }
        }

        // Colisión con ladrillos
        ladrillos.removeIf(ladrillo -> {
            if (pelota.intersects(ladrillo)) { // Si la pelota colisiona con un ladrillo
                velocidadPelotaY *= -1; // Invierte la dirección en el eje Y
                puntuacion += 10; // Aumenta la puntuación
                comprobarVictoria(); // Verifica si el jugador ha ganado
                return true; // El ladrillo se elimina
            }
            return false; // El ladrillo no se elimina
        });
    }

    /**
     * jmormez | 23-01-2025
     * Verifica si el jugador ha alcanzado la puntuación objetivo para ganar.
     */
    public void comprobarVictoria() {
        int objetivoDePuntos = (palaSuperior == null) ? 300 : 500; // Baja: 300, Media: 500
        if (puntuacion >= objetivoDePuntos) {
            terminarJuego(true); // Finaliza el juego con victoria
        }
    }

    /**
     * jmormez | 23-01-2025
     * Reinicia la posición de la pelota al centro de la pantalla.
     */
    public void reiniciarPelota() {
        pelota.setLocation(anchoVentana / 2 - tamanoPelota / 2, altoVentana / 2 - tamanoPelota / 2); // Coloca la pelota en el centro
    }

    /**
     * jmormez | 23-01-2025
     * Termina el juego y muestra un mensaje de victoria o derrota.
     *
     * @param haGanado Indica si el jugador ha ganado o no.
     */
    public void terminarJuego(boolean haGanado) {
        juegoEjecutandose = false; // Cambia el estado del juego a no en ejecución
        temporizadorJuego.stop(); // Detiene el temporizador principal
        if (temporizador != null) temporizador.stop(); // Detiene el temporizador de juego
        if (generarLadrillosDificultadMedia != null) generarLadrillosDificultadMedia.stop(); // Detiene el temporizador de ladrillos
        if (temporizadorGenerarLadrillo != null) temporizadorGenerarLadrillo.stop(); // Detiene el temporizador de generación de ladrillos

        // Muestra un mensaje de finalización del juego
        String mensajeFinal = haGanado ? "¡Has ganado! Puntuación final: " + puntuacion : "Fin del juego. Puntuación final: " + puntuacion;
        JOptionPane.showMessageDialog(this, mensajeFinal); // Muestra el mensaje en un cuadro de diálogo
        System.exit(0); // Cierra la aplicación
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Llama al método de la superclase para limpiar el panel

        // Dibujo en un búfer para evitar parpadeos
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics(); // Crea un objeto Graphics2D para dibujar en el búfer

        dibujarJuego(g2d); // Dibuja el estado del juego en el búfer
        g.drawImage(buffer, 0, 0, null); // Dibuja el búfer en el panel
        g2d.dispose(); // Libera los recursos del objeto Graphics2D
    }

    /**
     * jmormez | 23-01-2025
     * Dibuja todos los elementos del juego en el gráfico proporcionado.
     *
     * @param g Objeto Graphics en el que se dibujan los elementos del juego.
     */
    public void dibujarJuego(Graphics g) {
        g.setColor(Color.BLACK); // Establece el color del texto
        g.setFont(new Font("Arial", Font.BOLD , 16)); // Establece la fuente del texto
        g.drawString("Puntos: " + puntuacion, 20, 50); // Dibuja la puntuación en la pantalla
        g.drawString("Vidas: " + vidas, 20, 70); // Dibuja el número de vidas restantes
        g.drawString("Tiempo: " + formatTime(tiempoRestante), anchoVentana - 150, 50); // Dibuja el tiempo restante

        g.setColor(Color.WHITE); // Establece el color de la pelota
        g.fillOval(pelota.x, pelota.y, pelota.width, pelota.height); // Dibuja la pelota

        g.setColor(Color.BLUE); // Establece el color de la pala inferior
        g.fillRect(palaInferior.x, palaInferior.y, palaInferior.width, palaInferior.height); // Dibuja la pala inferior
        if (palaSuperior != null) { // Si existe la pala superior
            g.fillRect(palaSuperior.x, palaSuperior.y, palaSuperior.width, palaSuperior.height); // Dibuja la pala superior
        }

        g.setColor(Color.GREEN); // Color para el interior del ladrillo
        for (Rectangle ladrillo : ladrillos) { // Itera sobre cada ladrillo
            g.fillRect(ladrillo.x, ladrillo.y, ladrillo.width, ladrillo.height); // Rellena el ladrillo

            g.setColor(Color.RED); // Color para el borde del ladrillo
            g.drawRect(ladrillo.x, ladrillo.y, ladrillo.width, ladrillo.height); // Dibuja el borde del ladrillo
            g.setColor(Color.GREEN); // Vuelve a poner el color del interior
        }
    }

    /**
     * jmormez | 23-01-2025
     * Formatea el tiempo en segundos a un formato de minutos y segundos.
     *
     * @param segundos Tiempo en segundos.
     * @return Cadena formateada en el formato "MM:SS".
     */
    public String formatTime(int segundos) {
        int minutos = segundos / 60; // Calcula los minutos
        int segundosRestantes = segundos % 60; // Calcula los segundos restantes
        return String.format("%02d:%02d", minutos, segundosRestantes); // Devuelve el tiempo formateado
    }
}


