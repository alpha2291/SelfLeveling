package com.alpha.selfemployment.Views.PostUpload.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VideoPostUploadFields(
    val category: String = "",
    val specificCategory: String = "",
    val videoUrl: String = "",
    val language: String = "",
    val title: String = "",
    val shortDesc: String = "",
    val longDesc: String = ""
)

data class ArticlePostUploadFields(
    val category: String = "",
    val specificCategory: String = "",
    val images: List<String> = emptyList(),
    val title: String = "",
    val shortDesc: String = "",
    val longDesc: String = ""
)

data class YoutubeVideoPostUploadFields(
    val category: String = "",
    val specificCategory: String = "",
    val language: String = "",
    val youtubeLink: String = "",
    val title: String = "",
    val shortDesc: String = "",
    val longDesc: String = ""
)
data class ArticlePostUploadErrors(
    val category: String? = null,
    val specificCategory: String? = null,
    val images: String? = null,
    val title: String? = null,
    val shortDesc: String? = null,
    val longDesc: String? = null
)

data class YoutubeVideoPostUploadErrors(
    val category: String? = null,
    val specificCategory: String? = null,
    val language: String? = null,
    val youtubeLink: String? = null,
    val title: String? = null,
    val shortDesc: String? = null,
    val longDesc: String? = null
)

data class VideoPostUploadErrors(
    val category: String? = null,
    val specificCategory: String? = null,
    val videoUrl: String? = null,
    val language: String? = null,
    val title: String? = null,
    val shortDesc: String? = null,
    val longDesc: String? = null
)





class PostUploadUIViewModel : ViewModel() {

    private val _videoPostFields = MutableStateFlow(VideoPostUploadFields())
    val videoPostFields: StateFlow<VideoPostUploadFields> = _videoPostFields.asStateFlow()

    private val _videoPostErrors = MutableStateFlow(VideoPostUploadErrors())
    val videoPostErrors: StateFlow<VideoPostUploadErrors> = _videoPostErrors.asStateFlow()

    private val _showVideoErrors = MutableStateFlow(false)
    val showVideoErrors: StateFlow<Boolean> = _showVideoErrors.asStateFlow()

    fun setVideoUri(uri: Uri?) {
        _videoPostFields.update {
            it.copy(videoUrl = uri?.toString().orEmpty())
        }
        _videoPostErrors.update { it.copy(videoUrl = null) }
    }


    fun updateVideoCategory(value: String) {
        _videoPostFields.update {
            it.copy(
                category = value,
                specificCategory = if (value == "Others") it.specificCategory else ""
            )
        }
        _videoPostErrors.update {
            it.copy(category = null, specificCategory = null)
        }
    }


    fun updateSpecificCategory(value: String) {
        _videoPostFields.update { it.copy(specificCategory = value) }
        _videoPostErrors.update { it.copy(specificCategory = null) }
    }

    fun updateVideoLanguage(value: String) {
        _videoPostFields.update { it.copy(language = value) }
        _videoPostErrors.update { it.copy(language = null) }
    }

    fun updateVideoSpecificCategory(value: String) {
        _videoPostFields.update { it.copy(specificCategory = value) }
        _videoPostErrors.update { it.copy(specificCategory = null) }
    }

    fun updateVideoUrl(value: String) {
        _videoPostFields.update { it.copy(videoUrl = value) }
        _videoPostErrors.update { it.copy(videoUrl = null) }
    }

    fun updateVideoTitle(value: String) {
        _videoPostFields.update { it.copy(title = value) }
        _videoPostErrors.update { it.copy(title = null) }
    }

    fun updateVideoShortDesc(value: String) {
        _videoPostFields.update { it.copy(shortDesc = value) }
        _videoPostErrors.update { it.copy(shortDesc = null) }
    }

    fun updateVideoLongDesc(value: String) {
        _videoPostFields.update { it.copy(longDesc = value) }
        _videoPostErrors.update { it.copy(longDesc = null) }
    }

    fun isVideoFormValid(): Boolean {
        val fields = _videoPostFields.value
        val needsSpecificCategory = fields.category.equals("Others", ignoreCase = true)

        return fields.category.isNotBlank() &&
                fields.videoUrl.isNotBlank() &&
                fields.language.isNotBlank() &&
                fields.title.isNotBlank() &&
                fields.shortDesc.isNotBlank() &&
                fields.longDesc.isNotBlank() &&
                (!needsSpecificCategory || fields.specificCategory.isNotBlank())
    }

    fun validateVideoForm(): Boolean {
        _showVideoErrors.value = true

        val fields = _videoPostFields.value
        val isOthers = fields.category.equals("Others", ignoreCase = true)

        val errors = VideoPostUploadErrors(
            category = if (fields.category.isBlank()) "Select category" else null,
            specificCategory = if (isOthers && fields.specificCategory.isBlank()) "Enter your category" else null,
            videoUrl = if (fields.videoUrl.isBlank()) "Upload video" else null,
            language = if (fields.language.isBlank()) "Select language" else null,
            title = if (fields.title.isBlank()) "Enter valid title" else null,
            shortDesc = if (fields.shortDesc.isBlank()) "Enter short description" else null,
            longDesc = if (fields.longDesc.isBlank()) "Enter long description" else null
        )

        _videoPostErrors.value = errors

        return listOf(
            errors.category,
            errors.specificCategory,
            errors.videoUrl,
            errors.language,
            errors.title,
            errors.shortDesc,
            errors.longDesc
        ).all { it == null }
    }

    //// article

    private val _articlePostFields = MutableStateFlow(ArticlePostUploadFields())
    val articlePostFields: StateFlow<ArticlePostUploadFields> = _articlePostFields.asStateFlow()

    private val _articlePostErrors = MutableStateFlow(ArticlePostUploadErrors())
    val articlePostErrors: StateFlow<ArticlePostUploadErrors> = _articlePostErrors.asStateFlow()

    private val _showArticleErrors = MutableStateFlow(false)
    val showArticleErrors: StateFlow<Boolean> = _showArticleErrors.asStateFlow()


    fun updateArticleCategory(value: String) {
        _articlePostFields.update {
            it.copy(
                category = value,
                specificCategory = if (value == "Others") it.specificCategory else ""
            )
        }
        _articlePostErrors.update {
            it.copy(category = null, specificCategory = null)
        }
    }

    fun updateArticleSpecificCategory(value: String) {
        _articlePostFields.update { it.copy(specificCategory = value) }
        _articlePostErrors.update { it.copy(specificCategory = null) }
    }

    fun updateArticleTitle(value: String) {
        _articlePostFields.update { it.copy(title = value) }
        _articlePostErrors.update { it.copy(title = null) }
    }

    fun updateArticleShortDesc(value: String) {
        _articlePostFields.update { it.copy(shortDesc = value) }
        _articlePostErrors.update { it.copy(shortDesc = null) }
    }

    fun updateArticleLongDesc(value: String) {
        _articlePostFields.update { it.copy(longDesc = value) }
        _articlePostErrors.update { it.copy(longDesc = null) }
    }

    fun addArticleImage(uri: Uri) {
        _articlePostFields.update {
            it.copy(images = it.images + uri.toString())
        }
        _articlePostErrors.update { it.copy(images = null) }
    }

    fun removeArticleImage(image: String) {
        _articlePostFields.update {
            it.copy(images = it.images.filterNot { img -> img == image })
        }
    }

    fun clearArticleForm() {
        _articlePostFields.value = ArticlePostUploadFields()
        _articlePostErrors.value = ArticlePostUploadErrors()
        _showArticleErrors.value = false
    }

    fun isArticleFormValid(): Boolean {
        val fields = _articlePostFields.value
        val needsSpecificCategory = fields.category.equals("Others", ignoreCase = true)

        return fields.category.isNotBlank() &&
                fields.title.isNotBlank() &&
                fields.images.isNotEmpty() &&
                fields.shortDesc.isNotBlank() &&
                fields.longDesc.isNotBlank() &&
                (!needsSpecificCategory || fields.specificCategory.isNotBlank())
    }

    fun validateArticleForm(): Boolean {
        _showArticleErrors.value = true

        val fields = _articlePostFields.value
        val isOthers = fields.category.equals("Others", ignoreCase = true)

        val errors = ArticlePostUploadErrors(
            category = if (fields.category.isBlank()) "Select category" else null,
            specificCategory = if (isOthers && fields.specificCategory.isBlank()) "Enter your category" else null,
            images = if (fields.images.isEmpty()) "Upload at least one photo" else null,
            title = if (fields.title.isBlank()) "Enter valid title" else null,
            shortDesc = if (fields.shortDesc.isBlank()) "Enter short description" else null,
            longDesc = if (fields.longDesc.isBlank()) "Enter long description" else null
        )

        _articlePostErrors.value = errors

        return listOf(
            errors.category,
            errors.specificCategory,
            errors.images,
            errors.title,
            errors.shortDesc,
            errors.longDesc
        ).all { it == null }
    }


    //// youtube video opload

    private val _youtubePostFields = MutableStateFlow(YoutubeVideoPostUploadFields())
    val youtubePostFields: StateFlow<YoutubeVideoPostUploadFields> = _youtubePostFields.asStateFlow()

    private val _youtubePostErrors = MutableStateFlow(YoutubeVideoPostUploadErrors())
    val youtubePostErrors: StateFlow<YoutubeVideoPostUploadErrors> = _youtubePostErrors.asStateFlow()

    private val _showYoutubeErrors = MutableStateFlow(false)
    val showYoutubeErrors: StateFlow<Boolean> = _showYoutubeErrors.asStateFlow()

    fun updateYoutubeCategory(value: String) {
        _youtubePostFields.update {
            it.copy(
                category = value,
                specificCategory = if (value == "Others") it.specificCategory else ""
            )
        }
        _youtubePostErrors.update {
            it.copy(category = null, specificCategory = null)
        }
    }

    fun updateYoutubeSpecificCategory(value: String) {
        _youtubePostFields.update { it.copy(specificCategory = value) }
        _youtubePostErrors.update { it.copy(specificCategory = null) }
    }

    fun updateYoutubeLanguage(value: String) {
        _youtubePostFields.update { it.copy(language = value) }
        _youtubePostErrors.update { it.copy(language = null) }
    }

    fun updateYoutubeLink(value: String) {
        _youtubePostFields.update { it.copy(youtubeLink = value) }
        _youtubePostErrors.update { it.copy(youtubeLink = null) }
    }

    fun updateYoutubeTitle(value: String) {
        _youtubePostFields.update { it.copy(title = value) }
        _youtubePostErrors.update { it.copy(title = null) }
    }

    fun updateYoutubeShortDesc(value: String) {
        _youtubePostFields.update { it.copy(shortDesc = value) }
        _youtubePostErrors.update { it.copy(shortDesc = null) }
    }

    fun updateYoutubeLongDesc(value: String) {
        _youtubePostFields.update { it.copy(longDesc = value) }
        _youtubePostErrors.update { it.copy(longDesc = null) }
    }

    fun clearYoutubeForm() {
        _youtubePostFields.value = YoutubeVideoPostUploadFields()
        _youtubePostErrors.value = YoutubeVideoPostUploadErrors()
        _showYoutubeErrors.value = false
    }


    fun isYoutubeFormValid(): Boolean {
        val fields = _youtubePostFields.value
        val needsSpecificCategory = fields.category.equals("Others", ignoreCase = true)

        return fields.category.isNotBlank() &&
                fields.language.isNotBlank() &&
                fields.youtubeLink.isNotBlank() &&
                fields.title.isNotBlank() &&
                fields.shortDesc.isNotBlank() &&
                fields.longDesc.isNotBlank() &&
                (!needsSpecificCategory || fields.specificCategory.isNotBlank())
    }

    fun validateYoutubeForm(): Boolean {
        _showYoutubeErrors.value = true

        val fields = _youtubePostFields.value
        val isOthers = fields.category.equals("Others", ignoreCase = true)

        val isValidYoutubeLink =
            fields.youtubeLink.contains("youtube.com/watch?v=") ||
                    fields.youtubeLink.contains("youtu.be/") ||
                    fields.youtubeLink.contains("youtube.com/shorts/")

        val errors = YoutubeVideoPostUploadErrors(
            category = if (fields.category.isBlank()) "Select category" else null,
            specificCategory = if (isOthers && fields.specificCategory.isBlank()) "Enter your category" else null,
            language = if (fields.language.isBlank()) "Select language" else null,
            youtubeLink = when {
                fields.youtubeLink.isBlank() -> "Enter YouTube link"
                !isValidYoutubeLink -> "Enter valid YouTube link"
                else -> null
            },
            title = if (fields.title.isBlank()) "Enter valid title" else null,
            shortDesc = if (fields.shortDesc.isBlank()) "Enter short description" else null,
            longDesc = if (fields.longDesc.isBlank()) "Enter long description" else null
        )

        _youtubePostErrors.value = errors

        return listOf(
            errors.category,
            errors.specificCategory,
            errors.language,
            errors.youtubeLink,
            errors.title,
            errors.shortDesc,
            errors.longDesc
        ).all { it == null }
    }
}