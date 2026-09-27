package com.example.cesarmfagenda.model

data class Agenda(
    val id: String = "",
    val titulo: String = "",
    val pauta: String = "",
    val ata: String = "",
    val data: String = "",
    val professores: List<String> = emptyList(),

    val criadorId: String = "",
    val criadorNome: String = "",
    val criadorEmail: String = ""
)