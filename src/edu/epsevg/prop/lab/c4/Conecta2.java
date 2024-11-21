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
    private int _nJugades, _tPartida, _tExplorats, _profMax, _t_ExpT; //el profe provarà amb profunditat 4 o 8
   //nJugades representa el nombre de moviments que ha realitzat el jugador Conecta2 en la partida
   //_profMax representa el nivell màxim de profunditat on podem simular amb Conecta2
    //constructor jugador Conecta2
    public Conecta2(int profunditatMaxima, boolean poda) {
        _nom = "conecta2";
        _nJugades = 0;
        _tPartida = 0;
        _tExplorats = 0; //serveix per fer servir la complexitat de l'algoritme en cada torn. 
        //si explora molts taulers en un moviment pot ser hi ha algo mal en la configuració de la profunditat o en la heuristica
        _profMax = profunditatMaxima;
        _poda = poda;
        
        
    }
    
    //metodes de Jugador
@Override
    public int moviment(Tauler t, int color) {
        int millor_columna = -1;
        ++_nJugades;
        _tExplorats = 0;
        ++_tPartida;
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

        _t_ExpT += _tExplorats;
        Instant fi = Instant.now();
        long duracio = Duration.between(inici, fi).getSeconds();
        int c = millor_columna+1;

        System.out.println("> Jugada: " + _nJugades + " amb temps: " + duracio + "s.");
        System.out.println("> Columna triada per tirar la fitxa: " + c);
        System.out.println("> Nodes examinats per fer el moviment: " + _tExplorats);
        System.out.println("> Nodes explorats totals durant la partida: " + _t_ExpT);
        return millor_columna;
    }

    @Override
    public String nom() {
        return _nom;
    }

    private int MIN(Tauler t, int columna, int profunditat, int colorAct, int _alpha, int _beta) {
        int millor_valor = INFINIT;
        ++_tExplorats;

        // Lógica integrada de obteUltimColor
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
           // return 0;
           return heuristica(t, colorAct);
        } else {
            for (int col = 0; col < t.getMida(); col++) {
                if (t.movpossible(col)) {
                    Tauler Taux = new Tauler(t);
                    Taux.afegeix(col, -colorAct);
                    int h_a = MAX(Taux, col, profunditat - 1, colorAct, _alpha, _beta);
                    millor_valor = Math.min(millor_valor, h_a);
                    if(_poda){
                        _beta = Math.min(millor_valor, _beta); 
                        if(_beta <= _alpha){ // PODA
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
        ++_tExplorats;
        ++_tPartida;

        //Obtenim l'ultim color de la última fitxa colocada en aquesta columna
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
            //return 0;
            return heuristica(t, colorAct);
        } else {
            for (int col = 0; col < t.getMida(); ++col) {
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
      
      
      //HEURISITCA MÁS ESTRATÉGICA
/*
    private int heuristica(Tauler t, int colorAct) {
    int puntuacion = 0;
    final int FILAS = 8;
    final int COLUMNAS = 8;
    int oponente = -colorAct; // Color contrario al actual
    
    // Prioridad al centro
    int columnaCentral = COLUMNAS / 2;
    for (int fila = 0; fila < FILAS; fila++) {
        if (t.getColor(fila, columnaCentral) == colorAct) {
            puntuacion += 3; // Mayor peso en el centro
        } else if (t.getColor(fila, columnaCentral) == oponente) {
            puntuacion -= 3; // Penalización si el oponente controla el centro
        }
    }

    // Evaluación de alineaciones (horizontales, verticales, diagonales)
    puntuacion += evaluarAlineaciones(t, FILAS, COLUMNAS, colorAct, oponente);

    return puntuacion;
}

private int evaluarAlineaciones(Tauler t, int filas, int columnas, int colorAct, int oponente) {
    int puntuacion = 0;

    // Horizontales
    for (int fila = 0; fila < filas; fila++) {
        for (int col = 0; col < columnas - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, 0, 1, colorAct, oponente);
        }
    }

    // Verticales
    for (int col = 0; col < columnas; col++) {
        for (int fila = 0; fila < filas - 3; fila++) {
            puntuacion += evaluarGrupo(t, fila, col, 1, 0, colorAct, oponente);
        }
    }

    // Diagonales (abajo hacia arriba)
    for (int fila = 0; fila < filas - 3; fila++) {
        for (int col = 0; col < columnas - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, 1, 1, colorAct, oponente);
        }
    }

    // Diagonales (arriba hacia abajo)
    for (int fila = 3; fila < filas; fila++) {
        for (int col = 0; col < columnas - 3; col++) {
            puntuacion += evaluarGrupo(t, fila, col, -1, 1, colorAct, oponente);
        }
    }

    return puntuacion;
}

private int evaluarGrupo(Tauler t, int fila, int col, int dirFila, int dirCol, int colorAct, int oponente) {
    int puntuacion = 0;
    int fichasPropias = 0;
    int fichasOponente = 0;

    for (int i = 0; i < 4; i++) {
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
} */

private int heuristica(Tauler t, int colorAct) {
    int puntuacion = 0;
    int mida = t.getMida(); // Tamaño del tablero cuadrado (8x8)

    // Control del centro: Dar prioridad a las columnas centrales
    int columnaCentral = mida / 2;
    for (int fila = 0; fila < mida; fila++) {
        if (t.getColor(fila, columnaCentral) == colorAct) {
            puntuacion += 3; // Más peso a las fichas propias en el centro
        } else if (t.getColor(fila, columnaCentral) == -colorAct) {
            puntuacion -= 3; // Penalizar si el oponente controla el centro
        }
    }

    // Bloqueo y avance: Detectar amenazas o posibles alineaciones
    puntuacion += evaluarRapido(t, colorAct);

    return puntuacion;
}

private int evaluarRapido(Tauler t, int colorAct) {
    int puntuacion = 0;
    int mida = t.getMida();

    // Evaluar rápidamente amenazas y alineaciones en el tablero
    for (int fila = 0; fila < mida; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupoRapido(t, fila, col, 0, 1, colorAct); // Horizontales
        }
    }

    for (int col = 0; col < mida; col++) {
        for (int fila = 0; fila < mida - 3; fila++) {
            puntuacion += evaluarGrupoRapido(t, fila, col, 1, 0, colorAct); // Verticales
        }
    }

    // Diagonales (abajo hacia arriba)
    for (int fila = 0; fila < mida - 3; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupoRapido(t, fila, col, 1, 1, colorAct); // Diagonal ↘
        }
    }

    // Diagonales (arriba hacia abajo)
    for (int fila = 3; fila < mida; fila++) {
        for (int col = 0; col < mida - 3; col++) {
            puntuacion += evaluarGrupoRapido(t, fila, col, -1, 1, colorAct); // Diagonal ↗
        }
    }

    return puntuacion;
}

private int evaluarGrupoRapido(Tauler t, int fila, int col, int dirFila, int dirCol, int colorAct) {
    int fichasPropias = 0;
    int fichasOponente = 0;

    for (int i = 0; i < 4; i++) {
        int valorCasilla = t.getColor(fila + i * dirFila, col + i * dirCol);
        if (valorCasilla == colorAct) {
            fichasPropias++;
        } else if (valorCasilla == -colorAct) {
            fichasOponente++;
        }
    }

    // Simplificación: Valorar alineaciones propias y penalizar amenazas del oponente
    if (fichasPropias > 0 && fichasOponente == 0) {
        return fichasPropias * 10; // Peso proporcional al número de fichas propias
    } else if (fichasOponente > 0 && fichasPropias == 0) {
        return -(fichasOponente * 10); // Penalización proporcional al número de fichas del oponente
    }

    return 0; // Grupos mixtos no se valoran
}

}