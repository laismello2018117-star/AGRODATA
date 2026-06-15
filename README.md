Markdown
# AgroData - Catálogo & Gestão Fitossanitária do Cafeeiro

O **AgroData** é um aplicativo móvel voltado para o monitoramento e manejo integrado de pragas, doenças e deficiências nutricionais na cafeicultura. Unindo conceitos de tecnologia da informação e o setor agronômico, o app serve como um assistente de campo para produtores rurais tomarem decisões estratégicas de controle e amostragem.

---

## Funcionalidades Principais

* **Catálogo Fitossanitário:** Informações detalhadas sobre o ciclo, danos, condições favoráveis e estratégias de Manejo Integrado de Pragas (MIP) para:
    * Ácaro-da-leprose *(Brevipalpus yandeli)*
    * Bicho-mineiro *(Leucoptera coffeella)*
    * Broca-do-café *(Hypothenemus hampei)*
    * Ferrugem-do-cafeeiro *(Hemileia vastatrix)*
* **Calendário Fenológico:** Acompanhamento visual das fases da cultura (Floração, Chumbinho, Expansão, Maturação e Colheita).
* **Guia Nutricional Visual:** Identificação de sintomas visuais de deficiências de macronutrientes e micronutrientes (N, P, K, Mg, B, S, Ca).
* **Ferramentas de Campo:** Calculadora de calagem e módulos de monitoramento em tempo real (Clima e Mercado Físico).

---

## Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Interface Gráfica:** Jetpack Compose (Material Design 3)
* **Navegação:** Jetpack Navigation Compose (Arquitetura unificada de rotas)
* **Gerenciador de Dependências:** Gradle (Kotlin DSL)

---

## Estrutura de Arquivos do Projeto

Abaixo está a organização principal dos componentes customizados de interface desenvolvidos:

```text
app/src/main/java/com/example/agrodata/
│
├── MainActivity.kt          # Inicialização, fluxo de rotas (NavHost) e HomeScreen
├── TelasFerramentas.kt      # Componentes globais (GridPragas, TabelaFenologica, GuiaNutricional)
├── ui.theme/                # Configurações globais de estilos, tipografia e cores
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
