# LoreBound

**A gamified real-life adventure app that turns your daily life into an RPG.**

LoreBound assigns real-world quests, tracks personal growth across 6 character traits, records memories in a Lore Journal, and generates an annual AI-powered Chronicle — a cinematic year-in-review experience.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Quest System](#quest-system)
3. [Character & Progression](#character--progression)
4. [Lore Journal](#lore-journal)
5. [Quest Verification](#quest-verification)
6. [Chronicle System](#chronicle-system)
7. [AI Integration (Gemini)](#ai-integration-gemini)
8. [Time System](#time-system)
9. [Developer Mode](#developer-mode)
10. [Tech Stack](#tech-stack)
11. [Project Structure](#project-structure)

---

## Architecture Overview

- **Pattern:** MVVM + Clean Architecture
- **DI:** Hilt
- **Database:** Room (SQLite)
- **UI:** Jetpack Compose (Material 3)
- **Navigation:** Compose Navigation with animated transitions
- **AI:** Gemini REST API (no SDK dependency)
- **Image Loading:** Coil

---

## Quest System

### Quest Types

| Type | Duration | Refresh Cycle |
|------|----------|---------------|
| Daily | 1 day | Every day |
| Side Quest | 7 days | When expired |
| Adventure | 30 days | When expired |
| Epic | 90 days | When expired |

### Quest Content

Quests are stored as JSON assets in `app/src/main/assets/`:
- `daily_quests.json` — 75 quests
- `side_quests.json` — 100 quests
- `adventures.json` — 75 quests
- `epics.json` — 30 quests

Each quest has:
- `id`, `title`, `description`
- `questType` (DAILY, SIDE_QUEST, ADVENTURE, EPIC)
- `primaryAttribute` + optional `secondaryAttribute` (one of 6 traits)
- `xpReward`, `difficulty` (1–5)
- `estimatedMinutes`, `durationDays`
- `storyWeight` (1–10, narrative importance)
- `rarity` (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY)
- `verification.type` (how completion is verified)
- `tags` (searchable labels)

### Quest Selection Algorithm

Located in `LocalQuestGenerator.kt`:

1. Identify the character's **2 weakest traits** (lowest stat values)
2. Fetch all quests matching the requested type from Room DB
3. For Daily quests, filter to difficulty 1 only
4. **Priority pool:** Quests whose `primaryAttribute` or `secondaryAttribute` matches a weak trait (shuffled)
5. **General pool:** All remaining quests (shuffled)
6. Merge: priority first, then general, deduplicated by ID
7. Return the first `count` items

This ensures characters are pushed to **improve their weaknesses** while still getting variety.

### Quest Lifecycle

1. On app open, `HomeViewModel.ensureActiveQuestsExist()` checks if active quests exist for each type
2. If missing (expired or first launch), generates new ones via `QuestGenerator`
3. Expired quests are cleared based on `expiresDate`
4. Completed quests are marked in the `active_quests` table and recorded in `completed_quests`

---

## Character & Progression

### 6 Traits
- **Strength** — Physical challenges
- **Intelligence** — Learning, problem-solving
- **Charisma** — Social interactions
- **Creativity** — Art, creation, expression
- **Exploration** — Travel, discovery
- **Courage** — Fear-facing, risk-taking

### Rank System

XP accumulates from completed quests. Rank is calculated by `RankService` using a progressive XP curve.

### Titles

Unlockable titles earned through achievements (stored in `titles.json`). The active title displays below the character name.

### Streaks

Daily activity tracking — consecutive days with quest activity.

---

## Lore Journal

The Lore Journal stores two types of entries:

### Quest Completion Entries
Created automatically when a quest is completed. Includes:
- Quest title, XP earned, traits improved
- Optional: photo, GPS location, user notes
- Verification evidence

### Personal Lore Entries
Created manually by the user via the FAB button. Includes:
- Custom title and notes
- Optional: photo, location, tags
- Favorite toggle
- Auto-calculated story weight

### Memory Cards
Cards in the journal list show the attached photo as a **darkened background image** (72% black overlay) for visual richness while keeping text readable.

---

## Quest Verification

9 verification types determine how quest completion is validated:

| Type | User Action |
|------|-------------|
| `NONE` | Tap complete |
| `MANUAL` | Tap complete (honor system) |
| `PHOTO` | Attach a photo |
| `GPS` | Capture GPS location |
| `TEXT` | Write a text reflection |
| `LINK` | Provide a URL |
| `PHOTO_OR_TEXT` | Either photo or text |
| `PHOTO_AND_TEXT` | Both photo and text |
| `GPS_AND_PHOTO` | Both GPS and photo |

The completion dialog adapts its UI based on the verification type, showing the appropriate input fields.

---

## Chronicle System

### Seasonal Availability

The Chronicle is a **seasonal event**, not a permanent feature:

| Period | State | UI | System Action |
|--------|-------|----|---------------|
| Jan 16 → Nov 30 | Hidden | Nothing shown | — |
| Dec 1 → Dec 14 | Preparing | Grey pill "CHRONICLE" (not clickable) | On Dec 14 night: auto-calls Gemini API |
| Dec 15 → Jan 15 | Ready | Green pill "✦ CHRONICLE" (clickable) | Chronicle JSON is available |

### Production Flow

1. **Dec 1:** The grey "CHRONICLE" pill appears beside the character name — building anticipation
2. **Dec 14 (automatic):** When the app is opened on or after Dec 14, the system automatically triggers Chronicle generation via Gemini AI (if not already generated for this year)
3. **Dec 15:** Once the Chronicle JSON is saved locally, the pill turns green and becomes tappable
4. **Jan 15:** Last day to view. After this, the pill disappears until next December

### Chronicle Generation

Triggered automatically on Dec 14+ (or manually via Developer Mode for testing):
1. Collects all completed quests and lore entries for the year
2. Sends structured data to Gemini AI with a detailed prompt
3. AI generates a `Chronicle` JSON with:
   - `yearTitle`, `mainTheme`, `bestMonth`
   - `totalQuestsCompleted`, `totalXpEarned`, `totalLoreEntries`
   - `topAttributes`
   - Array of `ChronicleSlide` objects (type, title, subtitle, body, stats)
4. JSON is saved locally via `ChronicleStorage`
5. On next app open (Dec 15+), the Chronicle is ready to view

### Chronicle Experience

A full-screen immersive swipeable experience (`HorizontalPager`):
- **Title slide** — Year title, theme, summary stats
- **Content slides** — AI-generated narrative slides with stats
- **Closing slide** — "Your Story Continues" with best month and top traits

---

## AI Integration (Gemini)

### Architecture

- `AiProvider` — Interface for AI providers
- `GeminiProvider` — REST API implementation (no SDK)
- `AiKeyStore` — Encrypted API key storage (`EncryptedSharedPreferences`)
- `ChronicleGenerator` — Builds prompts and parses AI responses

### Model

Uses `gemini-3.1-flash-lite` via the Gemini REST API:
```
POST https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent
```

### Security

- API keys stored in `EncryptedSharedPreferences` (AndroidX Security)
- Keys never leave the device
- AI is completely optional — app functions fully without it

### Connection Test

Developer Mode includes a "Test Connection" button that sends a minimal prompt and reports latency or error details (with copy-to-clipboard).

---

## Time System

### TimeProvider Interface

All date-sensitive logic uses `TimeProvider` — never direct system time:

```kotlin
interface TimeProvider {
    fun now(): Instant
    fun todayDate(): String
    fun yesterdayDate(): String
    fun addDays(date: String, days: Int): String
}
```

### Debug Time Provider

In Developer Mode, `DebugTimeProvider` allows:
- Advancing time by days/weeks/months/years
- Jumping to specific dates (`setToDate`)
- Resetting to real time

This enables testing of:
- Quest expiration
- Chronicle seasonal states
- Streak logic
- Long-term progression

---

## Developer Mode

Hidden 4th tab (only in dev builds) with:

### AI Tools
- Provider selection (Disabled / Gemini)
- API key input with visibility toggle
- Connection testing

### Chronicle Testing
- Generate Chronicle JSON from current data
- Inspect raw JSON output
- Preview Chronicle metadata

### Year-End Simulation
- Simulate December 1 (Preparing state)
- Simulate December 15 (Ready state)
- Simulate January 16 (Hidden state)
- Simulate End of Year

### Dev Utilities
- Generate fake year data (100 quests, 50 lore entries, locations, photos)
- Reset Chronicle test data

### Time Controls
- Advance 1 day / 1 week / 1 month / 3 months / 1 year
- Reset time

### Character Controls
- Grant XP (100/500/1000)
- Set rank (5/10/25)
- Unlock all titles / achievements

### Quest Controls
- Generate new quests by type

### Database Controls
- Full database reset (returns to character creation)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Navigation | Compose Navigation |
| Image Loading | Coil |
| Serialization | kotlinx.serialization |
| AI | Gemini REST API |
| Security | AndroidX Security (EncryptedSharedPreferences) |
| Min SDK | 26 (Android 8.0) |

---

## Project Structure

```
app/src/main/
├── assets/                    # Quest JSON files
│   ├── daily_quests.json
│   ├── side_quests.json
│   ├── adventures.json
│   ├── epics.json
│   ├── titles.json
│   └── feats.json
├── java/com/pauls/lorebound/
│   ├── data/
│   │   ├── local/             # Room entities, DAOs, database
│   │   ├── repository/        # Repository implementations
│   │   └── seed/              # Asset loaders (quest/title/feat JSON parsing)
│   ├── domain/
│   │   ├── ai/                # AI provider, Gemini, Chronicle generation
│   │   ├── chronicle/         # Season manager, availability state
│   │   ├── model/             # Domain models (Character, Quest, LoreEntry, Chronicle)
│   │   ├── repository/        # Repository interfaces
│   │   └── service/           # QuestGenerator, RankService, TimeProvider
│   ├── navigation/            # NavGraph, Screen routes
│   └── ui/
│       ├── character/         # Character creation
│       ├── charactersheet/    # Character sheet display
│       ├── chronicle/         # Chronicle experience + entry point card
│       ├── components/        # Shared UI components
│       ├── developer/         # Developer menu
│       ├── home/              # Home/Quest screen
│       ├── lore/              # Lore Journal + detail + personal entry creation
│       ├── quest/             # Quest detail + completion dialog
│       ├── showcase/          # Dev showcase/entry point
│       └── theme/             # Colors, typography, theme
└── res/                       # Resources
```

---

## Design Philosophy

- **Void Aesthetic:** Pure black backgrounds, white text, olive green accent (#8B9A3C)
- **Editorial/Journal style:** Minimal, typographic, understated
- **Premium animations:** Spring physics, fade+slide transitions, staggered reveals
- **Floating nav bar:** Pill-shaped, transparent background with gradient fades
- **Everything optional:** AI, photos, GPS — the app works fully offline with zero dependencies on external services

