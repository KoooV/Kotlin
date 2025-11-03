package com.example.pr8

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class WaterTrackerUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addButton_incrementsWaterBy250() {
        // Изначально 0 мл
        composeTestRule.onNodeWithText("0 мл").assertExists()

        // Нажимаем +250 мл
        composeTestRule.onNodeWithText("+250 мл").performClick()

        // Проверяем, что стало 250 мл
        composeTestRule.onNodeWithText("250 мл").assertExists()
    }

    @Test
    fun finishDay_resetsWaterAndIncrementsStreakWhenReached1500() {
        // Нажимаем 6 раз +250 мл -> 1500 мл
        repeat(6) {
            composeTestRule.onNodeWithText("+250 мл").performClick()
        }

        // Завершаем день
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Вода сброшена
        composeTestRule.onNodeWithText("0 мл").assertExists()
        // Серия увеличена до 1
        composeTestRule.onNodeWithText("Серия успешных дней: 1").assertExists()
    }

    @Test
    fun streakText_isVisible_andResetsIfLessThan1500() {
        // Текст серии отображается (начальное значение 0)
        composeTestRule.onNodeWithText("Серия успешных дней: 0").assertExists()

        // Набираем меньше 1500 (например, 250 мл) и завершаем день
        composeTestRule.onNodeWithText("+250 мл").performClick()
        composeTestRule.onNodeWithText("Завершить день").performClick()

        // Серия должна быть 0
        composeTestRule.onNodeWithText("Серия успешных дней: 0").assertExists()
    }
}


