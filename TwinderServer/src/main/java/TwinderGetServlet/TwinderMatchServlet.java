package TwinderGetServlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;


@WebServlet(name = "TwinderGetServlet.TwinderMatchServlet", value = "/TwinderGetServlet.TwinderMatchServlet")
public class TwinderMatchServlet extends HttpServlet {
    private static final String COLLECTION_NAME = "swipes";
    private static final String MONGO_DB_ID = "jinp4095";
    private static final String MONGO_DB_PW = "Frenchpie02";
    private static final String DB_NAME = "TwinderDB";
    private static final String SWIPER_ID_KEY = "SwiperID";
    protected static final int SUCCESSFULRESCODE = 200;
    protected static final int UNSUCCESSFULRESCODE = 404;
    protected static final int INVALIDRESCODE = 400;
    private static final int OUTPUT_LIMIT = 100;
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
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (urlParts.length < 1) {
            res.setStatus(INVALIDRESCODE);
            res.getOutputStream().print(gson.toJson("Wrong input!"));

        } else {
            String userID = urlParts[1];
            System.out.println(userID);
            Document match = collection.find(Filters.eq(SWIPER_ID_KEY, userID)).first();
            if (match == null) {
                res.setStatus(UNSUCCESSFULRESCODE);
                res.getOutputStream().print(gson.toJson("Invalid User!"));
            } else {
                res.setStatus(SUCCESSFULRESCODE);
                StringBuilder likedSet = getSetInString(match, "LikedSwipee");
                res.getWriter().write("Match List: ");
                res.getWriter().write(String.valueOf(likedSet));
            }
        }
    }

    private StringBuilder getSetInString(Document doc, String key) {
        List<String> elements = doc.getList(key, String.class);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String element : elements) {
            sb.append(element);
            sb.append(",");
        }
        int lastIndex = sb.length() - 1;
        sb.setCharAt(lastIndex, ']');
        return sb;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}