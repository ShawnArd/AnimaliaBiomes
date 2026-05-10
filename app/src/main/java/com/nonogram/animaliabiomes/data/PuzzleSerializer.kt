package com.nonogram.animaliabiomes.data

import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle
import com.nonogram.animaliabiomes.data.model.Stage

class PuzzleValidationException(message: String) : RuntimeException(message)

object PuzzleSerializer {

    private const val MIN_SUPPORTED_SCHEMA_VERSION = 1
    private const val CURRENT_SCHEMA_VERSION = 3
    private const val MIN_GRID_SIZE = 1
    private const val MAX_GRID_SIZE = 40
    private val HEX_COLOR_REGEX = Regex("^#[0-9A-Fa-f]{6}$")

    private val gson = Gson()

    fun loadBiome(json: String): Biome {
        val root = gson.fromJson(json, JsonObject::class.java)
            ?: throw PuzzleValidationException("Biome JSON is empty or not an object")

        val schemaVersionElement = root.get("schemaVersion")
        if (schemaVersionElement == null || schemaVersionElement.isJsonNull) {
            val name = root.get("name")?.takeIf { !it.isJsonNull }?.asString ?: "<unknown>"
            throw PuzzleValidationException(
                "Biome '$name' is missing required field 'schemaVersion'"
            )
        }
        val schemaVersion = schemaVersionElement.asInt
        if (schemaVersion !in MIN_SUPPORTED_SCHEMA_VERSION..CURRENT_SCHEMA_VERSION) {
            val name = root.get("name")?.takeIf { !it.isJsonNull }?.asString ?: "<unknown>"
            throw PuzzleValidationException(
                "Biome '$name' has unsupported schemaVersion $schemaVersion, expected $MIN_SUPPORTED_SCHEMA_VERSION..$CURRENT_SCHEMA_VERSION"
            )
        }

        return when (schemaVersion) {
            1 -> loadBiomeV1(json)
            2 -> loadBiomeV2(json)
            3 -> loadBiomeV3(json)
            else -> throw PuzzleValidationException(
                "Biome has unsupported schemaVersion $schemaVersion"
            )
        }
    }

    private fun loadBiomeV1(json: String): Biome {
        val dto = gson.fromJson(json, BiomeDtoV1::class.java)
        val biomeName = requireBiomeName(dto.name)
        val biomeId = requireBiomeId(dto.id, biomeName)
        val puzzlesDto = requirePuzzles(dto.puzzles, biomeName)

        val seenIds = mutableSetOf<Int>()
        val puzzles = puzzlesDto.map { puzzleDto ->
            val pid = puzzleDto.id ?: throw PuzzleValidationException(
                "Biome '$biomeName' has a puzzle missing required field 'id'"
            )
            if (!seenIds.add(pid)) {
                throw PuzzleValidationException(
                    "Biome '$biomeName' has two puzzles with id=$pid"
                )
            }
            puzzleDto.toDomain(biomeName)
        }

        val syntheticStage = Stage(
            id = 1,
            name = "All",
            targetPuzzleCount = puzzles.size.coerceAtLeast(1),
            puzzles = puzzles
        )
        return Biome(id = biomeId, name = biomeName, stages = listOf(syntheticStage))
    }

    private fun loadBiomeV2(json: String): Biome {
        val dto = gson.fromJson(json, BiomeDtoV2::class.java)
        val biomeName = requireBiomeName(dto.name)
        val biomeId = requireBiomeId(dto.id, biomeName)
        val puzzlesDto = requirePuzzles(dto.puzzles, biomeName)

        val seenIds = mutableSetOf<Int>()
        val puzzles = puzzlesDto.map { puzzleDto ->
            val pid = puzzleDto.id ?: throw PuzzleValidationException(
                "Biome '$biomeName' has a puzzle missing required field 'id'"
            )
            if (!seenIds.add(pid)) {
                throw PuzzleValidationException(
                    "Biome '$biomeName' has two puzzles with id=$pid"
                )
            }
            puzzleDto.toDomain(biomeName)
        }

        val syntheticStage = Stage(
            id = 1,
            name = "All",
            targetPuzzleCount = puzzles.size.coerceAtLeast(1),
            puzzles = puzzles
        )
        return Biome(id = biomeId, name = biomeName, stages = listOf(syntheticStage))
    }

    private fun loadBiomeV3(json: String): Biome {
        val dto = gson.fromJson(json, BiomeDtoV3::class.java)
        val biomeName = requireBiomeName(dto.name)
        val biomeId = requireBiomeId(dto.id, biomeName)

        val stagesDto = dto.stages
            ?: throw PuzzleValidationException(
                "Biome '$biomeName' is missing required field 'stages'"
            )
        if (stagesDto.isEmpty()) {
            throw PuzzleValidationException(
                "Biome '$biomeName' has empty 'stages' (must contain at least one stage)"
            )
        }

        val seenStageIds = mutableSetOf<Int>()
        val seenPuzzleIds = mutableSetOf<Int>()

        val stages = stagesDto.map { stageDto ->
            val sid = stageDto.id ?: throw PuzzleValidationException(
                "Biome '$biomeName' has a stage missing required field 'id'"
            )
            if (!seenStageIds.add(sid)) {
                throw PuzzleValidationException(
                    "Biome '$biomeName' has two stages with id=$sid"
                )
            }
            val sname = stageDto.name ?: throw PuzzleValidationException(
                "Stage (id=$sid) in biome '$biomeName' is missing required field 'name'"
            )
            if (sname.isBlank()) {
                throw PuzzleValidationException(
                    "Stage (id=$sid) in biome '$biomeName' has blank name"
                )
            }
            val stargetCount = stageDto.targetPuzzleCount ?: throw PuzzleValidationException(
                "Stage '$sname' in biome '$biomeName' is missing required field 'targetPuzzleCount'"
            )
            if (stargetCount <= 0) {
                throw PuzzleValidationException(
                    "Stage '$sname' in biome '$biomeName' has invalid targetPuzzleCount $stargetCount (must be > 0)"
                )
            }
            val puzzlesDto = stageDto.puzzles ?: throw PuzzleValidationException(
                "Stage '$sname' in biome '$biomeName' is missing required field 'puzzles'"
            )
            if (puzzlesDto.size > stargetCount) {
                throw PuzzleValidationException(
                    "Stage '$sname' in biome '$biomeName' has ${puzzlesDto.size} puzzles but targetPuzzleCount is $stargetCount"
                )
            }

            val puzzles = puzzlesDto.map { puzzleDto ->
                val pid = puzzleDto.id ?: throw PuzzleValidationException(
                    "Stage '$sname' in biome '$biomeName' has a puzzle missing required field 'id'"
                )
                if (!seenPuzzleIds.add(pid)) {
                    throw PuzzleValidationException(
                        "Biome '$biomeName' has two puzzles with id=$pid"
                    )
                }
                puzzleDto.toDomain(biomeName)
            }

            Stage(
                id = sid,
                name = sname,
                targetPuzzleCount = stargetCount,
                puzzles = puzzles
            )
        }

        return Biome(id = biomeId, name = biomeName, stages = stages)
    }

    private fun requireBiomeName(name: String?): String {
        if (name == null) {
            throw PuzzleValidationException("Biome is missing required field 'name'")
        }
        if (name.isBlank()) {
            throw PuzzleValidationException("Biome 'name' must not be blank")
        }
        return name
    }

    private fun requireBiomeId(id: Int?, biomeName: String): Int {
        if (id == null) {
            throw PuzzleValidationException("Biome '$biomeName' is missing required field 'id'")
        }
        return id
    }

    private fun <T> requirePuzzles(puzzles: List<T>?, biomeName: String): List<T> {
        if (puzzles == null) {
            throw PuzzleValidationException("Biome '$biomeName' is missing required field 'puzzles'")
        }
        return puzzles
    }

    private fun PuzzleDtoV1.toDomain(biomeName: String): Puzzle {
        val pid = id ?: throw PuzzleValidationException(
            "Biome '$biomeName' has a puzzle missing required field 'id'"
        )
        val pname = name ?: throw PuzzleValidationException(
            "Puzzle (id=$pid) in biome '$biomeName' is missing required field 'name'"
        )
        if (pname.isBlank()) {
            throw PuzzleValidationException(
                "Puzzle (id=$pid) in biome '$biomeName' has blank name"
            )
        }
        val context = "Puzzle '$pname' (id=$pid) in biome '$biomeName'"

        val psize = size ?: throw PuzzleValidationException(
            "$context is missing required field 'size'"
        )
        if (psize !in MIN_GRID_SIZE..MAX_GRID_SIZE) {
            throw PuzzleValidationException(
                "$context has invalid size $psize (expected $MIN_GRID_SIZE..$MAX_GRID_SIZE)"
            )
        }
        val pfunFact = funFact ?: throw PuzzleValidationException(
            "$context is missing required field 'funFact'"
        )
        if (pfunFact.isBlank()) {
            throw PuzzleValidationException("$context has empty funFact")
        }
        val ppalette = palette ?: throw PuzzleValidationException(
            "$context is missing required field 'palette'"
        )
        if (ppalette.isEmpty()) {
            throw PuzzleValidationException("$context has no palette colors")
        }
        val psolution = solution ?: throw PuzzleValidationException(
            "$context is missing required field 'solution'"
        )
        if (psolution.size != psize) {
            throw PuzzleValidationException(
                "$context: solution has ${psolution.size} rows, expected $psize"
            )
        }
        psolution.forEachIndexed { rowIndex, row ->
            if (row.size != psize) {
                throw PuzzleValidationException(
                    "$context: row $rowIndex has ${row.size} cells, expected $psize"
                )
            }
            row.forEachIndexed { colIndex, value ->
                if (value < 0 || value > ppalette.size) {
                    throw PuzzleValidationException(
                        "$context cell at ($rowIndex,$colIndex) uses color index $value but palette has only ${ppalette.size} colors"
                    )
                }
            }
        }

        if (psolution.all { row -> row.all { it == 0 } }) {
            throw PuzzleValidationException(
                "$context has no filled cells (solution is entirely empty)"
            )
        }

        val parsedPalette = ppalette.mapIndexed { index, hex ->
            parseHexColor(hex, "$context palette[$index]")
        }

        return Puzzle(
            id = pid,
            name = pname,
            gridSize = psize,
            palette = parsedPalette,
            solution = psolution,
            funFact = pfunFact
        )
    }

    private fun PuzzleDtoV2.toDomain(biomeName: String): Puzzle {
        val pid = id ?: throw PuzzleValidationException(
            "Biome '$biomeName' has a puzzle missing required field 'id'"
        )
        val pname = name ?: throw PuzzleValidationException(
            "Puzzle (id=$pid) in biome '$biomeName' is missing required field 'name'"
        )
        if (pname.isBlank()) {
            throw PuzzleValidationException(
                "Puzzle (id=$pid) in biome '$biomeName' has blank name"
            )
        }
        val context = "Puzzle '$pname' (id=$pid) in biome '$biomeName'"

        val psize = size ?: throw PuzzleValidationException(
            "$context is missing required field 'size'"
        )
        if (psize !in MIN_GRID_SIZE..MAX_GRID_SIZE) {
            throw PuzzleValidationException(
                "$context has invalid size $psize (expected $MIN_GRID_SIZE..$MAX_GRID_SIZE)"
            )
        }
        val pfunFact = funFact ?: throw PuzzleValidationException(
            "$context is missing required field 'funFact'"
        )
        if (pfunFact.isBlank()) {
            throw PuzzleValidationException("$context has empty funFact")
        }
        val ppalette = palette ?: throw PuzzleValidationException(
            "$context is missing required field 'palette'"
        )
        if (ppalette.isEmpty()) {
            throw PuzzleValidationException("$context has no palette colors")
        }
        val psolution = solution ?: throw PuzzleValidationException(
            "$context is missing required field 'solution'"
        )

        val parsedPalette = mutableListOf<Int>()
        val keyToIndex = mutableMapOf<Char, Int>()
        var index = 1
        for ((key, hex) in ppalette) {
            if (key.length != 1) {
                throw PuzzleValidationException(
                    "$context palette key '$key' must be exactly 1 character"
                )
            }
            val keyChar = key[0]
            if (keyChar == '.' || keyChar == ' ') {
                throw PuzzleValidationException(
                    "$context palette key '$key' is reserved (use '.' or ' ' only for empty cells)"
                )
            }
            if (keyToIndex.containsKey(keyChar)) {
                throw PuzzleValidationException(
                    "$context palette has duplicate key '$key'"
                )
            }
            parsedPalette.add(parseHexColor(hex, "$context palette['$key']"))
            keyToIndex[keyChar] = index
            index++
        }

        if (psolution.size != psize) {
            throw PuzzleValidationException(
                "$context: solution has ${psolution.size} rows, expected $psize"
            )
        }

        val internalSolution = psolution.mapIndexed { rowIndex, rowString ->
            if (rowString.length != psize) {
                throw PuzzleValidationException(
                    "$context: row $rowIndex has ${rowString.length} cells, expected $psize"
                )
            }
            rowString.mapIndexed { colIndex, ch ->
                when (ch) {
                    '.', ' ' -> 0
                    else -> keyToIndex[ch] ?: throw PuzzleValidationException(
                        "$context cell at ($rowIndex,$colIndex) uses character '$ch' which is not a palette key"
                    )
                }
            }
        }

        if (internalSolution.all { row -> row.all { it == 0 } }) {
            throw PuzzleValidationException(
                "$context has no filled cells (solution is entirely empty)"
            )
        }

        return Puzzle(
            id = pid,
            name = pname,
            gridSize = psize,
            palette = parsedPalette,
            solution = internalSolution,
            funFact = pfunFact
        )
    }

    private fun parseHexColor(hex: String?, context: String): Int {
        if (hex == null) {
            throw PuzzleValidationException("$context is missing color value")
        }
        if (!HEX_COLOR_REGEX.matches(hex)) {
            throw PuzzleValidationException(
                "$context is not a valid #RRGGBB color: '$hex'"
            )
        }
        return try {
            Color.parseColor(hex)
        } catch (e: IllegalArgumentException) {
            throw PuzzleValidationException(
                "$context is not a valid #RRGGBB color: '$hex'"
            )
        }
    }

    private data class BiomeDtoV1(
        val schemaVersion: Int?,
        val id: Int?,
        val name: String?,
        val puzzles: List<PuzzleDtoV1>?
    )

    private data class PuzzleDtoV1(
        val id: Int?,
        val name: String?,
        val size: Int?,
        val funFact: String?,
        val palette: List<String>?,
        val solution: List<List<Int>>?
    )

    private data class BiomeDtoV2(
        val schemaVersion: Int?,
        val id: Int?,
        val name: String?,
        val puzzles: List<PuzzleDtoV2>?
    )

    private data class PuzzleDtoV2(
        val id: Int?,
        val name: String?,
        val size: Int?,
        val funFact: String?,
        val palette: Map<String, String>?,
        val solution: List<String>?
    )

    private data class BiomeDtoV3(
        val schemaVersion: Int?,
        val id: Int?,
        val name: String?,
        val stages: List<StageDto>?
    )

    private data class StageDto(
        val id: Int?,
        val name: String?,
        val targetPuzzleCount: Int?,
        val puzzles: List<PuzzleDtoV2>?
    )
}
