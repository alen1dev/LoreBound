# LoreBound Ideology and Business Logic

This document explains the "why" and "how" behind LoreBound:
- Product ideology (design intent)
- Core business rules
- Decision-making flows in code
- Practical guardrails for future extensions

## 1) Product Ideology (Why This App Exists)

LoreBound is designed as a real-life RPG that does **behavior shaping**, not just task tracking.

Core philosophy:
- **Life as narrative**: actions become memories, memories become story.
- **Growth by balancing traits**: the app nudges users toward weaker attributes, not only strengths.
- **Low-friction consistency**: daily actions build momentum; longer quests add depth.
- **Seasonal meaning**: Chronicle is intentionally scarce (seasonal), so it feels special.
- **Optional AI, never mandatory**: core progression works fully offline.

## 2) Product Pillars (What Must Stay True)

1. **Progressive challenge horizon**
   - Daily (1 day), Side Quest (7 days), Adventure (30 days), Epic (90 days)
   - Creates short/mid/long-term motivation in one screen.

2. **Weakness-guided quest recommendation**
   - Selection prioritizes weak traits first, then general variety.

3. **Dual-memory model**
   - Auto quest memories + user-authored personal lore entries.

4. **Ritualized yearly reflection**
   - Chronicle appears seasonally and is AI-generated from lived data.

## 3) Domain Model Intent

Primary entities and intent:
- `Character`: current player state (traits, XP, title, streak)
- `Quest`: reusable templates from assets
- `ActiveQuest`: currently assigned time-bounded instances
- `CompletedQuest`: proof of completion metadata
- `LoreEntry`: narrative memory (quest-derived or personal)
- `Title` / `Feat`: achievement systems
- `Chronicle`: seasonal year-in-review narrative artifact

Rule of thumb:
- **Templates** (`Quest`) are static content.
- **Instances/events** (`ActiveQuest`, `CompletedQuest`, `LoreEntry`) are behavioral history.

## 4) Decision Engines (How Logic Decides)

## 4.1 Quest Selection Logic

Location: `domain/service/LocalQuestGenerator.kt`

Decision sequence:
1. Compute character weak traits (lowest values)
2. Load quests for requested `QuestType`
3. For daily pool, apply difficulty guardrails
4. Split into:
   - **Priority pool**: matches weak traits in primary/secondary attributes
   - **General pool**: everything else
5. Shuffle pools, merge priority-first, dedupe
6. Take required count

Business intent:
- Preserve novelty while intentionally nudging growth where needed.

## 4.2 Active Quest Assignment & Expiry

Location: `ui/home/HomeViewModel.kt` (`ensureActiveQuestsExist`)

Decision sequence on load/refresh:
1. Clear expired active quests by `expiresDate`
2. For each type (daily/side/adventure/epic):
   - If count for "today" is 0, generate + assign
   - Set `assignedDate` and type-specific `expiresDate`

Business intent:
- User always has one active quest per horizon type without manual reset.

## 4.3 Streak Logic

Location: `ui/home/HomeViewModel.kt`

Current rule:
- Compare `lastActiveDate` and `yesterday`
- Continue or reset streak based on continuity

Business intent:
- Reward cadence, keep logic simple and predictable.

## 4.4 Verification Logic

Location: `domain/service/QuestVerificationService.kt` + completion UI

Decision model:
- Verification requirement type controls what evidence is required
- UI adapts to the verification type
- Completion accepted only when required payload is satisfied

Business intent:
- Support multiple proof strictness levels without hardcoding one UX.

## 4.5 Chronicle Seasonal Gate

Locations:
- `domain/chronicle/ChronicleSeasonManager.kt`
- `ui/home/HomeViewModel.kt`
- `ui/chronicle/*`

Season states:
- Hidden: Jan 16 -> Nov 30
- Preparing: Dec 1 -> Dec 14
- Ready: Dec 15 -> Jan 15

Decision sequence:
1. Determine seasonal state from date
2. If preparing/ready and no chronicle JSON exists, attempt generation
3. When ready and data exists, expose entry-point UI

Business intent:
- Chronicle is a ritual event, not an always-on feed.

## 4.6 Seasonal Star Background Gate

Locations:
- `domain/chronicle/ChronicleStarsPreference.kt`
- `navigation/LoreBoundNavGraph.kt`

Decision gate:
- Show stars on non-chronicle pages only when:
  1) date in Dec 15 -> Jan 15 window
  2) user has viewed chronicle for current season year

Business intent:
- Reward/ambience unlock after meaningful action.

## 4.7 Notification Scheduling Logic

Location: `notifications/WeeklyQuestReminderWorker.kt`

Rules:
- Weekly reminder: Friday 6 PM periodic work
- Chronicle reminder: one-time Dec 15 reminder
- Toggle in settings enables/disables scheduling

Business intent:
- Gentle re-engagement at predictable rhythm.

## 4.8 Onboarding & Tutorial Logic

Locations:
- `ui/character/*`
- `ui/tutorial/QuestTutorialOverlay.kt`
- `ui/lore/LoreJournalTutorialDialog.kt`

Rules:
- Character creation is step-based and constrained (2 strengths, 2 weaknesses)
- Quest tutorial shown once per install/update state key
- Lore tutorial shown once via dedicated preference key

Business intent:
- Teach mental model only when needed, avoid repeated friction.

## 5) UI Behavior as Business Logic

Some UX behaviors are intentional business rules:
- Pull-to-refresh on quest board triggers quest validity checks and chronicle reload.
- "Quests up to date" vs "New quests found" messaging confirms system action.
- Cinematic dialogs (title/tutorial/intro) reinforce milestone significance.

## 6) Architectural Boundaries

Current practical boundaries:
- **UI layer**: rendering + interaction orchestration
- **ViewModel layer**: screen-level business orchestration
- **Domain services**: reusable decision engines
- **Data layer**: persistence, mapping, repository implementations

Guideline:
- New business decisions should be extracted into domain service or repository methods when they are shared or complex.

## 7) Extension Strategy (Future-Safe)

Keep open for extension:
- AI provider abstraction (`AiProvider`) for alternate providers
- Chronicle generation pipeline and storage separation
- Verification service with pluggable proof handlers
- Seasonal feature flags based on date/preferences

Avoid in future:
- Embedding business rules directly in composables
- Duplicating decision logic across ViewModels
- Adding app-critical dependencies on external AI availability

## 8) Known Product Trade-offs

- Simplicity over perfect realism in streak logic
- Seasonal Chronicle scarcity over always-available analytics
- Lightweight preference-based tutorial state over heavier event systems

These are intentional and should only be changed with explicit product goals.

## 9) Quick Reference: "Where to Edit What"

- Quest recommendation behavior -> `LocalQuestGenerator`
- Assignment windows / expiry policy -> `HomeViewModel.ensureActiveQuestsExist`
- Chronicle season windows -> `ChronicleSeasonManager`
- Notification schedule times -> `QuestReminderScheduler`
- Tutorial one-time behavior -> `TutorialPreferences`
- Verification requirements -> `QuestVerificationService` + completion UI

## 10) Product North Star

LoreBound should make users feel:
- "I am progressing as a character"
- "My real life is becoming a story worth recording"
- "This app nudges me to grow where I usually avoid"

Any new feature should be validated against this north star.
