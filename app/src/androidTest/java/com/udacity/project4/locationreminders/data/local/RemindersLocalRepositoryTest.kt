package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Class under test
    private lateinit var repository: RemindersLocalRepository

    private lateinit var database: RemindersDatabase


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {

        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
            .allowMainThreadQueries().build()

        // Get a reference to the class under test
        repository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @Test
    fun getReminders_getAllRemindersFromDB()= runBlocking {

            //Given some reminders saved to database

            val reminder1 = ReminderDTO("Title1", "Description", "Location", 0.0, 0.0)
            val reminder2 = ReminderDTO("Title2", "Description", "Location", 0.0, 0.0)
            val reminder3 = ReminderDTO("Title3", "Description", "Location", 0.0, 0.0)

            repository.saveReminder(reminder1)
            repository.saveReminder(reminder2)
            repository.saveReminder(reminder3)

        // When reminders are requested from the RemindersRepository
        val result = repository.getReminders() as Result.Success

        // Then all reminders are loaded from the database
        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun saveReminderRetrieveReminder_saveNewReminderToDBAndRetrieveIt() = runBlocking {

        //Given a new reminder saved to database
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)

        repository.saveReminder(reminder)

        //when retrieve it by id
        val result = repository.getReminder(reminder.id) as Result.Success


        //then the right reminder returned with the right data
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun deleteAllReminders_deleteAllRemindersFromDBAndReturnEmptyList() = runBlocking{
        //Given some reminders saved to database

        val reminder1 = ReminderDTO("Title1", "Description", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title3", "Description", "Location", 0.0, 0.0)

        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        //when call deleteAll
        repository.deleteAllReminders()

        //then the the database should be empty

        val result = repository.getReminders() as Result.Success

        assertThat(result.data.size, `is`(0))


    }


}