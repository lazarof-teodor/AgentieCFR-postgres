package agentie.cfr.db.dao;

import agentie.cfr.db.ConexiuneDB;
import agentie.cfr.model.Bilet;
import agentie.cfr.model.Client;
import agentie.cfr.model.Cursa;
import agentie.cfr.model.Loc;
import agentie.cfr.model.Rol;
import agentie.cfr.model.Ruta;
import agentie.cfr.model.Statie;
import agentie.cfr.model.Tren;
import agentie.cfr.model.Utilizator;
import agentie.cfr.model.Vagon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BiletDAO {

    private static final String SELECT_DE_BAZA =
            "SELECT b.id, b.pret, b.data_emitere, b.status, "
            + "c.id AS cursa_id, c.data AS cursa_data, c.ora_plecare, c.ora_sosire, c.pret_baza, "
            + "t.id AS tren_id, t.numar AS tren_numar, t.tip AS tren_tip, "
            + "r.id AS ruta_id, r.durata_estimata, "
            + "sp.id AS plecare_id, sp.nume AS plecare_nume, sp.oras AS plecare_oras, "
            + "ss.id AS sosire_id, ss.nume AS sosire_nume, ss.oras AS sosire_oras, "
            + "l.id AS loc_id, l.numar_loc, "
            + "v.id AS vagon_id, v.numar_vagon, v.clasa, v.nr_locuri, v.multiplicator_pret, "
            + "cl.id AS client_id, cl.nume AS client_nume, cl.prenume AS client_prenume, "
            + "cl.email AS client_email, cl.telefon AS client_telefon, cl.categorie AS client_categorie, "
            + "u.id AS utilizator_id, u.username, u.parola_hash, u.nume AS utilizator_nume, "
            + "u.prenume AS utilizator_prenume, u.activ, "
            + "rl.id AS rol_id, rl.nume AS rol_nume "
            + "FROM Bilet b "
            + "JOIN Cursa c ON b.cursa_id = c.id "
            + "JOIN Tren t ON c.tren_id = t.id "
            + "JOIN Ruta r ON c.ruta_id = r.id "
            + "JOIN Statie sp ON r.statie_plecare_id = sp.id "
            + "JOIN Statie ss ON r.statie_sosire_id = ss.id "
            + "JOIN Loc l ON b.loc_id = l.id "
            + "JOIN Vagon v ON l.vagon_id = v.id "
            + "JOIN Client cl ON b.client_id = cl.id "
            + "JOIN Utilizator u ON b.utilizator_id = u.id "
            + "JOIN Rol rl ON u.rol_id = rl.id ";

    public List<Bilet> listaToate() {
        List<Bilet> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "ORDER BY b.data_emitere DESC";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapeazaBilet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Bilet gasesteDupaId(int id) {
        String sql = SELECT_DE_BAZA + "WHERE b.id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapeazaBilet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int emite(Bilet bilet) {
        String sql = "INSERT INTO Bilet (cursa_id, loc_id, client_id, utilizator_id, pret, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bilet.getCursa().getId());
            ps.setInt(2, bilet.getLoc().getId());
            ps.setInt(3, bilet.getClient().getId());
            ps.setInt(4, bilet.getUtilizator().getId());
            ps.setBigDecimal(5, bilet.getPret());
            ps.setString(6, bilet.getStatus());
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

    public boolean anuleaza(int id) {
        String sql = "UPDATE Bilet SET status = 'anulat' WHERE id = ?";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Bilet> cautaDupaClient(String text) {
        List<Bilet> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA
                + "WHERE cl.nume LIKE ? OR cl.prenume LIKE ? ORDER BY b.data_emitere DESC";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + text + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaBilet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Bilet> filtreazaDupaPerioada(LocalDateTime start, LocalDateTime stop) {
        List<Bilet> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE b.data_emitere BETWEEN ? AND ? ORDER BY b.data_emitere DESC";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(stop));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaBilet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Bilet> filtreazaDupaStatus(String status) {
        List<Bilet> lista = new ArrayList<>();
        String sql = SELECT_DE_BAZA + "WHERE b.status = ? ORDER BY b.data_emitere DESC";

        try (Connection con = ConexiuneDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeazaBilet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Bilet mapeazaBilet(ResultSet rs) throws SQLException {
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
                rs.getTime("durata_estimata").toLocalTime()
        );

        Cursa cursa = new Cursa(
                rs.getInt("cursa_id"),
                tren,
                ruta,
                rs.getDate("cursa_data").toLocalDate(),
                rs.getTime("ora_plecare").toLocalTime(),
                rs.getTime("ora_sosire").toLocalTime(),
                rs.getBigDecimal("pret_baza")
        );

        Vagon vagon = new Vagon(
                rs.getInt("vagon_id"),
                tren,
                rs.getInt("numar_vagon"),
                rs.getString("clasa"),
                rs.getInt("nr_locuri"),
                rs.getBigDecimal("multiplicator_pret")
        );

        Loc loc = new Loc(
                rs.getInt("loc_id"),
                vagon,
                rs.getInt("numar_loc")
        );

        Client client = new Client(
                rs.getInt("client_id"),
                rs.getString("client_nume"),
                rs.getString("client_prenume"),
                rs.getString("client_email"),
                rs.getString("client_telefon"),
                rs.getString("client_categorie")
        );

        Rol rol = new Rol(rs.getInt("rol_id"), rs.getString("rol_nume"));

        Utilizator utilizator = new Utilizator(
                rs.getInt("utilizator_id"),
                rs.getString("username"),
                rs.getString("parola_hash"),
                rs.getString("utilizator_nume"),
                rs.getString("utilizator_prenume"),
                rol,
                rs.getBoolean("activ")
        );

        return new Bilet(
                rs.getInt("id"),
                cursa,
                loc,
                client,
                utilizator,
                rs.getBigDecimal("pret"),
                rs.getTimestamp("data_emitere").toLocalDateTime(),
                rs.getString("status")
        );
    }
}