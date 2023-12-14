package com.example.client

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.client.retrofit.Login
import com.example.client.retrofit.LoginResponse
import com.example.client.retrofit.LoginService
import com.example.client.roomDB.User
import com.example.client.roomDB.UserDAO
import com.example.client.roomDB.UserDB
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity(), Draw {

    private val url = "https://webapiyoon.azurewebsites.net"
    private val nickName = mutableStateOf("")
    private val mail = mutableStateOf("")
    private val password = mutableStateOf("")
    private val token = mutableStateOf("")

    private lateinit var loginService: LoginService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPermissions()

        val passwordVisible = mutableStateOf(false)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            token.value = it.result
        }
        loginService = api()

        setContent {
            Login(mail, password, nickName, passwordVisible) { enter(mail, password, nickName) }
        }
    }

    private fun enter(
        mail: MutableState<String>, password: MutableState<String>, nickName: MutableState<String>
    ) {

        val userDao = UserDB.getInstance(this)!!.userDAO()
        if (mail.value.isEmpty() && password.value.isEmpty() && nickName.value.isEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val users = userDao.getAll()
                withContext(Dispatchers.Main) {
                    if (users.isEmpty()) {
                        Toast.makeText(this@MainActivity, "전부 다 작성해주세요!", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog(users, this@MainActivity, userDao)
                    }
                }

            }
            return
        }

        if (!isValidEmail(mail.value) && mail.value.isNotEmpty()) {
            Toast.makeText(this, "유효하지 않은 이메일 형식입니다!", Toast.LENGTH_SHORT).show()
            return
        }

        if (mail.value.isEmpty() || password.value.isEmpty() || nickName.value.isEmpty()) {
            Toast.makeText(this, "전부 다 작성해주세요!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val login = Login(mail.value, password.value, token.value, nickName.value)

            try {
                val response = loginService.sendLogin(login)

                if (response.isSuccessful) {
                    val sameUser = userDao.getUserByEmail(mail.value)
                    if (sameUser == null) {
                        val user =
                            User(
                                email = mail.value,
                                password = password.value,
                                name = nickName.value
                            )
                        userDao.insert(user)
                    } else if ((sameUser.password != password.value) || (sameUser.name != nickName.value)) {
                        sameUser.password = password.value
                        sameUser.name = nickName.value
                        userDao.update(sameUser)
                    }

                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@MainActivity, WebViewActivity::class.java).apply {
                            putExtra("email", mail.value)
                            putExtra("password", password.value)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<LoginResponse>() {}.type
                    response.errorBody()?.let {
                        val errorResponse = gson.fromJson<LoginResponse>(it.charStream(), type)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "${response.code()} : ${errorResponse.errorMessage.joinToString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

//        lifecycleScope.launch(Dispatchers.IO) {
//            val sameUser = userDao.getUserByEmail(mail.value)
//            if (sameUser == null) {
//                val user =
//                    User(email = mail.value, password = password.value, name = nickName.value)
//                userDao.insert(user)
//            } else if ((sameUser.password != password.value) || (sameUser.name != nickName.value)) {
//                sameUser.password = password.value
//                sameUser.name = nickName.value
//                userDao.update(sameUser)
//            }
//
//            withContext(Dispatchers.Main) {
//                val intent = Intent(this@MainActivity, WebViewActivity::class.java).apply {
//                    putExtra("email", mail.value)
//                    putExtra("password", password.value)
//
//                }
//                startActivity(intent)
//                finish()
//            }
//        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return pattern.matches(email)
    }

    private fun dialog(users: List<User>, context: Context, userDAO: UserDAO) {
        AlertDialog.Builder(context).setTitle("이메일")
            .setItems(users.map { it.email }.toTypedArray()) { _, which ->
                val user = users[which]
                nickName.value = user.name
                mail.value = user.email
                password.value = user.password
            }.setNegativeButton("삭제하기") { _, _ ->
                deleteDialog(users, context, userDAO)
            }.setPositiveButton("전체 삭제하기") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    userDAO.deleteAll()
                }
            }.show()
    }

    private fun deleteDialog(users: List<User>, context: Context, userDAO: UserDAO) {
        AlertDialog.Builder(context).setTitle("삭제할 이메일")
            .setItems(users.map { it.email }.toTypedArray()) { _, witch ->
                val user = users[witch]
                lifecycleScope.launch(Dispatchers.IO) {
                    userDAO.delete(user)
                }
            }.show()
    }

    private fun setPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) {
                    Toast.makeText(this, "권한을 허용 하지 않으면 사용할 수 없습니다!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        val permissions = listOf(Manifest.permission.CAMERA)

        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(it)
            }
        }
    }

    private fun api(): LoginService {
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(LoginService::class.java)
    }
}
