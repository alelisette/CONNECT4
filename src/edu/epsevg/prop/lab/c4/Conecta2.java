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

        for (int c = 0; c < t.getMida(); ++c) {
            if (t.movpossible(c)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(c, color);
                int h_minima = MIN(aux, c, _profMax - 1, color);
                if (h_actual <= h_minima) {
                    h_actual = h_minima;
                    millor_columna = c;
                }
            }
        }

        _t_ExpT += _tExplorats;
        Instant fi = Instant.now();
        long duracio = Duration.between(inici, fi).getSeconds();

        System.out.println("> Jugada: " + _nJugades + " amb temps: " + duracio + "s.");
        System.out.println("> Columna triada per tirar la fitxa: " + millor_columna);
        System.out.println("> Taulers examinats per fer el moviment: " + _tExplorats);
        System.out.println("> Taulers explorats totals durant la partida: " + _t_ExpT);
        return millor_columna;
    }

    @Override
    public String nom() {
        return _nom;
    }

    private int MIN(Tauler t, int columna, int profunditat, int colorAct) {
        int millor_valor = INFINIT;
        ++_tExplorats;

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
            return 0;
        } else {
            for (int col = 0; col < t.getMida(); col++) {
                if (t.movpossible(col)) {
                    Tauler Taux = new Tauler(t);
                    Taux.afegeix(col, -colorAct);
                    int h_a = MAX(Taux, col, profunditat - 1, colorAct);
                    millor_valor = Math.min(millor_valor, h_a);
                }
            }
        }
        return millor_valor;
    }

    private int MAX(Tauler t, int columna, int profunditat, int colorAct) {
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
            return 0;
        } else {
            for (int col = 0; col < t.getMida(); ++col) {
                if (t.movpossible(col)) {
                    Tauler Taux = new Tauler(t);
                    Taux.afegeix(col, colorAct);
                    int h_a = MIN(Taux, col, profunditat - 1, colorAct);
                    millor_valor = Math.max(millor_valor, h_a);
                }
            }
        }
        return millor_valor;
    }
      
      
      //HEURISITCA
      
}