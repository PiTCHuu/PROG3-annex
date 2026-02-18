package modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public List<InvoiceTotal> findInvoiceTotals() {
        List<InvoiceTotal> list = new ArrayList<>();

        String sql = "SELECT i.id,\n" +
                "       i.customer_name,\n" +
                "       i.status,\n" +
                "       SUM(il.quantity * il.unit_price) AS total\n" +
                "FROM invoice i\n" +
                "JOIN invoice_line il ON i.id = il.invoice_id\n" +
                "GROUP BY i.id, i.customer_name, i.status;\n";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        List<InvoiceTotal> list = new ArrayList<>();

        String sql = "SELECT i.id,\n" +
                "       i.customer_name,\n" +
                "       i.status,\n" +
                "       SUM(il.quantity * il.unit_price) AS total\n" +
                "FROM invoice i\n" +
                "JOIN invoice_line il ON i.id = il.invoice_id\n" +
                "WHERE i.status IN ('CONFIRMED','PAID')\n" +
                "GROUP BY i.id, i.customer_name, i.status;\n";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public InvoiceStatusTotals computeStatusTotals() {

        String sql = """
        SELECT
        SUM(CASE WHEN i.status='PAID'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_paid,

        SUM(CASE WHEN i.status='CONFIRMED'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_confirmed,

        SUM(CASE WHEN i.status='DRAFT'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_draft
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new InvoiceStatusTotals(
                        rs.getDouble("total_paid"),
                        rs.getDouble("total_confirmed"),
                        rs.getDouble("total_draft")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    public Double computeWeightedTurnover() {

        String sql = """
        SELECT SUM(
            CASE
                WHEN i.status = 'PAID'
                    THEN il.quantity * il.unit_price
                WHEN i.status = 'CONFIRMED'
                    THEN il.quantity * il.unit_price * 0.5
                ELSE 0
            END
        ) AS weighted_turnover
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("weighted_turnover");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0.0;
    }




}
