package com.example.apiexample

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
                NavHost(navController = navController, startDestination = HomeScreen) {
                    composable<HomeScreen> {
                        HomeScreen(navController)
                    }
                    composable<ProfileScreen> {
                        ProfileScreen(navController)
                    }
                }
            }
        }
    }
}

@Serializable
object HomeScreen

@Serializable
object ProfileScreen

data class ProfileModel(
    var age: String,
    var name: String,
    var email: String,
)

data class UserModel(
    var profile: ProfileModel
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {navController.navigate(HomeScreen)},
            icon = {Icon(imageVector = Icons.Default.Home, contentDescription = null)},
            label = {Text(text = "Home")})

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(ProfileScreen) },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null)},
            label = {Text(text = "Profile")})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Simple API Request",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val id = remember {
                mutableStateOf(TextFieldValue())
            }
            val profile = remember {
                mutableStateOf(
                    ProfileModel(
                        age = "",
                        name = "",
                        email = ""
                    )
                )
            }

            TextField(
                shape = RoundedCornerShape(50.dp),
                label = { Text(text = "User ID") },
                value = id.value,
                onValueChange = { id.value = it }
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.padding(8.dp),
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
                Text(text = profile.component1().toString(), fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding), shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp))
            {
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .weight(2f))
                {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.plant),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.White),
                            contentDescription = null
                        )
                        Text(
                            text = "Mehrunes Dagon"
                        )
                    }
                }
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .weight(4f))
            }
        }
    }
}

fun sendRequest(
    id: String,
    profileState: MutableState<ProfileModel>
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.68.116:3000")
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
fun MainPreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfilePreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        ProfileScreen(navController)
    }
}