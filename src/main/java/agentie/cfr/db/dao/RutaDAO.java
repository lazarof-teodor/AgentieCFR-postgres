package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Statie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT r.id, r.durata_estimata, "
            + "sp.id AS plecare_id, sp.nume AS plecare_nume, sp.oras AS plecare_oras, "
            + "ss.id AS sosire_id, ss.nume AS sosire_nume, ss.oras AS sosire_oras "
            + "FROM Ruta r "
            + "JOIN Statie sp ON r.statie_plecare_id = sp.id "
            + "JOIN Statie ss ON r.statie_sosire_id = ss.id ";

    public List<Ruta> listaToate() {
        List<Ruta> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY sp.nume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaRuta(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Ruta gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE r.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaRuta(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Ruta ruta) {
        String sql = "INSERT INTO Ruta (statie_plecare_id, statie_sosire_id, durata_estimata) VALUES (?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ruta.getStatiePlecare().getId());
            ps.setInt(2, ruta.getStatieSosire().getId());
            ps.setTime(3, Time.valueOf(ruta.getDurataEstimata()));
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

    public boolean modifica(Ruta ruta) {
        String sql = "UPDATE Ruta SET statie_plecare_id = ?, statie_sosire_id = ?, durata_estimata = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ruta.getStatiePlecare().getId());
            ps.setInt(2, ruta.getStatieSosire().getId());
            ps.setTime(3, Time.valueOf(ruta.getDurataEstimata()));
            ps.setInt(4, ruta.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Ruta WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ruta> cautaDupaStatie(String text) {
        List<Ruta> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE sp.nume LIKE ? OR ss.nume LIKE ? ORDER BY sp.nume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + text + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaRuta(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Ruta> filtreazaDupaStatiePlecare(int statiePlecareId) {
        List<Ruta> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE sp.id = ? ORDER BY ss.nume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, statiePlecareId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaRuta(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Ruta mapeazaRuta(ResultSet rs) throws SQLException {
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

        return new Ruta(
                rs.getInt("id"),
                plecare,
                sosire,
                rs.getTime("durata_estimata").toLocalTime()
        );
    }
}