package com.example.agrodata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ====================================================================
// TALHÕES COMPARTILHADOS
// O scanner e a calculadora ficam na mesma tela (home), então os dois
// recebem esta mesma instância: o talhão escolhido em um vale para o outro.
// ====================================================================
class TalhaoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AgroDataDatabase.get(application).talhaoDao()

    val talhoes: StateFlow<List<Talhao>> = dao.listar()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Guardamos só o id: se o talhão for editado, a seleção já mostra os dados novos
    private val idSelecionado = MutableStateFlow<Int?>(null)

    val talhaoSelecionado: StateFlow<Talhao?> = combine(talhoes, idSelecionado) { lista, id ->
        lista.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selecionar(talhao: Talhao) {
        idSelecionado.value = talhao.id
    }

    /** id nulo = novo talhão; com id = edição de um existente. */
    fun salvar(id: Int?, nome: String, areaHa: Double?) {
        val nomeLimpo = nome.trim()
        if (nomeLimpo.isEmpty()) return
        viewModelScope.launch {
            if (id == null) {
                val novoId = dao.inserir(Talhao(nome = nomeLimpo, areaHa = areaHa))
                idSelecionado.value = novoId.toInt()   // já deixa o novo talhão selecionado
            } else {
                dao.atualizar(Talhao(id = id, nome = nomeLimpo, areaHa = areaHa))
            }
        }
    }

    fun excluir(talhao: Talhao) {
        viewModelScope.launch {
            dao.excluir(talhao)
            if (idSelecionado.value == talhao.id) idSelecionado.value = null
        }
    }
}
