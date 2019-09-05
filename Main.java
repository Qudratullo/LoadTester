import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static List<Statistics> statisticsList = new ArrayList<>();
    private static long startTime;
    private static long endTime;
    private static int NUMBER_OF_CLIENTS = 1;
    static int INTERVAL_BETWEEN_REQUESTS = 0; // in milliseconds
    static int COUNT_REQUESTS = 1;


    public static void main(String[] args) {
        parseArguments(args);

        startTime = System.currentTimeMillis();
        System.out.println("Тестирование...");

        List<CompletableFuture<Statistics>> futures = Stream.generate(() -> CompletableFuture.supplyAsync(() -> new CalculationStatistics().calculate()))
                .limit(NUMBER_OF_CLIENTS).collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        statisticsList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        endTime = System.currentTimeMillis();

        printStatistics();
    }

    private static void printStatistics() {
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        long sumTime = 0;
        int sumSuccess = 0;

        for (Statistics statistics : statisticsList) {
            minTime = Math.min(minTime, statistics.getMinTime());
            maxTime = Math.max(maxTime, statistics.getMaxTime());
            sumTime += statistics.getSumTime();
            sumSuccess += statistics.getCountSuccess();
        }

        System.out.println("Number of clients:\t" + NUMBER_OF_CLIENTS);
        System.out.println("Number of requests for one client:\t" + COUNT_REQUESTS);
        System.out.println("Number of total requests:\t" + NUMBER_OF_CLIENTS * COUNT_REQUESTS);
        System.out.println("Test time:\t" + (endTime - startTime) / 1000. + " s");
        System.out.println();
        System.out.println("Min time:\t" + minTime + " ms");
        System.out.println("Max time:\t" + maxTime + " ms");
        System.out.println("Avg time:\t" + sumTime / (NUMBER_OF_CLIENTS * COUNT_REQUESTS) + " ms");
        System.out.println("Success responses:\t" + sumSuccess * 100 / (NUMBER_OF_CLIENTS * COUNT_REQUESTS) + "%");
    }

    private static void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (i == args.length - 1)
                break;

            switch (args[i]) {
                case "-u":
                    setCountUsers(args[i + 1]);
                    break;
                case "-r":
                    setCountRequests(args[i + 1]);
                    break;
                case "-i":
                    setInterval(args[i + 1]);
                    break;
            }
        }
    }

    private static void setCountUsers(String countString) {
        try {
            NUMBER_OF_CLIENTS = Integer.parseInt(countString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static void setCountRequests(String countString) {
        try {
            COUNT_REQUESTS = Integer.parseInt(countString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static void setInterval(String intervalString) {
        try {
            INTERVAL_BETWEEN_REQUESTS = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
