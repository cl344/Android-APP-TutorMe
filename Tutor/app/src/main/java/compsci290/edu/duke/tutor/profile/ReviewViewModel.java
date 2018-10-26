package compsci290.edu.duke.tutor.profile;

import compsci290.edu.duke.tutor.Review;


public class ReviewViewModel {

    public String mReviewerName;
    public String mRevieweeName;
    public String mComment;
    public String mReviewerId;
    public String mRevieweeId;
    public String mReviewId;

    public ReviewViewModel(Review review) {
        this(review.mComment, review.mReviewerId, review.mRevieweeId);
    }

    public ReviewViewModel(String comment, String reviewerId, String revieweeId) {
        this.mRevieweeId = revieweeId;
        this.mReviewerId = reviewerId;
        this.mComment = comment;
    }

    public void setReviewerName(String name) {
        this.mReviewerName = name;
    }

    public void setRevieweeName(String name) {
        this.mRevieweeName = name;
    }
}
