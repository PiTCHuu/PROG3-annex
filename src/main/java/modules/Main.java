package modules;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        dataRetriever.findInvoiceTotals().forEach(System.out::println);
        dataRetriever.findConfirmedAndPaidInvoiceTotals().forEach(System.out::println);
        System.out.println(dataRetriever.computeStatusTotals());
        System.out.println(dataRetriever.computeWeightedTurnover());
        dataRetriever.findInvoiceTaxSummaries().forEach(System.out::println);
        System.out.println(dataRetriever.computeWeightedTurnoverTtc());
    }
}