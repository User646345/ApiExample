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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
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
                    composable<SearchScreen> {
                        SearchScreen(navController)
                    }
                }
            }
        }
    }
}

//Каждый экран приложения для навигации
@Serializable
object HomeScreen
@Serializable
object SearchScreen
@Serializable
object ProfileScreen

//Это для API
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
    NavigationBar(modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate(HomeScreen) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            label = {Text(text = "Home")})
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(SearchScreen) },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            label = {Text(text = "Search")})
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(ProfileScreen) },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            label = {Text(text = "Profile")})
    }
}

//@Composable
//fun BottomNavigationBar(navController: NavController) {
//    // Состояние для хранения индекса выбранного элемента
//    val selectedIndex = remember { mutableIntStateOf(0) }
//
//    NavigationBar(modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
//        NavigationBarItem(
//            selected = selectedIndex.intValue == 0,
//            onClick = {
//                navController.navigate(HomeScreen)
//                selectedIndex.intValue = 0
//                },
//            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
//            label = { Text(text = "Home") }
//        )
//        NavigationBarItem(
//            selected = selectedIndex.intValue == 1,
//            onClick = {
//                navController.navigate(SearchScreen)
//                selectedIndex.intValue = 1
//            },
//            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
//            label = { Text(text = "Search") }
//        )
//        NavigationBarItem(
//            selected = selectedIndex.intValue == 2,
//            onClick = {
//                navController.navigate(ProfileScreen)
//                selectedIndex.intValue = 2
//            },
//            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
//            label = { Text(text = "Profile") }
//        )
//    }
//}

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
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {

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
                onValueChange = { id.value = it },
                singleLine = true
            )
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
            Text(
                text = profile.component1().toString(),
                fontSize = 22.sp)
        }
    }
}

@Composable
fun SearchScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp))
            {
                Box(modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
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
                            contentDescription = "Plant"
                        )
                        Text(
                            text = "Mehrunes Dagon"
                        )
                    }
                }
                Box(modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
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
        .baseUrl("http://192.168.68.101:3000")
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
fun SearchPreview() {
    ApiExampleTheme {
        val navController = rememberNavController()
        SearchScreen(navController)
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