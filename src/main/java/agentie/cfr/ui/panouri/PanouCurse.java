package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.CursaDAO;
import agentie.cfr.model.Cursa;
import agentie.cfr.service.CursaService;
import agentie.cfr.service.ValidareException;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogCursa;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

public class PanouCurse extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final CursaDAO cursaDAO;
    private final CursaService cursaService;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautare;
    private JTextField campDataStart;
    private JTextField campDataStop;
    private List<Cursa> curseAfisate;

    public PanouCurse(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.cursaDAO = new CursaDAO();
        this.cursaService = new CursaService();

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

        campCautare = new JTextField(14);
        barSus.add(campCautare);

        JButton butonCauta = new JButton("Cauta dupa statie");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        barSus.add(new javax.swing.JLabel("De la:"));
        campDataStart = new JTextField(10);
        barSus.add(campDataStart);

        barSus.add(new javax.swing.JLabel("Pana la:"));
        campDataStop = new JTextField(10);
        barSus.add(campDataStop);

        JButton butonFiltreaza = new JButton("Filtreaza perioada");
        butonFiltreaza.addActionListener(e -> filtreazaDupaPerioada());
        barSus.add(butonFiltreaza);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Tren", "Plecare", "Sosire", "Data", "Ora plecare", "Ora sosire", "Pret baza"};
        modelTabel = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int rand, int coloana) {
                return false;
            }
        };
        tabel = new JTable(modelTabel);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        boolean esteAdmin = "Admin".equalsIgnoreCase(
                fereastraPrincipala.getUtilizatorLogat().getRol().getNume());

        if (esteAdmin) {
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
    }

    private void incarcaToate() {
        curseAfisate = cursaDAO.listaToate();
        populeazaTabel(curseAfisate);
    }

    private void cauta() {
        String text = campCautare.getText().trim();
        if (text.isEmpty()) {
            incarcaToate();
            return;
        }
        curseAfisate = cursaDAO.cautaDupaStatie(text);
        populeazaTabel(curseAfisate);
    }

    private void filtreazaDupaPerioada() {
        try {
            LocalDate start = LocalDate.parse(campDataStart.getText().trim());
            LocalDate stop = LocalDate.parse(campDataStop.getText().trim());
            curseAfisate = cursaDAO.filtreazaDupaPerioada(start, stop);
            populeazaTabel(curseAfisate);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Introduceti ambele date in formatul yyyy-MM-dd.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populeazaTabel(List<Cursa> curse) {
        modelTabel.setRowCount(0);
        for (Cursa c : curse) {
            modelTabel.addRow(new Object[]{
                c.getId(),
                c.getTren().getNumar(),
                c.getRuta().getStatiePlecare().getNume(),
                c.getRuta().getStatieSosire().getNume(),
                c.getData(),
                c.getOraPlecare(),
                c.getOraSosire(),
                c.getPretBaza()
            });
        }
    }

    private Cursa getCursaSelectata() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati o cursa din tabel.",
                    "Nicio cursa selectata",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return curseAfisate.get(randSelectat);
    }

    private void adauga() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogCursa dialog = new DialogCursa(parinte, null);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void modifica() {
        Cursa cursa = getCursaSelectata();
        if (cursa == null) {
            return;
        }

        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogCursa dialog = new DialogCursa(parinte, cursa);
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void sterge() {
        Cursa cursa = getCursaSelectata();
        if (cursa == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Stergeti cursa din " + cursa.getData() + "?",
                "Confirmare stergere",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            cursaService.stergeCursa(cursa.getId());
            incarcaToate();
        } catch (ValidareException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}