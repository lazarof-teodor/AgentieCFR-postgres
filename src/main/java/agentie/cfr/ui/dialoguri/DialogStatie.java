package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.StatieDAO;
import agentie.cfr.model.Statie;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogStatie extends JDialog {

    private final StatieDAO statieDAO;
    private Statie statie;
    private boolean salvat;

    private JTextField campNume;
    private JTextField campOras;

    public DialogStatie(JFrame parinte, Statie statie) {
        super(parinte, true);
        this.statieDAO = new StatieDAO();
        this.statie = statie;
        this.salvat = false;

        setTitle(statie == null ? "Adaugare statie" : "Editare statie");
        setSize(350, 200);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (statie != null) {
            campNume.setText(statie.getNume());
            campOras.setText(statie.getOras());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Nume:"), gbc);
        campNume = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campNume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Oras:"), gbc);
        campOras = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campOras, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        String nume = campNume.getText().trim();
        String oras = campOras.getText().trim();

        if (nume.isEmpty() || oras.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Numele statiei si orasul sunt obligatorii.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (statie == null) {
            Statie statieNoua = new Statie(nume, oras);
            int idGenerat = statieDAO.adauga(statieNoua);
            reusit = idGenerat != -1;
        } else {
            statie.setNume(nume);
            statie.setOras(oras);
            reusit = statieDAO.modifica(statie);
        }

        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Salvarea a esuat. Incercati din nou.",
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