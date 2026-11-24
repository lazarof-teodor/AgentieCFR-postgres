package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Vagon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT l.id, l.numar_loc, "
            + "v.id AS vagon_id, v.numar_vagon, v.clasa, v.nr_locuri, v.multiplicator_pret, "
            + "t.id AS tren_id, t.numar AS tren_numar, t.tip AS tren_tip "
            + "FROM Loc l "
            + "JOIN Vagon v ON l.vagon_id = v.id "
            + "JOIN Tren t ON v.tren_id = t.id ";

    public List<Loc> listaToate() {
        List<Loc> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY t.numar, v.numar_vagon, l.numar_loc";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaLoc(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Loc gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE l.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaLoc(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Loc loc) {
        String sql = "INSERT INTO Loc (vagon_id, numar_loc) VALUES (?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, loc.getVagon().getId());
            ps.setInt(2, loc.getNumarLoc());
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

    public boolean modifica(Loc loc) {
        String sql = "UPDATE Loc SET vagon_id = ?, numar_loc = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loc.getVagon().getId());
            ps.setInt(2, loc.getNumarLoc());
            ps.setInt(3, loc.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Loc WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Loc> filtreazaDupaVagon(int vagonId) {
        List<Loc> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE v.id = ? ORDER BY l.numar_loc";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vagonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaLoc(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Loc> listaLocuriLibere(int cursaId) {
        List<Loc> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA
                + "WHERE t.id = (SELECT c.tren_id FROM Cursa c WHERE c.id = ?) "
                + "AND l.id NOT IN ("
                + "  SELECT b.loc_id FROM Bilet b WHERE b.cursa_id = ? AND b.status = 'activ'"
                + ") "
                + "ORDER BY v.numar_vagon, l.numar_loc";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cursaId);
            ps.setInt(2, cursaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaLoc(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Loc mapeazaLoc(ResultSet rs) throws SQLException {
        Tren tren = new Tren(
                rs.getInt("tren_id"),
                rs.getString("tren_numar"),
                rs.getString("tren_tip")
        );

        Vagon vagon = new Vagon(
                rs.getInt("vagon_id"),
                tren,
                rs.getInt("numar_vagon"),
                rs.getString("clasa"),
                rs.getInt("nr_locuri"),
                rs.getBigDecimal("multiplicator_pret")
        );

        return new Loc(
                rs.getInt("id"),
                vagon,
                rs.getInt("numar_loc")
        );
    }
}