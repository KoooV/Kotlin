package com.example.module3_3

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoListUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun shows_all_three_tasks_from_json() {
        // Небольшое ожидание, чтобы данные успели загрузиться
        composeTestRule.waitForIdle()
        // Проверяем, что на экране списка отображаются заголовки из todos.json
        composeTestRule.onNodeWithText("Купить молоко").assertExists()
        composeTestRule.onNodeWithText("Позвонить маме").assertExists()
        composeTestRule.onNodeWithText("Сделать ДЗ по Android").assertExists()
    }

    @Test
    fun checkbox_toggles_status() {
        val columnTag = "todo_column_1"
        val checkboxTag = "todo_checkbox_1"
        val firstTitle = "Купить молоко"

        waitUntilTextExists(firstTitle)
        waitUntilNodeWithTagExists(checkboxTag, useUnmergedTree = true)
        val checkboxNode = composeTestRule.onNodeWithTag(checkboxTag, useUnmergedTree = true)
        checkboxNode.assertIsOff()
        checkboxNode.performClick()
        composeTestRule.waitForIdle()

        waitUntilNodeWithTagExists(columnTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(columnTag, useUnmergedTree = true).performClick()

        waitUntilNodeWithTagExists("detail_status", useUnmergedTree = true)
        composeTestRule.onAllNodesWithTag("detail_status", useUnmergedTree = true)[0]
            .assertTextContains("Выполнено", substring = true)

        waitUntilNodeWithTagExists("detail_back", useUnmergedTree = true)
        composeTestRule.onNodeWithTag("detail_back", useUnmergedTree = true).performClick()

        composeTestRule.waitForIdle()
        waitUntilNodeWithTagGone("detail_status", useUnmergedTree = true)
        waitUntilTextExists(firstTitle)
        waitUntilNodeWithTagExists(checkboxTag, useUnmergedTree = true)
        composeTestRule.onNodeWithTag(checkboxTag, useUnmergedTree = true).assertIsOn()
    }

    @Test
    fun navigation_list_detail_list() {
        val title = "Позвонить маме"
        val columnTag = "todo_column_2"

        // Подождём появления заголовка в списке (unmerged)
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodesWithTag(columnTag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodes(hasText(title)).fetchSemanticsNodes().isNotEmpty()
        }

        val columnNodes = composeTestRule.onAllNodesWithTag(columnTag, useUnmergedTree = true)
        if (columnNodes.fetchSemanticsNodes().isNotEmpty()) {
            columnNodes[0].performClick()
        } else {
            composeTestRule.onNodeWithText(title).performClick()
        }

        // Ждём, что детальный экран отобразится (unmerged)
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodesWithTag("detail_title", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithTag("detail_title", useUnmergedTree = true)[0].assertTextEquals(title)
        composeTestRule.onAllNodesWithTag("detail_description", useUnmergedTree = true)[0].assertTextContains("Спросить", substring = true)

        // Нажать назад
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeTestRule.waitForIdle()

        // Вернулись к списку — проверим, что заголовок снова видим
        composeTestRule.onNodeWithText(title).assertExists()
    }

    private fun waitUntilNodeWithTagExists(tag: String, useUnmergedTree: Boolean = true) {
        waitUntilWithLogging("tag=$tag, unmerged=$useUnmergedTree exists") {
            composeTestRule.onAllNodesWithTag(tag, useUnmergedTree).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilNodeWithTagGone(tag: String, useUnmergedTree: Boolean = true) {
        waitUntilWithLogging("tag=$tag, unmerged=$useUnmergedTree gone") {
            composeTestRule.onAllNodesWithTag(tag, useUnmergedTree).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun waitUntilTextExists(text: String) {
        waitUntilWithLogging("text=$text exists") {
            composeTestRule.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilWithLogging(message: String, condition: () -> Boolean) {
        val tag = "TodoListUiTest"
        val start = System.currentTimeMillis()
        composeTestRule.waitUntil(10_000) {
            val result = runCatching { condition() }
                .getOrElse { throwable ->
                    if (throwable is IllegalStateException &&
                        throwable.message?.contains("No compose hierarchies") == true
                    ) {
                        Log.d(tag, "Compose not ready for $message yet...")
                        false
                    } else {
                        throw throwable
                    }
                }
            if (!result) {
                Log.d(tag, "Waiting for $message...")
            }
            result
        }
        Log.d(tag, "Condition met for $message in ${System.currentTimeMillis() - start} ms")
    }
}
