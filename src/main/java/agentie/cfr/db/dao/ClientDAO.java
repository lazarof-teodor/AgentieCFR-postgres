package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> listaToate() {
        List<Client> lista = new ArrayList<>();
        String sql = "SELECT id, nume, prenume, email, telefon, categorie FROM Client ORDER BY nume, prenume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaClient(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Client gasesteDupaId(int id) {
        String sql = "SELECT id, nume, prenume, email, telefon, categorie FROM Client WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaClient(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int adauga(Client client) {
        String sql = "INSERT INTO Client (nume, prenume, email, telefon, categorie) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getNume());
            ps.setString(2, client.getPrenume());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelefon());
            ps.setString(5, client.getCategorie());
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

    public boolean modifica(Client client) {
        String sql = "UPDATE Client SET nume = ?, prenume = ?, email = ?, telefon = ?, categorie = ? WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, client.getNume());
            ps.setString(2, client.getPrenume());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelefon());
            ps.setString(5, client.getCategorie());
            ps.setInt(6, client.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sterge(int id) {
        String sql = "DELETE FROM Client WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Client> cautaDupaNume(String text) {
        List<Client> lista = new ArrayList<>();
        String sql = "SELECT id, nume, prenume, email, telefon, categorie FROM Client "
                + "WHERE nume LIKE ? OR prenume LIKE ? ORDER BY nume, prenume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + text + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaClient(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Client> filtreazaDupaCategorie(String categorie) {
        List<Client> lista = new ArrayList<>();
        String sql = "SELECT id, nume, prenume, email, telefon, categorie FROM Client "
                + "WHERE categorie = ? ORDER BY nume, prenume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaClient(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Client mapeazaClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("nume"),
                rs.getString("prenume"),
                rs.getString("email"),
                rs.getString("telefon"),
                rs.getString("categorie")
        );
    }
}