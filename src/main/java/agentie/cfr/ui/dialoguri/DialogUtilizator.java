package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.RolDAO;
import agentie.cfr.db.dao.UtilizatorDAO;
import agentie.cfr.model.Rol;
import agentie.cfr.model.Utilizator;
import agentie.cfr.service.AutentificareService;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DialogUtilizator extends JDialog {

    private final UtilizatorDAO utilizatorDAO;
    private Utilizator utilizator;
    private boolean salvat;

    private JTextField campUsername;
    private JPasswordField campParola;
    private JTextField campNume;
    private JTextField campPrenume;
    private JComboBox<Rol> comboRol;
    private JCheckBox checkActiv;

    public DialogUtilizator(JFrame parinte, Utilizator utilizator) {
        super(parinte, true);
        this.utilizatorDAO = new UtilizatorDAO();
        this.utilizator = utilizator;
        this.salvat = false;

        setTitle(utilizator == null ? "Adaugare utilizator" : "Editare utilizator");
        setSize(400, 320);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (utilizator != null) {
            campUsername.setText(utilizator.getUsername());
            campNume.setText(utilizator.getNume());
            campPrenume.setText(utilizator.getPrenume());
            comboRol.setSelectedItem(utilizator.getRol());
            checkActiv.setSelected(utilizator.isActiv());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        RolDAO rolDAO = new RolDAO();
        List<Rol> roluri = rolDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Utilizator (username):"), gbc);
        campUsername = new JTextField(18);
        gbc.gridx = 1;
        panou.add(campUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        String etichetaParola = utilizator == null ? "Parola:" : "Parola noua (optional):";
        panou.add(new JLabel(etichetaParola), gbc);
        campParola = new JPasswordField(18);
        gbc.gridx = 1;
        panou.add(campParola, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Nume:"), gbc);
        campNume = new JTextField(18);
        gbc.gridx = 1;
        panou.add(campNume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panou.add(new JLabel("Prenume:"), gbc);
        campPrenume = new JTextField(18);
        gbc.gridx = 1;
        panou.add(campPrenume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panou.add(new JLabel("Rol:"), gbc);
        comboRol = new JComboBox<>(roluri.toArray(new Rol[0]));
        gbc.gridx = 1;
        panou.add(comboRol, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panou.add(new JLabel("Cont activ:"), gbc);
        checkActiv = new JCheckBox();
        checkActiv.setSelected(true);
        gbc.gridx = 1;
        panou.add(checkActiv, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        String username = campUsername.getText().trim();
        String parola = new String(campParola.getPassword());
        String nume = campNume.getText().trim();
        String prenume = campPrenume.getText().trim();
        Rol rolSelectat = (Rol) comboRol.getSelectedItem();
        boolean activ = checkActiv.isSelected();

        if (username.isEmpty() || nume.isEmpty() || prenume.isEmpty() || rolSelectat == null) {
            JOptionPane.showMessageDialog(this,
                    "Utilizator, nume, prenume si rol sunt obligatorii.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (utilizator == null && parola.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Parola este obligatorie la crearea unui utilizator nou.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean reusit;

        if (utilizator == null) {
            String parolaHash = AutentificareService.hashParola(parola);
            Utilizator utilizatorNou = new Utilizator(username, parolaHash, nume, prenume, rolSelectat);
            utilizatorNou.setActiv(activ);
            int idGenerat = utilizatorDAO.adauga(utilizatorNou);
            reusit = idGenerat != -1;
        } else {
            utilizator.setUsername(username);
            utilizator.setNume(nume);
            utilizator.setPrenume(prenume);
            utilizator.setRol(rolSelectat);
            utilizator.setActiv(activ);
            reusit = utilizatorDAO.modifica(utilizator);

            if (reusit && !parola.isEmpty()) {
                String parolaHash = AutentificareService.hashParola(parola);
                reusit = utilizatorDAO.schimbaParola(utilizator.getId(), parolaHash);
            }
        }

        if (!reusit) {
            JOptionPane.showMessageDialog(this,
                    "Salvarea a esuat. Verificati daca username-ul exista deja.",
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