package compsci290.edu.duke.tutor.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;

import java.util.List;

import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.profile.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class AvailableTutorsRecyclerViewAdapter extends RecyclerView.Adapter<AvailableTutorsRecyclerViewAdapter.MyViewHolder> {

    private List<RecyclerSource> RecyclerList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tutorName;
        public CardView cardView;
        public CircleImageView profilePic;

        public MyViewHolder(View view) {
            super(view);
            tutorName = (TextView) view.findViewById(R.id.firstName);
            cardView = (CardView) view.findViewById(R.id.card_view);
            profilePic = (CircleImageView) view.findViewById(R.id.list_avatar);
        }
    }


    public AvailableTutorsRecyclerViewAdapter(List<RecyclerSource> RecyclerList, Context context) {
        this.RecyclerList = RecyclerList;
        this.mContext = context;
    }

    @Override
    public AvailableTutorsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_row, parent, false);

        return new AvailableTutorsRecyclerViewAdapter.MyViewHolder(itemView);
    }

    //Set click listener to cardview and start intent on click
    @Override
    public void onBindViewHolder(AvailableTutorsRecyclerViewAdapter.MyViewHolder holder, int position) {
        final RecyclerSource availableTutor = RecyclerList.get(position);
        holder.tutorName.setText(availableTutor.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("tutorID", availableTutor.getTutorId());
                b.putString("courseID", availableTutor.getCourseId());
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });
        loadProfilePicture(holder, availableTutor);
    }

    //Get size of list
    @Override
    public int getItemCount() {
        return RecyclerList.size();
    }

    //Load profile picture from database
    public void loadProfilePicture(final MyViewHolder holder, RecyclerSource tutorInfo) {

        final String TAG = "AvailableTutorAda";

        AVObject userInfo = AVObject.createWithoutData("UserInfo", tutorInfo.getTutorId());
        userInfo.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                Log.d(TAG, "try to load profile pic");
                // load user profile picture on background thread
                AVFile pic = (AVFile) avObject.get("picture");
                // safety check if user didn't set profile picture
                if (pic != null) {
                    Log.d(TAG, "profile pic is not null");
                    pic.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            if (e == null) {
                                Log.d(TAG, "load profile picture success");
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.profilePic.setImageBitmap(Bitmap.createBitmap(bmp));
                            } else {
                                Log.d(TAG, "load profile picture error with " + e.toString());
                            }
                        }
                    });
                }
            }
        });
    }
}
