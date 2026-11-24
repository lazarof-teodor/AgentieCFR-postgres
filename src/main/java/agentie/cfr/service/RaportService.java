package agentie.cfr.service;

import agentie.cfr.db.dao.RaportDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class RaportService {

    private final RaportDAO raportDAO;

    public RaportService() {
        this.raportDAO = new RaportDAO();
    }

    public Map<String, BigDecimal> veniturePeZi(LocalDate start, LocalDate stop) throws ValidareException {
        valideazaPerioada(start, stop);
        return raportDAO.veniturePeZi(LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(stop, LocalTime.MAX));
    }

    public Map<String, Integer> biletePeRuta(LocalDate start, LocalDate stop) throws ValidareException {
        valideazaPerioada(start, stop);
        return raportDAO.biletePeRuta(LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(stop, LocalTime.MAX));
    }

    public Map<String, Integer> distributieStatus(LocalDate start, LocalDate stop) throws ValidareException {
        valideazaPerioada(start, stop);
        return raportDAO.distributieStatus(LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(stop, LocalTime.MAX));
    }

    private void valideazaPerioada(LocalDate start, LocalDate stop) throws ValidareException {
        if (start == null || stop == null) {
            throw new ValidareException("Data de inceput si data de sfarsit sunt obligatorii.");
        }
        if (stop.isBefore(start)) {
            throw new ValidareException("Data de sfarsit trebuie sa fie dupa data de inceput.");
        }
    }
}