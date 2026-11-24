package agentie.cfr.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bilet {

    private int id;
    private Cursa cursa;
    private Loc loc;
    private Client client;
    private Utilizator utilizator;
    private BigDecimal pret;
    private LocalDateTime dataEmitere;
    private String status;

    public Bilet() {
    }

    public Bilet(int id, Cursa cursa, Loc loc, Client client, Utilizator utilizator,
            BigDecimal pret, LocalDateTime dataEmitere, String status) {
        this.id = id;
        this.cursa = cursa;
        this.loc = loc;
        this.client = client;
        this.utilizator = utilizator;
        this.pret = pret;
        this.dataEmitere = dataEmitere;
        this.status = status;
    }

    public Bilet(Cursa cursa, Loc loc, Client client, Utilizator utilizator, BigDecimal pret) {
        this.cursa = cursa;
        this.loc = loc;
        this.client = client;
        this.utilizator = utilizator;
        this.pret = pret;
        this.status = "activ";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cursa getCursa() {
        return cursa;
    }

    public void setCursa(Cursa cursa) {
        this.cursa = cursa;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public BigDecimal getPret() {
        return pret;
    }

    public void setPret(BigDecimal pret) {
        this.pret = pret;
    }

    public LocalDateTime getDataEmitere() {
        return dataEmitere;
    }

    public void setDataEmitere(LocalDateTime dataEmitere) {
        this.dataEmitere = dataEmitere;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bilet #" + id + " - " + client + " - " + cursa + " - " + status;
    }
}