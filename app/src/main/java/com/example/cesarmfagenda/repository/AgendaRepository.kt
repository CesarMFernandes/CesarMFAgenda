package com.example.cesarmfagenda.repository

import com.example.cesarmfagenda.model.Agenda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AgendaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val agendasCollection = db.collection("agendas")

    // Criar uma nova agenda
    suspend fun criarAgenda(agenda: Agenda): Result<String> {
        return try {
            val documento = agendasCollection.add(agenda).await()

            Result.success(documento.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Buscar todas as agendas
    suspend fun buscarAgendas(): Result<List<Agenda>> {
        return try {
            val snapshot = agendasCollection
                .get()
                .await()

            val agendas = snapshot.documents.mapNotNull { documento ->
                documento.toObject(Agenda::class.java)?.copy(
                    id = documento.id
                )
            }

            Result.success(agendas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Buscar uma agenda pelo ID
    suspend fun buscarAgenda(id: String): Result<Agenda?> {
        return try {
            val documento = agendasCollection
                .document(id)
                .get()
                .await()

            if (documento.exists()) {
                val agenda = documento.toObject(Agenda::class.java)?.copy(
                    id = documento.id
                )

                Result.success(agenda)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualizar uma agenda
    suspend fun atualizarAgenda(agenda: Agenda): Result<Unit> {
        return try {
            agendasCollection
                .document(agenda.id)
                .set(agenda)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Excluir uma agenda
    suspend fun excluirAgenda(id: String): Result<Unit> {
        return try {
            agendasCollection
                .document(id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}