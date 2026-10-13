package com.example.agrodata

// ====================================================================
// LIGAÇÃO ENTRE O DIAGNÓSTICO DO CROP.HEALTH E AS FICHAS DO APP
// A API responde em inglês/latim; aqui procuramos palavras-chave no nome
// e no nome científico para abrir a ficha certa, em português.
// Se aparecer um nome que não é reconhecido, veja no Logcat (filtro
// "SCANNER_AGRODATA") como a API escreveu e acrescente a palavra aqui.
// ====================================================================

enum class TipoFicha { PRAGA, NUTRIENTE }

data class FichaCatalogo(val id: String, val tipo: TipoFicha)

private val regras: List<Pair<List<String>, FichaCatalogo>> = listOf(
    // pragas e doenças (a ordem importa: as mais específicas primeiro)
    listOf("leucoptera", "leaf miner") to FichaCatalogo("bicho-mineiro", TipoFicha.PRAGA),
    listOf("hypothenemus", "berry borer") to FichaCatalogo("broca", TipoFicha.PRAGA),
    listOf("brevipalpus", "leprosis", "ringspot") to FichaCatalogo("leprose", TipoFicha.PRAGA),
    listOf("oligonychus", "red mite", "red spider") to FichaCatalogo("acaro-vermelho", TipoFicha.PRAGA),
    listOf("meloidogyne", "nematode", "root-knot") to FichaCatalogo("nematoides", TipoFicha.PRAGA),
    listOf("mealybug", "scale insect", "coccus", "planococcus", "dysmicoccus") to FichaCatalogo("cochonilhas", TipoFicha.PRAGA),
    listOf("garcae", "syringae", "halo blight") to FichaCatalogo("mancha-aureolada", TipoFicha.PRAGA),
    listOf("cercospora", "brown eye spot") to FichaCatalogo("cercosporiose", TipoFicha.PRAGA),
    listOf("phoma") to FichaCatalogo("phoma", TipoFicha.PRAGA),
    listOf("hemileia", "leaf rust", "coffee rust") to FichaCatalogo("ferrugem", TipoFicha.PRAGA),
    listOf("cicada", "quesada") to FichaCatalogo("cigarra", TipoFicha.PRAGA),
    listOf("eacles", "caterpillar") to FichaCatalogo("lagarta", TipoFicha.PRAGA),
    // deficiências nutricionais
    listOf("nitrogen") to FichaCatalogo("nitrogenio", TipoFicha.NUTRIENTE),
    listOf("phosphorus") to FichaCatalogo("fosforo", TipoFicha.NUTRIENTE),
    listOf("potassium") to FichaCatalogo("potassio", TipoFicha.NUTRIENTE),
    listOf("calcium") to FichaCatalogo("calcio", TipoFicha.NUTRIENTE),
    listOf("magnesium") to FichaCatalogo("magnesio", TipoFicha.NUTRIENTE),
    listOf("sulfur", "sulphur") to FichaCatalogo("enxofre", TipoFicha.NUTRIENTE),
    listOf("boron") to FichaCatalogo("boro", TipoFicha.NUTRIENTE),
    listOf("zinc") to FichaCatalogo("zinco", TipoFicha.NUTRIENTE),
    listOf("iron deficiency", "iron chlorosis") to FichaCatalogo("ferro", TipoFicha.NUTRIENTE),
)

/** Procura a ficha do app correspondente ao diagnóstico. Retorna null se não houver. */
fun buscarFicha(nome: String?, nomeCientifico: String?): FichaCatalogo? {
    val texto = "${nome.orEmpty()} ${nomeCientifico.orEmpty()}".lowercase()
    return regras.firstOrNull { (chaves, _) -> chaves.any { it in texto } }?.second
}

/** Nome em português da ficha, para exibir no resultado. */
fun nomeDaFicha(ficha: FichaCatalogo): String? = when (ficha.tipo) {
    TipoFicha.PRAGA -> buscarPraga(ficha.id)?.nome
    TipoFicha.NUTRIENTE -> buscarNutriente(ficha.id)?.nome?.let { "Deficiência de $it" }
}

// ====================================================================
// TRADUÇÃO DE DIAGNÓSTICOS QUE NÃO TÊM FICHA NO CATÁLOGO
// A API devolve nomes e descrições em inglês. Aqui ficam o nome e uma
// descrição curta em português. Acrescente novos itens conforme aparecerem
// no Logcat (filtro "SCANNER_AGRODATA").
// ====================================================================
data class Traducao(val nome: String, val descricao: String?)

private val traducoes: List<Pair<List<String>, Traducao>> = listOf(
    listOf("healthy") to Traducao("Folha saudável", null),
    listOf("nutrient deficiency", "nutrient disorder") to Traducao(
        "Deficiência nutricional",
        "A folha mostra sinais de falta de nutrientes. Consulte o Guia Nutricional do app para identificar " +
                "qual nutriente pode estar faltando e confirme com análise foliar."
    ),
    listOf("cichorii") to Traducao(
        "Mancha-escura bacteriana",
        "Doença causada por bactéria, que forma manchas escuras e encharcadas nas folhas. " +
                "É favorecida por chuva, umidade alta e temperaturas amenas."
    ),
    listOf("anthracnose", "colletotrichum") to Traducao(
        "Antracnose",
        "Doença causada por fungos do gênero Colletotrichum. Provoca manchas escuras e secas em folhas, ramos " +
                "e frutos, e pode secar os ramos novos. Plantas enfraquecidas ou mal nutridas são as mais afetadas."
    ),
    listOf("sooty mold", "sooty mould") to Traducao(
        "Fumagina",
        "Camada escura de fungo que cresce sobre a substância açucarada eliminada por insetos sugadores, como " +
                "cochonilhas. Atrapalha a fotossíntese, e o controle é feito combatendo os insetos."
    ),
    listOf("coffee berry disease") to Traducao(
        "Doença dos frutos do café",
        "Doença fúngica que ataca os frutos verdes, causando manchas escuras e queda dos frutos. " +
                "Em caso de suspeita, procure a assistência técnica."
    ),
    listOf("sunburn", "sunscald", "sun scorch") to Traducao(
        "Escaldadura (queima pelo sol)",
        "Dano causado pelo excesso de sol e calor, que deixa áreas secas e queimadas nas folhas mais expostas. " +
                "Não é causado por fungos ou insetos."
    ),
    listOf("drought", "water stress") to Traducao(
        "Estresse hídrico (falta de água)",
        "A planta está sofrendo com a falta de água: folhas murchas, enroladas ou com as bordas secas."
    ),
    listOf("frost", "cold damage") to Traducao(
        "Dano por geada",
        "Queima das folhas e dos brotos causada por temperaturas muito baixas. As partes atingidas ficam " +
                "escuras e secas, principalmente no alto da planta."
    ),
    listOf("herbicide") to Traducao(
        "Dano por herbicida",
        "Folhas deformadas, amareladas ou queimadas pelo contato com herbicidas, geralmente levados pelo vento " +
                "durante a aplicação nas entrelinhas."
    ),
    listOf("mechanical damage") to Traducao(
        "Dano mecânico",
        "Ferimentos causados por máquinas, ferramentas, vento ou granizo. Eles podem servir de porta de entrada para doenças."
    ),
    listOf("algal leaf spot") to Traducao(
        "Mancha de algas",
        "Manchas causadas por algas na superfície das folhas, comuns em locais muito úmidos e sombreados. Em geral, causam pouco dano."
    ),
    listOf("aphid") to Traducao(
        "Pulgões",
        "Pequenos insetos sugadores que se concentram nos brotos novos, podem deformar as folhas e favorecem a fumagina."
    ),
    listOf("thrips") to Traducao(
        "Tripes",
        "Insetos muito pequenos que raspam as folhas novas, deixando áreas prateadas ou bronzeadas."
    ),
    listOf("whitefl") to Traducao(
        "Mosca-branca",
        "Insetos sugadores que ficam na face de baixo das folhas e eliminam uma substância açucarada que favorece a fumagina."
    ),
)

/** Indica se o diagnóstico é de planta saudável. */
fun ehSaudavel(nome: String?): Boolean = nome?.lowercase()?.contains("healthy") == true

/** Nome e descrição em português para diagnósticos sem ficha. Retorna null se não houver tradução. */
fun buscarTraducao(nome: String?, nomeCientifico: String?): Traducao? {
    val texto = "${nome.orEmpty()} ${nomeCientifico.orEmpty()}".lowercase()
    return traducoes.firstOrNull { (chaves, _) -> chaves.any { it in texto } }?.second
}