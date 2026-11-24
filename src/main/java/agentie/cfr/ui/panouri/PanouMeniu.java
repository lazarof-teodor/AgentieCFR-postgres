package agentie.cfr.ui.panouri;

import agentie.cfr.db.dao.RaportDAO;
import agentie.cfr.model.Utilizator;
import agentie.cfr.ui.FereastraPrincipala;
import agentie.cfr.util.Culori;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanouMeniu extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final Utilizator utilizator;
    private final RaportDAO raportDAO;
    private JLabel labelBileteAzi;
    private JLabel labelVenitAzi;

    public PanouMeniu(FereastraPrincipala fereastraPrincipala, Utilizator utilizator) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.utilizator = utilizator;
        this.raportDAO = new RaportDAO();

        construiesteInterfata();
    }

    private void construiesteInterfata() {
        setLayout(new BorderLayout());
        setBackground(Culori.FUNDAL);

        add(construiesteAntet(), BorderLayout.NORTH);

        JPanel continutCentral = new JPanel(new BorderLayout());
        continutCentral.setBackground(Culori.FUNDAL);
        continutCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        continutCentral.add(construiesteDashboard(), BorderLayout.NORTH);
        continutCentral.add(construiesteButoane(), BorderLayout.CENTER);

        add(continutCentral, BorderLayout.CENTER);
    }

    private JPanel construiesteAntet() {
        JPanel antet = new JPanel(new BorderLayout());
        antet.setBackground(Culori.PRIMAR);
        antet.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel etichetaUtilizator = new JLabel(
                "\uD83D\uDC64  " + utilizator.getNume() + " " + utilizator.getPrenume()
                + "   \u2022   " + utilizator.getRol().getNume());
        etichetaUtilizator.setFont(new Font("SansSerif", Font.BOLD, 15));
        etichetaUtilizator.setForeground(java.awt.Color.WHITE);
        antet.add(etichetaUtilizator, BorderLayout.WEST);

        JButton butonDelogare = new JButton("Delogare");
        butonDelogare.setFocusPainted(false);
        butonDelogare.setBackground(Culori.PRIMAR_INCHIS);
        butonDelogare.setForeground(java.awt.Color.WHITE);
        butonDelogare.addActionListener(e -> {
            int confirmare = JOptionPane.showConfirmDialog(this,
                    "Sigur doriti sa va delogati?",
                    "Confirmare delogare",
                    JOptionPane.YES_NO_OPTION);
            if (confirmare == JOptionPane.YES_OPTION) {
                fereastraPrincipala.delogare();
            }
        });
        antet.add(butonDelogare, BorderLayout.EAST);

        return antet;
    }

    private JPanel construiesteDashboard() {
        JPanel dashboard = new JPanel(new GridLayout(1, 2, 16, 0));
        dashboard.setBackground(Culori.FUNDAL);
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        dashboard.setPreferredSize(new Dimension(0, 100));

        dashboard.add(creazaCard("\uD83C\uDFAB", "Bilete emise azi", "0", Culori.PRIMAR));
        dashboard.add(creazaCard("\uD83D\uDCB0", "Venituri azi", "0.00 lei", Culori.VERDE_ACTIV));

        actualizeazaValoriDashboard();

        return dashboard;
    }

    private void actualizeazaValoriDashboard() {
        int biletAzi = raportDAO.bileteEmiseAstazi();
        String venitAzi = raportDAO.venitAstazi().setScale(2, java.math.RoundingMode.HALF_UP).toString();

        if (labelBileteAzi != null) {
            labelBileteAzi.setText(String.valueOf(biletAzi));
        }
        if (labelVenitAzi != null) {
            labelVenitAzi.setText(venitAzi + " lei");
        }
    }

    public void reincarcaDashboard() {
        actualizeazaValoriDashboard();
    }

    private JPanel creazaCard(String iconita, String titlu, String valoare, java.awt.Color culoareAccent) {
        JPanel card = new JPanel();
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBackground(Culori.CARD_FUNDAL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, culoareAccent),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        JLabel labelIconita = new JLabel(iconita);
        labelIconita.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        labelIconita.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        card.add(labelIconita);

        JLabel labelValoare = new JLabel(valoare);
        labelValoare.setFont(new Font("SansSerif", Font.BOLD, 24));
        labelValoare.setForeground(Culori.TEXT_PRINCIPAL);
        labelValoare.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        card.add(labelValoare);

        if (titlu.equals("Bilete emise azi")) {
            labelBileteAzi = labelValoare;
        } else if (titlu.equals("Venituri azi")) {
            labelVenitAzi = labelValoare;
        }

        JLabel labelTitlu = new JLabel(titlu);
        labelTitlu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelTitlu.setForeground(Culori.TEXT_SECUNDAR);
        labelTitlu.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        card.add(labelTitlu);

        return card;
    }

    private JPanel construiesteButoane() {
        JPanel panouButoane = new JPanel(new GridLayout(0, 3, 14, 14));
        panouButoane.setBackground(Culori.FUNDAL);

        boolean esteAdmin = "Admin".equalsIgnoreCase(utilizator.getRol().getNume());

        if (esteAdmin) {
            panouButoane.add(creazaButonMeniu("\uD83C\uDFDB", "Statii", () -> deschide(new PanouStatii(fereastraPrincipala), "statii")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDE86", "Trenuri", () -> deschide(new PanouTrenuri(fereastraPrincipala), "trenuri")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDDFA", "Rute", () -> deschide(new PanouRute(fereastraPrincipala), "rute")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDE83", "Vagoane", () -> deschide(new PanouVagoane(fereastraPrincipala), "vagoane")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDCBA", "Locuri", () -> deschide(new PanouLocuri(fereastraPrincipala), "locuri")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDC65", "Utilizatori", () -> deschide(new PanouUtilizatori(fereastraPrincipala), "utilizatori")));
            panouButoane.add(creazaButonMeniu("\uD83D\uDCCA", "Rapoarte", () -> deschide(new PanouRapoarte(fereastraPrincipala), "rapoarte")));
        }

        panouButoane.add(creazaButonMeniu("\uD83D\uDCC5", "Curse", () -> deschide(new PanouCurse(fereastraPrincipala), "curse")));
        panouButoane.add(creazaButonMeniu("\uD83E\uDDD1", "Clienti", () -> deschide(new PanouClienti(fereastraPrincipala), "clienti")));
        panouButoane.add(creazaButonMeniu("\uD83C\uDFAB", "Bilete", () -> deschide(new PanouBilete(fereastraPrincipala), "bilete")));

        return panouButoane;
    }

    private void deschide(JPanel panou, String nume) {
        fereastraPrincipala.arataPanou(panou, nume);
    }

    private JPanel creazaButonMeniu(String iconita, String text, Runnable actiune) {
        JPanel buton = new JPanel();
        buton.setLayout(new BoxLayout(buton, BoxLayout.Y_AXIS));
        buton.setBackground(Culori.CARD_FUNDAL);
        buton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buton.setPreferredSize(new Dimension(190, 90));
        buton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(0xDDE3E8), 1, true),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)));

        JLabel labelIconita = new JLabel(iconita);
        labelIconita.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        labelIconita.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(text);
        labelText.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelText.setForeground(Culori.TEXT_PRINCIPAL);
        labelText.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        labelText.setHorizontalAlignment(SwingConstants.CENTER);

        buton.add(javax.swing.Box.createVerticalGlue());
        buton.add(labelIconita);
        buton.add(javax.swing.Box.createRigidArea(new Dimension(0, 6)));
        buton.add(labelText);
        buton.add(javax.swing.Box.createVerticalGlue());

        buton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actiune.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                buton.setBackground(new java.awt.Color(0xEFF4FA));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buton.setBackground(Culori.CARD_FUNDAL);
            }
        });

        return buton;
    }
}