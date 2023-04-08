package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setupViewModel() {

        dataSource = FakeDataSource()
        val reminder1 = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description1", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title3", "Description1", "Location", 0.0, 0.0)

        runBlocking {
            dataSource.saveReminder(reminder1)
            dataSource.saveReminder(reminder2)
            dataSource.saveReminder(reminder3)
        }


        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @After
    fun stopDown() {
        stopKoin()
    }

    // TODO debug
    @Test
    fun loadReminders_checkLoading() = runBlocking{
        //Given some reminders saved to database
        dataSource.saveReminder(ReminderDTO(
            "title", "description", "location",0.0,0.0
        ))


        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the view model.
        viewModel.loadReminders()

        // Then assert that the progress indicator is shown.
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun loadReminders_allSavedRemindersLoaded(){
        runBlocking {

            val data = dataSource.getReminders() as Result.Success
            val remindersData = data.data.map{
                    reminder ->
                //map the reminder data from the DB
                ReminderDataItem(
                    reminder.title,
                    reminder.description,
                    reminder.location,
                    reminder.latitude,
                    reminder.longitude,
                    reminder.id
                )
            }.toList()

            // When call the loadReminders function
            viewModel.loadReminders()

            // Then all reminders are loaded
            assertThat(viewModel.remindersList.value!!.size,  `is`( remindersData.size))
            assertThat(viewModel.showNoData.value, `is` (false))
            assertThat(viewModel.showLoading.value, `is` (true))
        }
    }

    @Test
    fun loadRemindersWhenRemindersAreUnavailable_callErrorToDisplay(){
        // Given datasource return errors.
        dataSource.setReturnError(true)

        // When load reminders
        viewModel.loadReminders()

        // Then showSnackBar is true
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("Test exception"))
        }

    @Test
    fun invalidateShowNoData_showNowDataWhenRemindersListEmpty(){
        // When there is no reminders
        runBlocking {
            dataSource.deleteAllReminders()
        }

        //then showNoData is true
        viewModel.invalidateShowNoData()
         assertThat(viewModel.showNoData.value, `is` (true))
         assertThat(viewModel.remindersList.value.isNullOrEmpty(), `is` (true))
    }



}