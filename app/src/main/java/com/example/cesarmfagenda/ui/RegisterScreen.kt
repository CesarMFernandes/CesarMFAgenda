package com.example.cesarmfagenda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Agenda",
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Criar conta",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Comece a organizar suas reuniões",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        erro = ""
                    },
                    label = { Text("Nome") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        erro = ""
                    },
                    label = { Text("E-mail") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        erro = ""
                    },
                    label = { Text("Senha") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {

                        if (
                            nome.isBlank() ||
                            email.isBlank() ||
                            senha.isBlank()
                        ) {
                            erro = "Preencha todos os campos."
                            return@Button
                        }

                        auth.createUserWithEmailAndPassword(
                            email,
                            senha
                        )
                            .addOnSuccessListener { result ->

                                val uid =
                                    result.user?.uid
                                        ?: return@addOnSuccessListener

                                val usuario = hashMapOf(
                                    "nome" to nome,
                                    "email" to email
                                )

                                db.collection("usuarios")
                                    .document(uid)
                                    .set(usuario)
                                    .addOnSuccessListener {
                                        onRegistered()
                                    }
                                    .addOnFailureListener {
                                        erro =
                                            "Conta criada, mas houve erro ao salvar o usuário."
                                    }
                            }
                            .addOnFailureListener {
                                erro =
                                    it.message ?: "Erro ao criar conta."
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cadastrar")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Voltar")
                }

                if (erro.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = erro,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}