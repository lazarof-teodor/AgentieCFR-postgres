package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.LocDAO;
import agentie.cfr.db.dao.VagonDAO;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Vagon;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogLoc;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouLocuri extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final LocDAO locDAO;
    private final VagonDAO vagonDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JComboBox<Vagon> comboFiltruVagon;
    private List<Loc> locuriAfisate;

    public PanouLocuri(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.locDAO = new LocDAO();
        this.vagonDAO = new VagonDAO();

        construiesteInterfata();
        incarcaToate();
    }

    private void construiesteInterfata() {
        setLayout(new BorderLayout());

        JPanel barSus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barSus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton butonInapoi = new JButton("Inapoi la meniu");
        butonInapoi.addActionListener(e -> fereastraPrincipala.arataMeniu());
        barSus.add(butonInapoi);

        List<Vagon> vagoane = vagonDAO.listaToate();
        List<Vagon> optiuniFiltru = new ArrayList<>();
        optiuniFiltru.add(null);
        optiuniFiltru.addAll(vagoane);

        comboFiltruVagon = new JComboBox<>(optiuniFiltru.toArray(new Vagon[0]));
        comboFiltruVagon.setRenderer((lista, valoare, index, selectat, focalizat) -> {
            JLabel eticheta = new JLabel(valoare == null ? "Toate vagoanele" : valoare.toString());
            if (selectat) {
                eticheta.setOpaque(true);
                eticheta.setBackground(lista.getSelectionBackground());
            }
            return eticheta;
        });
        comboFiltruVagon.addActionListener(e -> filtreazaDupaVagon());
        barSus.add(comboFiltruVagon);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Tren", "Vagon", "Clasa", "Numar loc"};
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

    private void incarcaToate() {
        locuriAfisate = locDAO.listaToate();
        populeazaTabel(locuriAfisate);
    }

    private void filtreazaDupaVagon() {
        Vagon vagonSelectat = (Vagon) comboFiltruVagon.getSelectedItem();
        if (vagonSelectat == null) {
            incarcaToate();
            return;
        }
        locuriAfisate = locDAO.filtreazaDupaVagon(vagonSelectat.getId());
        populeazaTabel(locuriAfisate);
    }

    private void populeazaTabel(List<Loc> locuri) {
        modelTabel.setRowCount(0);
        for (Loc l : locuri) {
            modelTabel.addRow(new Object[]{
                l.getId(),
                l.getVagon().getTren().getNumar(),
                l.getVagon().getNumarVagon(),
                l.getVagon().getClasa(),
                l.getNumarLoc()
            });
        }
    }

    private Loc getLocSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un loc din tabel.",
                    "Niciun loc selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return locuriAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogLoc dialog = new DialogLoc(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Loc loc = getLocSelectat();
        if (loc == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogLoc dialog = new DialogLoc(parinte, loc);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Loc loc = getLocSelectat();
        if (loc == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti locul " + loc.getNumarLoc() + " din vagonul "
                + loc.getVagon().getNumarVagon() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = locDAO.sterge(loc.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Locul nu a putut fi sters. Este posibil sa aiba bilete emise.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToate();
    }
}