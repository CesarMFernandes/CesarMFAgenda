package com.example.cesarmfagenda.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cesarmfagenda.model.Agenda
import com.example.cesarmfagenda.repository.AgendaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaListScreen(
    onCreate: () -> Unit,
    onEdit: (Agenda) -> Unit,
    onLogout: () -> Unit
) {

    val repository = remember {
        AgendaRepository()
    }

    val scope = rememberCoroutineScope()

    var agendas by remember {
        mutableStateOf<List<Agenda>>(emptyList())
    }

    var erro by remember {
        mutableStateOf("")
    }

    fun carregarAgendas() {
        scope.launch {

            val resultado = repository.buscarAgendas()

            resultado.onSuccess {
                agendas = it
                erro = ""
            }

            resultado.onFailure {
                erro = it.message ?: "Erro ao carregar agendas."
            }
        }
    }

    LaunchedEffect(Unit) {
        carregarAgendas()
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )

                        Text("CesarMFAgenda")
                    }
                },

                actions = {

                    IconButton(
                        onClick = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            onLogout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        },

        floatingActionButton = {

            ExtendedFloatingActionButton(
                onClick = onCreate,
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null
                    )
                },
                text = {
                    Text("Nova agenda")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Minhas reuniões",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${agendas.size} agenda(s) cadastrada(s)",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (erro.isNotEmpty()) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = erro,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(
                    bottom = 100.dp
                )
            ) {

                items(agendas) { agenda ->

                    AgendaCard(
                        agenda = agenda,

                        onEdit = {
                            onEdit(agenda)
                        },

                        onDelete = {

                            scope.launch {

                                val resultado =
                                    repository.excluirAgenda(agenda.id)

                                resultado.onSuccess {
                                    carregarAgendas()
                                }

                                resultado.onFailure {
                                    erro = it.message
                                        ?: "Erro ao excluir agenda."
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AgendaCard(
    agenda: Agenda,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = agenda.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = agenda.data,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Pauta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = agenda.pauta
            )

            if (agenda.ata.isNotBlank()) {

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Ata",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = agenda.ata
                )
            }

            if (agenda.professores.isNotEmpty()) {

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Professores"
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                agenda.professores.forEach { professor ->

                    Text(
                        text = "• $professor",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Criado por: ${agenda.criadorNome}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = agenda.criadorEmail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Editar")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Excluir")
                }
            }
        }
    }
}