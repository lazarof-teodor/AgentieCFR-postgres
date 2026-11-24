package agentie.cfr.ui.panouri;

import agentie.cfr.model.Utilizator;
import agentie.cfr.service.AutentificareService;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.util.Culori;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PanouLogin extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final AutentificareService autentificareService;

    private JTextField campUsername;
    private JPasswordField campParola;

    public PanouLogin(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.autentificareService = new AutentificareService();

        construiesteInterfata();
    }

    private void construiesteInterfata() {
        setLayout(new BorderLayout());

        add(construiestePanouBrand(), BorderLayout.WEST);
        add(construiestePanouFormular(), BorderLayout.CENTER);
    }

    private JPanel construiestePanouBrand() {
        JPanel panouBrand = new JPanel(new GridBagLayout());
        panouBrand.setBackground(Culori.PRIMAR);
        panouBrand.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 30, 10, 30);

        JLabel iconita = new JLabel("\uD83D\uDE86");
        iconita.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconita.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        panouBrand.add(iconita, gbc);

        JLabel titlu = new JLabel("Agentie CFR");
        titlu.setFont(new Font("SansSerif", Font.BOLD, 28));
        titlu.setForeground(java.awt.Color.WHITE);
        titlu.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panouBrand.add(titlu, gbc);

        JLabel subtitlu = new JLabel("Sistem de gestiune bilete si curse");
        subtitlu.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitlu.setForeground(new java.awt.Color(255, 255, 255, 210));
        subtitlu.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panouBrand.add(subtitlu, gbc);

        return panouBrand;
    }

    private JPanel construiestePanouFormular() {
        JPanel panouExterior = new JPanel(new GridBagLayout());
        panouExterior.setBackground(Culori.FUNDAL);

        JPanel formular = new JPanel(new GridBagLayout());
        formular.setBackground(Culori.CARD_FUNDAL);
        formular.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(0xDDE3E8), 1, true),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titluForm = new JLabel("Autentificare");
        titluForm.setFont(new Font("SansSerif", Font.BOLD, 20));
        titluForm.setForeground(Culori.TEXT_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 8, 20, 8);
        formular.add(titluForm, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 4, 8);

        JLabel etichetaUsername = new JLabel("Utilizator");
        etichetaUsername.setForeground(Culori.TEXT_SECUNDAR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formular.add(etichetaUsername, gbc);

        campUsername = new JTextField(18);
        campUsername.setFont(new Font("SansSerif", Font.PLAIN, 15));
        campUsername.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 2;
        formular.add(campUsername, gbc);

        JLabel etichetaParola = new JLabel("Parola");
        etichetaParola.setForeground(Culori.TEXT_SECUNDAR);
        gbc.gridy = 3;
        formular.add(etichetaParola, gbc);

        campParola = new JPasswordField(18);
        campParola.setFont(new Font("SansSerif", Font.PLAIN, 15));
        campParola.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 4;
        formular.add(campParola, gbc);

        JButton butonLogin = new JButton("Autentificare");
        butonLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        butonLogin.setBackground(Culori.PRIMAR);
        butonLogin.setForeground(java.awt.Color.WHITE);
        butonLogin.setFocusPainted(false);
        butonLogin.setPreferredSize(new Dimension(260, 38));
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 8, 4, 8);
        formular.add(butonLogin, gbc);

        butonLogin.addActionListener(e -> incearcaLogin());
        campParola.addActionListener(e -> incearcaLogin());

        panouExterior.add(formular);
        return panouExterior;
    }

    private void incearcaLogin() {
        String username = campUsername.getText().trim();
        String parola = new String(campParola.getPassword());

        if (username.isEmpty() || parola.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Introduceti utilizator si parola.",
                    "Date lipsa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Utilizator utilizator = autentificareService.login(username, parola);

        if (utilizator == null) {
            JOptionPane.showMessageDialog(this,
                    "Utilizator sau parola incorecta.",
                    "Autentificare esuata",
                    JOptionPane.ERROR_MESSAGE);
            campParola.setText("");
            return;
        }

        campParola.setText("");
        fereastraPrincipala.autentificareReusita(utilizator);
    }
}