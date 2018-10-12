package com.kakeibo;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static int mPauseSecond = 300;

    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        mActivityTestRule.launchActivity(MY_ACTIVITY_INTENT);
    }

    @Test
    public void amountEdtExists() {
        onView(withId(R.id.edt_amount)).check(matches(isDisplayed()));
    }

    @Test
    public void incomeBtnExists() {
        onView(withId(R.id.btn_category1)).check(matches(isDisplayed()));
    }

    @Test
    public void fragmentTransactionText() {
        onView(withId(R.id.edt_amount)).perform(typeText("3.5"));
        pauseTestFor(mPauseSecond);
        onView(withId(R.id.edt_amount)).perform(clearText(), replaceText("13.5"));
        onView(withId(R.id.btn_category1)).perform(click());
        pauseTestFor(mPauseSecond);

        //onData(allOf(is(instanceOf(ExpandableListAdapter.class)), withListItemCheck("13.50")));

        onData(allOf(is(instanceOf(ExpandableListAdapter.class)), withListItemCheck("13.50")))
                .onChildView(withId(R.id.lsv_expandable))
                .atPosition(0)
                .check(matches(isDisplayed()));

//                .perform(longClick());
//                .inAdapterView(withId(R.id.lsv_expandable))
//                .atPosition(0)
//                .check(matches(isDisplayed()));
    }

    private void pauseTestFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Matcher<Object> withListItemCheck(final String check_value) {
        checkNotNull(check_value);
        return new BoundedMatcher<Object, Item>(Item.class){
            private String m_message = "with int amount: ";

            @Override
            public void describeTo (Description d){
                d.appendText(m_message).appendValue(check_value);
            }

            @Override
            public boolean matchesSafely (Item item){
                m_message = "Expected " + item + " and got ";
                if (item == null) {
                    m_message += "empty";
                    return false;
                }
                return item.getBigDecimalAmount().toString().equals(check_value);
            }
        };
    }
}
