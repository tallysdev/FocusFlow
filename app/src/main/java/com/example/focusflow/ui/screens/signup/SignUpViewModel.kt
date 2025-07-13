package com.example.focusflow.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.regex.Pattern

// Basic email validation pattern
private const val EMAIL_VALIDATION_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

data class SignUpFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val birthDate: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    // Error states
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val birthDateError: String? = null,
    val phoneNumberError: String? = null,
    val passwordError: String? = null,
    // General states
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val registrationError: String? = null,
)

class SignUpViewModel : ViewModel() {
    var formState by mutableStateOf(SignUpFormState())
        private set

    fun onFirstNameChange(firstName: String) {
        formState = formState.copy(firstName = firstName, firstNameError = null)
    }

    fun onLastNameChange(lastName: String) {
        formState = formState.copy(lastName = lastName, lastNameError = null)
    }

    fun onEmailChange(email: String) {
        formState = formState.copy(email = email, emailError = null)
    }

    fun onBirthDateChange(birthDate: String) {
        formState = formState.copy(birthDate = birthDate, birthDateError = null)
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        formState = formState.copy(phoneNumber = phoneNumber, phoneNumberError = null)
    }

    fun onPasswordChange(password: String) {
        formState = formState.copy(password = password, passwordError = null)
    }

    fun togglePasswordVisibility() {
        formState = formState.copy(isPasswordVisible = !formState.isPasswordVisible)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (formState.firstName.isBlank()) {
            formState = formState.copy(firstNameError = "First name cannot be empty")
            isValid = false
        }
        if (formState.lastName.isBlank()) {
            formState = formState.copy(lastNameError = "Last name cannot be empty")
            isValid = false
        }
        if (formState.email.isBlank()) {
            formState = formState.copy(emailError = "Email cannot be empty")
            isValid = false
        } else if (!Pattern.matches(EMAIL_VALIDATION_REGEX, formState.email)) {
            formState = formState.copy(emailError = "Invalid email format")
            isValid = false
        }
        if (formState.birthDate.isBlank()) {
            // You might have more specific validation for date format if needed
            formState = formState.copy(birthDateError = "Date of birth cannot be empty")
            isValid = false
        }
        if (formState.phoneNumber.isBlank()) {
            formState = formState.copy(phoneNumberError = "Phone number cannot be empty")
            isValid = false
        } else if (formState.phoneNumber.length < 10) { // Example: Minimum length
            formState = formState.copy(phoneNumberError = "Phone number seems too short")
            isValid = false
        }
        if (formState.password.isBlank()) {
            formState = formState.copy(passwordError = "Password cannot be empty")
            isValid = false
        } else if (formState.password.length < 6) { // Example: Minimum password length
            formState = formState.copy(passwordError = "Password must be at least 6 characters")
            isValid = false
        }

        return isValid
    }

    fun handleRegistration() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            formState = formState.copy(isLoading = true, registrationError = null)
            try {
                val auth = Firebase.auth
                auth.createUserWithEmailAndPassword(formState.email, formState.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUpViewModel", "createUserWithEmail:success")
                            formState =
                                formState.copy(isLoading = false, registrationSuccess = true)
                            // You might want to navigate or update user profile here
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUpViewModel", "createUserWithEmail:failure", task.exception)
                            formState =
                                formState.copy(
                                    isLoading = false,
                                    registrationError = task.exception?.message ?: "Registration failed",
                                )
                        }
                    }

                // For now, just simulating success
                val success = true // Simulate API call result
                formState =
                    if (success) {
                        formState.copy(isLoading = false, registrationSuccess = true)
                    } else {
                        formState.copy(
                            isLoading = false,
                            registrationError = "Simulated registration error.",
                        )
                    }
                // --- END TODO ---
            } catch (e: Exception) {
                formState =
                    formState.copy(
                        isLoading = false,
                        registrationError = e.message ?: "An unknown error occurred",
                    )
            }
        }
    }
}
