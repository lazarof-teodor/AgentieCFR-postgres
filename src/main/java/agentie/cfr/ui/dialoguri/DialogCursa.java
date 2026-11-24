package agentie.cfr.ui.dialoguri;

import agentie.cfr.db.dao.RutaDAO;
import agentie.cfr.db.dao.TrenDAO;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Tren;
import agentie.cfr.service.CursaService;
import agentie.cfr.service.ValidareException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
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

public class DialogCursa extends JDialog {

    private final CursaService cursaService;
    private Cursa cursa;
    private boolean salvat;

    private JComboBox<Tren> comboTren;
    private JComboBox<Ruta> comboRuta;
    private JTextField campData;
    private JTextField campOraPlecare;
    private JTextField campOraSosire;
    private JTextField campPretBaza;

    public DialogCursa(JFrame parinte, Cursa cursa) {
        super(parinte, true);
        this.cursaService = new CursaService();
        this.cursa = cursa;
        this.salvat = false;

        setTitle(cursa == null ? "Adaugare cursa" : "Editare cursa");
        setSize(400, 320);
        setLocationRelativeTo(parinte);

        construiesteInterfata();

        if (cursa != null) {
            comboTren.setSelectedItem(cursa.getTren());
            comboRuta.setSelectedItem(cursa.getRuta());
            campData.setText(cursa.getData().toString());
            campOraPlecare.setText(cursa.getOraPlecare().toString());
            campOraSosire.setText(cursa.getOraSosire().toString());
            campPretBaza.setText(cursa.getPretBaza().toString());
        }
    }

    private void construiesteInterfata() {
        JPanel panou = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        TrenDAO trenDAO = new TrenDAO();
        RutaDAO rutaDAO = new RutaDAO();
        List<Tren> trenuri = trenDAO.listaToate();
        List<Ruta> rute = rutaDAO.listaToate();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panou.add(new JLabel("Tren:"), gbc);
        comboTren = new JComboBox<>(trenuri.toArray(new Tren[0]));
        gbc.gridx = 1;
        panou.add(comboTren, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panou.add(new JLabel("Ruta:"), gbc);
        comboRuta = new JComboBox<>(rute.toArray(new Ruta[0]));
        gbc.gridx = 1;
        panou.add(comboRuta, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panou.add(new JLabel("Data (yyyy-MM-dd):"), gbc);
        campData = new JTextField(12);
        campData.setText(LocalDate.now().plusDays(1).toString());
        gbc.gridx = 1;
        panou.add(campData, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panou.add(new JLabel("Ora plecare (HH:mm):"), gbc);
        campOraPlecare = new JTextField(12);
        campOraPlecare.setText("08:00");
        gbc.gridx = 1;
        panou.add(campOraPlecare, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panou.add(new JLabel("Ora sosire (HH:mm):"), gbc);
        campOraSosire = new JTextField(12);
        campOraSosire.setText("11:00");
        gbc.gridx = 1;
        panou.add(campOraSosire, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panou.add(new JLabel("Pret de baza:"), gbc);
        campPretBaza = new JTextField(12);
        campPretBaza.setText("50.00");
        gbc.gridx = 1;
        panou.add(campPretBaza, gbc);

        JButton butonSalveaza = new JButton("Salveaza");
        butonSalveaza.addActionListener(e -> salveaza());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panou.add(butonSalveaza, gbc);

        add(panou);
    }

    private void salveaza() {
        Tren trenSelectat = (Tren) comboTren.getSelectedItem();
        Ruta rutaSelectata = (Ruta) comboRuta.getSelectedItem();

        if (trenSelectat == null || rutaSelectata == null) {
            JOptionPane.showMessageDialog(this,
                    "Trebuie sa existe cel putin un tren si o ruta create.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate data;
        LocalTime oraPlecare;
        LocalTime oraSosire;
        BigDecimal pretBaza;

        try {
            data = LocalDate.parse(campData.getText().trim());
            oraPlecare = LocalTime.parse(campOraPlecare.getText().trim());
            oraSosire = LocalTime.parse(campOraSosire.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data trebuie in formatul yyyy-MM-dd, orele in formatul HH:mm.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            pretBaza = new BigDecimal(campPretBaza.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Pretul de baza trebuie sa fie un numar valid.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (cursa == null) {
                Cursa cursaNoua = new Cursa(trenSelectat, rutaSelectata, data, oraPlecare, oraSosire, pretBaza);
                cursaService.adaugaCursa(cursaNoua);
            } else {
                cursa.setTren(trenSelectat);
                cursa.setRuta(rutaSelectata);
                cursa.setData(data);
                cursa.setOraPlecare(oraPlecare);
                cursa.setOraSosire(oraSosire);
                cursa.setPretBaza(pretBaza);
                cursaService.modificaCursa(cursa);
            }
        } catch (ValidareException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Eroare de validare",
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