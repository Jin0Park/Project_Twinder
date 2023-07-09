package TwinderClient.PostRequestClient;

import TwinderClient.PostRequestHelperMethods.Output;
import TwinderClient.PostRequestHelperMethods.RandomDataGenerator;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.ArrayList;

/**
 *  PostRequestThreadConsumer generates random values for swipe, swiperID, swipeeID, comment. Also, receives response code
 *  from the server.
 *  The methods that each thread will perform for part1.
 */
public class PostRequestThreadBuilder {
    protected static final int SWIPERIDLIMIT = 50000;
    protected static final int SWIPEEIDLIMIT = 50000;
    protected static final int COMMENTLIMIT = 256;
    protected static final int RETRY = 5;
    protected PostRequestClient counter;
    protected SwipeApi apiInstance;

    public PostRequestThreadBuilder(PostRequestClient counter, SwipeApi apiInstance) {
        this.counter = counter;
        this.apiInstance = apiInstance;
    }

    final static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    public void run(int numOfReq, ArrayList<Output> postLatencyRecords){
        for (int j = 0; j < numOfReq; j++) {
            SwipeDetails body = new SwipeDetails();
            Output output = new Output();

            String leftOrRight = randomDataGenerator.randomLeftOrRightGenerator();
            body.setSwiper(randomDataGenerator.randomNumGenerator(1, SWIPERIDLIMIT));
            body.setSwipee(randomDataGenerator.randomNumGenerator(1, SWIPEEIDLIMIT));
            body.setComment(randomDataGenerator.randomStringGenerator(COMMENTLIMIT));
            getResponsePart1(body, leftOrRight, postLatencyRecords, output);
        }
    }
    public void getResponsePart1(SwipeDetails body, String leftOrRight, ArrayList<Output> postLatencyRecords, Output output){
        long startTime = System.currentTimeMillis();
        output.setStartTime(startTime);
        for (int k = 0; k < RETRY; k++) {
            try {
                ApiResponse r = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                long endTime = System.currentTimeMillis();
                output.setLatency(endTime - startTime);
                if (r.getStatusCode() == 201 || r.getStatusCode() == 200) {
                    postLatencyRecords.add(output);
                    counter.inc();
                    break;
                }
            } catch (ApiException e) {
                System.err.println(e.getCode());
                System.err.println(e.getResponseBody());
                e.printStackTrace();
                postLatencyRecords.add(output);
            }
        }
    }
}