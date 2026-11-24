package agentie.cfr.ui.panouri;

import agentie.cfr.service.RaportService;
import agentie.cfr.service.ValidareException;
import agentie.cfr.ui.FereastraPrincipala;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class PanouRapoarte extends JPanel {

    private final FereastraPrincipala fereastraPrincipala;
    private final RaportService raportService;

    private JTextField campDataStart;
    private JTextField campDataStop;
    private JTabbedPane tabPanouri;

    public PanouRapoarte(FereastraPrincipala fereastraPrincipala) {
        this.fereastraPrincipala = fereastraPrincipala;
        this.raportService = new RaportService();

        construiesteInterfata();
    }

    private void construiesteInterfata() {
        setLayout(new BorderLayout());

        JPanel barSus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barSus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton butonInapoi = new JButton("Inapoi la meniu");
        butonInapoi.addActionListener(e -> fereastraPrincipala.arataMeniu());
        barSus.add(butonInapoi);

        barSus.add(new JLabel("De la:"));
        campDataStart = new JTextField(10);
        campDataStart.setText(LocalDate.now().minusDays(30).toString());
        barSus.add(campDataStart);

        barSus.add(new JLabel("Pana la:"));
        campDataStop = new JTextField(10);
        campDataStop.setText(LocalDate.now().toString());
        barSus.add(campDataStop);

        JButton butonGenereaza = new JButton("Genereaza rapoarte");
        butonGenereaza.addActionListener(e -> genereazaRapoarte());
        barSus.add(butonGenereaza);

        add(barSus, BorderLayout.NORTH);

        tabPanouri = new JTabbedPane();
        add(tabPanouri, BorderLayout.CENTER);
    }

    private void genereazaRapoarte() {
        LocalDate start;
        LocalDate stop;

        try {
            start = LocalDate.parse(campDataStart.getText().trim());
            stop = LocalDate.parse(campDataStop.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Introduceti ambele date in formatul yyyy-MM-dd.",
                    "Format invalid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Map<String, BigDecimal> venituri = raportService.veniturePeZi(start, stop);
            Map<String, Integer> biletePeRuta = raportService.biletePeRuta(start, stop);
            Map<String, Integer> distributieStatus = raportService.distributieStatus(start, stop);

            tabPanouri.removeAll();
            tabPanouri.addTab("Venituri pe zi", creazaGraficLinie(venituri));
            tabPanouri.addTab("Bilete pe ruta", creazaGraficBare(biletePeRuta));
            tabPanouri.addTab("Distributie status", creazaGraficPlacinta(distributieStatus));

        } catch (ValidareException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Eroare de validare",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ChartPanel creazaGraficLinie(Map<String, BigDecimal> venituri) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, BigDecimal> intrare : venituri.entrySet()) {
            dataset.addValue(intrare.getValue(), "Venituri", intrare.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Venituri pe zi (lei)",
                "Data",
                "Venituri (lei)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        return new ChartPanel(chart);
    }

    private ChartPanel creazaGraficBare(Map<String, Integer> biletePeRuta) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> intrare : biletePeRuta.entrySet()) {
            dataset.addValue(intrare.getValue(), "Bilete vandute", intrare.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Bilete vandute pe ruta (top 10)",
                "Ruta",
                "Numar bilete",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        return new ChartPanel(chart);
    }

    private ChartPanel creazaGraficPlacinta(Map<String, Integer> distributieStatus) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> intrare : distributieStatus.entrySet()) {
            dataset.setValue(intrare.getKey(), intrare.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distributie bilete active / anulate",
                dataset,
                true, true, false
        );

        return new ChartPanel(chart);
    }
}