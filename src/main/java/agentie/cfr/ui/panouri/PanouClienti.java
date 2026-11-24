package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.ClientDAO;
import agentie.cfr.model.Client;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogClient;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouClienti extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final ClientDAO clientDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private List<Client> clientiAfisati;

    public PanouClienti(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.clientDAO = new ClientDAO();

        construiesteInterfata();
        incarcaToti();
    }

    private void construiesteInterfata() {
        setLayout(new BorderLayout());

        JPanel barSus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barSus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton butonInapoi = new JButton("Inapoi la meniu");
        butonInapoi.addActionListener(e -> fereastraPrincipala.arataMeniu());
        barSus.add(butonInapoi);

        campCautare = new JTextField(20);
        barSus.add(campCautare);

        JButton butonCauta = new JButton("Cauta");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        JButton butonAfiseazaToti = new JButton("Afiseaza toti");
        butonAfiseazaToti.addActionListener(e -> incarcaToti());
        barSus.add(butonAfiseazaToti);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Nume", "Prenume", "Email", "Telefon", "Categorie"};
        modelTabel = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int rand, int coloana) {
                return false;
            }
        };
        tabel = new JTable(modelTabel);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel barJos = new JPanel(new FlowLayout(FlowLayout.CENTER));
        barJos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton butonAdauga = new JButton("Adauga");
        butonAdauga.addActionListener(e -> adauga());
        barJos.add(butonAdauga);

        JButton butonModifica = new JButton("Modifica");
        butonModifica.addActionListener(e -> modifica());
        barJos.add(butonModifica);

        JButton butonSterge = new JButton("Sterge");
        butonSterge.addActionListener(e -> sterge());
        barJos.add(butonSterge);

        add(barJos, BorderLayout.SOUTH);
    }

    private void incarcaToti() {
        clientiAfisati = clientDAO.listaToate();
        populeazaTabel(clientiAfisati);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToti();
            return;
        }
        clientiAfisati = clientDAO.cautaDupaNume(text);
        populeazaTabel(clientiAfisati);
    }

    private void populeazaTabel(List<Client> clienti) {
        modelTabel.setRowCount(0);
        for (Client c : clienti) {
            modelTabel.addRow(new Object[]{
                c.getId(), c.getNume(), c.getPrenume(), c.getEmail(), c.getTelefon(), c.getCategorie()
            });
        }
    }

    private Client getClientSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un client din tabel.",
                    "Niciun client selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return clientiAfisati.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogClient dialog = new DialogClient(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToti();
        }
    }

    private void modifica() {
        Client client = getClientSelectat();
        if (client == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogClient dialog = new DialogClient(parinte, client);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToti();
        }
    }

    private void sterge() {
        Client client = getClientSelectat();
        if (client == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti clientul " + client.getNume() + " " + client.getPrenume() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = clientDAO.sterge(client.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Clientul nu a putut fi sters. Este posibil sa aiba bilete emise.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToti();
    }
}