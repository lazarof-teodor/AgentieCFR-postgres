package agentie.cfr.model;

public class Tren {

    private int id;
    private String numar;
    private String tip;

    public Tren() {
    }

    public Tren(int id, String numar, String tip) {
        this.id = id;
        this.numar = numar;
        this.tip = tip;
    }

    public Tren(String numar, String tip) {
        this.numar = numar;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumar() {
        return numar;
    }

    public void setNumar(String numar) {
        this.numar = numar;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return numar + " (" + tip + ")";
    }
}