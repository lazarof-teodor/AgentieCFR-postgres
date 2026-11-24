package agentie.cfr.ui;

import agentie.cfr.model.Utilizator;
import agentie.cfr.ui.panouri.PanouLogin;
import agentie.cfr.ui.panouri.PanouMeniu;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FereastraPrincipala extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel continut;
    private Utilizator utilizatorLogat;
    private PanouMeniu panouMeniu;

    public FereastraPrincipala() {
        setTitle("Agentie CFR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new java.awt.Dimension(1024, 700));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        continut = new JPanel(cardLayout);

        PanouLogin panouLogin = new PanouLogin(this);
        continut.add(panouLogin, "login");

        add(continut);

        cardLayout.show(continut, "login");
    }

    public void autentificareReusita(Utilizator utilizator) {
        this.utilizatorLogat = utilizator;

        panouMeniu = new PanouMeniu(this, utilizator);
        continut.add(panouMeniu, "meniu");
        cardLayout.show(continut, "meniu");
    }

    public void delogare() {
        this.utilizatorLogat = null;
        cardLayout.show(continut, "login");
    }

    public void arataPanou(JPanel panou, String nume) {
        continut.add(panou, nume);
        cardLayout.show(continut, nume);
    }

    public void arataMeniu() {
        if (panouMeniu != null) {
            panouMeniu.reincarcaDashboard();
        }
        cardLayout.show(continut, "meniu");
    }

    public Utilizator getUtilizatorLogat() {
        return utilizatorLogat;
    }
}