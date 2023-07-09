package TwinderGetServlet;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "TwinderGetServlet.TwinderStatsServlet", value = "/TwinderGetServlet.TwinderStatsServlet")
public class TwinderStatsServlet extends HttpServlet {
    private static final String COLLECTION_NAME = "swipes";
    private static final String MONGO_DB_ID = "jinp4095";
    private static final String MONGO_DB_PW = "Frenchpie02";
    private static final String DB_NAME = "TwinderDB";
    private static final String SWIPER_ID_KEY = "SwiperID";
    protected static final int SUCCESSFULRESCODE = 200;
    protected static final int UNSUCCESSFULRESCODE = 404;
    protected static final int INVALIDRESCODE = 400;
    protected static MongoCollection<Document> collection;
    protected static MongoDatabase database;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            MongoDBConnection conn = new MongoDBConnection(MONGO_DB_ID, MONGO_DB_PW, COLLECTION_NAME, DB_NAME);
            database = conn.connectAndGetDatabase();
            collection = conn.getCollection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Gson gson = new Gson();

        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();
        // see the url
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (urlParts.length < 1) {
            res.setStatus(INVALIDRESCODE);
            res.getOutputStream().print(gson.toJson("Wrong input!"));

        } else {
            String userID = urlParts[1];
            Document match = collection.find(Filters.eq(SWIPER_ID_KEY, userID)).first();
            if (match == null) {
                res.setStatus(UNSUCCESSFULRESCODE);
                res.getOutputStream().print(gson.toJson("Invalid User!"));
            } else {
                res.setStatus(SUCCESSFULRESCODE);
                res.getWriter().write("Like count: ");
                res.getWriter().write(String.valueOf(match.get("LikeCount")));
                res.getWriter().write("\nDislike count: ");
                res.getWriter().write(String.valueOf(match.get("DislikeCount")));
            }

        }
    }
    private boolean isUrlValid(String[] urlPath) {
        return (urlPath.length >= 1);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}