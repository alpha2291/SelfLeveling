package com.alpha.selfemployment.Views

import androidx.compose.runtime.State
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreSearchProfileResponse
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreSearchProfileResponseData
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.ChatBEListResponseData
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.GetBlockListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.collections.filterNot

class SharedRepository {

    // ✅ PURE DATA (no UiState)
    private val _reelsItems = MutableStateFlow<List<PostPropertyData>>(emptyList())
    val reelsItems: StateFlow<List<PostPropertyData>> = _reelsItems.asStateFlow()

    private val _articlesItems = MutableStateFlow<List<PostPropertyData>>(emptyList())
    val articlesItems: StateFlow<List<PostPropertyData>> = _articlesItems.asStateFlow()

    // ✅ UI STATE (separate)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error





    fun updateCommentCount(
        user_post_id: Int,
        delta: Int // +1 or -1
    ) {
        _reelsItems.update { list ->
            list.map { post ->
                println("COMMENTS SIZE 000000 -- ${post.total_comments} . $$$ .  ${delta}")
                if (post.user_post_id == user_post_id) {
                    println("COMMENTS SIZE -- ${post.total_comments} . $$$ .  ${delta}")
                    post.copy(
                        total_comments = (post.total_comments + delta).coerceAtLeast(0)
                    )
                } else post
            }
        }

        _articlesItems.update { list ->
            list.map { post ->
                println("COMMENTS SIZE 000000 -- ${post.total_comments} . $$$ .  ${delta}")
                if (post.user_post_id == user_post_id) {
                    println("COMMENTS SIZE -- ${post.total_comments} . $$$ .  ${delta}")
                    post.copy(
                        total_comments = (post.total_comments + delta).coerceAtLeast(0)
                    )
                } else post
            }
        }

    }


    fun getPostByIdFlow(postId: Int): Flow<PostPropertyData?> {
        return combine(
            reelsItems,
            articlesItems
        ) { reels, articles ->

            reels.find { it.user_post_id == postId }
                ?: articles.find { it.user_post_id == postId }
//                ?: common.find { it.user_post_id == postId }

        }.distinctUntilChanged()
    }

    fun setReels(newList: List<PostPropertyData>, isFirstPage: Boolean) {
        _reelsItems.value =
            if (isFirstPage) newList
            else _reelsItems.value + newList
    }

    fun setArticles(newList: List<PostPropertyData>, isFirstPage: Boolean) {
        _articlesItems.value =
            if (isFirstPage) newList
            else _articlesItems.value + newList
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun setError(message: String?) {
        _error.value = message
    }

    fun toggleLike(postId: Int) {
        _reelsItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                    total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }

        _postCommonItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                    total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }


    }


    fun notInterest(postId: Int) {
        _reelsItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                    //total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }

        _postCommonItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                   // total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }


    }



    fun translateDescription(postId: Int, newData: PostPropertyData) {
        _reelsItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) newData else item
        }

        _postCommonItems.value = _postCommonItems.value.map { item ->
            if (item.user_post_id == postId) newData else item
        }
    }

    fun toggleSave(postId: Int) {
        _reelsItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(
                    is_saved = if (item.is_saved == 1) 0 else 1
                )
            } else item
        }
        _postCommonItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(
                    is_saved = if (item.is_saved == 1) 0 else 1
                )
            } else item
        }
    }

    fun reportPost(postId: Int) {
        _reelsItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(is_reported = 1)
            } else item
        }
        _postCommonItems.value = _reelsItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(is_reported = 1)
            } else item
        }
    }

    fun toggleLikeArticles(postId: Int) {
        _articlesItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                    total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }
        _postCommonItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                val isLiked = item.is_liked == 1
                item.copy(
                    is_liked = if (isLiked) 0 else 1,
                    total_likes = if (isLiked) item.total_likes - 1 else item.total_likes + 1
                )
            } else item
        }
    }

    fun toggleSaveArticles(postId: Int) {
        _articlesItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(
                    is_saved = if (item.is_saved == 1) 0 else 1
                )
            } else item
        }
        _postCommonItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(
                    is_saved = if (item.is_saved == 1) 0 else 1
                )
            } else item
        }
    }

    fun reportPostArticles(postId: Int) {
        _articlesItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(is_reported = 1)
            } else item
        }
        _postCommonItems.value = _articlesItems.value.map { item ->
            if (item.user_post_id == postId) {
                item.copy(is_reported = 1)
            } else item
        }
    }


    /// common handle for profile post view , search view

    private var _postCommonItems = MutableStateFlow<List<PostPropertyData>>(emptyList())
    var postCommonItems : StateFlow<List<PostPropertyData>> = _postCommonItems.asStateFlow()

    fun setPostCommonItems(newList: List<PostPropertyData>, isFirstPage: Boolean) {
        _postCommonItems.value =
            if (isFirstPage) newList
            else _postCommonItems.value + newList
    }


    /// profile search
    private val _profileSearch = MutableStateFlow<List<ExploreSearchProfileResponseData>>(emptyList())
    val profileSearch: StateFlow<List<ExploreSearchProfileResponseData>> = _profileSearch.asStateFlow()


    fun setProfileSearch(newList: List<ExploreSearchProfileResponseData>, isFirstPage: Boolean) {
        _profileSearch.value =
            if (isFirstPage) newList
            else _profileSearch.value + newList
    }






    /// chat list
    private val _chatList = MutableStateFlow<List<ChatBEListResponseData>>(emptyList())
    val chatList: StateFlow<List<ChatBEListResponseData>> = _chatList.asStateFlow()


    fun setChatList(newList: List<ChatBEListResponseData>, loadMore: Boolean) {
        _chatList.value = if (loadMore) {
            _chatList.value + newList
        } else {
            newList
        }
    }

    fun clearChatList() {
        _chatList.value = emptyList()
    }



    /// profile posts

    private var _profilePosts = MutableStateFlow<List<PostPropertyData>>(emptyList())
    var profilePosts : StateFlow<List<PostPropertyData>> = _profilePosts.asStateFlow()


    fun setProfilePosts(newList: List<PostPropertyData>, isFirstPage: Boolean) {
        _profilePosts.value =
            if (isFirstPage) newList
            else _profilePosts.value + newList
    }




    private val _blockList = MutableStateFlow<List<GetBlockListData>>(emptyList())
    val blockList: StateFlow<List<GetBlockListData>> = _blockList.asStateFlow()


    fun setBlockedList(newList: List<GetBlockListData>, isFirstPage: Boolean) {
        _blockList.value =
            if (isFirstPage) newList
            else _blockList.value + newList
    }


    fun unblockRemove(userId : Int){
        _blockList.update { it.filterNot { c -> c.user_id == userId } }
    }

    fun removereels(id: Int) {
        _reelsItems.value = _reelsItems.value.filter { reel ->
            reel.user_post_id != id
        }
        _articlesItems.value = _articlesItems.value.filter { reel ->
            reel.user_post_id != id
        }
        _postCommonItems.value = _postCommonItems.value.filter { reel ->
            reel.user_post_id != id
        }
    }


}