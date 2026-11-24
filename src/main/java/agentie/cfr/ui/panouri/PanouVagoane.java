package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.db.dao.VagonDAO;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Vagon;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogVagon;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouVagoane extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final VagonDAO vagonDAO;
    private final TrenDAO trenDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JComboBox<Tren> comboFiltruTren;
    private List<Vagon> vagoaneAfisate;

    public PanouVagoane(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.vagonDAO = new VagonDAO();
        this.trenDAO = new TrenDAO();

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

        List<Tren> trenuri = trenDAO.listaToate();
        List<Tren> optiuniFiltru = new ArrayList<>();
        optiuniFiltru.add(null);
        optiuniFiltru.addAll(trenuri);

        comboFiltruTren = new JComboBox<>(optiuniFiltru.toArray(new Tren[0]));
        comboFiltruTren.setRenderer((lista, valoare, index, selectat, focalizat) -> {
            javax.swing.JLabel eticheta = new javax.swing.JLabel(
                    valoare == null ? "Toate trenurile" : valoare.toString());
            if (selectat) {
                eticheta.setOpaque(true);
                eticheta.setBackground(lista.getSelectionBackground());
            }
            return eticheta;
        });
        comboFiltruTren.addActionListener(e -> filtreazaDupaTren());
        barSus.add(comboFiltruTren);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Tren", "Numar vagon", "Clasa", "Nr. locuri", "Multiplicator"};
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
        vagoaneAfisate = vagonDAO.listaToate();
        populeazaTabel(vagoaneAfisate);
    }

    private void filtreazaDupaTren() {
        Tren trenSelectat = (Tren) comboFiltruTren.getSelectedItem();
        if (trenSelectat == null) {
            incarcaToate();
            return;
        }
        vagoaneAfisate = vagonDAO.filtreazaDupaTren(trenSelectat.getId());
        populeazaTabel(vagoaneAfisate);
    }

    private void populeazaTabel(List<Vagon> vagoane) {
        modelTabel.setRowCount(0);
        for (Vagon v : vagoane) {
            modelTabel.addRow(new Object[]{
                v.getId(), v.getTren().getNumar(), v.getNumarVagon(),
                v.getClasa(), v.getNrLocuri(), v.getMultiplicatorPret()
            });
        }
    }

    private Vagon getVagonSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un vagon din tabel.",
                    "Niciun vagon selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return vagoaneAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogVagon dialog = new DialogVagon(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Vagon vagon = getVagonSelectat();
        if (vagon == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogVagon dialog = new DialogVagon(parinte, vagon);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Vagon vagon = getVagonSelectat();
        if (vagon == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti vagonul " + vagon.getNumarVagon() + " din trenul "
                + vagon.getTren().getNumar() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = vagonDAO.sterge(vagon.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Vagonul nu a putut fi sters. Este posibil sa aiba locuri asociate.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToate();
    }
}