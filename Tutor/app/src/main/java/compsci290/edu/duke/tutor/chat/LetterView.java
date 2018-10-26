package compsci290.edu.duke.tutor.chat;

/**
 * Created by user on 4/28/17.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Send MemberLetterEvent OnClick
 * Need to handle receiving part
 */
public class LetterView extends LinearLayout {

    public LetterView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        updateLetters();
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        updateLetters();
    }

    private void updateLetters() {
        setLetters(getSortLetters());
    }

    /**
     * setting scrolling letters
     */
    public void setLetters(List<Character> letters) {
        removeAllViews();
        for(Character content : letters) {
            TextView view = new TextView(getContext());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
            view.setLayoutParams(param);
            view.setText(content.toString());
            addView(view);
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());
                for (int i = 0; i < getChildCount(); i++) {
                    TextView child = (TextView) getChildAt(i);
                    if (y > child.getTop() && y < child.getBottom()) {
                        MemberLetterEvent letterEvent = new MemberLetterEvent();
                        letterEvent.letter = child.getText().toString().charAt(0);
                        EventBus.getDefault().post(letterEvent);
                    }
                }
                return true;
            }
        });
    }

    /**
     * Letter from A-Z
     */
    private List<Character> getSortLetters() {
        List<Character> letterList = new ArrayList<Character>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letterList.add(c);
        }
        return letterList;
    }
}