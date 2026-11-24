package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.ClientDAO;
import agentie.cfr.db.dao.CursaDAO;
import agentie.cfr.db.dao.LocDAO;
import agentie.cfr.model.Client;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Utilizator;
import agentie.cfr.service.BiletService;
import agentie.cfr.service.ValidareException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DialogEmitereBilet extends JDialog {

    private final CursaDAO cursaDAO;
    private final LocDAO locDAO;
    private final ClientDAO clientDAO;
    private final BiletService biletService;
    private final Utilizator utilizatorLogat;

    private boolean salvat;

    private JComboBox<Cursa> comboCursa;
    private JComboBox<Loc> comboLoc;
    private JComboBox<Client> comboClient;
    private JLabel etichetaPret;

    public DialogEmitereBilet(JFrame parinte, Utilizator utilizatorLogat) {
        super(parinte, true);
        this.cursaDAO = new CursaDAO();
        this.locDAO = new LocDAO();
        this.clientDAO = new ClientDAO();
        this.biletService = new BiletService();
        this.utilizatorLogat = utilizatorLogat;
        this.salvat = false;

        setTitle("Emitere bilet");
        setSize(480, 320);
        setLocationRelativeTo(parinte);

        construiesteInterfata();
        actualizeazaLocuriLibere();
        actualizeazaPret();
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Cursa> curse = cursaDAO.listaToate();
        List<Client> clienti = clientDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Cursa:"), gbc);
        comboCursa = new JComboBox<>(curse.toArray(new Cursa[0]));
        comboCursa.addActionListener(e -> {
            actualizeazaLocuriLibere();
            actualizeazaPret();
        });
        gbc.gridx = 1;
        panou.add(comboCursa, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Loc liber:"), gbc);
        comboLoc = new JComboBox<>();
        comboLoc.addActionListener(e -> actualizeazaPret());
        gbc.gridx = 1;
        panou.add(comboLoc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Client:"), gbc);
        comboClient = new JComboBox<>(clienti.toArray(new Client[0]));
        comboClient.addActionListener(e -> actualizeazaPret());
        gbc.gridx = 1;
        panou.add(comboClient, gbc);

        JButton butonClientNou = new JButton("Client nou...");
        butonClientNou.addActionListener(e -> adaugaClientNou());
        gbc.gridx = 1;
        gbc.gridy = 3;
        panou.add(butonClientNou, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panou.add(new JLabel("Pret calculat:"), gbc);
        etichetaPret = new JLabel("-");
        gbc.gridx = 1;
        panou.add(etichetaPret, gbc);

        JButton butonEmite = new JButton("Emite bilet");
        butonEmite.addActionListener(e -> emiteBilet());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panou.add(butonEmite, gbc);

        add(panou);
    }

    private void actualizeazaLocuriLibere() {
        comboLoc.removeAllItems();
        Cursa cursaSelectata = (Cursa) comboCursa.getSelectedItem();
        if (cursaSelectata == null) {
            return;
        }
        List<Loc> locuriLibere = locDAO.listaLocuriLibere(cursaSelectata.getId());
        for (Loc loc : locuriLibere) {
            comboLoc.addItem(loc);
        }
        if (locuriLibere.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nu mai exista locuri libere pentru cursa selectata.",
                    "Locuri indisponibile",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizeazaPret() {
        Cursa cursaSelectata = (Cursa) comboCursa.getSelectedItem();
        Loc locSelectat = (Loc) comboLoc.getSelectedItem();
        Client clientSelectat = (Client) comboClient.getSelectedItem();

        if (cursaSelectata == null || locSelectat == null) {
            etichetaPret.setText("-");
            return;
        }

        BigDecimal pretDupaVagon = cursaSelectata.getPretBaza()
                .multiply(locSelectat.getVagon().getMultiplicatorPret());

        if (clientSelectat == null) {
            etichetaPret.setText(pretDupaVagon.toString() + " lei");
            return;
        }

        BigDecimal procentReducere = BiletService.procentReducere(clientSelectat.getCategorie());
        BigDecimal factorReducere = BigDecimal.ONE.subtract(procentReducere.divide(new BigDecimal("100")));
        BigDecimal pretFinal = pretDupaVagon.multiply(factorReducere).setScale(2, java.math.RoundingMode.HALF_UP);

        String text = pretFinal.toString() + " lei";
        if (procentReducere.signum() > 0) {
            text += " (reducere " + procentReducere + "%, categorie " + clientSelectat.getCategorie() + ")";
        }
        etichetaPret.setText(text);
    }

    private void adaugaClientNou() {
        JFrame parinte = (JFrame) getParent();
        DialogClient dialog = new DialogClient(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            List<Client> clienti = clientDAO.listaToate();
            comboClient.removeAllItems();
            for (Client c : clienti) {
                comboClient.addItem(c);
            }
            comboClient.setSelectedIndex(comboClient.getItemCount() - 1);
        }
    }

    private void emiteBilet() {
        Cursa cursaSelectata = (Cursa) comboCursa.getSelectedItem();
        Loc locSelectat = (Loc) comboLoc.getSelectedItem();
        Client clientSelectat = (Client) comboClient.getSelectedItem();

        if (cursaSelectata == null || locSelectat == null || clientSelectat == null) {
            JOptionPane.showMessageDialog(this,
                    "Selectati cursa, locul si clientul inainte de a emite biletul.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            biletService.emiteBilet(cursaSelectata, locSelectat, clientSelectat, utilizatorLogat);
        } catch (ValidareException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Eroare la emitere",
                    JOptionPane.ERROR_MESSAGE);
            actualizeazaLocuriLibere();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Biletul a fost emis cu succes.",
                "Succes",
                JOptionPane.INFORMATION_MESSAGE);

        salvat = true;
        dispose();
    }

    public boolean isSalvat() {
        return salvat;
    }
}