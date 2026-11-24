package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Vagon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VagonDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT v.id, v.numar_vagon, v.clasa, v.nr_locuri, v.multiplicator_pret, "
            + "t.id AS tren_id, t.numar AS tren_numar, t.tip AS tren_tip "
            + "FROM Vagon v "
            + "JOIN Tren t ON v.tren_id = t.id ";

    public List<Vagon> listaToate() {
        List<Vagon> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY t.numar, v.numar_vagon";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaVagon(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Vagon gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE v.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaVagon(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Vagon vagon) {
        String sql = "INSERT INTO Vagon (tren_id, numar_vagon, clasa, nr_locuri, multiplicator_pret) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vagon.getTren().getId());
            ps.setInt(2, vagon.getNumarVagon());
            ps.setString(3, vagon.getClasa());
            ps.setInt(4, vagon.getNrLocuri());
            ps.setBigDecimal(5, vagon.getMultiplicatorPret());
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

    public boolean modifica(Vagon vagon) {
        String sql = "UPDATE Vagon SET tren_id = ?, numar_vagon = ?, clasa = ?, nr_locuri = ?, "
                + "multiplicator_pret = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vagon.getTren().getId());
            ps.setInt(2, vagon.getNumarVagon());
            ps.setString(3, vagon.getClasa());
            ps.setInt(4, vagon.getNrLocuri());
            ps.setBigDecimal(5, vagon.getMultiplicatorPret());
            ps.setInt(6, vagon.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Vagon WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Vagon> filtreazaDupaTren(int trenId) {
        List<Vagon> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE t.id = ? ORDER BY v.numar_vagon";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, trenId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaVagon(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Vagon> filtreazaDupaClasa(String clasa) {
        List<Vagon> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE v.clasa = ? ORDER BY t.numar, v.numar_vagon";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, clasa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaVagon(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Vagon mapeazaVagon(ResultSet rs) throws SQLException {
        Tren tren = new Tren(
                rs.getInt("tren_id"),
                rs.getString("tren_numar"),
                rs.getString("tren_tip")
        );

        return new Vagon(
                rs.getInt("id"),
                tren,
                rs.getInt("numar_vagon"),
                rs.getString("clasa"),
                rs.getInt("nr_locuri"),
                rs.getBigDecimal("multiplicator_pret")
        );
    }
}