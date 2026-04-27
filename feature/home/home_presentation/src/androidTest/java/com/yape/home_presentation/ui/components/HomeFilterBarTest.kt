package com.yape.home_presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yape.designsystem.theme.DocVaultTheme
import com.yape.document_domain.model.DocumentFilter
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFilterBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun given_all_filter_selected_when_rendered_then_all_chips_are_displayed() {
        composeTestRule.setContent {
            DocVaultTheme {
                HomeFilterBar(
                    selected = DocumentFilter.ALL,
                    onFilterSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Todos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Imágenes").assertIsDisplayed()
        composeTestRule.onNodeWithText("PDFs").assertIsDisplayed()
    }

    @Test
    fun given_image_chip_when_clicked_then_callback_receives_image_filter() {
        var selectedFilter: DocumentFilter? = null
        composeTestRule.setContent {
            DocVaultTheme {
                HomeFilterBar(
                    selected = DocumentFilter.ALL,
                    onFilterSelected = { selectedFilter = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Imágenes").performClick()

        assertEquals(DocumentFilter.IMAGE, selectedFilter)
    }

    @Test
    fun given_pdf_chip_when_clicked_then_callback_receives_pdf_filter() {
        var selectedFilter: DocumentFilter? = null
        composeTestRule.setContent {
            DocVaultTheme {
                HomeFilterBar(
                    selected = DocumentFilter.ALL,
                    onFilterSelected = { selectedFilter = it }
                )
            }
        }

        composeTestRule.onNodeWithText("PDFs").performClick()

        assertEquals(DocumentFilter.PDF, selectedFilter)
    }
}
