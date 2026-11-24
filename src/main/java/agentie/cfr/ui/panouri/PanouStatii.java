package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.StatieDAO;
import agentie.cfr.model.Statie;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogStatie;
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

public class PanouStatii extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final StatieDAO statieDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private List<Statie> statiiAfisate;

    public PanouStatii(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.statieDAO = new StatieDAO();

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

        campCautare = new JTextField(20);
        barSus.add(campCautare);

        JButton butonCauta = new JButton("Cauta dupa oras");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Nume", "Oras"};
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
        statiiAfisate = statieDAO.listaToate();
        populeazaTabel(statiiAfisate);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToate();
            return;
        }
        statiiAfisate = statieDAO.cautaDupaOras(text);
        populeazaTabel(statiiAfisate);
    }

    private void populeazaTabel(List<Statie> statii) {
        modelTabel.setRowCount(0);
        for (Statie s : statii) {
            modelTabel.addRow(new Object[]{s.getId(), s.getNume(), s.getOras()});
        }
    }

    private Statie getStatieSelectata() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati o statie din tabel.",
                    "Nicio statie selectata",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return statiiAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogStatie dialog = new DialogStatie(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Statie statie = getStatieSelectata();
        if (statie == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogStatie dialog = new DialogStatie(parinte, statie);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Statie statie = getStatieSelectata();
        if (statie == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti statia " + statie.getNume() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = statieDAO.sterge(statie.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Statia nu a putut fi stearsa. Este posibil sa fie folosita intr-o ruta.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToate();
    }
}