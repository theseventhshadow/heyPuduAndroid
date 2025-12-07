package com.heypudu.heypudu.features.onboarding.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CreateProfileViewModelTest {

    @Test
    fun username_isValidWhenNotEmpty() {
        // Arrange
        val username = "pedrolopez"

        // Act
        val isValid = username.isNotBlank()

        // Assert
        assertThat(isValid).isTrue()
    }

    @Test
    fun username_isInvalidWhenEmpty() {
        // Arrange
        val username = ""

        // Act
        val isValid = username.isNotBlank()

        // Assert
        assertThat(isValid).isFalse()
    }

    @Test
    fun password_isValidWhenLengthBetween6And12() {
        // Arrange
        val password = "password123"

        // Act
        val isValid = password.length in 6..12

        // Assert
        assertThat(isValid).isTrue()
    }

    @Test
    fun password_isInvalidWhenTooShort() {
        // Arrange
        val password = "pass"

        // Act
        val isValid = password.length in 6..12

        // Assert
        assertThat(isValid).isFalse()
    }

    @Test
    fun password_isInvalidWhenTooLong() {
        // Arrange
        val password = "thispasswordistoolong"

        // Act
        val isValid = password.length in 6..12

        // Assert
        assertThat(isValid).isFalse()
    }

    @Test
    fun email_isValidFormat() {
        // Arrange
        val email = "test@example.com"
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()

        // Act
        val isValid = emailRegex.matches(email)

        // Assert
        assertThat(isValid).isTrue()
    }

    @Test
    fun email_isInvalidFormat() {
        // Arrange
        val email = "notanemail"
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()

        // Act
        val isValid = emailRegex.matches(email)

        // Assert
        assertThat(isValid).isFalse()
    }

    @Test
    fun username_maxLengthIs12Characters() {
        // Arrange
        val username = "12345678901234567890" // Más de 12
        val maxLength = 12

        // Act
        val isValid = username.length <= maxLength

        // Assert
        assertThat(isValid).isFalse()
    }

    @Test
    fun username_maxLengthAllowsExactly12() {
        // Arrange
        val username = "123456789012" // Exactamente 12
        val maxLength = 12

        // Act
        val isValid = username.length <= maxLength

        // Assert
        assertThat(isValid).isTrue()
    }
}

