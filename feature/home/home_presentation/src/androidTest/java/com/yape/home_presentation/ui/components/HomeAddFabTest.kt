package com.yape.home_presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yape.designsystem.theme.DocVaultTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeAddFabTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun given_fab_collapsed_when_rendered_then_add_icon_is_displayed() {
        composeTestRule.setContent {
            DocVaultTheme {
                HomeAddFab(
                    expanded = false,
                    onFabClick = {},
                    onGalleryClick = {},
                    onCameraClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Agregar documento").assertIsDisplayed()
    }

    @Test
    fun given_fab_collapsed_when_clicked_then_on_fab_click_callback_is_invoked() {
        var clicked = false
        composeTestRule.setContent {
            DocVaultTheme {
                HomeAddFab(
                    expanded = false,
                    onFabClick = { clicked = true },
                    onGalleryClick = {},
                    onCameraClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Agregar documento").performClick()

        assertTrue(clicked)
    }

    @Test
    fun given_fab_expanded_when_rendered_then_mini_fabs_are_visible() {
        composeTestRule.setContent {
            DocVaultTheme {
                HomeAddFab(
                    expanded = true,
                    onFabClick = {},
                    onGalleryClick = {},
                    onCameraClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Galería").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cámara").assertIsDisplayed()
    }
}
