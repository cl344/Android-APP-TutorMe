package compsci290.edu.duke.tutor.profile;


import android.os.Parcel;
import android.os.Parcelable;

public class CourseTakenDetail implements Parcelable {

    public String mYearTaken;
    public String mWasTA;
    public String mGrade;
    public String mName;

    public CourseTakenDetail(String name, String yearTaken, String wasTA, String grade) {
        this.mWasTA = wasTA;
        this.mYearTaken = yearTaken;
        this.mGrade = grade;
        this.mName = name;
    }

    private CourseTakenDetail(Parcel in) {
        mYearTaken = in.readString();
        mWasTA = in.readString();
        mGrade = in.readString();
        mName = in.readString();
    }

    public static final Creator<CourseTakenDetail> CREATOR = new Creator<CourseTakenDetail>() {
        @Override
        public CourseTakenDetail createFromParcel(Parcel in) {
            return new CourseTakenDetail(in);
        }

        @Override
        public CourseTakenDetail[] newArray(int size) {
            return new CourseTakenDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mYearTaken);
        dest.writeString(mWasTA);
        dest.writeString(mGrade);
        dest.writeString(mName);
    }

    public String getGrade() {
        return mGrade;
    }

    public String getSemester() {
        return mYearTaken;
    }

    public void setName(String name) {
        this.mName = name;
    }
}

