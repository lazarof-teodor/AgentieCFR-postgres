package agentie.cfr.service;

import agentie.cfr.db.dao.UtilizatorDAO;
import agentie.cfr.model.Utilizator;
import org.mindrot.jbcrypt.BCrypt;

public class AutentificareService {

    private final UtilizatorDAO utilizatorDAO;

    public AutentificareService() {
        this.utilizatorDAO = new UtilizatorDAO();
    }

    public Utilizator login(String username, String parola) {
        Utilizator utilizator = utilizatorDAO.gasesteDupaUsername(username);

        if (utilizator == null) {
            return null;
        }

        if (!utilizator.isActiv()) {
            return null;
        }

        boolean parolaCorecta = BCrypt.checkpw(parola, utilizator.getParolaHash());
        if (!parolaCorecta) {
            return null;
        }

        return utilizator;
    }

    public static String hashParola(String parolaClara) {
        return BCrypt.hashpw(parolaClara, BCrypt.gensalt());
    }
}