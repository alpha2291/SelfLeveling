package com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// ─────────────────────────────────────────────────────────────────────────────
// FIX 3 — FFUiViewModel: fix initCounts so it always sets when called
// Also expose counts correctly
// ─────────────────────────────────────────────────────────────────────────────

class FFUiViewModel : ViewModel() {

    var ff_Profile_TabRow = listOf("Followers", "Following")

    private val _currentTab = MutableStateFlow(FF_Profile_TabRow.FOLLOWERS)
    val currentTab = _currentTab.asStateFlow()

    private val _followersSearch = MutableStateFlow("")
    val followersSearch = _followersSearch.asStateFlow()

    private val _followingSearch = MutableStateFlow("")
    val followingSearch = _followingSearch.asStateFlow()

    private val _followersCount = MutableStateFlow(0)
    val followersCount = _followersCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount = _followingCount.asStateFlow()

    // ✅ No guard — always update when called with real values
    fun initCounts(followers: Int, following: Int) {
        _followersCount.value = followers
        _followingCount.value = following
    }

    fun onFollowSuccess()  { _followingCount.update { (it + 1).coerceAtLeast(0) } }

    fun onUnfollowSuccess(){ _followingCount.update { (it - 1).coerceAtLeast(0) } }
    fun onRemoveSuccess()  { _followersCount.update { (it - 1).coerceAtLeast(0) } }

    fun switchTab(tab: FF_Profile_TabRow) { _currentTab.value = tab }
    fun setFollowersSearch(text: String)  { _followersSearch.value = text }
    fun setFollowingSearch(text: String)  { _followingSearch.value = text }
    fun clearFollowersSearch()            { _followersSearch.value = "" }
    fun clearFollowingSearch()            { _followingSearch.value = "" }
}


class FFUiViewModelokld2 : ViewModel() {

    var ff_Profile_TabRow = listOf("Followers", "Following")

    private val _currentTab = MutableStateFlow(FF_Profile_TabRow.FOLLOWERS)
    val currentTab = _currentTab.asStateFlow()

    private val _followersSearch = MutableStateFlow("")
    val followersSearch = _followersSearch.asStateFlow()

    private val _followingSearch = MutableStateFlow("")
    val followingSearch = _followingSearch.asStateFlow()

    // ── Live counts ───────────────────────────────────────────────────────────
    private val _followersCount = MutableStateFlow(0)
    val followersCount = _followersCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount = _followingCount.asStateFlow()

    // Call once when profile data loads to seed the counts
    fun initCounts(followers: Int, following: Int) {
        if (_followersCount.value == 0 && _followingCount.value == 0) {
            _followersCount.value = followers
            _followingCount.value = following
        }
    }

    // Call after successful follow
    fun onFollowSuccess() {
        _followingCount.value = (_followingCount.value + 1).coerceAtLeast(0)
    }

    // Call after successful unfollow
    fun onUnfollowSuccess() {
        _followingCount.value = (_followingCount.value - 1).coerceAtLeast(0)
    }

    // Call after successful remove (someone removed from your followers)
    fun onRemoveSuccess() {
        _followersCount.value = (_followersCount.value - 1).coerceAtLeast(0)
    }

    fun switchTab(tab: FF_Profile_TabRow) { _currentTab.value = tab }
    fun setFollowersSearch(text: String)  { _followersSearch.value = text }
    fun setFollowingSearch(text: String)  { _followingSearch.value = text }
    fun clearFollowersSearch()            { _followersSearch.value = "" }
    fun clearFollowingSearch()            { _followingSearch.value = "" }
}



class FFUiViewModelOld : ViewModel() {

    var ff_Profile_TabRow = listOf("Followers" , "Following")

    private val _currentTab =
        MutableStateFlow(FF_Profile_TabRow.FOLLOWERS)
    val currentTab = _currentTab.asStateFlow()

    private val _followersSearch = MutableStateFlow("")
    val followersSearch = _followersSearch.asStateFlow()

    private val _followingSearch = MutableStateFlow("")
    val followingSearch = _followingSearch.asStateFlow()

    fun switchTab(tab: FF_Profile_TabRow) {
        _currentTab.value = tab
    }

    fun setFollowersSearch(text: String) {
        _followersSearch.value = text
    }

    fun setFollowingSearch(text: String) {
        _followingSearch.value = text
    }

    fun clearFollowersSearch() {
        _followersSearch.value = ""

    }

    fun clearFollowingSearch() {
        _followingSearch.value = ""

    }
}