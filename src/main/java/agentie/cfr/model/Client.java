package agentie.cfr.model;

public class Client {

    private int id;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private String categorie;

    public Client() {
    }

    public Client(int id, String nume, String prenume, String email, String telefon, String categorie) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.categorie = categorie;
    }

    public Client(String nume, String prenume, String email, String telefon, String categorie) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
        this.categorie = categorie;
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

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return nume + " " + prenume + " (" + categorie + ")";
    }
}