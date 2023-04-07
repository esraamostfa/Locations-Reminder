package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // Subject under test
    private lateinit var viewModel: SaveReminderViewModel

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


        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @After
    fun stopDown() {
        stopKoin()
    }

    @Test
    fun onClear_AllReminderDataIsNull(){
        //with not fresh viewModel
        viewModel.reminderTitle.value = ""
        viewModel.reminderDescription.value = ""
        viewModel.reminderSelectedLocationStr.value = ""
        viewModel.latitude.value = 0.0
        viewModel.longitude.value = 0.0

        //when onClear function is called
        viewModel.onClear()

        //than all reminder data is null
        assertNull(viewModel.reminderTitle.value)
        assertNull(viewModel.reminderDescription.value)
        assertNull(viewModel.reminderSelectedLocationStr.value)
        assertNull(viewModel.selectedPOI.value)
        assertNull(viewModel.latitude.value)
        assertNull(viewModel.longitude.value)

    }

    @Test
    fun validateEnteredData_validatingCompleteDataTrue() {
        val reminderData = ReminderDataItem("Title", "", "Location", 0.0, 0.0)

        viewModel.validateEnteredData(reminderData)

        assertThat(viewModel.validateEnteredData(reminderData), `is`(true))

    }

    @Test
    fun validateEnteredData_validatingNoTitleFalse() {
        val reminderData = ReminderDataItem(null, "", "Location", 0.0, 0.0)

        viewModel.validateEnteredData(reminderData)

        assertThat(viewModel.validateEnteredData(reminderData), `is`(false))

    }

    @Test
    fun validateEnteredData_validatingNoLocationFalse() {
        val reminderData = ReminderDataItem("Title", "", null, 0.0, 0.0)

        viewModel.validateEnteredData(reminderData)

        assertThat(viewModel.validateEnteredData(reminderData), `is`(false))

    }

    @Test
    fun  saveReminder_addNewReminderToDB() {
        runBlocking {

            val reminderData = ReminderDataItem("Title", "", "Location", 0.0, 0.0)

            viewModel.saveReminder(reminderData)

            val savedReminder = dataSource.getReminder(reminderData.id) as Result.Success

            assertThat(reminderData.title, `is`(savedReminder.data.title))
        }

    }

    @Test
    fun validateAndSaveReminder_saveValid() {
        runBlocking {

            val reminderData = ReminderDataItem("Title", "", "Location", 0.0, 0.0)

            viewModel.validateAndSaveReminder(reminderData)

            val savedReminder = dataSource.getReminder(reminderData.id) as Result.Success

            assertThat(reminderData.title, `is`(savedReminder.data.title))
        }
    }


}