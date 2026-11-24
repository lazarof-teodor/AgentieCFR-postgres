package agentie.cfr.model;

import java.math.BigDecimal;

public class Vagon {

    private int id;
    private Tren tren;
    private int numarVagon;
    private String clasa;
    private int nrLocuri;
    private BigDecimal multiplicatorPret;

    public Vagon() {
    }

    public Vagon(int id, Tren tren, int numarVagon, String clasa, int nrLocuri, BigDecimal multiplicatorPret) {
        this.id = id;
        this.tren = tren;
        this.numarVagon = numarVagon;
        this.clasa = clasa;
        this.nrLocuri = nrLocuri;
        this.multiplicatorPret = multiplicatorPret;
    }

    public Vagon(Tren tren, int numarVagon, String clasa, int nrLocuri, BigDecimal multiplicatorPret) {
        this.tren = tren;
        this.numarVagon = numarVagon;
        this.clasa = clasa;
        this.nrLocuri = nrLocuri;
        this.multiplicatorPret = multiplicatorPret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tren getTren() {
        return tren;
    }

    public void setTren(Tren tren) {
        this.tren = tren;
    }

    public int getNumarVagon() {
        return numarVagon;
    }

    public void setNumarVagon(int numarVagon) {
        this.numarVagon = numarVagon;
    }

    public String getClasa() {
        return clasa;
    }

    public void setClasa(String clasa) {
        this.clasa = clasa;
    }

    public int getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(int nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    public BigDecimal getMultiplicatorPret() {
        return multiplicatorPret;
    }

    public void setMultiplicatorPret(BigDecimal multiplicatorPret) {
        this.multiplicatorPret = multiplicatorPret;
    }

    @Override
    public String toString() {
        return "Vagon " + numarVagon + " (clasa " + clasa + ", tren " + tren.getNumar() + ")";
    }
}