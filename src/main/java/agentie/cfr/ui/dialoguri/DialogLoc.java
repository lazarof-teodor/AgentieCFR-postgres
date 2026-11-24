package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.LocDAO;
import agentie.cfr.db.dao.VagonDAO;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Vagon;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogLoc extends JDialog {

    private final LocDAO locDAO;
    private Loc loc;
    private boolean salvat;

    private JComboBox<Vagon> comboVagon;
    private JTextField campNumarLoc;

    public DialogLoc(JFrame parinte, Loc loc) {
        super(parinte, true);
        this.locDAO = new LocDAO();
        this.loc = loc;
        this.salvat = false;

        setTitle(loc == null ? "Adaugare loc" : "Editare loc");
        setSize(380, 180);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (loc != null) {
            comboVagon.setSelectedItem(loc.getVagon());
            campNumarLoc.setText(String.valueOf(loc.getNumarLoc()));
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        VagonDAO vagonDAO = new VagonDAO();
        List<Vagon> vagoane = vagonDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Vagon:"), gbc);
        comboVagon = new JComboBox<>(vagoane.toArray(new Vagon[0]));
        gbc.gridx = 1;
        panou.add(comboVagon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Numar loc:"), gbc);
        campNumarLoc = new JTextField(10);
        gbc.gridx = 1;
        panou.add(campNumarLoc, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        Vagon vagonSelectat = (Vagon) comboVagon.getSelectedItem();

        if (vagonSelectat == null) {
            JOptionPane.showMessageDialog(this,
                    "Trebuie sa existe cel putin un vagon creat.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numarLoc;
        try {
            numarLoc = Integer.parseInt(campNumarLoc.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Numarul locului trebuie sa fie un numar intreg valid.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (loc == null) {
            Loc locNou = new Loc(vagonSelectat, numarLoc);
            int idGenerat = locDAO.adauga(locNou);
            reusit = idGenerat != -1;
        } else {
            loc.setVagon(vagonSelectat);
            loc.setNumarLoc(numarLoc);
            reusit = locDAO.modifica(loc);
        }

        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Salvarea a esuat. Verificati daca numarul de loc exista deja pe acest vagon.",
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