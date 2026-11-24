package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.RutaDAO;
import agentie.cfr.db.dao.StatieDAO;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Statie;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogRuta extends JDialog {

    private final RutaDAO rutaDAO;
    private Ruta ruta;
    private boolean salvat;

    private JComboBox<Statie> comboPlecare;
    private JComboBox<Statie> comboSosire;
    private JTextField campDurata;

    public DialogRuta(JFrame parinte, Ruta ruta) {
        super(parinte, true);
        this.rutaDAO = new RutaDAO();
        this.ruta = ruta;
        this.salvat = false;

        setTitle(ruta == null ? "Adaugare ruta" : "Editare ruta");
        setSize(380, 220);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (ruta != null) {
            comboPlecare.setSelectedItem(ruta.getStatiePlecare());
            comboSosire.setSelectedItem(ruta.getStatieSosire());
            campDurata.setText(ruta.getDurataEstimata().toString());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        StatieDAO statieDAO = new StatieDAO();
        List<Statie> statii = statieDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Statie plecare:"), gbc);
        comboPlecare = new JComboBox<>(statii.toArray(new Statie[0]));
        gbc.gridx = 1;
        panou.add(comboPlecare, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Statie sosire:"), gbc);
        comboSosire = new JComboBox<>(statii.toArray(new Statie[0]));
        gbc.gridx = 1;
        panou.add(comboSosire, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Durata (HH:mm):"), gbc);
        campDurata = new JTextField(10);
        campDurata.setText("02:00");
        gbc.gridx = 1;
        panou.add(campDurata, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        Statie plecare = (Statie) comboPlecare.getSelectedItem();
        Statie sosire = (Statie) comboSosire.getSelectedItem();

        if (plecare == null || sosire == null) {
            JOptionPane.showMessageDialog(this,
                    "Trebuie sa existe cel putin doua statii create.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (plecare.getId() == sosire.getId()) {
            JOptionPane.showMessageDialog(this,
                    "Statia de plecare trebuie sa fie diferita de cea de sosire.",
                    "Date invalide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalTime durata;
        try {
            durata = LocalTime.parse(campDurata.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Durata trebuie introdusa in formatul HH:mm, de exemplu 02:30.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (ruta == null) {
            Ruta rutaNoua = new Ruta(plecare, sosire, durata);
            int idGenerat = rutaDAO.adauga(rutaNoua);
            reusit = idGenerat != -1;
        } else {
            ruta.setStatiePlecare(plecare);
            ruta.setStatieSosire(sosire);
            ruta.setDurataEstimata(durata);
            reusit = rutaDAO.modifica(ruta);
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