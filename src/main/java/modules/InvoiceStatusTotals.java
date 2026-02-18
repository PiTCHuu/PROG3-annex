package modules;

public class InvoiceStatusTotals {

    private double totalPaid;
    private double totalConfirmed;
    private double totalDraft;

    public InvoiceStatusTotals(double totalPaid,
                               double totalConfirmed,
                               double totalDraft) {
        this.totalPaid = totalPaid;
        this.totalConfirmed = totalConfirmed;
        this.totalDraft = totalDraft;
    }

    @Override
    public String toString() {
        return "Paid=" + totalPaid +
                ", Confirmed=" + totalConfirmed +
                ", Draft=" + totalDraft;
    }
}

