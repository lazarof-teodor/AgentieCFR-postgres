package agentie.cfr.model;

public class Rol {

    private int id;
    private String nume;

    public Rol() {
    }

    public Rol(int id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public Rol(String nume) {
        this.nume = nume;
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

    @Override
    public String toString() {
        return nume;
    }
}