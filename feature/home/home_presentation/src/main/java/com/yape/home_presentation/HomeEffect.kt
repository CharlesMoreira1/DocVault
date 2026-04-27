package com.yape.home_presentation

import androidx.annotation.StringRes

sealed class HomeEffect {
    object LaunchGallery : HomeEffect()
    object LaunchCamera : HomeEffect()
    data class NavigateToDetail(val id: String) : HomeEffect()
    data class ShowError(@param:StringRes val messageRes: Int) : HomeEffect()
}
