package agentie.cfr.service;

import agentie.cfr.db.dao.CursaDAO;
import agentie.cfr.model.Cursa;
import java.time.LocalDate;

public class CursaService {

    private final CursaDAO cursaDAO;

    public CursaService() {
        this.cursaDAO = new CursaDAO();
    }

    public int adaugaCursa(Cursa cursa) throws ValidareException {
        valideazaCursa(cursa);

        int idGenerat = cursaDAO.adauga(cursa);
        if (idGenerat == -1) {
            throw new ValidareException("Cursa nu a putut fi salvata in baza de date.");
        }
        return idGenerat;
    }

    public void modificaCursa(Cursa cursa) throws ValidareException {
        valideazaCursa(cursa);

        boolean reusit = cursaDAO.modifica(cursa);
        if (!reusit) {
            throw new ValidareException("Cursa nu a putut fi modificata.");
        }
    }

    public void stergeCursa(int id) throws ValidareException {
        boolean reusit = cursaDAO.sterge(id);
        if (!reusit) {
            throw new ValidareException(
                    "Cursa nu a putut fi stearsa. Este posibil sa aiba deja bilete emise.");
        }
    }

    private void valideazaCursa(Cursa cursa) throws ValidareException {
        if (cursa.getTren() == null || cursa.getRuta() == null) {
            throw new ValidareException("Cursa trebuie sa aiba tren si ruta selectate.");
        }

        if (cursa.getData() == null) {
            throw new ValidareException("Data cursei este obligatorie.");
        }

        if (cursa.getData().isBefore(LocalDate.now())) {
            throw new ValidareException("Data cursei nu poate fi in trecut.");
        }

        if (cursa.getOraPlecare() == null || cursa.getOraSosire() == null) {
            throw new ValidareException("Ora de plecare si ora de sosire sunt obligatorii.");
        }

        if (!cursa.getOraSosire().isAfter(cursa.getOraPlecare())) {
            throw new ValidareException("Ora de sosire trebuie sa fie dupa ora de plecare.");
        }

        if (cursa.getPretBaza() == null || cursa.getPretBaza().signum() <= 0) {
            throw new ValidareException("Pretul de baza trebuie sa fie mai mare decat zero.");
        }
    }
}