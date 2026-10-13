package com.example.agrodata

// ====================================================================
// GUIA NUTRICIONAL VISUAL
// Todo o conteúdo dos nutrientes fica aqui. Para incluir um novo,
// basta adicionar um item na lista: a tela de detalhes é a mesma para todos.
// ====================================================================
data class DetalheNutriente(
    val id: String,               // usado na navegação; não mude depois de publicado
    val nome: String,
    val classificacao: String,    // macronutriente primário, secundário ou micronutriente
    val resumo: String,           // frase curta mostrada na lista
    val imagem: Int,
    val folhasAfetadas: String,   // "mais velhas" ou "mais novas": ajuda no diagnóstico
    val funcao: String,
    val sintomas: String,
    val condicoes: String,
    val correcao: String
)

val catalogoNutrientes = listOf(
    DetalheNutriente(
        id = "nitrogenio",
        nome = "Nitrogênio (N)",
        classificacao = "Macronutriente primário",
        resumo = "Amarelamento foliar.",
        imagem = R.drawable.nitrogenio,
        folhasAfetadas = "mais velhas",
        funcao = "É o nutriente ligado ao crescimento vegetativo. Faz parte da clorofila e das proteínas, " +
                "e é essencial para a formação de novos ramos e folhas, que vão sustentar a produção do ano seguinte.",
        sintomas = "• Amarelamento uniforme das folhas, começando pelas mais velhas.\n" +
                "• Folhas menores e crescimento lento dos ramos.\n" +
                "• Em casos severos, queda de folhas e seca dos ponteiros.",
        condicoes = "• Solos pobres em matéria orgânica.\n" +
                "• Chuvas intensas, que arrastam o nitrogênio para camadas profundas do solo.\n" +
                "• Carga alta de frutos, que consome grande parte do nitrogênio da planta.\n" +
                "• Períodos secos, que dificultam a absorção.",
        correcao = "1. Fazer a adubação nitrogenada parcelada ao longo do período chuvoso, com doses definidas pela análise de solo e foliar.\n" +
                "2. Aplicar o adubo com o solo úmido, para reduzir as perdas.\n" +
                "3. Usar fontes orgânicas, como esterco e palha de café, para melhorar a matéria orgânica do solo."
    ),
    DetalheNutriente(
        id = "fosforo",
        nome = "Fósforo (P)",
        classificacao = "Macronutriente primário",
        resumo = "Tons arroxeados.",
        imagem = R.drawable.deficiencia_de_fosforo,
        folhasAfetadas = "mais velhas",
        funcao = "Participa do armazenamento e da transferência de energia na planta. É importante para o " +
                "desenvolvimento das raízes, o florescimento e a formação dos frutos.",
        sintomas = "• Folhas mais velhas amareladas, com manchas arroxeadas ou bronzeadas.\n" +
                "• Queda precoce das folhas afetadas.\n" +
                "• Crescimento lento, principalmente em lavouras novas.",
        condicoes = "• Solos ácidos e argilosos, que retêm o fósforo e o tornam pouco disponível.\n" +
                "• Falta de calagem.\n" +
                "• Adubação fosfatada insuficiente no plantio.",
        correcao = "1. Corrigir a acidez do solo com calagem, o que aumenta a disponibilidade do fósforo (use a Calculadora de Calagem).\n" +
                "2. Caprichar na adubação fosfatada no plantio, aplicada na cova ou no sulco.\n" +
                "3. Em lavouras em produção, repor o fósforo conforme a análise de solo."
    ),
    DetalheNutriente(
        id = "potassio",
        nome = "Potássio (K)",
        classificacao = "Macronutriente primário",
        resumo = "Necrose nas bordas.",
        imagem = R.drawable.deficiencia_potassio,
        folhasAfetadas = "mais velhas",
        funcao = "É o nutriente mais exigido pelos frutos do café. Atua no enchimento dos grãos, na qualidade " +
                "da bebida e na regulação da água dentro da planta.",
        sintomas = "• Amarelecimento seguido de secamento (necrose) nas bordas e pontas das folhas mais velhas.\n" +
                "• Frutos mal granados, leves ou chochos.\n" +
                "• Sintomas mais fortes em anos de carga alta.",
        condicoes = "• Safras com produção elevada, que retiram muito potássio da planta.\n" +
                "• Solos arenosos, de onde o potássio é perdido com facilidade.\n" +
                "• Desequilíbrio com cálcio e magnésio no solo.",
        correcao = "1. Fazer a adubação potássica parcelada durante o período chuvoso, conforme a análise de solo.\n" +
                "2. Ajustar as doses à expectativa de produção da safra.\n" +
                "3. Manter o equilíbrio entre potássio, cálcio e magnésio no solo."
    ),
    DetalheNutriente(
        id = "calcio",
        nome = "Cálcio (Ca)",
        classificacao = "Macronutriente secundário",
        resumo = "Pontas e bordas deformadas.",
        imagem = R.drawable.deficiencia_calcio,
        folhasAfetadas = "mais novas",
        funcao = "Faz parte da parede das células e é essencial para o crescimento das raízes e dos brotos.",
        sintomas = "• Folhas novas com amarelecimento a partir das bordas, que ficam deformadas e encurvadas.\n" +
                "• Morte das gemas da ponta dos ramos.\n" +
                "• Raízes pouco desenvolvidas.",
        condicoes = "• Solos ácidos e sem calagem.\n" +
                "• Excesso de potássio, que dificulta a absorção do cálcio.\n" +
                "• Períodos secos.",
        correcao = "1. Fazer a calagem, que fornece cálcio e corrige a acidez (use a Calculadora de Calagem).\n" +
                "2. Aplicar gesso agrícola, quando recomendado, para levar cálcio às camadas mais profundas do solo.\n" +
                "3. Manter o equilíbrio entre potássio, cálcio e magnésio."
    ),
    DetalheNutriente(
        id = "magnesio",
        nome = "Magnésio (Mg)",
        classificacao = "Macronutriente secundário",
        resumo = "Espinha de peixe.",
        imagem = R.drawable.deficiencia_de_magnesio,
        folhasAfetadas = "mais velhas",
        funcao = "Fica no centro da molécula de clorofila, por isso é indispensável para a fotossíntese.",
        sintomas = "• Amarelecimento entre as nervuras das folhas mais velhas, enquanto as nervuras continuam verdes (padrão em \"espinha de peixe\").\n" +
                "• Com a evolução, as folhas podem ficar amarelo-alaranjadas e cair.",
        condicoes = "• Solos ácidos e sem calagem.\n" +
                "• Excesso de potássio, que prejudica a absorção do magnésio.\n" +
                "• Solos arenosos e chuvas intensas.",
        correcao = "1. Fazer a calagem com calcário dolomítico, que fornece magnésio (use a Calculadora de Calagem).\n" +
                "2. Em casos já instalados, aplicar sulfato de magnésio via solo ou via folha.\n" +
                "3. Evitar excesso de adubação potássica."
    ),
    DetalheNutriente(
        id = "enxofre",
        nome = "Enxofre (S)",
        classificacao = "Macronutriente secundário",
        resumo = "Folhas novas amareladas.",
        imagem = R.drawable.enxofre_deficiencia,
        folhasAfetadas = "mais novas",
        funcao = "Participa da formação de proteínas e ajuda a planta a aproveitar melhor o nitrogênio.",
        sintomas = "• Amarelamento uniforme das folhas mais novas. Na falta de nitrogênio, o amarelamento começa nas folhas velhas, e essa é a principal diferença entre os dois.\n" +
                "• Folhas pequenas e entrenós curtos.",
        condicoes = "• Solos pobres em matéria orgânica.\n" +
                "• Uso contínuo de adubos sem enxofre, como ureia e cloreto de potássio.",
        correcao = "1. Usar adubos que contenham enxofre, como sulfato de amônio e superfosfato simples.\n" +
                "2. Aplicar gesso agrícola, quando recomendado pela análise de solo.\n" +
                "3. Manter a matéria orgânica do solo."
    ),
    DetalheNutriente(
        id = "boro",
        nome = "Boro (B)",
        classificacao = "Micronutriente",
        resumo = "Deformação dos brotos.",
        imagem = R.drawable.deficiencia_borob,
        folhasAfetadas = "mais novas",
        funcao = "É essencial para os pontos de crescimento da planta, como brotos e raízes, e para o " +
                "florescimento e a fixação das flores.",
        sintomas = "• Folhas novas pequenas, deformadas e com bordas irregulares.\n" +
                "• Morte das gemas da ponta dos ramos e brotação excessiva, com aspecto de \"leque\".\n" +
                "• Florada fraca e queda de flores.",
        condicoes = "• Períodos de estiagem, que reduzem a disponibilidade do boro.\n" +
                "• Solos arenosos ou pobres em matéria orgânica.\n" +
                "• Calagem em excesso.",
        correcao = "1. Aplicar fontes de boro, como ácido bórico, via solo ou via folha, conforme a análise.\n" +
                "2. Parcelar as aplicações ao longo do ano.\n" +
                "3. Atenção à dose: a diferença entre falta e excesso de boro é pequena, e o excesso também causa danos."
    ),
    DetalheNutriente(
        id = "zinco",
        nome = "Zinco (Zn)",
        classificacao = "Micronutriente",
        resumo = "Folhas pequenas e estreitas.",
        imagem = R.drawable.deficiencia_zinco,
        folhasAfetadas = "mais novas",
        funcao = "Participa da produção dos hormônios de crescimento, responsáveis pelo alongamento dos ramos e das folhas.",
        sintomas = "• Folhas novas pequenas, estreitas e alongadas, com amarelecimento entre as nervuras.\n" +
                "• Entrenós curtos, com as folhas agrupadas na ponta dos ramos (aspecto de roseta).\n" +
                "• Frutos menores e queda na produção.",
        condicoes = "• Solos naturalmente pobres em zinco, comuns no Cerrado.\n" +
                "• Calagem em excesso.\n" +
                "• Excesso de fósforo, que prejudica a absorção do zinco.",
        correcao = "1. Fazer pulverizações foliares com fontes de zinco, como o sulfato de zinco, parceladas ao longo do ano.\n" +
                "2. Aplicar zinco via solo quando indicado pela análise.\n" +
                "3. Evitar excesso de calagem e de adubação fosfatada."
    ),
    DetalheNutriente(
        id = "ferro",
        nome = "Ferro (Fe)",
        classificacao = "Micronutriente",
        resumo = "Folhas novas com nervuras verdes.",
        imagem = R.drawable.deficiencia_ferro,
        folhasAfetadas = "mais novas",
        funcao = "É necessário para a formação da clorofila e para a respiração da planta.",
        sintomas = "• Folhas novas amarelo-claras, com as nervuras verdes formando uma rede fina.\n" +
                "• Em casos severos, as folhas novas ficam quase brancas.",
        condicoes = "• Calagem em excesso, que deixa o ferro pouco disponível.\n" +
                "• Solos encharcados ou mal drenados.",
        correcao = "1. Corrigir a causa: evitar excesso de calcário e melhorar a drenagem do solo.\n" +
                "2. Quando recomendado, fazer aplicações foliares de fontes de ferro.\n" +
                "3. Confirmar o diagnóstico com análise foliar, porque o sintoma pode ser confundido com outras deficiências."
    )
)

fun buscarNutriente(id: String): DetalheNutriente? = catalogoNutrientes.find { it.id == id }