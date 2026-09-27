package com.example.cesarmfagenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.cesarmfagenda.model.Agenda
import com.example.cesarmfagenda.ui.AgendaFormScreen
import com.example.cesarmfagenda.ui.AgendaListScreen
import com.example.cesarmfagenda.ui.LoginScreen
import com.example.cesarmfagenda.ui.RegisterScreen
import com.example.cesarmfagenda.ui.theme.CesarMFAgendaTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            CesarMFAgendaTheme {

                AgendaApp()
            }
        }
    }
}

@Composable
fun AgendaApp() {

    val auth = FirebaseAuth.getInstance()

    var tela by remember {

        mutableStateOf(
            if (auth.currentUser != null)
                "agendas"
            else
                "login"
        )
    }

    var agendaEditar by remember {
        mutableStateOf<Agenda?>(null)
    }

    when (tela) {

        "login" -> {

            LoginScreen(

                onLogin = {
                    tela = "agendas"
                },

                onRegister = {
                    tela = "cadastro"
                }
            )
        }

        "cadastro" -> {

            RegisterScreen(

                onRegistered = {
                    tela = "agendas"
                },

                onBack = {
                    tela = "login"
                }
            )
        }

        "agendas" -> {

            AgendaListScreen(

                onCreate = {

                    agendaEditar = null

                    tela = "formulario"
                },

                onEdit = { agenda ->

                    agendaEditar = agenda

                    tela = "formulario"
                },

                onLogout = {

                    tela = "login"
                }
            )
        }

        "formulario" -> {

            AgendaFormScreen(

                agendaEditar = agendaEditar,

                onSave = {

                    agendaEditar = null

                    tela = "agendas"
                },

                onCancel = {

                    agendaEditar = null

                    tela = "agendas"
                }
            )
        }
    }
}