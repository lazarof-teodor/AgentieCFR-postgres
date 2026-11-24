package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.ClientDAO;
import agentie.cfr.model.Client;
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

public class DialogClient extends JDialog {

    private static final String[] CATEGORII = {"normal", "student", "elev", "pensionar"};

    private final ClientDAO clientDAO;
    private Client client;
    private boolean salvat;

    private JTextField campNume;
    private JTextField campPrenume;
    private JTextField campEmail;
    private JTextField campTelefon;
    private JComboBox<String> comboCategorie;

    public DialogClient(JFrame parinte, Client client) {
        super(parinte, true);
        this.clientDAO = new ClientDAO();
        this.client = client;
        this.salvat = false;

        setTitle(client == null ? "Adaugare client" : "Editare client");
        setSize(400, 300);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (client != null) {
            campNume.setText(client.getNume());
            campPrenume.setText(client.getPrenume());
            campEmail.setText(client.getEmail());
            campTelefon.setText(client.getTelefon());
            comboCategorie.setSelectedItem(client.getCategorie());
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
        panou.add(new JLabel("Prenume:"), gbc);
        campPrenume = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campPrenume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Email:"), gbc);
        campEmail = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panou.add(new JLabel("Telefon:"), gbc);
        campTelefon = new JTextField(20);
        gbc.gridx = 1;
        panou.add(campTelefon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panou.add(new JLabel("Categorie:"), gbc);
        comboCategorie = new JComboBox<>(CATEGORII);
        gbc.gridx = 1;
        panou.add(comboCategorie, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        String nume = campNume.getText().trim();
        String prenume = campPrenume.getText().trim();
        String email = campEmail.getText().trim();
        String telefon = campTelefon.getText().trim();
        String categorie = (String) comboCategorie.getSelectedItem();

        if (nume.isEmpty() || prenume.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Numele si prenumele sunt obligatorii.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (client == null) {
            Client clientNou = new Client(nume, prenume, email, telefon, categorie);
            int idGenerat = clientDAO.adauga(clientNou);
            reusit = idGenerat != -1;
        } else {
            client.setNume(nume);
            client.setPrenume(prenume);
            client.setEmail(email);
            client.setTelefon(telefon);
            client.setCategorie(categorie);
            reusit = clientDAO.modifica(client);
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