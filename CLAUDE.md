# Animalia Biomes (Nonogram)

Animal-themed picture nonogram game for Android. Players reveal pixel-art animals organized by biome and stage.

---

## Working principles (read first)

### Build to current best practices, even if slightly overkill
- Use modern Android idioms: `suspend` functions, `lifecycleScope`, `LiveData`/`StateFlow`, sealed classes, type safety end-to-end.
- Validate at boundaries (JSON parsing, user input). Never trust schema-shape assumptions implicitly.
- Repository pattern even with one implementation. Interfaces over concrete classes when something might be swapped (asset → remote, prefs → DataStore).
- Tests for non-trivial pure logic (parsers, calculators, unlock math).
- It's acceptable to introduce structure slightly earlier than strictly needed — clean architecture now beats refactoring later. Don't ship "good enough"; ship "right."
- Don't go all-in on enterprise patterns either (no premature DI framework, no use-case classes for trivial flows). "Slightly overkill," not "fully overkill."

### Do not modify the user's design work without explicit permission
The following are **design decisions** owned by the user. Surface concerns, propose options, wait for the call. Don't silently rewrite them.

- **Puzzle solutions** in `app/src/main/assets/puzzles/*.json` — the pixel art itself.
- **Palette colors** chosen for any animal, even if contrast seems poor.
- **Fun facts**, animal names. **Stage names are scientific sub-biomes** — each biome's 4 stages are the recognized ecological sub-types (see "Biomes & sub-biomes" below).
- **Game rules**: 3-strike reset, 75% stage unlock threshold, merged-clue rule (adjacent cells of any colors count as one clue), tap-to-fill / long-press-to-mark, no hint system, **no color picker** (player taps any cell; the game auto-fills with the correct color from the solution).
- **Clue text color**: hint numbers around the grid stay **black** (`#212121`) for *all* puzzles regardless of palette. Don't tint clues to match palette colors. Filled cells use palette colors; clues do not.
- **Minimum palette colors per puzzle size**:
  - `5×5` (Shores) — 1+ colors
  - `10×10` (Shallows) — **2+ colors required**
  - `15×15` (Depths) — **3+ colors required**
  - `20×20` (Open Ocean) — **3+ colors required**

  Enforced in `PuzzleSerializer` so authoring violations fail at parse time. Larger grids carry richer detail; the palette minimums prevent visually-flat designs at higher difficulty.
- **Visual decisions**: ocean gradient background, transparent empty cells AND transparent incorrect cells (only the red X is drawn for incorrect), lock icon style, tooltip behavior, contrast adjustments.
- **Schema choices**: keyed palette + string-row solution format, schema versioning policy.

If a change to any of the above seems necessary (e.g., a critique flags a real bug), ask first.

### Engineering changes are always fair game
Refactors, bug fixes, validation, error handling, tests, dependency upgrades, architecture restructuring as the codebase grows — proceed without hand-holding. Touch design only when explicitly invited.

---

## Biomes & sub-biomes

5 biomes shipped, each with 4 scientifically-grounded sub-biome stages. Stage index → grid size: 1=5×5, 2=10×10, 3=15×15, 4=20×20. Target puzzle counts: 4/4/4/8.

| Biome | Stage 1 (5×5) | Stage 2 (10×10) | Stage 3 (15×15) | Stage 4 (20×20) |
|---|---|---|---|---|
| Aquatic | Intertidal | Neritic | Pelagic | Abyssal |
| Grassland | Pampas | Steppe | Prairie | Savanna |
| Forest | Taiga | Temperate Deciduous | Temperate Coniferous | Tropical Rainforest |
| Desert | Semi-Arid | Coastal Desert | Hot and Dry | Cold Desert |
| Tundra | Alpine | Subarctic | Arctic | Polar Sea Ice |

Sub-biome stages are conceptual containers. Animals slot into whichever stage matches their actual habitat. The size-per-stage rule (5/10/15/20 by index) creates the difficulty curve independent of the sub-biome's scientific identity.

## Project context

- **Platform:** Android, Kotlin, Gradle KTS, min SDK 24, target 34
- **Package:** `com.nonogram.animaliabiomes`
- **Persistence:** `SharedPreferences` (progress) + bundled JSON (puzzles). No backend in v1.
- **Schema:** v3 — `palette: { "K": "#hex" }` + `solution: ["..OO."]`. v1/v2 still parse for backward compat.
- **Architecture:** Activity → `ViewModel` → `Repository` → asset/prefs data sources.

## Where things live

```
app/src/main/
├── java/com/nonogram/animaliabiomes/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── model/             ← Stage, Biome, Puzzle, CellState, ColorClue
│   │   ├── repository/        ← PuzzleRepository, ProgressRepository, Repositories.kt
│   │   ├── PuzzleSerializer.kt
│   │   └── ClueCalculator.kt
│   └── ui/
│       ├── stageselect/       ← StageSelectActivity
│       ├── puzzlelist/        ← PuzzleListActivity (dynamic buttons)
│       ├── funfact/           ← FunFactActivity
│       └── game/              ← GameActivity, GameViewModel, PicrossGridView, ColorPaletteView
├── assets/puzzles/ocean.json  ← shipped puzzles
└── res/                       ← layouts, drawables, strings, themes
app/src/test/                  ← unit tests (ClueCalculatorTest, PuzzleSerializerTest, StageProgressTest)

tools/puzzle-editor.html       ← browser-based puzzle authoring tool
```

## Tooling notes

- `tools/puzzle-editor.html` is the authoritative tool for designing puzzles. It mirrors the in-game rendering (ocean gradient background, transparent empty cells, merged-clue rule). When game rules or rendering change, mirror them in the editor.
- All puzzle validation happens in `PuzzleSerializer.toDomain*` — add new constraints there with matching `PuzzleSerializerTest` cases.

## Process patterns established

- Major restructures land as numbered **Phases**. After every phase, run two critique agents in parallel: one focused on **long-term scalability** (multi-biome, future remote/DLC), one on **schema/design** (data model, authoring ergonomics, lock UX).
- Critique findings drive the next phase's scope. Deferred items are explicitly recorded, not silently dropped.
- The user is iterative — propose plans, ask before executing big changes, surface tradeoffs.
