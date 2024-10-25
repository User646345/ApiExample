package com.example.apiexample

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apiexample.api.UserApi
import com.example.apiexample.ui.theme.ApiExampleTheme
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
                }
            }
        }
    }
}

@Serializable
object Home
@Serializable
object Settings

//Это для API
data class ProfileModel(
    var age: String,
    var name: String,
    var email: String,
)
data class UserModel(
    var profile: ProfileModel
)

//@Composable
//fun BottomNavigationBar() {
//    NavigationBar(modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
//        NavigationBarItem(
//            selected = true,
//            onClick = { navController.navigate(Home) },
//            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
//            label = {Text(text = "Home")})
//        NavigationBarItem(
//            selected = false,
//            onClick = { navController.navigate(Search) },
//            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
//            label = {Text(text = "Search")})
//        NavigationBarItem(
//            selected = false,
//            onClick = { navController.navigate(Profile) },
//            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
//            label = {Text(text = "Profile")})
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Simple API Request",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = { /*TODO*/ },
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Settings")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            //BottomNavigationBar()
        },
        topBar = {
            TopAppBar(navController)
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            val id = remember { mutableStateOf(TextFieldValue()) }
            val profile = remember {
                mutableStateOf(
                    ProfileModel(age = "", name = "", email = "")
                )
            }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                label = { Text(text = "User ID") },
                value = id.value,
                onValueChange = { id.value = it},
                singleLine = true
            )
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    sendRequest(
                        id = id.value.text,
                        profileState = profile
                    )
                    Log.d("Main Activity", profile.toString())
                }
            ) {
                Text(text = "Get Data", fontSize = 22.sp)
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Profile:",
                    fontSize = 22.sp
                )
                Text(text = "Name: ${profile.value.name}")
                Text(text = "Age: ${profile.value.age}")
                Text(text = "Email: ${profile.value.email}")
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
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                title = {
                    Text(
                        text = "Settings",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(8.dp)) {
            Text(
                text = "Пока так",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center))
        }
    }
}

fun sendRequest(
    id: String,
    profileState: MutableState<ProfileModel>
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.68.101:8000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)

    val call: Call<UserModel?>? = api.getUserById(id)

    call!!.enqueue(object: Callback<UserModel?> {
        override fun onResponse(call: Call<UserModel?>, response: Response<UserModel?>) {
            if(response.isSuccessful) {
                Log.d("Main", "Всё работает!" + response.body().toString())
                profileState.value = response.body()!!.profile
            }
        }
        override fun onFailure(call: Call<UserModel?>, t: Throwable) {
            Log.e("Main", "Не работает " + t.message.toString())
        }
    })
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsPreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        SettingsScreen(navController)
    }
}