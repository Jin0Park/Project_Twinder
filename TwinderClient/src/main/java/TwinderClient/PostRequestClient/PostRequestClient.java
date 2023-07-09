package TwinderClient.PostRequestClient;

import TwinderClient.*;
import TwinderClient.GetRequestClient.GetRequestClient;
import TwinderClient.PostRequestHelperMethods.Output;
import TwinderClient.PostRequestHelperMethods.WriteAndAnalyze;
import io.swagger.client.ApiClient;
import io.swagger.client.api.SwipeApi;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *  part1.Part1Client uses multithreaded method and calls part1.Part1ThreadConsumer. Also prints out the performance of part 1.
 */
public class PostRequestClient {
    protected static AtomicInteger count = new AtomicInteger(0);
    private static String[] results = new String[8];
    synchronized public void inc() {
        count.getAndIncrement();
    }
    public static AtomicInteger getVal() {
        return count;
    }
    public static String postURL = Main.url + "swipe/";
    public static GetRequestClient getRequestClient;
    public static ArrayList<Output> postLatencyRecords = new ArrayList<>();

    public static void generatePostRequest() throws InterruptedException {
        final PostRequestClient counter = new PostRequestClient();
        long start = System.currentTimeMillis();
        int numOfRequests = Main.NUMREQUESTS / Main.POST_THREADS;
        int leftoverRequests = Main.NUMREQUESTS % Main.POST_THREADS;
        for (int i = 0; i < Main.POST_THREADS; i++) {
            ApiClient client = new ApiClient();
            SwipeApi apiInstance = new SwipeApi(client);
            client.setBasePath(postURL);

            int finalNumOfReq;
            if (i == Main.POST_THREADS - 1) {
                finalNumOfReq = numOfRequests + leftoverRequests;
            } else {
                finalNumOfReq = numOfRequests;
            }

            Runnable thread = () -> {
                PostRequestThreadBuilder consumer = new PostRequestThreadBuilder(counter, apiInstance);
                consumer.run(finalNumOfReq, postLatencyRecords);
                Main.completed.countDown();
            };
            new Thread(thread).start();
        }
        getRequestClient = new GetRequestClient();
        getRequestClient.run();

        Main.completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;

        double throughput = Main.NUMREQUESTS / timeTakenInSec;
        WriteAndAnalyze wa = new WriteAndAnalyze(postLatencyRecords, Main.NUMREQUESTS);

        results[0] = "Number of successful requests sent is  " + counter.getVal();
        results[1] = "Number of unsuccessful requests is  " + (Main.NUMREQUESTS - counter.getVal().intValue());
        results[2] = "The total run time (wall time) is  " + timeTakenInSec;
        results[3] = "The total throughput in requests per second is  " + throughput;
        results[4] = "Post Mean response time is " + wa.getMeanResTime();
        results[5] = "Post Median response time is " + wa.getMedianResTime();
        results[6] = "Post p99 is " + wa.get99Percentile();
        results[7] = "Minimum and Maximum response times are " + wa.getMinResTime() + ", " + wa.getMaxResTime();
    }

    public void printResults() {
        for (int i = 0; i < 4; i++) {
            System.out.println(results[i]);
        }
        getRequestClient.getLatencyResults();
    }
}
