package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.RolDAO;
import agentie.cfr.db.dao.UtilizatorDAO;
import agentie.cfr.model.Rol;
import agentie.cfr.model.Utilizator;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogUtilizator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouUtilizatori extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final UtilizatorDAO utilizatorDAO;
    private final RolDAO rolDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private JComboBox<Rol> comboFiltruRol;
    private List<Utilizator> utilizatoriAfisati;

    public PanouUtilizatori(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.utilizatorDAO = new UtilizatorDAO();
        this.rolDAO = new RolDAO();

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

        campCautare = new JTextField(15);
        barSus.add(campCautare);

        JButton butonCauta = new JButton("Cauta dupa username");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        List<Rol> roluri = rolDAO.listaToate();
        List<Rol> optiuniFiltru = new ArrayList<>();
        optiuniFiltru.add(null);
        optiuniFiltru.addAll(roluri);

        comboFiltruRol = new JComboBox<>(optiuniFiltru.toArray(new Rol[0]));
        comboFiltruRol.setRenderer((lista, valoare, index, selectat, focalizat) -> {
            JLabel eticheta = new JLabel(valoare == null ? "Toate rolurile" : valoare.toString());
            if (selectat) {
                eticheta.setOpaque(true);
                eticheta.setBackground(lista.getSelectionBackground());
            }
            return eticheta;
        });
        comboFiltruRol.addActionListener(e -> filtreazaDupaRol());
        barSus.add(comboFiltruRol);

        JButton butonAfiseazaToti = new JButton("Afiseaza toti");
        butonAfiseazaToti.addActionListener(e -> incarcaToti());
        barSus.add(butonAfiseazaToti);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Username", "Nume", "Prenume", "Rol", "Activ"};
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
        utilizatoriAfisati = utilizatorDAO.listaToate();
        populeazaTabel(utilizatoriAfisati);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToti();
            return;
        }
        utilizatoriAfisati = utilizatorDAO.cautaDupaUsername(text);
        populeazaTabel(utilizatoriAfisati);
    }

    private void filtreazaDupaRol() {
        Rol rolSelectat = (Rol) comboFiltruRol.getSelectedItem();
        if (rolSelectat == null) {
            incarcaToti();
            return;
        }
        utilizatoriAfisati = utilizatorDAO.filtreazaDupaRol(rolSelectat.getId());
        populeazaTabel(utilizatoriAfisati);
    }

    private void populeazaTabel(List<Utilizator> utilizatori) {
        modelTabel.setRowCount(0);
        for (Utilizator u : utilizatori) {
            modelTabel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getNume(), u.getPrenume(),
                u.getRol().getNume(), u.isActiv() ? "Da" : "Nu"
            });
        }
    }

    private Utilizator getUtilizatorSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un utilizator din tabel.",
                    "Niciun utilizator selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return utilizatoriAfisati.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogUtilizator dialog = new DialogUtilizator(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToti();
        }
    }

    private void modifica() {
        Utilizator utilizator = getUtilizatorSelectat();
        if (utilizator == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogUtilizator dialog = new DialogUtilizator(parinte, utilizator);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToti();
        }
    }

    private void sterge() {
        Utilizator utilizator = getUtilizatorSelectat();
        if (utilizator == null) {
            return;
        }

        if (utilizator.getId() == fereastraPrincipala.getUtilizatorLogat().getId()) {
            JOptionPane.showMessageDialog(this,
                    "Nu va puteti sterge propriul cont in timp ce sunteti autentificat.",
                    "Operatie interzisa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti utilizatorul " + utilizator.getUsername() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = utilizatorDAO.sterge(utilizator.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Utilizatorul nu a putut fi sters. Este posibil sa aiba bilete emise pe numele sau.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToti();
    }
}