import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    private static String mongodbID;
    private static String mongodbPW;
    private static String collectionName;
    private static String DBName;

    public MongoDBConnection(String mongodbID, String mongodbPW, String collectionName, String DBName) {
        this.mongodbID = mongodbID;
        this.mongodbPW = mongodbPW;
        this.collectionName = collectionName;
        this.DBName = DBName;
    }
    public static String createConnectionString() {
        String conStr = "mongodb+srv://" + mongodbID + ":" + mongodbPW + "@twinderdb.d1wrmgt.mongodb.net/?retryWrites=true&w=majority";
        return conStr;
    }

    public static MongoDatabase connectAndGetDatabase() {
        ConnectionString connectionString = new ConnectionString(createConnectionString());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(DBName);
        return database;
    }

    public static MongoCollection<Document> getCollection() {
        MongoDatabase database = connectAndGetDatabase();
        if (database.getCollection(collectionName) == null) {
            database.createCollection(collectionName);
        }
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }
}
