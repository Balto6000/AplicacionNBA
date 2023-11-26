package com.example.aplicacionnba

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionnba.ui.theme.AplicacionNBATheme
import java.text.SimpleDateFormat
import java.util.*

class CreatePlayerScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionNBATheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NBAApp(navController = navController)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlayerScreen(
    onPlayerCreated: (Player) -> Unit,
    navController: NavController
) {
    var playerName by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var selectedPositions by remember { mutableStateOf<List<String>>(emptyList()) }
    var points by remember { mutableStateOf(50) }
    var selectedOpponentTeam by remember { mutableStateOf<Team?>(null) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val prefixedPlayerNames = listOf("Michael Jordan", "LeBron James", "Kobe Bryant", "Kevin Durant")
    val filteredPlayerNames = prefixedPlayerNames.filter {
        it.contains(playerName, ignoreCase = true)
    }

    val teams = listOf(
        Team("Boston Celtics", Color.Red),
        Team("Brooklyn Nets", Color.Blue),
        Team("New York Knicks", Color.Green)
    )

    val filteredTeams = teams.filter {
        it.name.contains(selectedTeam?.name ?: "", ignoreCase = true)
    }

    val positions = listOf("PG", "SG", "SF", "PF", "C")

    val opponentTeams = teams.filterNot { it == selectedTeam }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Jugador") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("nbaApp")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val player = Player(
                                prefixedPlayerNames.find { it == playerName } ?: playerName,
                                selectedTeam!!,
                                selectedPositions,
                                points,
                                selectedOpponentTeam!!,
                                selectedDate.time.toString(),
                                R.drawable.default_player
                            )
                            onPlayerCreated(player)
                            navController.navigate("nbaApp")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                content = {
                    item {
                        Text(text = "Seleccionar nombre del jugador:")
                        DropdownMenu(
                            expanded = isDropdownVisible && playerName.isNotEmpty(),
                            onDismissRequest = {
                                isDropdownVisible = false
                                playerName = ""
                            },
                        ) {
                            filteredPlayerNames.forEach { playerNameOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        playerName = playerNameOption
                                        isDropdownVisible = false
                                    },
                                    text = { Text(playerNameOption) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = {
                                playerName = it
                                isDropdownVisible = true
                            },
                            label = { Text("Nombre del Jugador") },
                            modifier = Modifier.clickable {
                                isDropdownVisible = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(text = "Introducir nombre del equipo:")
                        DropdownMenu(
                            expanded = isDropdownVisible && selectedTeam != null,
                            onDismissRequest = {
                                isDropdownVisible = false
                            },
                        ) {
                            filteredTeams.forEach { team ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedTeam = team
                                        isDropdownVisible = false
                                    },
                                    text = { Text(team.name) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = selectedTeam?.name ?: "",
                            onValueChange = {
                                selectedTeam = teams.find { team -> team.name.equals(it, ignoreCase = true) }
                                isDropdownVisible = true
                            },
                            label = { Text("Equipo") },
                            modifier = Modifier.clickable {
                                isDropdownVisible = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(text = "Seleccionar posiciones del jugador:")
                        CheckboxGroup(
                            options = positions,
                            selectedOptions = selectedPositions,
                            onOptionSelected = { selectedPositions = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text("Seleccionar número de puntos: $points")
                        Slider(
                            value = points.toFloat(),
                            onValueChange = { points = it.toInt() },
                            valueRange = 0f..100f
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(text = "Introducir nombre del equipo rival:")
                        DropdownMenu(
                            expanded = selectedOpponentTeam != null,
                            onDismissRequest = { selectedOpponentTeam = null },
                        ) {
                            opponentTeams.forEach { team ->
                                DropdownMenuItem(
                                    onClick = { selectedOpponentTeam = team },
                                    text = { Text(team.name) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = selectedOpponentTeam?.name ?: "",
                            onValueChange = {},
                            label = { Text("Equipo Rival") },
                            modifier = Modifier.clickable { selectedOpponentTeam = opponentTeams.firstOrNull() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(text = "Selecciona la fecha del partido:")
                        CustomDatePicker(
                            selectedDate = selectedDate,
                            onDateChange = { selectedDate = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    OutlinedTextField(
        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time),
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha") },
        modifier = modifier.clickable {
            isDatePickerVisible = true
        }
    )

    if (isDatePickerVisible) {
        ShowDatePickerDialog(
            context = context,
            selectedDate = selectedDate,
            onDateChange = { date ->
                onDateChange(date)
                isDatePickerVisible = false
            }
        )
    }
}

@Composable
fun ShowDatePickerDialog(
    context: Context,
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit
) {
    var pickedDate by remember { mutableStateOf(selectedDate) }
    val onDateSet: (DatePicker, Int, Int, Int) -> Unit = { _, year, month, dayOfMonth ->
        pickedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
    }

    DisposableEffect(context) {
        val datePickerDialog = DatePickerDialog(
            context,
            onDateSet,
            pickedDate.get(Calendar.YEAR),
            pickedDate.get(Calendar.MONTH),
            pickedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            onDateChange(pickedDate)
            datePickerDialog.dismiss()
        }

        datePickerDialog.show()

        onDispose {
            datePickerDialog.dismiss()
        }
    }
}

@Composable
fun CheckboxGroup(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = {
                        val newSelectedOptions = if (it) {
                            selectedOptions + option
                        } else {
                            selectedOptions - option
                        }
                        onOptionSelected(newSelectedOptions)
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = option)
            }
        }
    }
}
