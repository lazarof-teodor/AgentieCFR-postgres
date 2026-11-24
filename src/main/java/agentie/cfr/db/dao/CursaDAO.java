package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Statie;
import agentie.cfr.model.Tren;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CursaDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT c.id, c.data, c.ora_plecare, c.ora_sosire, c.pret_baza, "
            + "t.id AS tren_id, t.numar AS tren_numar, t.tip AS tren_tip, "
            + "r.id AS ruta_id, r.durata_estimata AS ruta_durata, "
            + "sp.id AS plecare_id, sp.nume AS plecare_nume, sp.oras AS plecare_oras, "
            + "ss.id AS sosire_id, ss.nume AS sosire_nume, ss.oras AS sosire_oras "
            + "FROM Cursa c "
            + "JOIN Tren t ON c.tren_id = t.id "
            + "JOIN Ruta r ON c.ruta_id = r.id "
            + "JOIN Statie sp ON r.statie_plecare_id = sp.id "
            + "JOIN Statie ss ON r.statie_sosire_id = ss.id ";

    public List<Cursa> listaToate() {
        List<Cursa> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY c.data, c.ora_plecare";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaCursa(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Cursa gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE c.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaCursa(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Cursa cursa) {
        if (cursa == null || cursa.getTren() == null || cursa.getRuta() == null) {
            System.out.println("EROARE [CursaDAO]: Date incomplete (Cursa, Trenul sau Ruta sunt null).");
            return -1;
        }

        String sql = "INSERT INTO Cursa (tren_id, ruta_id, data, ora_plecare, ora_sosire, pret_baza) "
               + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cursa.getTren().getId());
            ps.setInt(2, cursa.getRuta().getId());
            ps.setDate(3, Date.valueOf(cursa.getData()));
            ps.setTime(4, Time.valueOf(cursa.getOraPlecare()));
            ps.setTime(5, Time.valueOf(cursa.getOraSosire()));
            ps.setBigDecimal(6, cursa.getPretBaza());
            int randuri = ps.executeUpdate();

            if (randuri > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean modifica(Cursa cursa) {
        String sql = "UPDATE Cursa SET tren_id = ?, ruta_id = ?, data = ?, ora_plecare = ?, "
                + "ora_sosire = ?, pret_baza = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cursa.getTren().getId());
            ps.setInt(2, cursa.getRuta().getId());
            ps.setDate(3, Date.valueOf(cursa.getData()));
            ps.setTime(4, Time.valueOf(cursa.getOraPlecare()));
            ps.setTime(5, Time.valueOf(cursa.getOraSosire()));
            ps.setBigDecimal(6, cursa.getPretBaza());
            ps.setInt(7, cursa.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Cursa WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cursa> cautaDupaStatie(String text) {
        List<Cursa> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE sp.nume LIKE ? OR ss.nume LIKE ? ORDER BY c.data, c.ora_plecare";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + text + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaCursa(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Cursa> filtreazaDupaPerioada(LocalDate dataStart, LocalDate dataStop) {
        List<Cursa> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE c.data BETWEEN ? AND ? ORDER BY c.data, c.ora_plecare";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dataStart));
            ps.setDate(2, Date.valueOf(dataStop));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaCursa(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Cursa mapeazaCursa(ResultSet rs) throws SQLException {
        Tren tren = new Tren(
                rs.getInt("tren_id"),
                rs.getString("tren_numar"),
                rs.getString("tren_tip")
        );

        Statie plecare = new Statie(
                rs.getInt("plecare_id"),
                rs.getString("plecare_nume"),
                rs.getString("plecare_oras")
        );

        Statie sosire = new Statie(
                rs.getInt("sosire_id"),
                rs.getString("sosire_nume"),
                rs.getString("sosire_oras")
        );

        Ruta ruta = new Ruta(
                rs.getInt("ruta_id"),
                plecare,
                sosire,
                rs.getTime("ruta_durata").toLocalTime()
        );

        return new Cursa(
                rs.getInt("id"),
                tren,
                ruta,
                rs.getDate("data").toLocalDate(),
                rs.getTime("ora_plecare").toLocalTime(),
                rs.getTime("ora_sosire").toLocalTime(),
                rs.getBigDecimal("pret_baza")
        );
    }
}