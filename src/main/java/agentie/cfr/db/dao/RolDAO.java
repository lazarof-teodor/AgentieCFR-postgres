package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listaToate() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id, nume FROM Rol ORDER BY nume";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Rol(rs.getInt("id"), rs.getString("nume")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Rol gasesteDupaId(int id) {
        String sql = "SELECT id, nume FROM Rol WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Rol(rs.getInt("id"), rs.getString("nume"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}