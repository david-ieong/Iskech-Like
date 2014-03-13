package client;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author david & quentin
 */
public interface Observable {

     void ajout_Observateur(Observateur o);
     void supp_Observateur(Observateur o);
     void notifieTousObservateur();
}
