package modules;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {

        List<InvoiceTaxSummary> list = new ArrayList<>();

        String sql = """
        SELECT
            i.id,
            SUM(il.quantity * il.unit_price) AS total_ht,
            SUM(il.quantity * il.unit_price) * tc.rate / 100 AS total_tva,
            SUM(il.quantity * il.unit_price) * (1 + tc.rate / 100) AS total_ttc
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        CROSS JOIN tax_config tc
        GROUP BY i.id, tc.rate
        ORDER BY i.id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new InvoiceTaxSummary(
                        rs.getInt("id"),
                        rs.getDouble("total_ht"),
                        rs.getDouble("total_tva"),
                        rs.getDouble("total_ttc")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
    public BigDecimal computeWeightedTurnoverTtc() {

        String sql = """
        SELECT SUM(
            CASE
                WHEN i.status='PAID'
                    THEN il.quantity * il.unit_price * (1 + tc.rate/100)
                WHEN i.status='CONFIRMED'
                    THEN il.quantity * il.unit_price * 0.5 * (1 + tc.rate/100)
                ELSE 0
            END
        ) AS weighted_ttc
        FROM invoice i
        JOIN invoice_line il ON i.id = il.invoice_id
        CROSS JOIN tax_config tc
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("weighted_ttc").setScale(2, RoundingMode.HALF_UP);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return BigDecimal.ZERO;
    }
}
