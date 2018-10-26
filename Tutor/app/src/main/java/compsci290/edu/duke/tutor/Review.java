package compsci290.edu.duke.tutor;


public class Review {

    public String mComment;
    public String mReviewerId;
    public String mRevieweeId;

    public Review(String comment, String reviewerId, String revieweeId) {
        this.mComment = comment;
        this.mRevieweeId = revieweeId;
        this.mReviewerId = reviewerId;
    }

}
