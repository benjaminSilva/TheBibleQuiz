package com.example.novagincanabiblica.data.models.state

import com.example.novagincanabiblica.R

sealed class FeedbackMessage(val messageId: Int, val extraData: Any? = null) {
    //Error
    object WordNotIntList: FeedbackMessage(R.string.error_word_is_not_in_our_list)
    object InternetIssues: FeedbackMessage(R.string.error_network_issue)
    object RepeatedWord: FeedbackMessage(R.string.error_wordle_repeated_word)
    class WordNotLongEnough(length: Int): FeedbackMessage(R.string.error_network_issue, extraData = length)
    object NoError: FeedbackMessage(R.string.no_error)

    //Just Feedback
    object CantAddYourself: FeedbackMessage(R.string.feedback_you_cant_add_yourself)
    object UserDoesntExist: FeedbackMessage(R.string.error_profile_user_doesnt_exist)
    object YouAreFriendsAlready: FeedbackMessage(R.string.feedback_you_are_already_friends_with_this_user)
    object YouHaveAlreadySent: FeedbackMessage(R.string.feedback_you_have_already_sent_a_friend_request_to_this_user)
    object FriendRequestSent: FeedbackMessage(R.string.feedback_you_have_sent_a_friend_request)
}
