package com.mattg.rovergallery.repositories

import android.app.Application
import android.content.Context
import androidx.paging.PagingSource
import androidx.test.core.app.ApplicationProvider
import com.mattg.rovergallery.R
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.network.MarsApi
import com.mattg.rovergallery.network.PhotosPagingSource
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PhotosRepositoryTest {

    val context = ApplicationProvider.getApplicationContext<Context>()
    private val testSol = 500
    private val testRover = "Curiosity"

    @Mock lateinit var api: MarsApi

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun event_dialog_trigger_posts() {

    }


}