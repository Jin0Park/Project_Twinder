package TwinderClient.GetRequestClient;
import TwinderClient.Main;
import TwinderClient.PostRequestClient.PostRequestClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class GetRequestClient {
    public static final int REQ_PER_SEC = 5;
    public static final int THREAD_SLEEP_MILSEC = 1000;
    public static CountDownLatch getCompleted = new CountDownLatch(Main.GET_THREADS);
    public static ArrayList<Long> latencyRecords = new ArrayList<>();
    public static GetRequestHelper threadBuilder = new GetRequestHelper();

    public void getLatencyResults() {
        GetRequestAnalyze result = new GetRequestAnalyze(latencyRecords);
        System.out.println("GetRequests' minimum latency: " + result.getMinLatency());
        System.out.println("GetRequests' mean latency: " + result.getMeanLatency());
        System.out.println("GetRequests' maximum latency: " + result.getMaxLatency());
    }

    public void run() {
        Runnable thread = () -> {
            while (PostRequestClient.getVal().intValue() < Main.NUMREQUESTS) {
                int currCmd = threadBuilder.getRandomCmd();
                String userID = threadBuilder.getRandomUserID();
                if (currCmd == 0) {
                    sendMatchReq(userID);
                } else {
                    sendStatsReq(userID);
                }
                try {
                    Thread.sleep(THREAD_SLEEP_MILSEC);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        new Thread(thread).start();
    }


    public void sendStatsReq(String userID) {
        StatsApi apiInstance = new StatsApi();
        try {
            long start = System.currentTimeMillis();
            ApiResponse r = apiInstance.matchStatsWithHttpInfo(userID);
            long end = System.currentTimeMillis();
            latencyRecords.add((end - start));
        } catch (ApiException e) {
            System.err.println("Exception when calling MatchStatsApi#matches");
            e.printStackTrace();
        }
    }

    public void sendMatchReq(String userID) {
        MatchesApi apiInstance = new MatchesApi();
        try {
            long start = System.currentTimeMillis();
            ApiResponse r = apiInstance.matchesWithHttpInfo(userID);
            long end = System.currentTimeMillis();
            latencyRecords.add((end - start));
        } catch (ApiException e) {
            System.err.println("Exception when calling MatchesApi#matches");
            e.printStackTrace();
        }
    }
}
