package TwinderClient.GetRequestClient;
import java.util.Random;
import java.util.Timer;

public class GetRequestHelper {
    private static int NUM_IDS = 50000;
    private static Random random = new Random();
    private static Timer timer = new Timer();


    public int getRandomCmd() {
        return random.nextInt(2);
    }

    public String getRandomUserID() {
        return String.valueOf(random.nextInt(NUM_IDS));
    }

}
