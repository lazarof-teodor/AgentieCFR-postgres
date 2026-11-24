package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.BiletDAO;
import agentie.cfr.model.Bilet;
import agentie.cfr.service.BiletService;
import agentie.cfr.service.ValidareException;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.ui.dialoguri.DialogEmitereBilet;
import agentie.cfr.util.ExportPDF;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanouBilete extends JPanel {

    private static final String TOATE_STATUSURILE = "Toate";

    private final FereastraPrincipala fereastraPrincipala;
    private final BiletDAO biletDAO;
    private final BiletService biletService;

    private DefaultTableModel modelTabel;
    private JTable tabel;
    private JTextField campCautareClient;
    private JTextField campDataStart;
    private JTextField campDataStop;
    private JComboBox<String> comboStatus;
    private List<Bilet> biletelAfisate;

    public PanouBilete(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.biletDAO = new BiletDAO();
        this.biletService = new BiletService();

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

        campCautareClient = new JTextField(12);
        barSus.add(campCautareClient);

        JButton butonCauta = new JButton("Cauta dupa client");
        butonCauta.addActionListener(e -> cauta());
        barSus.add(butonCauta);

        comboStatus = new JComboBox<>(new String[]{TOATE_STATUSURILE, "activ", "anulat"});
        comboStatus.addActionListener(e -> filtreazaDupaStatus());
        barSus.add(comboStatus);

        barSus.add(new JLabel("De la:"));
        campDataStart = new JTextField(9);
        barSus.add(campDataStart);

        barSus.add(new JLabel("Pana la:"));
        campDataStop = new JTextField(9);
        barSus.add(campDataStop);

        JButton butonFiltreaza = new JButton("Filtreaza perioada");
        butonFiltreaza.addActionListener(e -> filtreazaDupaPerioada());
        barSus.add(butonFiltreaza);

        JButton butonAfiseazaToate = new JButton("Afiseaza toate");
        butonAfiseazaToate.addActionListener(e -> incarcaToate());
        barSus.add(butonAfiseazaToate);

        add(barSus, BorderLayout.NORTH);

        String[] coloane = {"ID", "Client", "Cursa", "Data cursa", "Loc", "Pret", "Data emitere", "Status"};
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

        JButton butonEmite = new JButton("Emite bilet nou");
        butonEmite.addActionListener(e -> emite());
        barJos.add(butonEmite);

        JButton butonAnuleaza = new JButton("Anuleaza bilet");
        butonAnuleaza.addActionListener(e -> anuleaza());
        barJos.add(butonAnuleaza);

        JButton butonExportPDF = new JButton("Export PDF");
        butonExportPDF.addActionListener(e -> exportaPDF());
        barJos.add(butonExportPDF);

        add(barJos, BorderLayout.SOUTH);
    }

    private void incarcaToate() {
        biletelAfisate = biletDAO.listaToate();
        populeazaTabel(biletelAfisate);
    }

    private void cauta() {
        String text = campCautareClient.getText().trim();
        if (text.isEmpty()) {
            incarcaToate();
            return;
        }
        biletelAfisate = biletDAO.cautaDupaClient(text);
        populeazaTabel(biletelAfisate);
    }

    private void filtreazaDupaStatus() {
        String statusSelectat = (String) comboStatus.getSelectedItem();
        if (TOATE_STATUSURILE.equals(statusSelectat)) {
            incarcaToate();
            return;
        }
        biletelAfisate = biletDAO.filtreazaDupaStatus(statusSelectat);
        populeazaTabel(biletelAfisate);
    }

    private void filtreazaDupaPerioada() {
        try {
            LocalDate start = LocalDate.parse(campDataStart.getText().trim());
            LocalDate stop = LocalDate.parse(campDataStop.getText().trim());
            LocalDateTime startDateTime = LocalDateTime.of(start, LocalTime.MIN);
            LocalDateTime stopDateTime = LocalDateTime.of(stop, LocalTime.MAX);
            biletelAfisate = biletDAO.filtreazaDupaPerioada(startDateTime, stopDateTime);
            populeazaTabel(biletelAfisate);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Introduceti ambele date in formatul yyyy-MM-dd.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populeazaTabel(List<Bilet> bilete) {
        modelTabel.setRowCount(0);
        for (Bilet b : bilete) {
            modelTabel.addRow(new Object[]{
                b.getId(),
                b.getClient().getNume() + " " + b.getClient().getPrenume(),
                b.getCursa().getRuta().getStatiePlecare().getNume() + " -> "
                        + b.getCursa().getRuta().getStatieSosire().getNume(),
                b.getCursa().getData(),
                "Vagon " + b.getLoc().getVagon().getNumarVagon() + ", loc " + b.getLoc().getNumarLoc(),
                b.getPret(),
                b.getDataEmitere(),
                b.getStatus()
            });
        }
    }

    private Bilet getBiletSelectat() {
        int randSelectat = tabel.getSelectedRow();
        if (randSelectat == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selectati un bilet din tabel.",
                    "Niciun bilet selectat",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return biletelAfisate.get(randSelectat);
    }

    private void emite() {
        JFrame parinte = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogEmitereBilet dialog = new DialogEmitereBilet(parinte, fereastraPrincipala.getUtilizatorLogat());
        dialog.setVisible(true);

        if (dialog.isSalvat()) {
            incarcaToate();
        }
    }

    private void anuleaza() {
        Bilet bilet = getBiletSelectat();
        if (bilet == null) {
            return;
        }

        int confirmare = JOptionPane.showConfirmDialog(this,
                "Anulati biletul #" + bilet.getId() + " pentru "
                + bilet.getClient().getNume() + " " + bilet.getClient().getPrenume() + "?",
                "Confirmare anulare",
                JOptionPane.YES_NO_OPTION);

        if (confirmare != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biletService.anuleazaBilet(bilet.getId());
            incarcaToate();
        } catch (ValidareException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportaPDF() {
        Bilet bilet = getBiletSelectat();
        if (bilet == null) {
            return;
        }

        JFileChooser selectorFisier = new JFileChooser();
        selectorFisier.setSelectedFile(new File("bilet_" + bilet.getId() + ".pdf"));

        int rezultat = selectorFisier.showSaveDialog(this);
        if (rezultat != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fisier = selectorFisier.getSelectedFile();
        if (!fisier.getName().toLowerCase().endsWith(".pdf")) {
            fisier = new File(fisier.getParentFile(), fisier.getName() + ".pdf");
        }

        try {
            ExportPDF.exportaBilet(bilet, fisier);
            JOptionPane.showMessageDialog(this,
                    "Biletul a fost exportat cu succes:\n" + fisier.getAbsolutePath(),
                    "Export reusit",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Exportul a esuat: " + e.getMessage(),
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}