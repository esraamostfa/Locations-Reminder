package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database =
            Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
                .build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminders_returnAllReminders() = mainCoroutineRule.runBlockingTest {
        //Given some reminders saved to database

        val reminder1 = ReminderDTO("Title1", "Description", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title3", "Description", "Location", 0.0, 0.0)

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // When reminders are requested from the RemindersRepository
        val result = database.reminderDao().getReminders()

        // Then all reminders are loaded from the database
        assertThat(result.size, `is`(3))
    }

    @Test
    fun saveReminder_saveReminderAndGetItById() = mainCoroutineRule.runBlockingTest {
        // given reminder saved to database.
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)

        // when get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // then the loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllReminders_deleteAllRemindersFromDBAndReturnEmptyList() = mainCoroutineRule.runBlockingTest {
        //Given some reminders saved to database

        val reminder1 = ReminderDTO("Title1", "Description", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2", "Description", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title3", "Description", "Location", 0.0, 0.0)

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        //when call deleteAll
        database.reminderDao().deleteAllReminders()

        //then the the database should be empty

        val result = database.reminderDao().getReminders()

        assertThat(result.size, `is`(0))
    }


}