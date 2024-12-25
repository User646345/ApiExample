package com.example.apiexample

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apiexample.api.ApiService
import com.example.apiexample.ui.theme.ApiExampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiExampleTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = Home) {
                    composable<Home> { HomeScreen(navController)}
                    composable<Settings> { SettingsScreen(navController)}
                    composable<Post> { PostScreen(navController) }
                }
            }
        }
    }
}

@Serializable
object Home
@Serializable
object Settings
@Serializable
object Post

//Для пост запроса
data class UserCreation(
    var username: String,
    var age: String,
    var email: String,
    var password: String
)

//Для гет запроса
data class UserModel(
    var profile: ProfileModel
)

//Для парсинга
data class ProfileModel(
    var age: String,
    var name: String,
    var email: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.get),
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = { navController.navigate(Post) },
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(
                modifier = Modifier,
                onClick ={ navController.navigate(Settings) },
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
fun HomeScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(navController)
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            val id = remember { mutableStateOf(TextFieldValue()) }
            val profile = remember {
                mutableStateOf(ProfileModel(age = "", name = "", email = "")) }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(0.dp),
                label = { Text(text = stringResource(id = R.string.user_id)) },
                value = id.value,
                onValueChange = { id.value = it},
                singleLine = true
            )

            Row( modifier = Modifier.padding(16.dp)) {
                ElevatedButton(
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp, topEnd = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(48.dp),
                    onClick = {
                        coroutineScope.launch { getUser(context, id = id.value.text, profileState = profile) }
                    }
                ) {
                    Text(text = stringResource(id = R.string.button_get), fontSize = 22.sp)
                }

                ElevatedButton(
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomEnd = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(48.dp),
                    onClick = {
                        coroutineScope.launch { removeUser(context, userId = id.value.text) }
                    }
                ) {
                    Text(text = stringResource(id = R.string.button_delete), fontSize = 22.sp)
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "${stringResource(id = R.string.profile)}:",
                    fontSize = 22.sp
                )
                Text(text = "${stringResource(R.string.name)}: ${profile.value.name}")
                Text(text = "${stringResource(R.string.age)}: ${profile.value.age}")
                Text(text = "${stringResource(R.string.email)}: ${profile.value.email}")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Home) }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back") }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.post),
                        textAlign = TextAlign.Left,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var name by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name") },
                value = name,
                onValueChange = { name = it},
                label = { Text(text = stringResource(id = R.string.name)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(8.dp))

            var age by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Age") },
                value = age,
                onValueChange = { age = it},
                label = { Text(text = stringResource(id = R.string.age)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(8.dp))

            var email by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                value = email,
                onValueChange = { email = it},
                label = { Text(text = stringResource(id = R.string.email)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(8.dp))

            var password by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password") },
                value = password,
                onValueChange = { password = it},
                label = { Text(text = stringResource(id = R.string.password)) },
                singleLine = true
            )

            ElevatedButton(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        createUser (context, username = name, age = age, email = email, password = password)
                    }
                }) {
                Text(text = stringResource(id = R.string.button_post), fontSize = 22.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Home) }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back") }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        textAlign = TextAlign.Left,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .padding(start = 4.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.app_name_variant),
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
            )
            Text(
                text = "${stringResource(R.string.app_version)}: ${BuildConfig.VERSION_NAME}",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.68.105:8000/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val apiService: ApiService = retrofit.create(ApiService::class.java)

fun createUser(
    context: Context,
    username: String,
    age: String,
    email: String,
    password: String
) {
    val user = UserCreation(username = username, age = age, email = email, password = password)
    val call = apiService.createUser(user)
    val duration = Toast.LENGTH_SHORT

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Успешно создано
                Toast.makeText(context, "Пользователь успешно создан", duration).show()
            } else {
                // Ошибка при создании
                Toast.makeText(context, "Ошибка при создании пользователя", duration).show()
            }
        }
        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Ошибка сети или другая ошибка
            Toast.makeText(context, "Не удалось создать пользователя: ${t.message}", duration).show()
        }
    })
}

fun removeUser (
    context: Context,
    userId: String
) {
    val duration = Toast.LENGTH_SHORT
    val call = apiService.deleteUserById (userId)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // Успешно удалено
                Log.d("DeleteUser ", "Пользователь удален")
                Toast.makeText(context, "Пользователь удален", duration).show()
            } else {
                // Ошибка при удалении
                Log.e("DeleteUser ", "Ошибка: ${response.code()}")
                Toast.makeText(context, "Ошибка при удалении пользователя", duration).show()
            }
        }
        override fun onFailure(call: Call<Void>, t: Throwable) {
            // Ошибка сети или другая ошибка
            Log.e("DeleteUser ", "Не удалось удалить пользователя: ${t.message}")
            Toast.makeText(context, "Не удается подключиться к серверу", duration).show()
        }
    })
}

fun getUser(
    context: Context,
    id: String,
    profileState: MutableState<ProfileModel>
) {
    val duration = Toast.LENGTH_SHORT
    val call: Call<UserModel?>? = apiService.getUserById(id)
    call!!.enqueue(object: Callback<UserModel?> {
        override fun onResponse(call: Call<UserModel?>, response: Response<UserModel?>) {
            if(response.isSuccessful) {
                //Успешно получено
                Log.d("Main", "Данные получены: " + response.body().toString())
                profileState.value = response.body()!!.profile
            } else {
                //Ошибка при получении
                Log.d("Main", "Данные не получены: " + response.body().toString())
                Toast.makeText(context, "Данные не получены", duration).show()
            }
        }
        override fun onFailure(call: Call<UserModel?>, t: Throwable) {
            // Ошибка сети или другая ошибка
            Log.e("Main", "Данные не получены: " + t.message.toString())
            Toast.makeText(context, "Не удается подключиться к серверу", duration).show()
        }
    })
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        HomeScreen(navController) } }

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PostPreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        PostScreen(navController) } }

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsPreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        SettingsScreen(navController) } }