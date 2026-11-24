package agentie.cfr.model;

import java.time.LocalTime;

public class Ruta {

    private int id;
    private Statie statiePlecare;
    private Statie statieSosire;
    private LocalTime durataEstimata;

    public Ruta() {
    }

    public Ruta(int id, Statie statiePlecare, Statie statieSosire, LocalTime durataEstimata) {
        this.id = id;
        this.statiePlecare = statiePlecare;
        this.statieSosire = statieSosire;
        this.durataEstimata = durataEstimata;
    }

    public Ruta(Statie statiePlecare, Statie statieSosire, LocalTime durataEstimata) {
        this.statiePlecare = statiePlecare;
        this.statieSosire = statieSosire;
        this.durataEstimata = durataEstimata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Statie getStatiePlecare() {
        return statiePlecare;
    }

    public void setStatiePlecare(Statie statiePlecare) {
        this.statiePlecare = statiePlecare;
    }

    public Statie getStatieSosire() {
        return statieSosire;
    }

    public void setStatieSosire(Statie statieSosire) {
        this.statieSosire = statieSosire;
    }

    public LocalTime getDurataEstimata() {
        return durataEstimata;
    }

    public void setDurataEstimata(LocalTime durataEstimata) {
        this.durataEstimata = durataEstimata;
    }

    @Override
    public String toString() {
        return statiePlecare.getNume() + " -> " + statieSosire.getNume() + " (" + durataEstimata + ")";
    }
}