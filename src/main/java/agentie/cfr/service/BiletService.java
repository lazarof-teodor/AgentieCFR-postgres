package agentie.cfr.service;

import agentie.cfr.db.dao.BiletDAO;
import agentie.cfr.db.dao.LocDAO;
import agentie.cfr.model.Bilet;
import agentie.cfr.model.Client;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Utilizator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BiletService {

    private final BiletDAO biletDAO;
    private final LocDAO locDAO;

    public BiletService() {
        this.biletDAO = new BiletDAO();
        this.locDAO = new LocDAO();
    }

    public int emiteBilet(Cursa cursa, Loc loc, Client client, Utilizator utilizator) throws ValidareException {
        if (cursa == null || loc == null || client == null || utilizator == null) {
            throw new ValidareException("Toate datele biletului (cursa, loc, client, utilizator) sunt obligatorii.");
        }

        if (cursa.getData().isBefore(LocalDate.now())) {
            throw new ValidareException("Nu se pot emite bilete pentru curse din trecut.");
        }

        if (!locEsteLiber(cursa.getId(), loc.getId())) {
            throw new ValidareException("Locul selectat a fost deja ocupat. Alegeti alt loc.");
        }

        BigDecimal pretDupaVagon = cursa.getPretBaza().multiply(loc.getVagon().getMultiplicatorPret());
        BigDecimal procentReducere = procentReducere(client.getCategorie());
        BigDecimal factorReducere = BigDecimal.ONE.subtract(procentReducere.divide(new BigDecimal("100")));
        BigDecimal pret = pretDupaVagon.multiply(factorReducere).setScale(2, java.math.RoundingMode.HALF_UP);

        Bilet bilet = new Bilet(cursa, loc, client, utilizator, pret);
        int idGenerat = biletDAO.emite(bilet);

        if (idGenerat == -1) {
            throw new ValidareException("Biletul nu a putut fi emis. Incercati din nou.");
        }

        return idGenerat;
    }

    public static BigDecimal procentReducere(String categorie) {
        if (categorie == null) {
            return BigDecimal.ZERO;
        }
        switch (categorie) {
            case "student":
                return new BigDecimal("50");
            case "elev":
                return new BigDecimal("50");
            case "pensionar":
                return new BigDecimal("25");
            default:
                return BigDecimal.ZERO;
        }
    }

    public void anuleazaBilet(int idBilet) throws ValidareException {
        Bilet bilet = biletDAO.gasesteDupaId(idBilet);

        if (bilet == null) {
            throw new ValidareException("Biletul nu a fost gasit.");
        }

        if ("anulat".equals(bilet.getStatus())) {
            throw new ValidareException("Biletul este deja anulat.");
        }

        boolean reusit = biletDAO.anuleaza(idBilet);
        if (!reusit) {
            throw new ValidareException("Anularea biletului a esuat.");
        }
    }

    private boolean locEsteLiber(int cursaId, int locId) {
        List<Loc> locuriLibere = locDAO.listaLocuriLibere(cursaId);
        for (Loc l : locuriLibere) {
            if (l.getId() == locId) {
                return true;
            }
        }
        return false;
    }
}