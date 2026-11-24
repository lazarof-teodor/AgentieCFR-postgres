package agentie.cfr.model;

public class Loc {

    private int id;
    private Vagon vagon;
    private int numarLoc;

    public Loc() {
    }

    public Loc(int id, Vagon vagon, int numarLoc) {
        this.id = id;
        this.vagon = vagon;
        this.numarLoc = numarLoc;
    }

    public Loc(Vagon vagon, int numarLoc) {
        this.vagon = vagon;
        this.numarLoc = numarLoc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vagon getVagon() {
        return vagon;
    }

    public void setVagon(Vagon vagon) {
        this.vagon = vagon;
    }

    public int getNumarLoc() {
        return numarLoc;
    }

    public void setNumarLoc(int numarLoc) {
        this.numarLoc = numarLoc;
    }

    @Override
    public String toString() {
        return "Vagon " + vagon.getNumarVagon() + ", loc " + numarLoc;
    }
}