import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CalculationStatistics {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Statistics calculate() {
        setHttpConnection();
        return run();
    }

    private void setHttpConnection() {
        try {
            socket = new Socket("localhost", 8086);
            socket.setKeepAlive(true);

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Statistics run() {
        long sumMillis = 0;
        long minMillis = Long.MAX_VALUE;
        long maxMillis = Long.MIN_VALUE;
        int countSuccess = 0;

        for (int i = 0; i < Main.COUNT_REQUESTS; i++) {
            long startMillis = System.currentTimeMillis();

            sendRequest();
            countSuccess += isSuccessfulResponse() ? 1 : 0;

            long diffMillis = System.currentTimeMillis() - startMillis;
            sumMillis += diffMillis;
            minMillis = Math.min(minMillis, diffMillis);
            maxMillis = Math.max(maxMillis, diffMillis);

            pause(Main.INTERVAL_BETWEEN_REQUESTS);
        }

        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Statistics(minMillis, maxMillis, sumMillis, countSuccess);
    }

    private void sendRequest() {
        String jsonInputString = "{\"currency\":\"string\",\"platformAccountId\":0,\"totalBet\":0}\r\n\r";
        String path = "/mq/approveBet";

        writer.println("POST " + path + " HTTP/1.1\r\n" +
                "Host: 192.168.0.104:8086\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Connection: Keep-Alive\r\n" +
                "Content-Type: application/json;charset=UTF-8\r\n" +
                "Content-Length: " + jsonInputString.length() +
                "\r\n\n" + jsonInputString);
    }

    private boolean isSuccessfulResponse() {
        try {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\n");

                if ("0".equals(line)) {
                    reader.readLine();
                    break;
                }
            }
            return parseResponseCode(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean parseResponseCode(String response) {
        int searchIndex = response.indexOf("\n");

        if (searchIndex == -1)
            return false;

        String substring = response.substring(0, searchIndex);
        return substring.contains("HTTP/1.1 200");
    }

    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
