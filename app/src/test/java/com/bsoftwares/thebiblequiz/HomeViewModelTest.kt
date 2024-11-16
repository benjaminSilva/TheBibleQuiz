package com.bsoftwares.thebiblequiz

import com.bsoftwares.thebiblequiz.client.GoogleAuthUiClient
import com.bsoftwares.thebiblequiz.data.models.SessionCache
import io.mockk.mockk
import org.junit.Before
import org.junit.Test


class HomeViewModelTest {

    lateinit var googleAuthUiClient: GoogleAuthUiClient
    lateinit var session: SessionCache

    @Before
    fun setup() {
        googleAuthUiClient = mockk<GoogleAuthUiClient>()
        session = mockk<SessionCache>()
    }

    @Test
    fun `Happy path`() {

        /*every {
            googleAuthUiClient.getSignerUser()
        } returns Session(
            data = UserData(
                userId = null, userName = null, profilePictureUrl = null
            ), hasPlayerWordleGame = false, hasPlayedQuizGame = false, dayReset = true
        ).data

        every {
            session.getActiveSession()
        } returns Session(
            data = UserData(
                userId = null, userName = null, profilePictureUrl = null
            ), hasPlayerWordleGame = true, hasPlayedQuizGame = true, dayReset = false
        )

        val signedSession = googleAuthUiClient.getSignerUser()
        val localSession = session.getActiveSession()

        val viewModel = HomeViewModel(googleAuthUiClient, session)
        viewModel.updateSession()
        assert(viewModel.localSession.value.hasPlayedQuizGame == localSession!!.hasPlayedQuizGame)*/

    }
}