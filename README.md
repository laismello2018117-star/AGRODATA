# AgroData Café

O **AgroData Café** é um aplicativo Android de apoio à decisão para pequenos e médios cafeicultores. Ele reúne, em um só lugar, informações que hoje ficam espalhadas: clima, preço do café, pragas e doenças, nutrição da planta e manejo do solo.

Projeto desenvolvido como Trabalho de Conclusão de Curso (TCC).

---

## Funcionalidades

- **Scanner com IA:** a partir de uma foto, identifica a espécie da planta e sugere o diagnóstico de doenças, pragas e deficiências nutricionais do cafeeiro.
- **Clima e alertas:** temperatura, umidade, sensação térmica e vento pela localização do aparelho, com alertas ligados ao manejo (risco de geada, tempestade, calor na florada e ar seco).
- **Mercado físico:** indicadores do café arábica e robusta do CEPEA/ESALQ, exibidos pelo widget oficial e atualizados diariamente.
- **Calculadora de calagem:** necessidade de calcário pelo método da saturação por bases, com o total para a área de cada talhão.
- **Calendário fenológico:** seis fases do ciclo do café arábica, com a fase atual em destaque, manejos recomendados e pontos de atenção.
- **Catálogo fitossanitário:** 12 fichas de pragas e doenças (danos, condições favoráveis e manejo integrado).
- **Guia nutricional visual:** 9 nutrientes (N, P, K, Ca, Mg, S, B, Zn e Fe), com sintomas, causas e correção.
- **Talhões:** cadastro das áreas da lavoura com nome e área, compartilhado entre o scanner e a calculadora, com histórico de análises.

---

## Como funciona o scanner

1. **Confirmação da espécie:** a foto é enviada ao Pl@ntNet. Se a planta não for do gênero *Coffea*, o app avisa o usuário e a análise termina ali.
2. **Diagnóstico:** fotos de café são enviadas ao crop.health, que sugere até três possíveis problemas com a probabilidade de cada um.
3. **Orientação:** quando o diagnóstico corresponde a uma ficha do app, o usuário abre a ficha em português com danos e manejo. O resultado fica salvo no histórico do talhão.

Antes do envio, a imagem é redimensionada e tem a rotação corrigida, para reduzir o consumo de internet no campo. O diagnóstico é apresentado como sugestão e deve ser confirmado por um engenheiro agrônomo.

---

## Tecnologias

- **Linguagem:** Kotlin
- **Interface:** Jetpack Compose (Material Design 3)
- **Arquitetura:** MVVM, com ViewModel e StateFlow
- **Banco de dados local:** Room (talhões e histórico de análises, com migrações entre versões)
- **Rede:** Retrofit, OkHttp e Gson
- **Navegação:** Navigation Compose
- **Localização:** Google Play Services Location
- **Build:** Gradle (Kotlin DSL), KSP

---

## APIs e serviços utilizados

- **Diagnóstico de doenças, pragas e deficiências:** API [crop.health](https://www.kindwise.com/crop-health), da [Kindwise](https://www.kindwise.com).
- **Identificação da espécie:** API [Pl@ntNet](https://my.plantnet.org/), serviço de identificação de plantas por imagem mantido por um consórcio de pesquisa.
- **Clima:** [OpenWeather](https://openweathermap.org/).
- **Mercado:** indicadores do [CEPEA/ESALQ-USP](https://www.cepea.org.br/), exibidos pelo widget oficial do Cepea (dados sob licença CC BY-NC 4.0).

---

## Como executar o projeto

1. Clone o repositório e abra a pasta no Android Studio.
2. Na raiz do projeto, crie ou edite o arquivo `local.properties` e adicione as chaves das APIs:

   ```properties
   PLANTNET_API_KEY=sua_chave_do_plantnet
   WEATHER_API_KEY=sua_chave_do_openweather
   CROP_HEALTH_API_KEY=sua_chave_do_crop_health
   ```

   As chaves são obtidas gratuitamente em [my.plantnet.org](https://my.plantnet.org/), [openweathermap.org](https://openweathermap.org/) e no painel da [Kindwise](https://www.kindwise.com/crop-health). O `local.properties` não deve ser enviado ao repositório.
3. Clique em **Sync Now** e depois em **Build > Assemble Project**.
4. Execute o app em um aparelho ou emulador com Android 7.0 (API 24) ou superior.

---

## Estrutura do projeto

```
app/src/main/java/com/example/agrodata/
│
├── MainActivity.kt            # Tela inicial e navegação entre telas
├── CardModulo.kt              # Card expansível de cada módulo
│
├── ScannerScreen.kt           # Tela do scanner, resultados e histórico
├── ScannerViewModel.kt        # Fluxo de identificação e diagnóstico
├── PlantNetApi.kt             # Integração com o Pl@ntNet
├── CropHealthApi.kt           # Integração com o crop.health
├── DiagnosticoCatalogo.kt     # Liga o diagnóstico às fichas do app e traduz os resultados
├── NomesPopulares.kt          # Nomes populares em português
├── ImagemUtils.kt             # Redimensionamento e preparo das fotos
│
├── AnaliseDatabase.kt         # Banco Room: talhões e histórico de análises
├── TalhaoViewModel.kt         # Talhões compartilhados entre os módulos
│
├── ModulosFerramentas.kt      # Clima, mercado e seletor de talhão
├── CalculadoraCalagem.kt      # Calculadora de calagem
├── TabelaFenologica.kt        # Calendário fenológico
├── ModulosInfo.kt             # Catálogo de pragas e guia nutricional (listas)
├── CatalogoPragas.kt          # Conteúdo das fichas de pragas e doenças
├── CatalogoNutrientes.kt      # Conteúdo das fichas de nutrientes
├── TelasDetalhes.kt           # Tela de detalhes de pragas e doenças
├── TelaDetalheNutriente.kt    # Tela de detalhes de nutrientes
├── Agrodatamodel.kt           # Modelos e cliente da API de clima
│
└── ui/theme/                  # Cores, tipografia e tema
```

---

## Avisos

- Os diagnósticos do scanner são sugestões geradas por inteligência artificial e não substituem a avaliação de um engenheiro agrônomo.
- O conteúdo técnico do calendário, do catálogo e do guia nutricional está em processo de validação com o material da Embrapa Café.
- Os indicadores do Cepea são preços de referência. O valor pago ao produtor varia conforme a região e a qualidade do café.
