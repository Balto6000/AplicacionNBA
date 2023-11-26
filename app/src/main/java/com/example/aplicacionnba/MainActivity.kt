package com.example.aplicacionnba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aplicacionnba.ui.theme.AplicacionNBATheme
import java.util.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionNBATheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

data class Team(val name: String, val color: Color)

data class Player(
    val name: String,
    val team: Team,
    val positions: List<String>,
    val points: Int,
    val opponentTeam: Team,
    val date: String,
    val imageResId: Int
)

@Composable
fun NBAApp(navController: NavController) {
    var isDeleteMode by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var players by remember {
        mutableStateOf(
            listOf(
                Player(
                    "Michael Jordan",
                    Team("Boston Celtics", Color.Red),
                    listOf("PG"),
                    20,
                    Team("Brooklyn Nets", Color.Blue),
                    "2023-01-01",
                    R.drawable.player1
                ),
                Player(
                    "Kobe Bryant",
                    Team("Brooklyn Nets", Color.Blue),
                    listOf("SG"),
                    15,
                    Team("Boston Celtics", Color.Red),
                    "2023-01-02",
                    R.drawable.player2
                ),
                Player(
                    "Kevin Durant",
                    Team("Boston Celtics", Color.Red),
                    listOf("PF", "C"),
                    25,
                    Team("New York Knicks", Color.Green),
                    "2023-01-03",
                    R.drawable.player3
                )
            )
        )
    }

    val filteredPlayers = if (selectedTeam?.name.isNullOrBlank()) {
        players
    } else {
        players.filter { it.team.name == selectedTeam?.name }
    }
    var playersToDelete by remember { mutableStateOf(mutableListOf<Player>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            teams = players.map { it.team },
            onTeamSelected = { team ->
                selectedTeam = team
            },
            onClear = {
                selectedTeam = null
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerList(
            players = filteredPlayers,
            onDeleteClick = { player ->
                if (isDeleteMode) {
                    players = players.filter { it != player }
                }
            },
            playersToDelete = playersToDelete,
            isDeleteMode = isDeleteMode
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            var isAddButtonEnabled by remember { mutableStateOf(true) }

            ExtendedFloatingActionButton(
                text = { Text("Añadir jugador") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onClick = {
                    if (!isDeleteMode) {
                        selectedPosition = ""
                        navController.navigate("createPlayerScreen")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .height(56.dp)
                    .clickable { !isDeleteMode && isAddButtonEnabled },
            )


            ExtendedFloatingActionButton(
                text = { Text("Borrar") },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                onClick = {
                    if (isDeleteMode) {
                        showDialog = true
                    } else {
                        isDeleteMode = true
                        isAddButtonEnabled = false
                    }
                },
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .height(56.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "Borrar jugadores")
            },
            text = {
                Text("¿Estás seguro de que quieres borrar estos jugadores?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        players = players.filterNot { it in playersToDelete }
                        isDeleteMode = false
                        playersToDelete.clear()
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(teams: List<Team>, onTeamSelected: (Team) -> Unit, onClear: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }

    val filteredTeams = teams.filter {
        it.name.lowercase().contains(searchText.lowercase())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                isDropdownVisible = it.isNotEmpty()
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            placeholder = { Text("Buscar por equipo") },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchText = ""
                            selectedTeam = null
                            onClear()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        )

        DropdownMenu(
            expanded = isDropdownVisible,
            onDismissRequest = {
                isDropdownVisible = false
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            if (filteredTeams.isEmpty() && searchText.isNotEmpty()) {
                Text("No se encontraron equipos")
            } else {
                filteredTeams.forEach { team ->
                    TextButton(
                        onClick = {
                            selectedTeam = team
                            onTeamSelected(team)
                            searchText = team.name
                            isDropdownVisible = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(team.name)
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedTeam) {
        if (selectedTeam != null) {
            onTeamSelected(selectedTeam!!)
        }
    }
}

@Composable
fun PlayerList(
    players: List<Player>,
    onDeleteClick: (Player) -> Unit,
    playersToDelete: MutableList<Player>,
    isDeleteMode: Boolean
) {
    LazyColumn {
        items(players) { player ->
            PlayerListItem(player = player, onDeleteClick = onDeleteClick, playersToDelete = playersToDelete, isDeleteMode = isDeleteMode)
        }
    }
}

@Composable
fun PlayerListItem(
    player: Player,
    onDeleteClick: (Player) -> Unit,
    playersToDelete: MutableList<Player>,
    isDeleteMode: Boolean
) {
    var isChecked by remember { mutableStateOf(player in playersToDelete) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(player.team.color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = player.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Column {
                Text(text = player.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${player.team.name} vs ${player.opponentTeam.name}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Puntos: ${player.points}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Fecha: ${player.date}", style = MaterialTheme.typography.bodyMedium)
            }

            Checkbox(
                checked = isChecked,
                onCheckedChange = { newCheckedState ->
                    isChecked = newCheckedState
                    if (isChecked) {
                        playersToDelete.add(player)
                    } else {
                        playersToDelete.remove(player)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(if (isDeleteMode) 1f else 0f)
            )
        }
    }
}