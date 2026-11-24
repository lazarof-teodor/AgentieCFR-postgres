package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Rol;
import agentie.cfr.model.Utilizator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilizatorDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT u.id, u.username, u.parola_hash, u.nume, u.prenume, u.activ, "
            + "r.id AS rol_id, r.nume AS rol_nume "
            + "FROM Utilizator u "
            + "JOIN Rol r ON u.rol_id = r.id ";

    public List<Utilizator> listaToate() {
        List<Utilizator> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY u.username";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaUtilizator(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Utilizator gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE u.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaUtilizator(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Utilizator gasesteDupaUsername(String username) {
        String sql = SELECT_DE_BAZA + "WHERE u.username = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaUtilizator(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Utilizator utilizator) {
        String sql = "INSERT INTO Utilizator (username, parola_hash, nume, prenume, rol_id, activ) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, utilizator.getUsername());
            ps.setString(2, utilizator.getParolaHash());
            ps.setString(3, utilizator.getNume());
            ps.setString(4, utilizator.getPrenume());
            ps.setInt(5, utilizator.getRol().getId());
            ps.setBoolean(6, utilizator.isActiv());
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

    public boolean modifica(Utilizator utilizator) {
        String sql = "UPDATE Utilizator SET username = ?, nume = ?, prenume = ?, rol_id = ?, activ = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, utilizator.getUsername());
            ps.setString(2, utilizator.getNume());
            ps.setString(3, utilizator.getPrenume());
            ps.setInt(4, utilizator.getRol().getId());
            ps.setBoolean(5, utilizator.isActiv());
            ps.setInt(6, utilizator.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean schimbaParola(int id, String parolaHashNoua) {
        String sql = "UPDATE Utilizator SET parola_hash = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, parolaHashNoua);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Utilizator WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Utilizator> cautaDupaUsername(String text) {
        List<Utilizator> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE u.username LIKE ? ORDER BY u.username";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + text + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaUtilizator(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Utilizator> filtreazaDupaRol(int rolId) {
        List<Utilizator> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE r.id = ? ORDER BY u.username";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rolId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaUtilizator(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Utilizator mapeazaUtilizator(ResultSet rs) throws SQLException {
        Rol rol = new Rol(rs.getInt("rol_id"), rs.getString("rol_nume"));

        return new Utilizator(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("parola_hash"),
                rs.getString("nume"),
                rs.getString("prenume"),
                rol,
                rs.getBoolean("activ")
        );
    }
}