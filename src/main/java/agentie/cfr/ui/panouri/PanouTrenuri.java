package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.model.Tren;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogTren;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouTrenuri extends JPanel {

    private static final String TOATE_TIPURILE = "Toate tipurile";
    private static final String[] TIPURI_FILTRU = {TOATE_TIPURILE, "InterCity", "InterRegio", "Regio"};

    private final FereastraPrincipala fereastraPrincipala;
    private final TrenDAO trenDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private JComboBox<String> comboFiltruTip;
    private List<Tren> trenuriAfisate;

    public PanouTrenuri(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
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

        campCautare = new JTextField(15);
        barSus.add(campCautare);

        JButton butonCauta = new JButton("Cauta dupa numar");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        comboFiltruTip = new JComboBox<>(TIPURI_FILTRU);
        comboFiltruTip.addActionListener(e -> filtreazaDupaTip());
        barSus.add(comboFiltruTip);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Numar", "Tip"};
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
        trenuriAfisate = trenDAO.listaToate();
        populeazaTabel(trenuriAfisate);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToate();
            return;
        }
        trenuriAfisate = trenDAO.cautaDupaNumar(text);
        populeazaTabel(trenuriAfisate);
    }

    private void filtreazaDupaTip() {
        String tipSelectat = (String) comboFiltruTip.getSelectedItem();
        if (TOATE_TIPURILE.equals(tipSelectat)) {
            incarcaToate();
            return;
        }
        trenuriAfisate = trenDAO.filtreazaDupaTip(tipSelectat);
        populeazaTabel(trenuriAfisate);
    }

    private void populeazaTabel(List<Tren> trenuri) {
        modelTabel.setRowCount(0);
        for (Tren t : trenuri) {
            modelTabel.addRow(new Object[]{t.getId(), t.getNumar(), t.getTip()});
        }
    }

    private Tren getTrenSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un tren din tabel.",
                    "Niciun tren selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return trenuriAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogTren dialog = new DialogTren(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Tren tren = getTrenSelectat();
        if (tren == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogTren dialog = new DialogTren(parinte, tren);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Tren tren = getTrenSelectat();
        if (tren == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti trenul " + tren.getNumar() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = trenDAO.sterge(tren.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Trenul nu a putut fi sters. Este posibil sa fie folosit intr-o cursa sau sa aiba vagoane asociate.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToate();
    }
}