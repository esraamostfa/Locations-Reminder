package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {


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