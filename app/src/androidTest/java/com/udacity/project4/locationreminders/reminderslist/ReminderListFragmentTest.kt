package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupTest() {
        dataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(getApplicationContext(), dataSource)
        stopKoin()
        val myModule = module {
            single {
                viewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @After
    fun stopDown(){
        stopKoin()
    }


    //    TODO: test the navigation of the fragments.
    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment() = runBlockingTest {

        // given - on the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // when click on add FAB button
        Espresso.onView(withId(R.id.addReminderFAB))
            .perform(ViewActions.click())

        // then verify that we navigate to the SaveReminderFragment
        Mockito.verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }

    @Test
    fun addedReminders_DisplayedInUi() = runBlockingTest {
        // given add 3 reminders to the DB
        val reminder1 = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description1", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title3", "Description1", "Location", 0.0, 0.0)

        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.saveReminder(reminder3)

        //when reminderListFragment launched to display reminders list
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // then all reminders are displayed on the screen
        Espresso.onView(withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(CoreMatchers.not(isDisplayed())))
        Espresso.onView(withId(R.id.reminderssRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder1.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder2.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminder3.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun noReminders_DisplayedInUi() = runBlockingTest {
        // given no reminders added to the DB
        dataSource.deleteAllReminders()

        // when ReminderListFragment launched to display reminders list
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)

        // then No Data message is displayed on the screen
        Espresso.onView(withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(isDisplayed()))
    }

}