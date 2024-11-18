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
    
    int INFINIT = Integer.MAX_VALUE;
    int MENYS_INFINIT = Integer.MIN_VALUE;          // Para puntuaciones enteras.

    //atributs
    private String _nom;
    private Boolean _poda;
    private int _nJugades, _tPartida, _tExplorats, _profMax = 2, _t_ExpT=0; //el profe provarà amb profunditat 4 o 8
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
    ++_nJugades; // Incrementa el número de jugadas
    _tExplorats = 0; // Reinicia los tableros explorados del turno actual
    ++_tPartida; // Incrementa el contador de turnos
    Instant inici = Instant.now();
    int h_actual = MENYS_INFINIT;

    for (int c = 0; c < t.getMida(); ++c) { // Simula todas las posibles jugadas
        Tauler aux = new Tauler(t);
        aux.afegeix(c, color);
        int h_minima = MIN(aux, c, _profMax - 1, color);
        if (h_actual <= h_minima) {
            h_actual = h_minima;
            millor_columna = c;
        }
    }

    _t_ExpT += _tExplorats; // Acumula los explorados del turno actual
    Instant fi = Instant.now();
    long duracio = Duration.between(inici, fi).getSeconds();

    System.out.println("> Jugada: " + _nJugades + " amb temps: " + duracio + "s.");
    System.out.println("> Columna triada per tirar la fitxa: " + millor_columna);
    System.out.println("> Taulers examinats per fer el moviment: " + _tExplorats);
    System.out.println("> Taulers explorats totals durant la partida: " + _t_ExpT);
    return millor_columna; // Retorna la millor columna
}


    @Override
    public String nom() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return _nom;
    }

    private int obteUltimColor(Tauler t, int columna) {
        int i = t.getMida() - 1;
        while (i >= 0) { 
            int c = t.getColor(i, columna);
            if (c != 0) return c; // Retorna el color si encuentra una ficha
            i--; 
        }
        return 0; // Si no encuentra fichas, retorna 0
    }
    
      private int MIN(Tauler t, int columna, int profunditat, int colorAct) {
          int millor_valor = INFINIT;
          ++_tExplorats;
          //++_tPartida;
          int color = obteUltimColor(t, columna);
          if (t.solucio(columna, color)) return millor_valor;
          else if (profunditat==0 || !t.espotmoure()) {
              return 0;
          } else {
              for (int col = 0; col < t.getMida(); col++) { //// Itera sobre todas las posibles columnas (moviments)
                  if (t.movpossible(columna)) { //indica si podem posar si o no una fitxa a la columna ma
                      Tauler Taux = new Tauler(t);
                      Taux.afegeix(columna, -colorAct);
                      int h_a = MAX(Taux, col, profunditat-1, colorAct);
                      millor_valor = Math.min(millor_valor, h_a);
                      
                  }
              }
          }
          return millor_valor;
      }
    
      private int MAX(Tauler t, int columna, int profunditat, int colorAct) {
          int millor_valor = MENYS_INFINIT; //sempre comença amb menys infinit
          ++_tExplorats;
          ++_tPartida;
          int color = obteUltimColor(t, columna);
          if ( t.solucio(columna, color) ) return millor_valor; //aquesta funcio ens diu que aquesta jugada ha sigut la guanyadora 
          else if(profunditat==0 || !t.espotmoure()) {
              return 0; //posso 0 perque ara heuristica = 0, despres ho implementarem
          } else {
              for (int col=0; col < t.getMida(); ++col) { 
                  if (t.movpossible(col)) {
                      Tauler Taux = new Tauler(t); //sempre hem de actualitzar-ho amb una copia mitjançant una constructora per no modificar l'inicial 
                      Taux.afegeix(col, colorAct);
                      int h_a = MIN(Taux, col, profunditat-1, colorAct);
                      millor_valor = Math.max(millor_valor, h_a); //El max sempre maximitxa la seva jugada
                      
                  }   
              }
          }
          return millor_valor;
      }
      
      
      //HEURISITCA
      
}
