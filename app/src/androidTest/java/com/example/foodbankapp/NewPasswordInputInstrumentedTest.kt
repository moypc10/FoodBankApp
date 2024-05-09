package com.example.foodbankapp


import android.graphics.Color
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewPasswordInputInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(New_password_input::class.java)

    @Test
    fun testPasswordValidationValid() {
        val newPasswordInput = activityRule.activity

        // Prueba de validación de contraseña 1
        val isValid1 = newPasswordInput.isPasswordValid("Password$123", "Password$123")
        assertTrue(isValid1)

        // Verifica que la actividad se inicia correctamente
        assertNotNull(newPasswordInput)
    }

    @Test
    fun testPasswordValidationIValid() {
        val newPasswordInput = activityRule.activity
        val isValid1 = newPasswordInput.isPasswordValid("Password123", "Password123")
        assertFalse(isValid1)


        assertNotNull(newPasswordInput)
    }

    @Test
    fun testPasswordValidationValidButton() {
        val newPasswordInput = activityRule.activity

        // Prueba de validación de contraseña 2
        val isValid2 = newPasswordInput.isPasswordValid("Pass123", "Pass123")
        assertFalse(isValid2)

        // Verifica que la actividad se inicia correctamente
        assertNotNull(newPasswordInput)

        // Ingresa una contraseña válida
        Espresso.onView(withId(R.id.password_input)).perform(typeText("Password$123"), closeSoftKeyboard())
        Espresso.onView(withId(R.id.password_input2)).perform(typeText("Password$123"), closeSoftKeyboard())

        // Haz clic en el botón de restablecer contraseña
        Espresso.onView(withId(R.id.myprofile)).perform(click())

        // Verifica que la actividad LogInActivity se haya iniciado
        Espresso.onView(withId(R.id.LogInButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testSetConditionToLightGreen() {
        val conditionView = TextView(activityRule.activity)
        activityRule.activity.setConditionToLightGreen(conditionView)
        assertEquals(Color.parseColor("#06CB52"), conditionView.currentTextColor)
    }

    @Test
    fun testResetConditionColor() {
        val conditionView = TextView(activityRule.activity)
        conditionView.setTextColor(Color.parseColor("#123456")) // Set an initial color
        activityRule.activity.resetConditionColor(conditionView)
        assertEquals(Color.parseColor("#FA3C1B"), conditionView.currentTextColor)
    }


}
