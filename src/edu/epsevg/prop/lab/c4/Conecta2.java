/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;
import java.time.Instant;
import java.time.Duration;

/**
 * Clase {@code Conecta2} que implementa un jugador automático para el juego
 * Conecta 4 utilizando el algoritmo Minimax con poda alfa-beta.
 * 
 * Este jugador prioriza movimientos estratégicos en el tablero, como ocupar 
 * el centro, evitar alineaciones peligrosas y maximizar su puntaje heurístico.
 * 
 * Implementa las interfaces {@code Jugador} y {@code IAuto}.
 * 
 * @author AlejandraLisette
 */
public class Conecta2 implements Jugador, IAuto {
    /**
     * Valor infinito utilizado para inicializar variables en el algoritmo Minimax.
     */
    int INFINIT = Integer.MAX_VALUE;    //numero gran que representa el infinit
    /**
     * Valor negativo infinito utilizado para inicializar variables en el algoritmo Minimax.
     */
    int MENYS_INFINIT = Integer.MIN_VALUE;    //numero molt petit que representa el menys infinit
 // Atributos privados
    private String _nom; // Nombre del jugador
    private Boolean _poda; // Indica si la poda alfa-beta está habilitada
    private int _nJugades; // Número de movimientos realizados por el jugador
    private int _profMax; // Profundidad máxima del árbol de búsqueda
    private int _calculsHeuristica; // Número de veces que se ha calculado la heurística
    private static final int[] PUNTUACIONES = {0, 1, 10, 100, 1000}; 
    //Puntuaciones es una constante que define los valores asignados a configuraciones de fichas en un grupo de 4 posiciones durante
    //la evaluacion de la heuristica del tablero de Connect4
     /**
     * Constructor de la clase {@code Conecta2}.
     * 
     * @param profunditatMaxima Profundidad máxima del árbol de búsqueda.
     * @param poda              Indica si se debe utilizar poda alfa-beta.
     */
    public Conecta2(int profunditatMaxima, boolean poda) {
        _nom = "conecta2";
        _nJugades = 0;
        _profMax = profunditatMaxima;
        _poda = poda;
        _calculsHeuristica = 0;

        
    }
     /**
     * Calcula el próximo movimiento a realizar en el tablero utilizando 
     * el algoritmo Minimax con poda alfa-beta.
     * 
     * @param t     Tablero actual del juego.
     * @param color Color del jugador (1 o -1).
     * @return Columna donde se realizará el movimiento.
     */
    @Override
    public int moviment(Tauler t, int color) {
        int millor_columna = -1;
        ++_nJugades;
        Instant inici = Instant.now();
        int h_actual = MENYS_INFINIT;
        int h_alpha = MENYS_INFINIT;
        int h_beta = INFINIT;
        for (int c = 0; c < t.getMida(); ++c) {
            if (t.movpossible(c)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(c, color);
                int h_minima = MIN(aux, c, _profMax - 1, color, h_alpha, h_beta);
                if (h_actual <= h_minima) {
                    h_actual = h_minima;
                    millor_columna = c;
                }
            }
        }
        Instant fi = Instant.now();
        long duracio = Duration.between(inici, fi).getSeconds();
        int c = millor_columna+1;

        System.out.println("> Jugada: " + _nJugades + " amb temps: " + duracio + "s.");
        System.out.println("> Columna triada per tirar la fitxa: " + c);
       // System.out.println("> Nodes examinats per fer el moviment: " + _tExplorats);
       // System.out.println("> Nodes explorats totals durant la partida: " + _t_ExpT);
        System.out.println("> Nodes on s'ha calculat l'heuristica fins ara:" + _calculsHeuristica);

        return millor_columna;
    }
    /**
     * Retorna el nombre del jugador.
     * 
     * @return Nombre del jugador.
     */
    @Override
    public String nom() {
        return _nom;
    }
    /**
     * Método que implementa el paso "MIN" del algoritmo Minimax con o sin poda alfa-beta.
     * Evalúa el peor caso posible para el jugador actual equivalente a minimizar el valor.
     * 
     * @param t            Tablero actual.
     * @param columna      Columna del último movimiento.
     * @param profunditat  Profundidad actual del árbol de búsqueda.
     * @param colorAct     Color del jugador actual.
     * @param _alpha       Valor alfa (poda máxima).
     * @param _beta        Valor beta (poda mínima).
     * @return Evaluación mínima calculada para este nivel del árbol.
     */
    private int MIN(Tauler t, int columna, int profunditat, int colorAct, int _alpha, int _beta) {
    int millor_valor = INFINIT;
    int color = 0;
    for (int i = t.getMida() - 1; i >= 0; --i) {
        int c = t.getColor(i, columna);
        if (c != 0) {
            color = c;
            break;
        }
    }

    if (t.solucio(columna, color)) return millor_valor;
    else if (profunditat == 0 || !t.espotmoure()) {
        return heuristica(t, colorAct);
    } else {
        // Ordre dels nodes a explorar: 3, 4, 2, 5, 1, 6, 0, 7
        int[] ordenColumnas = {3, 4, 2, 5, 1, 6, 0, 7};

        for (int idx = 0; idx < ordenColumnas.length; idx++) {
            int col = ordenColumnas[idx];
            if (t.movpossible(col)) {
                Tauler Taux = new Tauler(t);
                Taux.afegeix(col, -colorAct);
                int h_a = MAX(Taux, col, profunditat - 1, colorAct, _alpha, _beta);
                millor_valor = Math.min(millor_valor, h_a);
                if (_poda) {
                    _beta = Math.min(millor_valor, _beta);
                    if (_beta <= _alpha) { // PODA
                        break;
                    }
                }
            }
        }
    }
    return millor_valor;
}
    /**
     * Método auxiliar que implementa el paso "MAX" del algoritmo Minimax con poda alfa-beta.
     * Evalúa el mejor caso posible para el jugador actual.
     * 
     * @param t            Tablero actual.
     * @param columna      Columna del último movimiento.
     * @param profunditat  Profundidad actual del árbol de búsqueda.
     * @param colorAct     Color del jugador actual.
     * @param _alpha       Valor alfa (poda máxima).
     * @param _beta        Valor beta (poda mínima).
     * @return Evaluación máxima calculada para este nivel del árbol.
     */
    private int MAX(Tauler t, int columna, int profunditat, int colorAct, int _alpha, int _beta) {
    int millor_valor = MENYS_INFINIT;
    int color = 0;
    for (int i = t.getMida() - 1; i >= 0; --i) {
        int c = t.getColor(i, columna);
        if (c != 0) {
            color = c;
            break;
        }
    }

    if (t.solucio(columna, color)) return millor_valor;
    else if (profunditat == 0 || !t.espotmoure()) {
        return heuristica(t, colorAct);
    } else {
        // Ordre dels nodes a explorar: 3, 4, 2, 5, 1, 6, 0, 7
        int[] ordenColumnas = {3, 4, 2, 5, 1, 6, 0, 7};

        for (int idx = 0; idx < ordenColumnas.length; idx++) {
            int col = ordenColumnas[idx];
            if (t.movpossible(col)) {
                Tauler Taux = new Tauler(t);
                Taux.afegeix(col, colorAct);
                int h_a = MIN(Taux, col, profunditat - 1, colorAct, _alpha, _beta);
                millor_valor = Math.max(millor_valor, h_a);
                if (_poda) {
                    _alpha = Math.max(millor_valor, _alpha);
                    if (_beta <= _alpha) { // PODA
                        break;
                    }
                }
            }
        }
    }
    return millor_valor;
}

     /**
     * Calcula la heurística del tablero actual para el jugador dado.
     * Evalúa factores como control del centro y alineaciones de fichas.
     * 
     * @param t        Tablero actual.
     * @param colorAct Color del jugador actual.
     * @return Puntuación heurística del tablero.
     */
    private int heuristica(Tauler t, int colorAct) {
    int puntuacion = 0;
    int mida = t.getMida(); // Tamaño del tablero cuadrado (8x8)
    int oponente = -colorAct; // Color contrario al actual
    ++_calculsHeuristica;

    // Prioridad al centro
    int columnaCentral = mida / 2;
    for (int fila = 0; fila < mida; fila++) {
        if (t.getColor(fila, columnaCentral) == colorAct) {
            puntuacion += 3; // Mayor peso en el centro
        } else if (t.getColor(fila, columnaCentral) == oponente) {
            puntuacion -= 3; // Penalización si el oponente controla el centro
        }
    }

    // Evaluación de alineaciones (horizontales, verticales, diagonales)
    puntuacion += evaluarAlineaciones(t, mida, colorAct, oponente);

    return puntuacion;
}
    /**
     * Evalúa las alineaciones (horizontales, verticales y diagonales) en el tablero
     * y asigna puntuaciones basadas en el número de fichas del jugador y del oponente.
     * 
     * @param t        Tablero actual.
     * @param mida     Tamaño del tablero.
     * @param colorAct Color del jugador actual.
     * @param oponente Color del oponente.
     * @return Puntuación heurística acumulada basada en las alineaciones.
     */
    private int evaluarAlineaciones(Tauler t, int mida, int colorAct, int oponente) {
    int puntuacion = 0;

    // Horizontales
    for (int fila = 0; fila < mida; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, 0, 1, colorAct, oponente);
        }
    }

    // Verticales
    for (int col = 0; col < mida; col++) {
        for (int fila = 0; fila < mida - 3; fila++) {
            puntuacion += evaluarGrupo(t, fila, col, 1, 0, colorAct, oponente);
        }
    }

    // Diagonales (abajo hacia arriba)
    for (int fila = 0; fila < mida - 3; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, 1, 1, colorAct, oponente);
        }
    }

    // Diagonales (arriba hacia abajo)
    for (int fila = 3; fila < mida; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, -1, 1, colorAct, oponente);
        }
    }

    return puntuacion;
}
    /**
     * Evalúa un grupo de 4 fichas en el tablero en la dirección indicada.
     * 
     * @param t        Tablero actual.
     * @param fila     Fila inicial.
     * @param col      Columna inicial.
     * @param dirFila  Dirección de la fila (1, -1, o 0).
     * @param dirCol   Dirección de la columna (1, -1, o 0).
     * @param colorAct Color del jugador actual.
     * @param oponente Color del oponente.
     * @return Puntuación heurística del grupo evaluado.
     */
private int evaluarGrupo(Tauler t, int fila, int col, int dirFila, int dirCol, int colorAct, int oponente) {
    int fichasPropias = 0;
    int fichasOponente = 0;

    for (int i = 0; i < 4; i++) {
        int nuevaFila = fila + i * dirFila;
        int nuevaCol = col + i * dirCol;

        if (nuevaFila < 0 || nuevaFila >= t.getMida() || nuevaCol < 0 || nuevaCol >= t.getMida()) {
            return 0; // Grupo inválido
        }

        int valorCasilla = t.getColor(nuevaFila, nuevaCol);
        if (valorCasilla == colorAct) {
            fichasPropias++;
        } else if (valorCasilla == oponente) {
            fichasOponente++;
        }
    }

    if (fichasPropias > 0 && fichasOponente == 0) {
        return PUNTUACIONES[fichasPropias];
    } else if (fichasOponente > 0 && fichasPropias == 0) {
        return -PUNTUACIONES[fichasOponente];
    }

    return 0;
    }

}
