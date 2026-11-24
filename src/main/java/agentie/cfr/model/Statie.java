package agentie.cfr.model;

public class Statie {

    private int id;
    private String nume;
    private String oras;

    public Statie() {
    }

    public Statie(int id, String nume, String oras) {
        this.id = id;
        this.nume = nume;
        this.oras = oras;
    }

    public Statie(String nume, String oras) {
        this.nume = nume;
        this.oras = oras;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    @Override
    public String toString() {
        return nume + " (" + oras + ")";
    }
}