package com.example.cesarmfagenda.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cesarmfagenda.model.Agenda
import com.example.cesarmfagenda.repository.AgendaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun AgendaFormScreen(
    agendaEditar: Agenda?,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {

    val repository = remember {
        AgendaRepository()
    }

    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var titulo by remember {
        mutableStateOf(agendaEditar?.titulo ?: "")
    }

    var data by remember {
        mutableStateOf(agendaEditar?.data ?: "")
    }

    var pauta by remember {
        mutableStateOf(agendaEditar?.pauta ?: "")
    }

    var ata by remember {
        mutableStateOf(agendaEditar?.ata ?: "")
    }

    var professoresTexto by remember {
        mutableStateOf(
            agendaEditar
                ?.professores
                ?.joinToString("\n")
                ?: ""
        )
    }

    var erro by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        Text(
            text =
                if (agendaEditar == null)
                    "Nova Agenda"
                else
                    "Editar Agenda",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
            },
            label = {
                Text("Título")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = data,
            onValueChange = {
                data = it
            },
            label = {
                Text("Data")
            },
            placeholder = {
                Text("Ex.: 30/09/2026")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = pauta,
            onValueChange = {
                pauta = it
            },
            label = {
                Text("Pauta")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = ata,
            onValueChange = {
                ata = it
            },
            label = {
                Text("Ata")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = professoresTexto,
            onValueChange = {
                professoresTexto = it
            },
            label = {
                Text("Professores convocados")
            },
            placeholder = {
                Text("Digite um professor por linha")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                if (
                    titulo.isBlank() ||
                    data.isBlank() ||
                    pauta.isBlank()
                ) {
                    erro = "Preencha título, data e pauta."
                    return@Button
                }

                val usuario = auth.currentUser

                if (usuario == null) {
                    erro = "Usuário não autenticado."
                    return@Button
                }

                val professores =
                    professoresTexto
                        .lines()
                        .map {
                            it.trim()
                        }
                        .filter {
                            it.isNotEmpty()
                        }

                // EDIÇÃO
                if (agendaEditar != null) {

                    val agendaAtualizada =
                        agendaEditar.copy(
                            titulo = titulo,
                            data = data,
                            pauta = pauta,
                            ata = ata,
                            professores = professores
                        )

                    scope.launch {

                        val resultado =
                            repository.atualizarAgenda(
                                agendaAtualizada
                            )

                        resultado.onSuccess {
                            onSave()
                        }

                        resultado.onFailure {
                            erro = it.message
                                ?: "Erro ao atualizar agenda."
                        }
                    }

                } else {

                    // CRIAÇÃO
                    db.collection("usuarios")
                        .document(usuario.uid)
                        .get()
                        .addOnSuccessListener { document ->

                            val nome =
                                document.getString("nome")
                                    ?: usuario.email
                                    ?: "Usuário"

                            val novaAgenda = Agenda(
                                titulo = titulo,
                                data = data,
                                pauta = pauta,
                                ata = ata,
                                professores = professores,
                                criadorId = usuario.uid,
                                criadorNome = nome,
                                criadorEmail =
                                    usuario.email ?: ""
                            )

                            scope.launch {

                                val resultado =
                                    repository.criarAgenda(
                                        novaAgenda
                                    )

                                resultado.onSuccess {
                                    onSave()
                                }

                                resultado.onFailure {
                                    erro = it.message
                                        ?: "Erro ao criar agenda."
                                }
                            }
                        }
                        .addOnFailureListener {
                            erro = it.message
                                ?: "Erro ao buscar dados do usuário."
                        }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (agendaEditar == null)
                    "Criar agenda"
                else
                    "Salvar alterações"
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        if (erro.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = erro,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}