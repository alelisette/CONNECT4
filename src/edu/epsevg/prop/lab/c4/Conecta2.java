/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;
import java.time.Instant;
import java.time.Duration;

/**
 *
 * @author AlejandraLisette
 */
public class Conecta2 implements Jugador, IAuto {
    
    int INFINIT = Integer.MAX_VALUE;    //numero gran que representa el infinit
    int MENYS_INFINIT = Integer.MIN_VALUE;    //numero molt petit que representa el menys infinit
//final int FILAS = 8; // Número de filas
//final int COLUMNAS = 8; // Número de columnas

    //atributs
    private String _nom;
    private Boolean _poda;
    private int _nJugades, _profMax, _t_ExpT, _calculsHeuristica; //el profe provarà amb profunditat 4 o 8
   //nJugades representa el nombre de moviments que ha realitzat el jugador Conecta2 en la partida
   //_profMax representa el nivell màxim de profunditat on podem simular amb Conecta2
    //constructor jugador Conecta2
    public Conecta2(int profunditatMaxima, boolean poda) {
        _nom = "conecta2";
        _nJugades = 0;
        //si explora molts taulers en un moviment pot ser hi ha algo mal en la configuració de la profunditat o en la heuristica
        _profMax = profunditatMaxima;
        _poda = poda;
        _calculsHeuristica = 0;

        
    }
    
    //metodes de Jugador
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

    @Override
    public String nom() {
        return _nom;
    }
 
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
        // Orden específico: 3, 4, 2, 5, 1, 6, 0, 7
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
        // Orden específico: 3, 4, 2, 5, 1, 6, 0, 7
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

    
    //millor heuristica
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

/*
private int evaluarGrupo(Tauler t, int fila, int col, int dirFila, int dirCol, int colorAct, int oponente) {
    int puntuacion = 0;
    int fichasPropias = 0;
    int fichasOponente = 0;

    for (int i = 0; i < 4; i++) {
        // Validar que no salimos del tablero
        if (fila + i * dirFila >= t.getMida() || col + i * dirCol >= t.getMida() || fila + i * dirFila < 0 || col + i * dirCol < 0) {
            return 0;
        }

        int valorCasilla = t.getColor(fila + i * dirFila, col + i * dirCol);
        if (valorCasilla == colorAct) {
            fichasPropias++;
        } else if (valorCasilla == oponente) {
            fichasOponente++;
        }
    }

    if (fichasPropias > 0 && fichasOponente == 0) {
        puntuacion += Math.pow(10, fichasPropias); // Incrementa exponencialmente por cada ficha propia
    } else if (fichasOponente > 0 && fichasPropias == 0) {
        puntuacion -= Math.pow(10, fichasOponente); // Penaliza exponencialmente por cada ficha del oponente
    }

    return puntuacion;
  }
  */

private static final int[] PUNTUACIONES = {0, 1, 10, 100, 1000};

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
