package com.example.alit.bakingappnano;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.alit.bakingappnano.main.MainActivity;
import com.example.alit.bakingappnano.myDatastructures.RecipeDescription;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by AliT on 10/17/17.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    @Rule
    public IntentsTestRule<MainActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(MainActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {

        idlingResource = new IdlingResource();

        registerIdlingResources(idlingResource);

    }

    @Test
    public void verifyIntentData() {

        String recipeName = "Nutella Pie";

        onView(withId(R.id.recipeRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(allOf(
                isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))
        )).check(matches(withText(recipeName)));

    }

    @After
    public void unregisterIdlingResource() {

        Espresso.unregisterIdlingResources(idlingResource);

    }

    public class IdlingResource implements android.support.test.espresso.IdlingResource {

        private volatile ResourceCallback resourceCallback;

        @Override
        public String getName() {
            return mainActivityIntentsTestRule.getActivity().getClass().getName();
        }

        @Override
        public boolean isIdleNow() {
            ArrayList<RecipeDescription> recipeDescriptions = mainActivityIntentsTestRule.getActivity().recipeDescriptions;
            if (recipeDescriptions != null && !recipeDescriptions.isEmpty()) return true;
            else return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }
    }

}
