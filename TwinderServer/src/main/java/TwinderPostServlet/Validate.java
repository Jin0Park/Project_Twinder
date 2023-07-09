package TwinderPostServlet;// Jin Young Park
// CS6650 Assignment 2

/**
 *  Validate class contains methods isUrlValid, isSwiperValid, isSwipeeValid, isCommentValid. Each method validate
 *  each object based on the given requirements.
 */
public class Validate {
    protected static final int SWIPERIDLIMIT = 50000;
    protected static final int SWIPEEIDLIMIT = 50000;
    protected static final int COMMENTLIMIT = 256;
    protected boolean isUrlValid(String[] urlPath) {
        if (urlPath.length >= 2 && (urlPath[3].equalsIgnoreCase("left") || urlPath[3].equalsIgnoreCase("right"))) {
            return true;
        }
        return false;
    }

    protected boolean isSwiperValid(String swiperId) {
        try {
            if (Integer.parseInt(swiperId) < 1 || Integer.parseInt(swiperId) > SWIPERIDLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isSwipeeValid(String swipeeId) {
        try {
            if (Integer.parseInt(swipeeId) < 1 || Integer.parseInt(swipeeId) > SWIPEEIDLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isCommentValid(String comment) {
        try {
            if (comment.length() < 0 || comment.length() > COMMENTLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
