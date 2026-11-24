package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.model.Tren;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogTren extends JDialog {

    private static final String[] TIPURI = {"InterCity", "InterRegio", "Regio"};

    private final TrenDAO trenDAO;
    private Tren tren;
    private boolean salvat;

    private JTextField campNumar;
    private JComboBox<String> comboTip;

    public DialogTren(JFrame parinte, Tren tren) {
        super(parinte, true);
        this.trenDAO = new TrenDAO();
        this.tren = tren;
        this.salvat = false;

        setTitle(tren == null ? "Adaugare tren" : "Editare tren");
        setSize(350, 200);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (tren != null) {
            campNumar.setText(tren.getNumar());
            comboTip.setSelectedItem(tren.getTip());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Numar tren:"), gbc);
        campNumar = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campNumar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Tip:"), gbc);
        comboTip = new JComboBox<>(TIPURI);
        gbc.gridx = 1;
        panou.add(comboTip, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        String numar = campNumar.getText().trim();
        String tip = (String) comboTip.getSelectedItem();

        if (numar.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Numarul de tren este obligatoriu.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (tren == null) {
            Tren trenNou = new Tren(numar, tip);
            int idGenerat = trenDAO.adauga(trenNou);
            reusit = idGenerat != -1;
        } else {
            tren.setNumar(numar);
            tren.setTip(tip);
            reusit = trenDAO.modifica(tren);
        }

        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Salvarea a esuat. Verificati daca numarul de tren exista deja.",
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