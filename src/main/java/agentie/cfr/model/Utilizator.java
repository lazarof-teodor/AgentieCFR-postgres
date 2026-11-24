package agentie.cfr.model;

public class Utilizator {

    private int id;
    private String username;
    private String parolaHash;
    private String nume;
    private String prenume;
    private Rol rol;
    private boolean activ;

    public Utilizator() {
    }

    public Utilizator(int id, String username, String parolaHash, String nume, String prenume, Rol rol, boolean activ) {
        this.id = id;
        this.username = username;
        this.parolaHash = parolaHash;
        this.nume = nume;
        this.prenume = prenume;
        this.rol = rol;
        this.activ = activ;
    }

    public Utilizator(String username, String parolaHash, String nume, String prenume, Rol rol) {
        this.username = username;
        this.parolaHash = parolaHash;
        this.nume = nume;
        this.prenume = prenume;
        this.rol = rol;
        this.activ = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParolaHash() {
        return parolaHash;
    }

    public void setParolaHash(String parolaHash) {
        this.parolaHash = parolaHash;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActiv() {
        return activ;
    }

    public void setActiv(boolean activ) {
        this.activ = activ;
    }

    @Override
    public String toString() {
        return username + " (" + rol.getNume() + ")";
    }
}