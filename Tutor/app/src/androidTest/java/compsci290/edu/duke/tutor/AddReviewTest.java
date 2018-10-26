package compsci290.edu.duke.tutor;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddReviewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void addReviewTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.uemail),
                        withParent(allOf(withId(R.id.uidPWDLayout),
                                withParent(withId(R.id.checkboxHolder)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Me"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.pwd),
                        withParent(allOf(withId(R.id.uidPWDLayout),
                                withParent(withId(R.id.checkboxHolder)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.lgn), withText("Login"),
                        withParent(allOf(withId(R.id.loginbtnHolder),
                                withParent(withId(R.id.loginLinear)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.lgn), withText("Login"),
                        withParent(allOf(withId(R.id.loginbtnHolder),
                                withParent(withId(R.id.loginLinear)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.loginTutee), withText("Login as Tutee"),
                        withParent(allOf(withId(R.id.loginID),
                                withParent(withId(R.id.activity_id)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.profile_info_review), withText("Reviews")));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.profile_review_layout_add), isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.profile_review_add_user_input));
        appCompatEditText3.perform(scrollTo(), replaceText("Good"), closeSoftKeyboard());

        ViewInteraction mDButton = onView(
                allOf(withId(R.id.md_buttonDefaultPositive), withText("Submit"),
                        withParent(allOf(withId(R.id.md_root),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        mDButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.profile_review_card_review_string), withText("Good"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.profile_review_card_view),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Good")));

        ViewInteraction recyclerView6 = onView(
                allOf(withId(R.id.recycler_view), isDisplayed()));
        recyclerView6.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction recyclerView7 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView7.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.profile_info_review), withText("Reviews")));
        appCompatTextView2.perform(scrollTo(), click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.profile_review_card_review_string), withText("Good"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.profile_review_card_view),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Good")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
