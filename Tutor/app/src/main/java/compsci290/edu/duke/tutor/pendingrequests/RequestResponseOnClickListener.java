package compsci290.edu.duke.tutor.pendingrequests;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import compsci290.edu.duke.tutor.model.Request;
import compsci290.edu.duke.tutor.model.Session;


public class RequestResponseOnClickListener implements View.OnClickListener {

    public Context mContext;
    public Request mRequest;
    public boolean isAccpet;
    public ViewGroup viewGroup;

    public RequestResponseOnClickListener(Context context, Request request, boolean isAccept, ViewGroup viewGroup) {
        this.mContext = context;
        this.mRequest = request;
        this.isAccpet = isAccept;
        this.viewGroup = viewGroup;
    }

    @Override
    public void onClick(View v) {
        viewGroup.setVisibility(View.GONE);
        if (isAccpet) {
            onAccept();
        } else {
            onDecline();
        }
    }

    /*
    * update request status to accepted
    * create session and send it to server
    * */
    private void onAccept() {
        AVObject currentRequest = AVObject.createWithoutData("Request", mRequest.getObjectId());
        currentRequest.put("hasChosen", true);
        currentRequest.put("isUpdated", true);
        currentRequest.put("isUpdatedRequestSent", true);
        currentRequest.put("state", "accept");
        currentRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("update request accept", " success");
                } else {
                    Log.d("update request accept", " fail with " + e.toString());
                }

            }
        });
        AVACL acl = new AVACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        Session newSession = new Session(mRequest);
        newSession.setTuteeID(mRequest.getTuteeId());
        newSession.setTutorID(mRequest.getTutorId());
        newSession.setACL(acl);
        newSession.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("save new session", " on success");
                } else {
                    Log.d("save new session", " on failure with " + e.toString());
                }
            }
        });

        Toast.makeText(mContext, "You have accepted the tutor request", Toast.LENGTH_SHORT).show();
    }

    /*
    * update hasChosen to be true
    * mark choice as decline
    * save object in background
    * */
    private void onDecline() {
        AVObject currentRequest = AVObject.createWithoutData("Request", mRequest.getObjectId());
        currentRequest.put("hasChosen", true);
        currentRequest.put("isUpdated", true);
        currentRequest.put("isUpdatedRequestSent", true);
        currentRequest.put("state", "decline");
        currentRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("update request decline ", " success");
                } else {
                    Log.d("update request decline ", " fail with " + e.toString());
                }

            }
        });
        Toast.makeText(mContext, "You have declined the tutor request", Toast.LENGTH_SHORT).show();
    }
}
