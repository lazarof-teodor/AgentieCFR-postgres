package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.db.dao.VagonDAO;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Vagon;
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
import javax.swing.JTextField;

public class DialogVagon extends JDialog {

    private static final String[] CLASE = {"I", "II"};

    private final VagonDAO vagonDAO;
    private final TrenDAO trenDAO;
    private Vagon vagon;
    private boolean salvat;

    private JComboBox<Tren> comboTren;
    private JTextField campNumarVagon;
    private JComboBox<String> comboClasa;
    private JTextField campNrLocuri;
    private JTextField campMultiplicator;

    public DialogVagon(JFrame parinte, Vagon vagon) {
        super(parinte, true);
        this.vagonDAO = new VagonDAO();
        this.trenDAO = new TrenDAO();
        this.vagon = vagon;
        this.salvat = false;

        setTitle(vagon == null ? "Adaugare vagon" : "Editare vagon");
        setSize(380, 280);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (vagon != null) {
            comboTren.setSelectedItem(vagon.getTren());
            campNumarVagon.setText(String.valueOf(vagon.getNumarVagon()));
            comboClasa.setSelectedItem(vagon.getClasa());
            campNrLocuri.setText(String.valueOf(vagon.getNrLocuri()));
            campMultiplicator.setText(vagon.getMultiplicatorPret().toString());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Tren> trenuri = trenDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Tren:"), gbc);
        comboTren = new JComboBox<>(trenuri.toArray(new Tren[0]));
        gbc.gridx = 1;
        panou.add(comboTren, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Numar vagon:"), gbc);
        campNumarVagon = new JTextField(10);
        gbc.gridx = 1;
        panou.add(campNumarVagon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Clasa:"), gbc);
        comboClasa = new JComboBox<>(CLASE);
        gbc.gridx = 1;
        panou.add(comboClasa, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panou.add(new JLabel("Numar locuri:"), gbc);
        campNrLocuri = new JTextField(10);
        gbc.gridx = 1;
        panou.add(campNrLocuri, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panou.add(new JLabel("Multiplicator pret:"), gbc);
        campMultiplicator = new JTextField(10);
        campMultiplicator.setText("1.00");
        gbc.gridx = 1;
        panou.add(campMultiplicator, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        Tren trenSelectat = (Tren) comboTren.getSelectedItem();
        String clasaSelectata = (String) comboClasa.getSelectedItem();

        if (trenSelectat == null) {
            JOptionPane.showMessageDialog(this,
                    "Trebuie sa existe cel putin un tren creat inainte de a adauga vagoane.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numarVagon;
        int nrLocuri;
        BigDecimal multiplicator;

        try {
            numarVagon = Integer.parseInt(campNumarVagon.getText().trim());
            nrLocuri = Integer.parseInt(campNrLocuri.getText().trim());
            multiplicator = new BigDecimal(campMultiplicator.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Numarul vagonului, numarul de locuri si multiplicatorul trebuie sa fie numere valide.",
                    "Date invalide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (vagon == null) {
            Vagon vagonNou = new Vagon(trenSelectat, numarVagon, clasaSelectata, nrLocuri, multiplicator);
            int idGenerat = vagonDAO.adauga(vagonNou);
            reusit = idGenerat != -1;
        } else {
            vagon.setTren(trenSelectat);
            vagon.setNumarVagon(numarVagon);
            vagon.setClasa(clasaSelectata);
            vagon.setNrLocuri(nrLocuri);
            vagon.setMultiplicatorPret(multiplicator);
            reusit = vagonDAO.modifica(vagon);
        }

        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Salvarea a esuat. Verificati daca numarul de vagon exista deja pe acest tren.",
                    "Eroare",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        salvat = true;
        dispose();
    }

    public boolean isSalvat() {
        return salvat;
    }
}