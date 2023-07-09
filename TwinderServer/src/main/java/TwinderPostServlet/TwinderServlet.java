package TwinderPostServlet;// Jin Young Park
// CS6650 Assignment 2

/**
 *  TwinderServlet connects Tomcat server to RabbitMQ server by creating connection, channels (channel pool), and exchange.
 *  It sends the requests to RabbitMQ exchange which will be sent to consumers.
 */

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "TwinderServlet", value = "/TwinderServlet")
public class TwinderServlet extends HttpServlet {
    protected Validate validate = new Validate();
    protected static final int SUCCESSFULRESCODE = 201;
    protected static final int UNSUCCESSFULRESCODE = 404;
    protected static final int INVALIDRESCODE = 400;
    private final int NUMOFCHANNEL = 30;
    private final int PORT = 5672;
    private final String RMQSERVERIP = "54.191.95.134";
    protected static RMQChannelPool pool;
    protected Connection connection = null;
    protected ConnectionFactory factory = new ConnectionFactory();
    protected RMQChannelFactory cf = new RMQChannelFactory(connection);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String msg = "Hello World";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        PrintWriter out = response.getWriter();
        out.println("<h1>" + msg + "</h1>");
    }

    @Override
    public void init() throws ServletException {
        super.init();
        factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("v1");
        factory.setHost(RMQSERVERIP);
        factory.setPort(PORT);
        try {
            connection = factory.newConnection();
            cf = new RMQChannelFactory(connection);
            pool = new RMQChannelPool(NUMOFCHANNEL, cf);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String urlPath = request.getPathInfo();

        try {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            String[] urlParts = urlPath.split("/");
            Status status = (Status) gson.fromJson(sb.toString(), Status.class);
            Channel msgChannel = pool.borrowObject();
            String message = "failure";

            msgChannel.exchangeDeclare(pool.EXCHANGENAME, "fanout");

            if (validate.isUrlValid(urlParts)) {
                if (validate.isSwiperValid(status.getSwiper()) && validate.isSwipeeValid(status.getSwipee()) &&
                        validate.isCommentValid(status.getComment())) {
                    response.setStatus(SUCCESSFULRESCODE);
                    response.getOutputStream().print("Write Successful!");
                    message = urlParts[3].toLowerCase() + "," + status.getSwiper() + "," + status.getSwipee() +
                            "," + status.getComment();
                } else {
                    response.setStatus(UNSUCCESSFULRESCODE);
                    response.getOutputStream().print(gson.toJson("Invalid user!"));
                }
            } else {
                response.setStatus(INVALIDRESCODE);
                response.getOutputStream().print(gson.toJson("Wrong input!"));
            }
            msgChannel.basicPublish(pool.EXCHANGENAME, "", null, message.getBytes());
            pool.returnObject(msgChannel);
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.getOutputStream().print(gson.toJson("Wrong input!"));
            response.getOutputStream().flush();
        }
    }
}