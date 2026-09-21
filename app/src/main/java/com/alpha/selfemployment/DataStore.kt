package com.alpha.selfemployment

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore



object PreferenceKeys {

    val USER_ID = intPreferencesKey("user_id")

    val OTP = "otp"

    val USER_TOKEN = stringPreferencesKey("user_token")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val INTEREST_COMPLETED = intPreferencesKey("interest_completed")
    val LOCATION_RECEIVED = intPreferencesKey("location_received")
    val POST_ID = intPreferencesKey("post_id")

    val LAT = stringPreferencesKey("lat")
    val LON = stringPreferencesKey("lon")

    val VERSION = intPreferencesKey("version")
    val TIMESTAMP = longPreferencesKey("timestamp")
    val SKIP_COUNT = intPreferencesKey("skip_count")

    val VERIFY_COMPLETE = intPreferencesKey("verify_complete")
    val PROFILE_IMAGE = stringPreferencesKey("profile_image")

    val NAME = stringPreferencesKey("name")
    val REAL_NAME = stringPreferencesKey("real_name")
    val EMAIL = stringPreferencesKey("email")
    val BIO = stringPreferencesKey("bio")

    val PINCODE = stringPreferencesKey("pincode")
    val STATE = stringPreferencesKey("state")
    val COUNTRY = stringPreferencesKey("country")

    val LAST_UPDATE_POPUP_TIME = longPreferencesKey("last_update_popup_time")
}



class AppPreferences(
    context: Context
)
{

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    /* ----------------- SAVE ----------------- */

    fun saveOnboardingCompleted(done: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", done).apply()
    }

    fun saveLanguageCompleted(done: Boolean) {
        prefs.edit().putBoolean("language_completed", done).apply()
    }

    fun saveOtp(otp : String) {
        prefs.edit().putString(PreferenceKeys.OTP, otp).apply()
    }



    fun save_Dob(dob : Long){
        prefs.edit().putLong("user_Dob", dob).apply()
    }



    fun saveUserToken(token: String) {
        prefs.edit().putString("user_token", token).apply()
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt("user_id", id).apply()
    }

    fun savePhoneNumber(id: String) {
        prefs.edit().putString("phone_number", id).apply()
    }

    fun saveLocationPage(id: Int){
        prefs.edit().putInt("location_page", id).apply()
    }

    fun save_NotificationEnabled(value : Boolean){
        prefs.edit().putBoolean("notificationEnabled", value).apply()
    }

    fun saveLatLong(lat: String, lon: String) {
        prefs.edit()
            .putString("lat", lat)
            .putString("lon", lon)
            .apply()
    }

    fun saveUpdatePopupDismissTime() {
        prefs.edit()
            .putLong("last_update_popup_time", System.currentTimeMillis())
            .apply()
    }




    fun saveProfileImage(profile_image : String){
        prefs.edit()
            .putString("profile_image", profile_image)
            .apply()
    }

    fun save_UserName(username : String){
        prefs.edit()
            .putString("username", username)
            .apply()
    }

    fun saveRealName(realname : String){
        prefs.edit()
            .putString("realname", realname)
            .apply()
    }

    fun saveBio(bio : String){
        prefs.edit()
            .putString("bio", bio)
            .apply()
    }

    fun saveLocation(location : String){
        prefs.edit()
            .putString("userLocation", location)
            .apply()
    }

    fun saveCurrentPostId(id : Int){
        prefs.edit()
            .putInt("currentPostId", id)
            .apply()
    }

    /// user address

    fun saveUserPincode(location : String){
        prefs.edit()
            .putString("userPincode", location)
            .apply()
    }

    fun saveUserState(location : String){
        prefs.edit()
            .putString("userState", location)
            .apply()
    }

    fun save_AppLanguage(location : String){
        prefs.edit()
            .putString("appLanguage", location)
            .apply()
    }


    /* ----------------- GET ----------------- */

    fun getOnboardingCompleted(): Boolean =
        prefs.getBoolean("onboarding_completed", false)

    fun getLanguageCompleted(): Boolean =
        prefs.getBoolean("language_completed", false)


    fun getOTP() : String =
        prefs.getString(PreferenceKeys.OTP , "") ?: ""


    fun getUserToken(): String =
        prefs.getString("user_token", "") ?: ""

    fun getUserId(): Int =
        prefs.getInt("user_id", -1)

    fun getPhoneNumber(): String =
        prefs.getString("phone_number", "") ?: ""

    fun getLocationPage() : Int =
        prefs.getInt("location_page" , 0)


    fun getProfileImage(): String =
        prefs.getString("profile_image", "") ?: ""

    fun getUserName(): String =
        prefs.getString("username", "") ?: ""

    fun getRealName(): String =
        prefs.getString("realname", "") ?: ""


    fun getUserLocation(): String =
        prefs.getString("userLocation", "") ?: ""


    fun getBio(): String =
        prefs.getString("bio", "") ?: ""


    fun getCurrentPostId() : Int =
        prefs.getInt("currentPostId" , 0)


    fun getNotificationEnable() : Boolean =
        prefs.getBoolean("notificationEnabled" , false)


    /// user address

    fun getPincode(): String =
        prefs.getString("userPincode", "") ?: ""


    fun get_Dob() : Long =
        prefs.getLong("user_Dob" , 0L) ?: 0L

    fun getState(): String =
        prefs.getString("userState", "") ?: ""



    fun getAppLanguage(): String =
        prefs.getString("appLanguage", "en") ?: "en"



//    fun getLatLong(): Pair<String, String> =
//        prefs.getString("lat", "") to
//                prefs.getString("lon", ""
//                )

    /* ----------------- UPDATE POPUP LOGIC ----------------- */

    fun shouldShowUpdatePopup(): Boolean {
        val lastTime = prefs.getLong("last_update_popup_time", 0L)
        if (lastTime == 0L) return true

        val fiveDays = 5 * 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - lastTime >= fiveDays
    }

    /* ----------------- CLEAR ----------------- */



    fun clearAll() {

        val onboardingCompleted = getOnboardingCompleted()

        prefs.edit().clear().apply()

        // restore onboarding flag
        prefs.edit()
            .putBoolean("onboarding_completed", onboardingCompleted)
            .apply()
    }

}

