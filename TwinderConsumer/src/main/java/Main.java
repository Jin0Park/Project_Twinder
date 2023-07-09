// Jin Young Park
// CS6650 Assignment 3
public class Main {
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 200;
    private static final String IP = "34.221.182.150";
    private static final String COLLECTION_NAME = "swipes";
    private static final String MONGO_DB_ID = "jinp4095";
    private static final String MONGO_DB_PW = "Frenchpie02";
    private static final String DB_NAME = "TwinderDB";


    public static void main(String[] argv) throws Exception {
        MongoDBConnection conn = new MongoDBConnection(MONGO_DB_ID, MONGO_DB_PW, COLLECTION_NAME, DB_NAME);
        TwinderConsumer consumer = new TwinderConsumer(IP, PORT, NUMTHREADS, conn.connectAndGetDatabase(), conn.getCollection());
        consumer.receive();
    }
}
