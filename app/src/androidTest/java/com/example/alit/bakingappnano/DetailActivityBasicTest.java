package com.example.alit.bakingappnano;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.alit.bakingappnano.recipeDetail.MasterDetailFragment;
import com.example.alit.bakingappnano.recipeDetail.RecipeDetailActivity;
import com.example.alit.bakingappnano.recipeProvider.RecipesTable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by AliT on 10/18/17.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityBasicTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> recipeDetailActivityActivityTestRule =
            new ActivityTestRule<RecipeDetailActivity>(RecipeDetailActivity.class) {

                @Override
                protected Intent getActivityIntent() {

                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(targetContext, RecipeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(RecipesTable._ID, 1);
                    bundle.putString(RecipesTable.NAME, "Nutella Pie");
                    intent.putExtras(bundle);
                    return intent;
                }
            };

    private StepsIdlingResource stepsIdlingResource;

    @Before
    public void registerIdlingResource() {

        stepsIdlingResource = new StepsIdlingResource();

        Espresso.registerIdlingResources(stepsIdlingResource);

    }

    @Test
    public void detailStepClickTest() {

        String firstStepDesc = "Recipe Introduction";

        onView(withId(R.id.stepsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.stepNumText)).check(matches(isDisplayed()));

        onView(withId(R.id.stepNumText)).check(matches(withText(firstStepDesc)));

    }

    @After
    public void unregisterIdlingResource() {

        Espresso.unregisterIdlingResources(stepsIdlingResource);

    }

    public class StepsIdlingResource implements android.support.test.espresso.IdlingResource {

        private volatile ResourceCallback resourceCallback;

        @Override
        public String getName() {
            return recipeDetailActivityActivityTestRule.getActivity().getClass().getName();
        }

        @Override
        public boolean isIdleNow() {
            RecipeDetailActivity detailActivity = recipeDetailActivityActivityTestRule.getActivity();
            MasterDetailFragment masterDetailFragment = (MasterDetailFragment) detailActivity.getSupportFragmentManager().findFragmentById(R.id.masterDetailFragment);
            if (masterDetailFragment != null && masterDetailFragment.stepsAdapter != null) return true;
            else return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }
    }

}
