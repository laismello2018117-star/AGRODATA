package com.example.agrodata

// ====================================================================
// NOMES POPULARES EM PORTUGUÊS
// O Pl@ntNet nem sempre tem o nome em português e devolve em inglês.
// Para as plantas que mais aparecem na lavoura de café, usamos este
// dicionário próprio, que tem prioridade sobre o nome vindo da API.
// ====================================================================
object NomesPopulares {

    // Espécie exata (nome científico sem autor)
    private val porEspecie = mapOf(
        // Cafeeiros
        "Coffea arabica" to "Café-arábica",
        "Coffea canephora" to "Café-conilon (robusta)",
        "Coffea liberica" to "Café-libérica",

        // Plantas daninhas comuns no cafezal
        "Bidens pilosa" to "Picão-preto",
        "Galinsoga parviflora" to "Picão-branco",
        "Commelina benghalensis" to "Trapoeraba",
        "Urochloa decumbens" to "Braquiária",
        "Brachiaria decumbens" to "Braquiária",
        "Cynodon dactylon" to "Grama-seda",
        "Digitaria horizontalis" to "Capim-colchão",
        "Eleusine indica" to "Capim-pé-de-galinha",
        "Cyperus rotundus" to "Tiririca",
        "Sida rhombifolia" to "Guanxuma",
        "Emilia sonchifolia" to "Falsa-serralha",
        "Leonurus sibiricus" to "Rubim",
        "Richardia brasiliensis" to "Poaia-branca",
        "Conyza bonariensis" to "Buva",
        "Portulaca oleracea" to "Beldroega",
        "Nicandra physalodes" to "Joá-de-capote",
        "Raphanus raphanistrum" to "Nabiça"
    )

    // Quando só o gênero é conhecido (várias espécies com o mesmo nome popular)
    private val porGenero = mapOf(
        "Coffea" to "Cafeeiro",
        "Amaranthus" to "Caruru",
        "Ipomoea" to "Corda-de-viola",
        "Conyza" to "Buva"
    )

    /** Devolve o nome em português, ou null se não estiver no dicionário. */
    fun buscar(nomeCientifico: String, genero: String?): String? =
        porEspecie[nomeCientifico.trim()] ?: genero?.let { porGenero[it.trim()] }
}
