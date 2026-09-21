package com.alpha.selfemployment.Views.CommonView.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.domain.CommonRepository
import com.alpha.selfemployment.Views.CommonView.domain.model.CommentResponseData
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CommonViewModel (val repository : CommonRepository , val sharedRepository: SharedRepository): ViewModel(){


    private var _commonAlert = MutableStateFlow(false)
    var commonAlert : StateFlow<Boolean> = _commonAlert.asStateFlow()


    fun enable_Alert(){
        _commonAlert.value = true
    }

    fun disable_Alert(){
        _commonAlert.value = false
    }

    private val _getMainComments = MutableStateFlow<List<CommentResponseData>>(emptyList())
    val getMainComments = _getMainComments.asStateFlow()

    private val _isLoadingMoreMC = MutableStateFlow(false)
    val isLoadingMoreMC = _isLoadingMoreMC.asStateFlow()



    private val _isErrorMC = MutableStateFlow(false)
    val isErrorMC = _isErrorMC.asStateFlow()




    private val _singleLoginMComments = MutableStateFlow(false)
    val singleLoginMComments = _singleLoginMComments.asStateFlow()

    private var currentPageMC = 1
    private var totalPagesMC = Int.MAX_VALUE

    fun loadMainComments(
        user_id: Int,
        user_post_id: Int,
        loadMore: Boolean = false
    ) {
        if (_isLoadingMoreMC.value) return
        if (currentPageMC > totalPagesMC) return

        viewModelScope.launch {
            _isLoadingMoreMC.value = true
            _isErrorMC.value = false

            try {
                val jsonObject = JSONObject().apply {
                    put("user_id", user_id)
                    put("user_post_id", user_post_id)
                    put("page", currentPageMC)
                }

                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                repository.getMainComments(requestBody).collect { result ->
                    when (result) {
                        is ResultHandler.Success -> {
                            _isErrorMC.value = false

                            val response = result.data
                            if (response.result == "5"){
                                _singleLoginMComments.update { true }
                                // GlobalSnackbar.show("Already LoggedIn used this account in another device")
                            }
                            else {
                                _singleLoginMComments.update { false }
                                totalPagesMC = response.totalPages

                                _getMainComments.update { old ->
                                    if (loadMore) old + response.data
                                    else response.data
                                }

                                currentPageMC++
                            }

                        }

                        is ResultHandler.Error -> {

                            _isErrorMC.value = true
                        }
                        else -> Unit
                    }
                }
            } finally {
                _isLoadingMoreMC.value = false
            }
        }
    }


    fun retryComments(user_id: Int , user_post_id: Int){
        currentPageMC = 1
        totalPagesMC = Int.MAX_VALUE
        _isErrorMC.value = false
        _getMainComments.value = emptyList()

        loadMainComments(
            user_id,
            user_post_id,
            false,
        )
    }


    fun resetMC() {
        currentPageMC = 1
        totalPagesMC = Int.MAX_VALUE
        _isErrorMC.value = false
        _getMainComments.value = emptyList()
    }

    /////////////////////////////////////////////


    private val _getReplyComments = MutableStateFlow<List<CommentResponseData>>(emptyList())
    val getReplyComments = _getReplyComments.asStateFlow()

    private val _isLoadingMoreRC = MutableStateFlow(false)
    val isLoadingMoreRC = _isLoadingMoreRC.asStateFlow()



    private val _singleLoginRComments = MutableStateFlow(false)
    val singleLoginRComments = _singleLoginRComments.asStateFlow()


    private val replyPageMap = mutableMapOf<Int, Int>()
    private val replyTotalPageMap = mutableMapOf<Int, Int>()


    fun loadReplyComments(
        user_id: Int,
        user_post_id: Int,
        comment_id: Int,
        loadMore: Boolean = false
    ) {
        val currentPage = replyPageMap[comment_id] ?: 1
        val totalPages = replyTotalPageMap[comment_id] ?: Int.MAX_VALUE

        if (_isLoadingMoreRC.value) return
        if (currentPage > totalPages) return

        viewModelScope.launch {
            _isLoadingMoreRC.value = true

            try {
                val jsonObject = JSONObject().apply {
                    put("user_id", user_id)
                    put("user_post_id", user_post_id)
                    put("comment_id", comment_id)
                    put("page", currentPage)
                }

                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                repository.getReplyComments(requestBody).collect { result ->
                    when (result) {
                        is ResultHandler.Success -> {
                            val response = result.data

                            if (response.result == "5"){

                            }
                            else {
                                replyTotalPageMap[comment_id] = response.totalPages
                                replyPageMap[comment_id] = currentPage + 1

                                _getReplyComments.update { old ->
                                    old + response.data
                                }
                            }


                        }

                        else -> Unit
                    }
                }
            } finally {
                _isLoadingMoreRC.value = false
            }
        }
    }




    fun resetRC() {
        replyPageMap.clear()
        replyTotalPageMap.clear()
        _getReplyComments.value = emptyList()
    }


    /// add comment



    private fun List<CommentResponseData>.replace(
        updated: CommentResponseData
    ): List<CommentResponseData> {
        return map {
            if (it.comment_id == updated.comment_id) updated else it
        }
    }

    private fun List<CommentResponseData>.removeById(
        commentId: Int
    ): List<CommentResponseData> {
        return filterNot { it.comment_id == commentId }
    }

    fun addCommentsApi(
        user_id: Int,
        user_post_id: Int,
        comment: String,
        replies_comment_id: Int,
        status: String, // 1-add, 2-edit, 3-delete
        comment_id: Int,
        mention_id: Int,
        mentionUserName: String = "",
        parentCommentId: Int = 0,
        resultCallback: (ResultHandler<CommentResponseData>) -> Unit
    ) {
        viewModelScope.launch {
            resultCallback(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("comment", comment)
                put("replies_comment_id", replies_comment_id)
                put("status", status)
                put("comment_id", comment_id)
                put("mention_id", mention_id)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repository.addComments(requestBody).collect { result ->
                    when (result) {

                        is ResultHandler.Success -> {
                            val serverComment = result.data.data.firstOrNull()

                            when (status) {

                                // ✅ ADD
                                "1" -> {
                                    if (serverComment == null) return@collect

                                    println("REPLY DATA -- $mentionUserName -- $mention_id ---- $parentCommentId")

                                    // ✅ INCREASE TOTAL COMMENTS (MAIN + REPLY)
                                    sharedRepository.updateCommentCount(
                                        user_post_id = user_post_id,
                                        delta = +1
                                    )


                                    if (mentionUserName.isEmpty() && mention_id == 0 && parentCommentId == 0) {
                                        // MAIN COMMENT → bottom
                                        _getMainComments.update { listOf(serverComment) + it }

                                    }
                                    else {
                                        // REPLY → top

                                        /// ✅ REPLY → update main comment last_reply WITHOUT replacing old ones
                                        _getMainComments.update { list ->
                                            list.map { main ->
                                                if (main.comment_id == parentCommentId) {

                                                    val newLastReply = serverComment
                                                    //.toLastReply()
                                                    val existingReplies = main.last_reply ?: emptyList()

                                                    main.copy(
                                                        last_reply = listOf(newLastReply) + existingReplies
                                                    )
                                                } else {
                                                    main
                                                }
                                            }
                                        }

                                    }



                                    resultCallback(ResultHandler.Success(serverComment))
                                }

                                // ✅ EDIT
                                "2" -> {
                                    if (serverComment == null) return@collect

                                    _getMainComments.update { it.replace(serverComment) }
                                    _getReplyComments.update { it.replace(serverComment) }

                                    resultCallback(ResultHandler.Success(serverComment))
                                }

                                // ✅ DELETE
                                "3" -> {
                                    if (serverComment == null) return@collect

                                    _getMainComments.update { it.removeById(comment_id) }
                                    _getReplyComments.update { it.removeById(comment_id) }


                                    // ✅ DECREASE TOTAL COMMENTS
                                    sharedRepository.updateCommentCount(
                                        user_post_id = user_post_id,
                                        delta = -1
                                    )


                                    resultCallback(ResultHandler.Success(
                                        serverComment
                                    ))
                                }
                            }
                        }

                        is ResultHandler.Error -> {
                            resultCallback(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                resultCallback(ResultHandler.Error(e.message ?: "Unexpected error", e))
            }
        }
    }


    fun deleteCommentApi(
        user_id: Int,
        user_post_id: Int,
        comment_id: Int,
        parent_comment_id: Int
    ) {
        viewModelScope.launch {

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("comment_id", comment_id)
                put("status", "3") // delete
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            repository.addComments(requestBody).collect { result ->
                if (result is ResultHandler.Success) {
                    deleteCommentLocally(
                        commentId = comment_id,
                        parentId = parent_comment_id
                    )

                    // ✅ DECREASE TOTAL COMMENTS
                    sharedRepository.updateCommentCount(
                        user_post_id = user_post_id,
                        delta = -1
                    )

                    toast("Comment Deleted Successfully")

                }
            }
        }
    }


    // -------- DELETE COMMENT LOCALLY --------
    fun deleteCommentLocally(commentId: Int, parentId: Int) {
        if (parentId == 0) {
            _getMainComments.update { it.filterNot { c -> c.comment_id == commentId } }
        } else {
            _getReplyComments.update { it.filterNot { c -> c.comment_id == commentId } }
        }
    }


    //// comment like
    fun commentLikeApi(
        user_id: Int,
        user_post_id: Int,
        comment_id: Int,
        status: String,
        resultCallback: (ResultHandler<PostLikeResponse>) -> Unit
    ) {
        viewModelScope.launch {
            resultCallback(ResultHandler.Loading)
//            _postLike.value = APIResultHandler.Loading

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("user_post_id", user_post_id)
                put("comment_id", comment_id)
                put("status", status)
            }

            val requestBody: RequestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


            try {
                repository.commentLike(requestBody)
                    .collect { result ->
                        resultCallback(result)
                        //  _postLike.value = result

                    }
            } catch (e: Exception) {
                resultCallback(ResultHandler.Error(e.message ?: "Unexpected error", e))
//                _postLike.value = APIResultHandler.Error(e.message ?: "Unexpected error", e)
                // _registerState.value = Event(ResultHandler.Idle)
            }
        }
    }

    fun setCommentLike(user_id: Int, comment_id: Int) {
        _getMainComments.update { list ->
            list.map { post ->

                if (post.user_id == user_id && post.comment_id == comment_id) {

                    val isCurrentlyLiked = post.is_liked == 1
                    val newLikeCount = if (isCurrentlyLiked) {
                        (post.like_count - 1).coerceAtLeast(0)
                    } else {
                        post.like_count + 1
                    }

                    post.copy(
                        is_liked = if (isCurrentlyLiked) 0 else 1,
                        like_count = newLikeCount
                    )

                } else post
            }
        }
    }

    fun setCommentLastReplyLike(userId: Int, replyCommentId: Int) {
        _getMainComments.update { list ->
            list.map { mainComment ->

                val updatedReplies = mainComment.last_reply?.map { reply ->

                    if (reply.user_id == userId && reply.comment_id == replyCommentId) {

                        val isCurrentlyLiked = reply.is_liked == 1
                        val newLikeCount = if (isCurrentlyLiked) {
                            (reply.like_count - 1).coerceAtLeast(0)
                        } else {
                            reply.like_count + 1
                        }

                        reply.copy(
                            is_liked = if (isCurrentlyLiked) 0 else 1,
                            like_count = newLikeCount
                        )

                    } else reply
                }

                mainComment.copy(last_reply = updatedReplies)
            }
        }
    }

    fun setReplyCommentLike(user_id: Int, comment_id: Int) {
        _getReplyComments.update { list ->
            list.map { post ->

                if (post.user_id == user_id && post.comment_id == comment_id) {

                    val isCurrentlyLiked = post.is_liked == 1
                    val newLikeCount = if (isCurrentlyLiked) {
                        (post.like_count - 1).coerceAtLeast(0)
                    } else {
                        post.like_count + 1
                    }

                    post.copy(
                        is_liked = if (isCurrentlyLiked) 0 else 1,
                        like_count = newLikeCount
                    )

                } else post
            }
        }
    }


    fun setCommentReport(comment_id: Int) {
        println("═══════════════════════════════════════════════════")
        println("🔍 setCommentReport called")
        println("📌 Looking for comment_id: $comment_id")

        _getMainComments.value = _getMainComments.value.map { comment ->
            if (comment.comment_id == comment_id) {
                println("   ✅ Updating comment ${comment.comment_id}")
                comment.copy(is_report = 1)
            } else {
                comment
            }
        }

        println("🎯 After update, comment $comment_id has is_report: ${_getMainComments.value.find { it.comment_id == comment_id }?.is_report}")
        println("═══════════════════════════════════════════════════")
    }

    fun setCommentLastReplyReport(replyCommentId: Int) {
        println("═══════════════════════════════════════════════════")
        println("🔍 setCommentLastReplyReport called")
        println("📌 Looking for reply comment_id: $replyCommentId")

        _getMainComments.value = _getMainComments.value.map { mainComment ->
            val updatedReplies = mainComment.last_reply?.map { reply ->
                if (reply.comment_id == replyCommentId) {
                    println("   ✅ Updating last reply ${reply.comment_id}")
                    reply.copy(is_report = 1)
                } else {
                    reply
                }
            }

            if (updatedReplies != mainComment.last_reply) {
                mainComment.copy(last_reply = updatedReplies)
            } else {
                mainComment
            }
        }

        println("═══════════════════════════════════════════════════")
    }

    fun setReplyCommentReport(comment_id: Int) {
        println("═══════════════════════════════════════════════════")
        println("🔍 setReplyCommentReport called")
        println("📌 Looking for reply comment_id: $comment_id")

        _getReplyComments.value = _getReplyComments.value.map { comment ->
            if (comment.comment_id == comment_id) {
                println("   ✅ Updating reply comment ${comment.comment_id}")
                comment.copy(is_report = 1)
            } else {
                comment
            }
        }

        println("═══════════════════════════════════════════════════")
    }



}