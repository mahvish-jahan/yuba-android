package com.yuba.cafe.ui.home.profile

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuba.cafe.R
import com.yuba.cafe.ui.components.JetsnackButton
import com.yuba.cafe.ui.components.SnackImage
import com.yuba.cafe.ui.theme.JetsnackTheme

@Composable
fun Profile(
    application: Application,
    modifier: Modifier = Modifier,
    onAllOrderClick: () -> Unit,
    onManageMenuClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(application)),
) {

    val showSignIn = viewModel.showSignIn.collectAsStateWithLifecycle()
    val showSignUp = viewModel.showSignUp.collectAsStateWithLifecycle()
    val showProfile = viewModel.showProfile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getProfile()
    }

    if (showSignIn.value) {
        signIn(viewModel)
    }
    if (showSignUp.value) {
        signup(viewModel)
    }
    if (showProfile.value) {
        profile(viewModel, modifier, onAllOrderClick, onManageMenuClick)
    }
}

@Composable
private fun signIn(
    viewModel: ProfileViewModel
) {

    val username = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }

    val error by viewModel.signInSignUpError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Email") },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))

        JetsnackButton(
            onClick = {
                viewModel.signIn(
                    username.value.text,
                    password.value.text
                )
            },
        ) {
            Text(
                text = stringResource(R.string.login),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        JetsnackButton(
            onClick = {
                viewModel.showSignup()
            },
        ) {
            Text(
                text = "Signup Here",
                textAlign = TextAlign.Center
            )
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error, color = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun signup(
    viewModel: ProfileViewModel
) {

    val name = remember { mutableStateOf(TextFieldValue("")) }
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }

    val error by viewModel.signInSignUpError.collectAsStateWithLifecycle()

    // signup
    val confirmPassword = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Signup",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Name") },
            value = name.value,
            onValueChange = { name.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Email") },
            value = email.value,
            onValueChange = { email.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = JetsnackTheme.colors.uiBackground,
                focusedLabelColor = JetsnackTheme.colors.brand,
                unfocusedLabelColor = JetsnackTheme.colors.brand,
            ),
            label = { Text(text = "Confirm Password") },
            value = confirmPassword.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { confirmPassword.value = it })

        Spacer(modifier = Modifier.height(20.dp))

        JetsnackButton(
            onClick = {
                viewModel.signUp(
                    name.value.text,
                    email.value.text,
                    password.value.text,
                    confirmPassword.value.text
                )
            },
        ) {
            Text(
                text = stringResource(R.string.signup),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        JetsnackButton(
            onClick = {
                viewModel.showSignIn()
            },
        ) {
            Text(
                text = "Sign in here",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error, color = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun profile(
    viewModel: ProfileViewModel,
    modifier: Modifier,
    onAllOrderClick: () -> Unit,
    onManageMenuClick: () -> Unit
) {

    val userProfile = viewModel.userProfile.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {

        SnackImage(
            imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
            elevation = 4.dp,
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = userProfile.value.name,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = userProfile.value.email,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        Row {
            Spacer(Modifier.weight(1f))
            JetsnackButton(
                onClick = {
                    onAllOrderClick()
                },
                shape = RectangleShape,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (viewModel.isManager()) stringResource(id = R.string.manage_order) else stringResource(
                        id = R.string.view_all_orders
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }


        if (viewModel.isManager()) {
            Row {
                Spacer(Modifier.weight(1f))
                JetsnackButton(
                    onClick = {
                        onManageMenuClick()
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.manage_menu),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        Row {
            Spacer(Modifier.weight(1f))
            JetsnackButton(
                onClick = {
                    viewModel.signOut()
                },
                shape = RectangleShape,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}