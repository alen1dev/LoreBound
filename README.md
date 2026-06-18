# LoreBound

LoreBound turns daily life into an RPG-style progression system with quests, character growth, a lore journal, and seasonal chronicle experiences.

## Architecture Diagram

```mermaid
flowchart TB
    A[MainActivity]
    B[LoreBoundNavGraph]

    subgraph UI [UI Layer - Compose]
      C[HomeScreen + HomeViewModel]
      D[LoreJournalScreen + ViewModels]
      E[QuestDetailScreen + ViewModel]
      F[CharacterSheetScreen + ViewModel]
      G[ChronicleExperienceScreen]
      H[DeveloperMenuScreen + ViewModel]
      I[StyleShowcaseScreen Settings]
    end

    subgraph Domain [Domain Layer]
      J[Repository Interfaces]
      K[Services QuestGenerator RankService TitleEngine TimeProvider]
      L[AI Interfaces + ChronicleGenerator]
      M[ChronicleSeasonManager + Availability State]
      N[Domain Models Character Quest LoreEntry Chronicle]
    end

    subgraph Data [Data Layer]
      O[Repository Implementations]
      P[Room DAOs + Entities]
      Q[LoreBoundDatabase]
      R[Seeders + Asset Loaders JSON]
      S[ChronicleStorage + AiKeyStore]
    end

    T[(Quest Assets JSON)]
    U[(Titles + Feats JSON)]
    V[(SQLite)]
    W[(Gemini API Optional)]

    A --> B
    B --> C
    B --> D
    B --> E
    B --> F
    B --> G
    B --> H
    B --> I

    C --> J
    D --> J
    E --> J
    F --> J
    G --> J
    H --> J
    I --> J

    C --> K
    D --> K
    E --> K
    F --> K
    G --> M
    H --> M

    J --> O
    O --> P
    P --> Q
    Q --> V

    R --> T
    R --> U
    O --> R

    L --> W
    O --> S
    G --> L

    N --- J
    N --- K
    N --- O
```

## Product Ideology and Business Logic

- Deep dive: `docs/IDEOLOGY_AND_BUSINESS_LOGIC.md`
- Includes Mermaid decision-flow diagrams for quest refresh, generator logic, chronicle season gating, notifications, and tutorial visibility.
- Covers decision-making flows for quest generation, assignment/expiry, streaks, chronicle season gating, notifications, verification, and onboarding tutorials.

## Layers (Summary)

- **UI Layer**: Compose screens + ViewModels for presentation and interaction.
- **Domain Layer**: business logic, repository contracts, quest/chronicle/title systems, AI abstractions.
- **Data Layer**: Room, DAO/entity mapping, repository implementations, asset seeding, encrypted key storage.

## Project Structure

- App module docs and deeper feature details: `app/README.md`
- Core sources: `app/src/main/java/com/pauls/lorebound/`
- JSON assets: `app/src/main/assets/`

## Build

```powershell
cd C:\Users\z004wujn\AndroidStudioProjects\LoreBound
.\gradlew.bat assembleDebug
```

APK output:

- `app/build/outputs/apk/debug/LoreBound.apk`
