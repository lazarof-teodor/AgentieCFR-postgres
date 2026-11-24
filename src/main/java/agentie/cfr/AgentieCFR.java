package agentie.cfr;

import agentie.cfr.ui.FereastraPrincipala;
import javax.swing.SwingUtilities;


public class AgentieCFR {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            FereastraPrincipala fereastra = new FereastraPrincipala();
            fereastra.setVisible(true);
        });
    }
}