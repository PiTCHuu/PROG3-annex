package modules;

public class InvoiceTaxSummary {
    private int id;
    private double totalHt;
    private double totalTva;
    private double totalTtc;

    public InvoiceTaxSummary(int id,
                             double totalHt,
                             double totalTva,
                             double totalTtc) {
        this.id = id;
        this.totalHt = totalHt;
        this.totalTva = totalTva;
        this.totalTtc = totalTtc;
    }

    @Override
    public String toString() {
        return id + " | HT " + totalHt +
                " | TVA " + totalTva +
                " | TTC " + totalTtc;
    }
}

