package com.example.focusflow.ui.screens.signup

import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.focusflow.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController, signUpViewModel: SignUpViewModel = viewModel()) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            birthDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0A0E20), Color(0xFF1B2448))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.img_focus),
                contentDescription = "Focus Flow logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Get Started now",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "First Name",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Last Name",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = Color.White
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "Date of Birth",
                    fontSize = 14.sp,
                    color = Color.White
                )
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {},
                    label = { Text("Date of Birth") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    enabled = false, // Disable manual input
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledIndicatorColor = Color.White,
                        disabledTextColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "Phone Number",
                    fontSize = 14.sp,
                    color = Color.White
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    color = Color.White
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Set Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye),
                                // Todo: adicionar isso pra quando o usuário clicar no olho mudar o icone= if (passwordVisible) Icons.Default. else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Handle registration */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D61E7)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("login_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Login", color = Color.Black)
            }
        }
    }
}


@Preview(showBackground = true, name = "Login Screen Preview", showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(navController = NavHostController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String, // Placeholder/Label inside the TextField
    fieldLabel: String, // Text label above the TextField
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    onContainerClick: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = fieldLabel,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(labelText) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onContainerClick != null && enabled) Modifier.clickable { onContainerClick() }
                    else Modifier
                ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f), // More subtle when disabled
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.LightGray, // Less prominent when not focused
                disabledIndicatorColor = Color.Gray,
                disabledTextColor = Color.Black.copy(alpha = 0.7f), // Good contrast when disabled
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Gray,
                disabledLabelColor = Color.Gray.copy(alpha = 0.7f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,
                errorTrailingIconColor = MaterialTheme.colorScheme.error,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            enabled = enabled,
            isError = isError,
            readOnly = readOnly, // Important for the DatePicker field
            supportingText = {
                if (isError && errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        )
    }
}