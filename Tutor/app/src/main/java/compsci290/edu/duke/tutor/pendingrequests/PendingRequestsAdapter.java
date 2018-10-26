package compsci290.edu.duke.tutor.pendingrequests;

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
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;

import java.util.List;

import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Request;
import compsci290.edu.duke.tutor.profile.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.MyViewHolder> {
    private List<Request> requestList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tuteeName;
        public TextView date;
        public TextView comment;
        public Button acceptButton;
        public Button declineButton;
        public CardView cardView;
        public CircleImageView tuteePic;
        public ViewGroup viewGroup;

        public MyViewHolder(View view) {
            super(view);
            tuteeName = (TextView) view.findViewById(R.id.pending_request_card_tutee_name);
            date = (TextView) view.findViewById(R.id.list_desc);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tuteePic = (CircleImageView) view.findViewById(R.id.pending_request_card_image);
            acceptButton = (Button) view.findViewById(R.id.pending_request_card_button_accept);
            declineButton = (Button) view.findViewById(R.id.pending_request_card_button_decline);
            viewGroup = (ViewGroup) view.findViewById(R.id.pending_request_card_button_layout);
            comment = (TextView) view.findViewById(R.id.pending_request_card_comment);
        }
    }


    public PendingRequestsAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.mContext = context;
    }

    @Override
    public PendingRequestsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_card, parent, false);

        return new PendingRequestsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PendingRequestsAdapter.MyViewHolder holder, int position) {
        final Request request = requestList.get(position);
        holder.tuteeName.setText(request.getTuteeName());
        holder.date.setText(request.getSentTime() + " (" + request.getState() + ")");
        if (request.hasChosen()) {
            holder.viewGroup.setVisibility(View.GONE);
        }
        Log.d("PendingRequest", "comment is " + request.getRequestFormComment());
        holder.comment.setText(request.getRequestFormComment());
        holder.acceptButton.setOnClickListener(new RequestResponseOnClickListener(mContext,request,true, holder.viewGroup));
        holder.declineButton.setOnClickListener(new RequestResponseOnClickListener(mContext,request,false, holder.viewGroup));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("tutorId", request.getTutorUserInfoId());
                b.putString("courseId", request.getCourseId());
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });

        loadProfilePicture(holder, position, request);

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void loadProfilePicture(final PendingRequestsAdapter.MyViewHolder holder, int position, AVObject avObject) {

        final String TAG = "PendingRequestAdap";

        Log.d(TAG, "try to load profile pic");
        // load user profile picture on background thread
        AVObject tutee = avObject.getAVObject("tuteeID");
        AVFile pic = (AVFile) tutee.get("picture");
        // safety check if user didn't set profile picture
        if (pic != null) {
            Log.d(TAG, "profile pic is not null");
            pic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if (e == null) {
                        Log.d(TAG, "load profile picture success");
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.tuteePic.setImageBitmap(Bitmap.createBitmap(bmp));
                    } else {
                        Log.d(TAG, "load profile picture error with " + e.toString());
                    }
                }
            });
        }

    }
}

