package agentie.cfr.test;

import agentie.cfr.db.dao.BiletDAO;
import agentie.cfr.db.dao.ClientDAO;
import agentie.cfr.db.dao.CursaDAO;
import agentie.cfr.db.dao.LocDAO;
import agentie.cfr.db.dao.RolDAO;
import agentie.cfr.db.dao.RutaDAO;
import agentie.cfr.db.dao.StatieDAO;
import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.db.dao.UtilizatorDAO;
import agentie.cfr.db.dao.VagonDAO;
import agentie.cfr.model.Bilet;
import agentie.cfr.model.Client;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Rol;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Statie;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Utilizator;
import agentie.cfr.model.Vagon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import agentie.cfr.service.AutentificareService;


public class TestJDBC {

    public static void main(String[] args) {
        testFluxComplet();
        System.out.println(AutentificareService.hashParola("admin123"));
        System.out.println(AutentificareService.hashParola("casier123"));
    }

    private static void testFluxComplet() {

        String sufixUnic = String.valueOf(System.currentTimeMillis() % 100000);

        StatieDAO statieDAO = new StatieDAO();
        TrenDAO trenDAO = new TrenDAO();
        RutaDAO rutaDAO = new RutaDAO();
        CursaDAO cursaDAO = new CursaDAO();
        VagonDAO vagonDAO = new VagonDAO();
        LocDAO locDAO = new LocDAO();
        ClientDAO clientDAO = new ClientDAO();
        UtilizatorDAO utilizatorDAO = new UtilizatorDAO();
        RolDAO rolDAO = new RolDAO();
        BiletDAO biletDAO = new BiletDAO();

        System.out.println("=== 1. Creare statii ===");
        int idPlecare = statieDAO.adauga(new Statie("Gara Test Nord", "Bucuresti"));
        int idSosire = statieDAO.adauga(new Statie("Gara Test Sud", "Craiova"));
        System.out.println("Statie plecare id = " + idPlecare);
        System.out.println("Statie sosire id = " + idSosire);

        if (idPlecare == -1 || idSosire == -1) {
            System.out.println("EROARE: nu s-au putut crea statiile, opresc testul.");
            return;
        }

        Statie statiePlecare = statieDAO.gasesteDupaId(idPlecare);
        Statie statieSosire = statieDAO.gasesteDupaId(idSosire);

        System.out.println("\n=== 2. Creare tren ===");
        int idTren = trenDAO.adauga(new Tren("TEST" + sufixUnic, "InterCity"));
        System.out.println("Tren id = " + idTren);
        Tren tren = trenDAO.gasesteDupaId(idTren);

        if (tren == null) {
            System.out.println("EROARE: trenul nu a fost creat, opresc testul.");
            return;
        }

        System.out.println("\n=== 3. Creare ruta ===");
        int idRuta = rutaDAO.adauga(new Ruta(statiePlecare, statieSosire, LocalTime.of(3, 30)));
        System.out.println("Ruta id = " + idRuta);
        Ruta ruta = rutaDAO.gasesteDupaId(idRuta);

        if (ruta == null) {
            System.out.println("EROARE: ruta nu a fost creata, opresc testul.");
            return;
        }

        System.out.println("\n=== 4. Creare cursa ===");
        int idCursa = cursaDAO.adauga(new Cursa(
                tren, ruta, LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(11, 30),
                new BigDecimal("50.00")
        ));
        System.out.println("Cursa id = " + idCursa);
        Cursa cursa = cursaDAO.gasesteDupaId(idCursa);

        if (cursa == null) {
            System.out.println("EROARE: cursa nu a fost creata, opresc testul.");
            return;
        }

        System.out.println("\n=== 5. Creare vagon ===");
        int idVagon = vagonDAO.adauga(new Vagon(
                tren, 1, "I", 40, new BigDecimal("1.50")
        ));
        System.out.println("Vagon id = " + idVagon);
        Vagon vagon = vagonDAO.gasesteDupaId(idVagon);

        if (vagon == null) {
            System.out.println("EROARE: vagonul nu a fost creat, opresc testul.");
            return;
        }

        System.out.println("\n=== 6. Creare loc ===");
        int idLoc = locDAO.adauga(new Loc(vagon, 12));
        System.out.println("Loc id = " + idLoc);
        Loc loc = locDAO.gasesteDupaId(idLoc);

        if (loc == null) {
            System.out.println("EROARE: locul nu a fost creat, opresc testul.");
            return;
        }

        System.out.println("\n=== 7. Creare client ===");
        int idClient = clientDAO.adauga(new Client(
                "Popescu", "Ion", "ion.popescu@test.ro", "0722111222", "normal"
        ));
        System.out.println("Client id = " + idClient);
        Client client = clientDAO.gasesteDupaId(idClient);

        System.out.println("\n=== 8. Creare utilizator (casier) ===");
        List<Rol> roluri = rolDAO.listaToate();
        Rol rolCasier = null;
        for (Rol r : roluri) {
            if (r.getNume().equalsIgnoreCase("Casier")) {
                rolCasier = r;
            }
        }
        if (rolCasier == null) {
            System.out.println("EROARE: rolul Casier nu exista in tabelul Rol, opresc testul.");
            return;
        }
        int idUtilizator = utilizatorDAO.adauga(new Utilizator(
                "casier_test_" + sufixUnic, "hash_test_temporar", "Ionescu", "Maria", rolCasier
        ));
        System.out.println("Utilizator id = " + idUtilizator);
        Utilizator utilizator = utilizatorDAO.gasesteDupaId(idUtilizator);

        if (utilizator == null) {
            System.out.println("EROARE: utilizatorul nu a fost creat, opresc testul.");
            return;
        }

        System.out.println("\n=== 9. Verificare loc liber inainte de emitere ===");
        List<Loc> locuriLibere = locDAO.listaLocuriLibere(idCursa);
        boolean locGasitLiber = false;
        for (Loc l : locuriLibere) {
            if (l.getId() == idLoc) {
                locGasitLiber = true;
            }
        }
        System.out.println("Locul e liber inainte de emitere: " + locGasitLiber);

        System.out.println("\n=== 10. Emitere bilet ===");
        BigDecimal pretCalculat = cursa.getPretBaza().multiply(vagon.getMultiplicatorPret());
        System.out.println("Pret calculat (pret_baza * multiplicator): " + pretCalculat);

        Bilet bilet = new Bilet(cursa, loc, client, utilizator, pretCalculat);
        int idBilet = biletDAO.emite(bilet);
        System.out.println("Bilet id = " + idBilet);

        if (idBilet == -1) {
            System.out.println("EROARE: biletul nu a putut fi emis, opresc testul.");
            return;
        }

        System.out.println("\n=== 11. Verificare loc liber dupa emitere ===");
        locuriLibere = locDAO.listaLocuriLibere(idCursa);
        locGasitLiber = false;
        for (Loc l : locuriLibere) {
            if (l.getId() == idLoc) {
                locGasitLiber = true;
            }
        }
        System.out.println("Locul e liber dupa emitere (ar trebui sa fie false): " + locGasitLiber);

        System.out.println("\n=== 12. Citire bilet emis ===");
        Bilet biletCitit = biletDAO.gasesteDupaId(idBilet);
        System.out.println("Bilet citit: " + biletCitit);
        System.out.println("Status: " + biletCitit.getStatus());

        System.out.println("\n=== 13. Anulare bilet ===");
        boolean anulat = biletDAO.anuleaza(idBilet);
        System.out.println("Anulare reusita: " + anulat);

        System.out.println("\n=== 14. Verificare loc liber dupa anulare ===");
        locuriLibere = locDAO.listaLocuriLibere(idCursa);
        locGasitLiber = false;
        for (Loc l : locuriLibere) {
            if (l.getId() == idLoc) {
                locGasitLiber = true;
            }
        }
        System.out.println("Locul e liber dupa anulare (ar trebui sa fie true): " + locGasitLiber);

        System.out.println("\n=== TEST FINALIZAT ===");
    }
}