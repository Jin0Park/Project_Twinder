// Jin Young Park
// CS6650 Assignment 3
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.rabbitmq.client.*;
import org.bson.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.squareup.okhttp.internal.Internal.logger;

public class TwinderConsumer {
    private static final String EXCHANGENAME = "SWIPEEXCHANGE";
    private static final String LIKEDIR = "right";
    private static final int NUMCHANNELS = 200;
    private static final String QUEUENAME = "LIKE";
    private static Channel channel;
    private static String ip;
    private static int port;
    private static int numThreads;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static final String LIKED_SWIPEE_ARRAY = "LikedSwipee";
    private static final String DISLIKED_SWIPEE_ARRAY = "DislikedSwipee";
    private static final String SWIPER_ID_KEY = "SwiperID";
    private static List<WriteModel<Document>> updates = new ArrayList<>();


    public TwinderConsumer(String ip, int port, int numThreads, MongoDatabase database, MongoCollection<Document> collection) {
        this.ip = ip;
        this.port = port;
        this.numThreads = numThreads;
        this.database = database;
        this.collection = collection;
    }

    public static void receive() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("v1");
        factory.setHost(ip);
        factory.setPort(port);

        final Connection connection = factory.newConnection();
        RMQChannelFactory cf = new RMQChannelFactory(connection);
        RMQChannelPool pool = new RMQChannelPool(NUMCHANNELS, cf);

        for (int i = 0; i < numThreads; i++) {
            Runnable runnable = () -> {
                try {
                    channel = pool.borrowObject();
                    channel.queueBind(QUEUENAME, EXCHANGENAME, "");
                    channel.basicQos(250);
                    final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        String[] info = message.split(",");
                        addToDB(info, collection);
                    };
                    channel.basicConsume(QUEUENAME, false, deliverCallback, consumerTag -> {});
                    pool.returnObject(channel);
                } catch (IOException e) {
                    logger.info(e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            new Thread(runnable).start();
        }
        System.out.println("INFO: Queue based pool finished");
    }


    public static void insertNewDocument(String key, String value, boolean like) {
        Set<String> likeSet = new HashSet<>();
        Set<String> dislikeSet = new HashSet<>();

        AtomicInteger likeCount = new AtomicInteger(0);
        AtomicInteger dislikeCount = new AtomicInteger(0);
        if (like) {
            likeCount.getAndIncrement();
            likeSet.add(value);
        } else {
            dislikeCount.getAndIncrement();
            dislikeSet.add(value);
        }
        Document doc = new Document();
        doc.append(SWIPER_ID_KEY, key);
        doc.append(LIKED_SWIPEE_ARRAY, likeSet);
        doc.append(DISLIKED_SWIPEE_ARRAY, likeSet);
        doc.append("LikeCount", likeCount);
        doc.append("DislikeCount", dislikeCount);
        collection.insertOne(doc);
    }

    public static void addToDB(String[] messages, MongoCollection<Document> collection) {
        String swiperID = messages[1];
        String swipeeID = messages[2];
        boolean like = messages[0].equalsIgnoreCase(LIKEDIR);
        Document match = collection.find(Filters.eq(SWIPER_ID_KEY, swiperID)).first();
        if (match == null) {
            insertNewDocument(swiperID, swipeeID, like);
        } else {
            if (like) {
                updates.add(new UpdateOneModel<>(Filters.eq(SWIPER_ID_KEY, swiperID),
                                Updates.combine(Updates.addToSet(LIKED_SWIPEE_ARRAY, swipeeID),
                                        Updates.inc("LikeCount", 1))));
            } else {
                updates.add(new UpdateOneModel<>(Filters.eq(SWIPER_ID_KEY, swiperID),
                        Updates.combine(Updates.addToSet(DISLIKED_SWIPEE_ARRAY, swipeeID),
                                Updates.inc("DisikeCount", 1))));
            }
            if (updates.size() == 500) {
                collection.bulkWrite(updates);
                updates.clear();
            }
        }
    }
}
