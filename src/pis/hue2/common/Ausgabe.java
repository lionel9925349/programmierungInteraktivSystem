package pis.hue2.common;

/**
        *Schnittstelle, die als Verbindung zwischen den Komponenten und der grafischen Benutzeroberfläche des Servers und des Clients dient.
        * @author <b>Lionel Ullrich Noumi Noumi and Ruben Chester Dieutchou</b>
        */
public interface Ausgabe {
    /**
     * Gibt auf der Schnittstelle die Antwortnachricht des Servers oder des Clients aus.
     * @param message Nachricht vom Server/Client
     */
    void zeigeNachricht(String message);

    /**
     * Zeigt auf der Benutzeroberfläche eine bestimmte Liste des Servers oder des Clients an.
     * @param list lst von folder
     */
    void zeigeListe(String list);
}