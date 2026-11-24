package agentie.cfr.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Cursa {

    private int id;
    private Tren tren;
    private Ruta ruta;
    private LocalDate data;
    private LocalTime oraPlecare;
    private LocalTime oraSosire;
    private BigDecimal pretBaza;

    public Cursa() {
    }

    public Cursa(int id, Tren tren, Ruta ruta, LocalDate data, LocalTime oraPlecare, LocalTime oraSosire, BigDecimal pretBaza) {
        this.id = id;
        this.tren = tren;
        this.ruta = ruta;
        this.data = data;
        this.oraPlecare = oraPlecare;
        this.oraSosire = oraSosire;
        this.pretBaza = pretBaza;
    }

    public Cursa(Tren tren, Ruta ruta, LocalDate data, LocalTime oraPlecare, LocalTime oraSosire, BigDecimal pretBaza) {
        this.tren = tren;
        this.ruta = ruta;
        this.data = data;
        this.oraPlecare = oraPlecare;
        this.oraSosire = oraSosire;
        this.pretBaza = pretBaza;
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

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOraPlecare() {
        return oraPlecare;
    }

    public void setOraPlecare(LocalTime oraPlecare) {
        this.oraPlecare = oraPlecare;
    }

    public LocalTime getOraSosire() {
        return oraSosire;
    }

    public void setOraSosire(LocalTime oraSosire) {
        this.oraSosire = oraSosire;
    }

    public BigDecimal getPretBaza() {
        return pretBaza;
    }

    public void setPretBaza(BigDecimal pretBaza) {
        this.pretBaza = pretBaza;
    }

    @Override
    public String toString() {
        return tren.getNumar() + " " + ruta.getStatiePlecare().getNume() + " -> "
                + ruta.getStatieSosire().getNume() + " (" + data + ", " + oraPlecare + ")";
    }
}