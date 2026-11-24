package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.RutaDAO;
import agentie.cfr.model.Ruta;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogRuta;
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

public class PanouRute extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final RutaDAO rutaDAO;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private List<Ruta> ruteAfisate;

    public PanouRute(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.rutaDAO = new RutaDAO();

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

        JButton butonCauta = new JButton("Cauta dupa statie");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Plecare", "Sosire", "Durata estimata"};
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
        ruteAfisate = rutaDAO.listaToate();
        populeazaTabel(ruteAfisate);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToate();
            return;
        }
        ruteAfisate = rutaDAO.cautaDupaStatie(text);
        populeazaTabel(ruteAfisate);
    }

    private void populeazaTabel(List<Ruta> rute) {
        modelTabel.setRowCount(0);
        for (Ruta r : rute) {
            modelTabel.addRow(new Object[]{
                r.getId(),
                r.getStatiePlecare().getNume(),
                r.getStatieSosire().getNume(),
                r.getDurataEstimata()
            });
        }
    }

    private Ruta getRutaSelectata() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati o ruta din tabel.",
                    "Nicio ruta selectata",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return ruteAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogRuta dialog = new DialogRuta(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Ruta ruta = getRutaSelectata();
        if (ruta == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogRuta dialog = new DialogRuta(parinte, ruta);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Ruta ruta = getRutaSelectata();
        if (ruta == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti ruta " + ruta.getStatiePlecare().getNume() + " -> "
                + ruta.getStatieSosire().getNume() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        boolean reusit = rutaDAO.sterge(ruta.getId());
        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Ruta nu a putut fi stearsa. Este posibil sa fie folosita intr-o cursa.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        incarcaToate();
    }
}