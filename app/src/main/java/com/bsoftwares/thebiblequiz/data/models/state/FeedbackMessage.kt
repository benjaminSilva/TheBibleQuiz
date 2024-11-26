package com.bsoftwares.thebiblequiz.data.models.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bsoftwares.thebiblequiz.R

sealed class FeedbackMessage(
    private val messageId: Int = 0,
    val messageText: String = "",
    var extraData: Array<out Any> = arrayOf()
) {

    @Composable
    fun get(): String =
        if (messageId != 0) {
            stringResource(id = messageId, formatArgs = extraData)
        } else {
            messageText
        }

    //Error
    object WordNotIntList : FeedbackMessage(R.string.error_word_is_not_in_our_list)
    object InternetIssues : FeedbackMessage(R.string.error_network_issue)
    object RepeatedWord : FeedbackMessage(R.string.error_wordle_repeated_word)
    class WordNotLongEnough(length: Int) :
        FeedbackMessage(R.string.error_word_not_long_enough, extraData = arrayOf(length))

    object NoMessage : FeedbackMessage(R.string.no_error)
    data class Error(val message: String) : FeedbackMessage(messageText = message)

    //Just Feedback
    object CantAddYourself : FeedbackMessage(R.string.feedback_you_cant_add_yourself)
    object UserDoesntExist : FeedbackMessage(R.string.error_profile_user_doesnt_exist)
    object EmptyUser : FeedbackMessage(R.string.error_no_user_id)
    object YouAreFriendsAlready :
        FeedbackMessage(R.string.feedback_you_are_already_friends_with_this_user)

    object YouHaveAlreadySent :
        FeedbackMessage(R.string.feedback_you_have_already_sent_a_friend_request_to_this_user)

    object FriendRequestSent : FeedbackMessage(R.string.feedback_you_have_sent_a_friend_request)
    object FriendRemoved : FeedbackMessage(R.string.feedback_friend_removed)
    object QuestionSuggestionSent: FeedbackMessage(R.string.feedback_question_suggestion_sent)
    object LeagueCreated: FeedbackMessage(R.string.feedback_question_suggestion_sent)
    object LeagueDeleted: FeedbackMessage(R.string.league_was_deleted)
    object FriendInvited: FeedbackMessage(R.string.feedback_friend_invitation)
    object LeagueUpdated: FeedbackMessage(R.string.feedback_league_updated)
    object NoChange: FeedbackMessage(R.string.feedback_no_change)
    object ImageUpdated: FeedbackMessage(R.string.feedback_image_updated)
    object YouAreNowPremium: FeedbackMessage(R.string.feedback_user_premium)
    object YouAreNotPremium: FeedbackMessage(R.string.feedback_user_not_premium)
    object LeftLeagueSuccessfully: FeedbackMessage(R.string.you_ve_left_the_league_successfully)
    object RemovedUserSuccessfully: FeedbackMessage(R.string.you_have_removed_1_successfully)
    object NewDay: FeedbackMessage(R.string.a_new_day_has_started)
}

enum class LogTypes(val message: String) {
    PERMISSION("Permission Issue"),
    LEAGUE_ERROR("League Related Error"),
    FIREBASE_ERROR("Firebase failure error")
}