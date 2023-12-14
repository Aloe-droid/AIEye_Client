package com.example.client

import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.client.roomDB.User

interface Draw {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(
        mail: MutableState<String>,
        password: MutableState<String>,
        nickName: MutableState<String>,
        passwordVisible: MutableState<Boolean>,
        enter: () -> Unit
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.header),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "LOG IN",
                    fontSize = 45.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(50.dp))

                OutlinedTextField(value = nickName.value,
                    onValueChange = { newText -> nickName.value = newText },
                    label = {
                        Text(
                            text = "Nickname", modifier = Modifier.background(Color.Transparent)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    leadingIcon = {
                        Image(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            Modifier
                                .height(24.dp)
                                .height(24.dp)
                        )
                    })

                Spacer(modifier = Modifier.height(25.dp))

                OutlinedTextField(value = mail.value,
                    onValueChange = { newText -> mail.value = newText },
                    label = { Text(text = "Email", Modifier.background(Color.Transparent)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    singleLine = true,
                    placeholder = { Text(text = "example@mail.com", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Image(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    })

                Spacer(modifier = Modifier.height(25.dp))

                OutlinedTextField(value = password.value,
                    onValueChange = { newText -> password.value = newText },
                    label = { Text(text = "Password", Modifier.background(Color.Transparent)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    singleLine = true,
                    placeholder = { Text(text = "password", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    leadingIcon = {
                        Image(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    },
                    trailingIcon = {
                        val image = if (passwordVisible.value) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    })

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { enter() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "CONTINUE TO ACCOUNT", fontSize = 20.sp)
                }
            }
        }
    }

    @Composable
    fun Web(webView: WebView) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = { webView })
        }
    }
}