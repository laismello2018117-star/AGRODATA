package com.example.agrodata

// ====================================================================
// CATÁLOGO FITOSSANITÁRIO
// Todo o conteúdo das pragas e doenças fica aqui. Para incluir uma nova,
// basta adicionar um item na lista: a tela de detalhes é a mesma para todas.
// ====================================================================
data class DetalhePraga(
    val id: String,               // usado na navegação; não mude depois de publicado
    val nome: String,
    val nomeCientifico: String,
    val tipo: String,             // "Praga", "Doença fúngica", "Doença bacteriana"...
    val resumo: String,           // frase curta mostrada no card do catálogo
    val imagem: Int,
    val danos: String,
    val condicoes: String,
    val manejo: String
)

val catalogoPragas = listOf(
    DetalhePraga(
        id = "broca",
        nome = "Broca-do-café",
        nomeCientifico = "Hypothenemus hampei",
        tipo = "Praga",
        resumo = "Ataca os frutos.",
        imagem = R.drawable.broca,
        danos = "O pequeno besouro perfura os frutos do cafeeiro e deposita os ovos no interior da semente. " +
                "As larvas se alimentam da amêndoa, destruindo-a parcial ou totalmente. Isso causa perda de peso " +
                "dos grãos e depreciação da qualidade da bebida (grãos brocados).",
        condicoes = "• Lavouras muito densas, sombreadas ou com espaçamentos fechados (alta umidade interna).\n" +
                "• Frutos da safra anterior deixados nos ramos ou no chão.\n" +
                "• Temperaturas amenas a quentes associadas a alta umidade relativa do ar.",
        manejo = "1. Fazer uma colheita e um repasse bem feitos, para não deixar alimento para a praga na entressafra.\n" +
                "2. Monitorar os frutos a partir da fase de chumbinho, coletando amostras no terço médio das plantas em vários pontos do talhão.\n" +
                "3. Iniciar o controle químico seletivo apenas quando a infestação atingir o nível de controle (em geral, de 3% a 5% de frutos brocados)."
    ),
    DetalhePraga(
        id = "bicho-mineiro",
        nome = "Bicho-mineiro",
        nomeCientifico = "Leucoptera coffeella",
        tipo = "Praga",
        resumo = "Causa desfolha.",
        imagem = R.drawable.bicho_mineiro,
        danos = "As larvas desta mariposa minam a parte interna das folhas, destruindo o tecido fotossintético. " +
                "Isso causa necrose na região afetada e desfolha severa, começando pelo terço superior do cafeeiro, " +
                "o que reduz a produtividade das safras seguintes.",
        condicoes = "• Períodos de estiagem prolongada.\n" +
                "• Altas temperaturas.\n" +
                "• Espaçamentos muito abertos e lavouras com face voltada para o sol, com mais luz e vento.",
        manejo = "1. Monitorar quinzenalmente, coletando folhas do 3º ou 4º par de ramos do terço médio e superior.\n" +
                "2. Preservar os inimigos naturais, como as vespas predadoras.\n" +
                "3. Usar inseticidas sistêmicos via solo ou foliares apenas ao atingir o nível de controle recomendado pela assistência técnica."
    ),
    DetalhePraga(
        id = "ferrugem",
        nome = "Ferrugem",
        nomeCientifico = "Hemileia vastatrix",
        tipo = "Doença fúngica",
        resumo = "Manchas laranjas.",
        imagem = R.drawable.ferrugem,
        danos = "A ferrugem destrói as células das folhas, reduzindo a capacidade fotossintética do cafeeiro. " +
                "Forma pústulas com pó alaranjado na face inferior das folhas, causando desfolha precoce e o " +
                "secamento dos ramos produtivos.",
        condicoes = "• Períodos chuvosos combinados com temperaturas quentes (em torno de 22 °C a 24 °C).\n" +
                "• Água livre na superfície da folha por pelo menos 6 horas, o que permite a germinação dos esporos.\n" +
                "• Pouca circulação de ar no interior da planta.\n" +
                "• Plantas com carga alta de frutos.",
        manejo = "1. Usar cultivares resistentes à ferrugem no plantio.\n" +
                "2. Fazer podas e desbrotas para melhorar o arejamento e a entrada de luz.\n" +
                "3. Aplicar fungicidas sistêmicos associados a fungicidas cúpricos protetores, de forma preventiva ou no início da doença."
    ),
    DetalhePraga(
        id = "cochonilhas",
        nome = "Cochonilhas",
        nomeCientifico = "Coccus viridis, Planococcus spp. e Dysmicoccus spp.",
        tipo = "Praga",
        resumo = "Sugadores.",
        imagem = R.drawable.cochonilhas,
        danos = "As cochonilhas sugam a seiva da planta, enfraquecendo ramos, folhas e rosetas. Muitas espécies " +
                "eliminam uma substância açucarada que favorece a fumagina, um fungo escuro que cobre as folhas e " +
                "atrapalha a fotossíntese. A cochonilha-da-raiz ataca o sistema radicular e pode causar amarelecimento " +
                "e até a morte de plantas, principalmente em lavouras novas.",
        condicoes = "• Tempo quente e seco.\n" +
                "• Presença de formigas doceiras, que protegem as cochonilhas e ajudam a espalhá-las.\n" +
                "• Mudas já infestadas levadas para a lavoura.",
        manejo = "1. Inspecionar as mudas antes do plantio e descartar as infestadas.\n" +
                "2. Controlar as formigas doceiras, que favorecem as colônias.\n" +
                "3. Preservar os inimigos naturais, como joaninhas e fungos que atacam as cochonilhas.\n" +
                "4. Aplicar inseticidas registrados apenas nos focos; para a cochonilha-da-raiz, usar produtos sistêmicos via solo."
    ),
    DetalhePraga(
        id = "acaro-vermelho",
        nome = "Ácaros",
        nomeCientifico = "Oligonychus ilicis (ácaro-vermelho)",
        tipo = "Praga",
        resumo = "Bronzeamento.",
        imagem = R.drawable.acaro_v,
        danos = "O ácaro-vermelho vive na face superior das folhas e se alimenta das células superficiais. As folhas " +
                "perdem o brilho e ficam com aspecto bronzeado. Em ataques fortes, há queda de folhas e redução do " +
                "vigor da planta.",
        condicoes = "• Períodos secos e quentes.\n" +
                "• Folhas cobertas de poeira, comuns perto de carreadores e estradas.\n" +
                "• Uso frequente de inseticidas de amplo espectro, que eliminam os ácaros predadores.",
        manejo = "1. Monitorar as folhas com lupa de bolso durante os períodos secos.\n" +
                "2. Preservar os ácaros predadores, evitando produtos de amplo espectro sem necessidade.\n" +
                "3. Reduzir a poeira nos carreadores.\n" +
                "4. Usar acaricidas seletivos apenas quando a infestação justificar."
    ),
    DetalhePraga(
        id = "cigarra",
        nome = "Cigarra",
        nomeCientifico = "Quesada gigas, Fidicinoides spp. e outras",
        tipo = "Praga",
        resumo = "Dano na raiz.",
        imagem = R.drawable.cigarra_do_cafeeiro,
        danos = "As ninfas vivem no solo por longos períodos sugando a seiva das raízes. A planta atacada fica com " +
                "folhas amareladas e menores, perde vigor, produz menos e pode apresentar seca de ramos. Em infestações " +
                "fortes, a planta pode morrer.",
        condicoes = "• Lavouras mais velhas, já instaladas há vários anos.\n" +
                "• Áreas com histórico de cigarras na região.\n" +
                "• A emergência dos adultos, marcada pelo canto, acontece principalmente no início do período chuvoso.",
        manejo = "1. Monitorar a presença de ninfas abrindo pequenas trincheiras no solo sob a copa, e observar os orifícios de saída e as cascas (exúvias) deixadas pelos adultos.\n" +
                "2. Registrar os talhões com histórico de ataque.\n" +
                "3. Usar inseticidas sistêmicos via solo no período chuvoso, quando recomendado pela assistência técnica."
    ),
    DetalhePraga(
        id = "mancha-aureolada",
        nome = "Mancha-aureolada",
        nomeCientifico = "Pseudomonas syringae pv. garcae",
        tipo = "Doença bacteriana",
        resumo = "Bactéria.",
        imagem = R.drawable.mancha_aureolada,
        danos = "A bactéria causa manchas escuras nas folhas cercadas por um halo amarelado (a \"auréola\"). Também " +
                "ataca ramos novos, flores e frutos jovens, provocando a seca dos ponteiros. É especialmente danosa em " +
                "viveiros e lavouras novas.",
        condicoes = "• Regiões altas, com temperaturas amenas.\n" +
                "• Ventos fortes e frequentes, que causam ferimentos nas folhas e facilitam a entrada da bactéria.\n" +
                "• Chuvas, que espalham a bactéria entre as plantas.",
        manejo = "1. Plantar mudas sadias, de viveiros de procedência conhecida.\n" +
                "2. Implantar quebra-ventos nas áreas mais expostas.\n" +
                "3. Evitar ferimentos nas plantas durante os tratos culturais.\n" +
                "4. Aplicar produtos cúpricos de forma preventiva nos períodos favoráveis à doença."
    ),
    DetalhePraga(
        id = "leprose",
        nome = "Ácaro-da-leprose",
        nomeCientifico = "Brevipalpus spp.",
        tipo = "Praga (vetor de vírus)",
        resumo = "Queda de frutos.",
        imagem = R.drawable.acaro_da_leprose,
        danos = "Este ácaro transmite o vírus da mancha-anular do cafeeiro (Coffee ringspot virus, CoRSV), causador " +
                "da leprose. Surgem lesões cloróticas em forma de anel nas folhas, nos ramos e nos frutos. O ataque " +
                "severo causa queda de frutos e folhas e a seca dos ramos afetados.",
        condicoes = "• Clima quente e com baixa umidade relativa do ar.\n" +
                "• Períodos prolongados de estiagem (veranicos).\n" +
                "• Folhas com poeira acumulada, comuns nas beiras de carreadores.",
        manejo = "1. Monitorar o terço interno e inferior das plantas, inspecionando ramos e frutos com lupa de bolso (10x).\n" +
                "2. Fazer a poda de limpeza, retirando da lavoura os ramos secos afetados.\n" +
                "3. Aplicar acaricidas seletivos logo no início da infestação, preservando os ácaros predadores."
    ),
    DetalhePraga(
        id = "lagarta",
        nome = "Lagarta-dos-cafezais",
        nomeCientifico = "Eacles imperialis magnifica",
        tipo = "Praga",
        resumo = "Desfolha rápida.",
        imagem = R.drawable.lagarta_dos_cafezais,
        danos = "As lagartas são grandes e se alimentam das folhas com voracidade. Em surtos, podem desfolhar " +
                "plantas inteiras em poucos dias, prejudicando a produção atual e a seguinte.",
        condicoes = "• Os surtos são ocasionais e costumam ocorrer em reboleiras (manchas dentro do talhão).\n" +
                "• Desequilíbrios causados pelo uso excessivo de inseticidas, que eliminam os inimigos naturais.",
        manejo = "1. Fazer o monitoramento visual das folhas e dos ramos.\n" +
                "2. Em focos pequenos, fazer a catação manual das lagartas.\n" +
                "3. Preferir produtos biológicos, como Bacillus thuringiensis, aplicados com as lagartas ainda pequenas.\n" +
                "4. Usar inseticidas químicos apenas em surtos, preservando os inimigos naturais."
    ),
    DetalhePraga(
        id = "cercosporiose",
        nome = "Cercosporiose",
        nomeCientifico = "Cercospora coffeicola",
        tipo = "Doença fúngica",
        resumo = "Manchas em forma de olho.",
        imagem = R.drawable.cercosporiose,
        danos = "Também chamada de mancha-de-olho-pardo. Nas folhas, forma manchas arredondadas de cor parda, com " +
                "centro claro e um halo amarelado, o que lembra um olho. Nos frutos, causa lesões escuras e afundadas, " +
                "amadurecimento antecipado e queda, prejudicando a qualidade da bebida. Ataques fortes provocam desfolha.",
        condicoes = "• Plantas mal nutridas, principalmente com falta de nitrogênio e potássio.\n" +
                "• Lavouras e mudas muito expostas ao sol.\n" +
                "• Falta de água e outros estresses que enfraquecem a planta.",
        manejo = "1. Manter a adubação equilibrada, conforme a análise de solo e foliar (consulte o Guia Nutricional).\n" +
                "2. Evitar o estresse hídrico, com irrigação quando possível e cobertura do solo.\n" +
                "3. Nos viveiros, usar sombreamento parcial nas mudas.\n" +
                "4. Aplicar fungicidas registrados quando a doença atingir o nível recomendado pela assistência técnica."
    ),
    DetalhePraga(
        id = "phoma",
        nome = "Mancha-de-phoma",
        nomeCientifico = "Phoma spp.",
        tipo = "Doença fúngica",
        resumo = "Seca de brotos e flores.",
        imagem = R.drawable.phoma,
        danos = "Ataca as partes novas da planta. As folhas novas ficam com manchas escuras, geralmente nas bordas, " +
                "e se deformam. Os ponteiros e os ramos novos secam, e os botões florais e os frutos recém-formados " +
                "escurecem e caem, reduzindo a produção.",
        condicoes = "• Regiões altas e frias.\n" +
                "• Ventos frios e frequentes, que causam ferimentos nas folhas e brotos.\n" +
                "• Umidade alta e neblina, principalmente perto da florada.",
        manejo = "1. Implantar quebra-ventos nas áreas mais expostas.\n" +
                "2. Nas áreas com histórico da doença, aplicar fungicidas registrados de forma preventiva, antes e depois da florada.\n" +
                "3. Manter a nutrição equilibrada, para que a planta se recupere melhor dos ataques."
    ),
    DetalhePraga(
        id = "nematoides",
        nome = "Nematoides",
        nomeCientifico = "Meloidogyne spp.",
        tipo = "Praga de solo",
        resumo = "Atacam as raízes.",
        imagem = R.drawable.nematoides,
        danos = "São vermes microscópicos que vivem no solo e atacam as raízes. Dependendo da espécie, formam " +
                "engrossamentos (galhas) ou causam rachaduras e descascamento nas raízes. A planta passa a absorver " +
                "menos água e nutrientes: fica amarelada, com folhas pequenas, murcha nas horas mais quentes, perde " +
                "folhas e produz menos. Os sintomas costumam aparecer em reboleiras e são facilmente confundidos com falta de adubo.",
        condicoes = "• Solos arenosos.\n" +
                "• Mudas contaminadas levadas para a lavoura.\n" +
                "• Solo carregado por máquinas, ferramentas e água de enxurrada, que espalham os nematoides entre áreas.",
        manejo = "1. Plantar apenas mudas sadias, de viveiros com certificação.\n" +
                "2. Antes do plantio, fazer a análise nematológica do solo e das raízes em laboratório.\n" +
                "3. Em áreas infestadas, usar cultivares ou porta-enxertos resistentes à espécie de nematoide presente.\n" +
                "4. Limpar máquinas e ferramentas antes de passar para outro talhão.\n" +
                "5. Usar nematicidas registrados apenas com orientação técnica."
    )
)

fun buscarPraga(id: String): DetalhePraga? = catalogoPragas.find { it.id == id }
