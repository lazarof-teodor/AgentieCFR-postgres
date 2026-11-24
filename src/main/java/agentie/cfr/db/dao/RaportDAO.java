package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class RaportDAO {

    public Map<String, BigDecimal> veniturePeZi(LocalDateTime start, LocalDateTime stop) {
        Map<String, BigDecimal> rezultat = new LinkedHashMap<>();
        String sql = "SELECT CAST(data_emitere AS DATE) AS ziua, SUM(pret) AS suma "
                + "FROM Bilet "
                + "WHERE status = 'activ' AND data_emitere BETWEEN ? AND ? "
                + "GROUP BY CAST(data_emitere AS DATE) "
                + "ORDER BY ziua";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(stop));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rezultat.put(rs.getDate("ziua").toString(), rs.getBigDecimal("suma"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rezultat;
    }

    public Map<String, Integer> biletePeRuta(LocalDateTime start, LocalDateTime stop) {
        Map<String, Integer> rezultat = new LinkedHashMap<>();
        String sql = "SELECT CONCAT(sp.nume, ' -> ', ss.nume) AS ruta, COUNT(*) AS nr "
                + "FROM Bilet b "
                + "JOIN Cursa c ON b.cursa_id = c.id "
                + "JOIN Ruta r ON c.ruta_id = r.id "
                + "JOIN Statie sp ON r.statie_plecare_id = sp.id "
                + "JOIN Statie ss ON r.statie_sosire_id = ss.id "
                + "WHERE b.status = 'activ' AND b.data_emitere BETWEEN ? AND ? "
                + "GROUP BY r.id, sp.nume, ss.nume "
                + "ORDER BY nr DESC "
                + "LIMIT 10";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(stop));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rezultat.put(rs.getString("ruta"), rs.getInt("nr"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rezultat;
    }

    public Map<String, Integer> distributieStatus(LocalDateTime start, LocalDateTime stop) {
        Map<String, Integer> rezultat = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS nr "
                + "FROM Bilet "
                + "WHERE data_emitere BETWEEN ? AND ? "
                + "GROUP BY status";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(stop));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rezultat.put(rs.getString("status"), rs.getInt("nr"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rezultat;
    }

    public int bileteEmiseAstazi() {
        String sql = "SELECT COUNT(*) AS nr FROM Bilet WHERE status = 'activ' AND CAST(data_emitere AS DATE) = CURRENT_DATE";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("nr");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public BigDecimal venitAstazi() {
        String sql = "SELECT COALESCE(SUM(pret), 0) AS suma FROM Bilet "
                + "WHERE status = 'activ' AND CAST(data_emitere AS DATE) = CURRENT_DATE";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("suma");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public int curseAstazi() {
        String sql = "SELECT COUNT(*) AS nr FROM Cursa WHERE data = CURRENT_DATE";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("nr");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}