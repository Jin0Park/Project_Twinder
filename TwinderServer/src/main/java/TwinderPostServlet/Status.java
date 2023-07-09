package TwinderPostServlet;// Jin Young Park
// CS6650 Assignment 2

/**
 *  Status class stores each request's swipe decision (left or right), swiper ID, swipee ID, comment.
 */
public class Status {
    private String leftorright;
    private String swiper;
    private String swipee;
    private String comment;

    public String getLeftorright() {
        return leftorright;
    }

    public void setLeftorright(String leftorright) {
        this.leftorright = leftorright;
    }

    public String getSwiper() {
        return swiper;
    }

    public void setSwiper(String swiper) {
        this.swiper = swiper;
    }

    public String getSwipee() {
        return swipee;
    }

    public void setSwipee(String swipee) {
        this.swipee = swipee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
