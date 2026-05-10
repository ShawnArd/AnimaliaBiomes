package com.nonogram.animaliabiomes.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PuzzleSerializerTest {

    @Test
    fun parsesValidBiome() {
        val biome = PuzzleSerializer.loadBiome(VALID_FISH_BIOME)
        assertEquals(1, biome.id)
        assertEquals("Ocean", biome.name)
        assertEquals(1, biome.puzzles.size)
        assertEquals("Fish", biome.puzzles[0].name)
        assertEquals(5, biome.puzzles[0].gridSize)
        assertEquals(1, biome.stages.size)
    }

    @Test
    fun v3ParsesOceanWithFourStages() {
        val biome = PuzzleSerializer.loadBiome(VALID_V3_OCEAN)
        assertEquals(1, biome.id)
        assertEquals("Ocean", biome.name)
        assertEquals(4, biome.stages.size)
        assertEquals("Shores", biome.stages[0].name)
        assertEquals(2, biome.stages[0].puzzles.size)
        assertEquals(4, biome.stages[0].targetPuzzleCount)
        assertEquals("Shallows", biome.stages[1].name)
        assertEquals(0, biome.stages[1].puzzles.size)
        assertEquals("Depths", biome.stages[2].name)
        assertEquals(0, biome.stages[2].puzzles.size)
        assertEquals("Open Ocean", biome.stages[3].name)
        assertEquals(8, biome.stages[3].targetPuzzleCount)
        assertEquals(2, biome.puzzles.size)
    }

    @Test
    fun v3RejectsMissingStages() {
        val json = """
            { "schemaVersion": 3, "id": 1, "name": "Ocean" }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'stages'"))
    }

    @Test
    fun v3RejectsEmptyStages() {
        val json = """
            { "schemaVersion": 3, "id": 1, "name": "Ocean", "stages": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("empty 'stages'"))
    }

    @Test
    fun v3RejectsStageWithPuzzlesOverTarget() {
        val json = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                {
                  "id": 1,
                  "name": "Shores",
                  "targetPuzzleCount": 1,
                  "puzzles": [
                    {
                      "id": 1, "name": "Fish", "size": 1, "funFact": "Hi.",
                      "palette": { "O": "#F57C00" }, "solution": ["O"]
                    },
                    {
                      "id": 2, "name": "Crab", "size": 1, "funFact": "Hi.",
                      "palette": { "R": "#C62828" }, "solution": ["R"]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("targetPuzzleCount is 1"))
    }

    @Test
    fun v3RejectsCrossStageDuplicatePuzzleId() {
        val json = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                {
                  "id": 1, "name": "Shores", "targetPuzzleCount": 4,
                  "puzzles": [
                    {
                      "id": 1, "name": "Fish", "size": 1, "funFact": "Hi.",
                      "palette": { "O": "#F57C00" }, "solution": ["O"]
                    }
                  ]
                },
                {
                  "id": 2, "name": "Shallows", "targetPuzzleCount": 4,
                  "puzzles": [
                    {
                      "id": 1, "name": "Crab", "size": 1, "funFact": "Hi.",
                      "palette": { "R": "#C62828" }, "solution": ["R"]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("two puzzles with id=1"))
    }

    @Test
    fun v3RejectsBlankStageName() {
        val json = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                {
                  "id": 1, "name": "   ", "targetPuzzleCount": 4, "puzzles": []
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("blank name"))
    }

    @Test
    fun v3RejectsTargetPuzzleCountZero() {
        val json = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                {
                  "id": 1, "name": "Shores", "targetPuzzleCount": 0, "puzzles": []
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("invalid targetPuzzleCount"))
    }

    @Test
    fun v3RejectsDuplicateStageIds() {
        val json = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                { "id": 1, "name": "Shores", "targetPuzzleCount": 4, "puzzles": [] },
                { "id": 1, "name": "Shallows", "targetPuzzleCount": 4, "puzzles": [] }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("two stages with id=1"))
    }

    @Test
    fun rejectsMissingSchemaVersion() {
        val json = """
            { "id": 1, "name": "Ocean", "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("schemaVersion"))
    }

    @Test
    fun rejectsUnsupportedSchemaVersion() {
        val json = """
            { "schemaVersion": 99, "id": 1, "name": "Ocean", "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("unsupported"))
    }

    @Test
    fun rejectsSchemaVersionZero() {
        val json = """
            { "schemaVersion": 0, "id": 1, "name": "Ocean", "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("unsupported"))
    }

    @Test
    fun rejectsSchemaVersion999() {
        val json = """
            { "schemaVersion": 999, "id": 1, "name": "Ocean", "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("unsupported"))
    }

    @Test
    fun rejectsSolutionRowCountMismatch() {
        val json = puzzleJson(
            size = 5,
            palette = listOf("#FF0000"),
            solutionRows = listOf(
                "[1,1,1,1,1]",
                "[1,1,1,1,1]"
            )
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("solution has 2 rows"))
    }

    @Test
    fun rejectsSolutionRowWidthMismatch() {
        val json = puzzleJson(
            size = 5,
            palette = listOf("#FF0000"),
            solutionRows = listOf(
                "[1,1,1,1,1]",
                "[1,1,1,1,1]",
                "[1,1,1,1]",
                "[1,1,1,1,1]",
                "[1,1,1,1,1]"
            )
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("row 2 has 4 cells"))
    }

    @Test
    fun rejectsOutOfRangeColorIndex() {
        val json = puzzleJson(
            size = 3,
            palette = listOf("#FF0000"),
            solutionRows = listOf(
                "[1,1,1]",
                "[1,2,1]",
                "[1,1,1]"
            )
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("color index 2"))
    }

    @Test
    fun rejectsBadHexColor() {
        val json = puzzleJson(
            size = 1,
            palette = listOf("not-a-hex"),
            solutionRows = listOf("[1]")
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("not a valid #RRGGBB color"))
    }

    @Test
    fun rejectsDuplicatePuzzleId() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                ${puzzleObject(id = 1, name = "Fish", size = 1, palette = listOf("#FF0000"), solutionRows = listOf("[1]"))},
                ${puzzleObject(id = 1, name = "Crab", size = 1, palette = listOf("#00FF00"), solutionRows = listOf("[1]"))}
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("two puzzles with id=1"))
    }

    @Test
    fun rejectsEmptyPalette() {
        val json = puzzleJson(
            size = 1,
            palette = emptyList(),
            solutionRows = listOf("[0]")
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("no palette colors"))
    }

    @Test
    fun rejectsBlankFunFact() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 1,
                  "funFact": "",
                  "palette": ["#FF0000"],
                  "solution": [[1]]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("empty funFact"))
    }

    @Test
    fun rejectsSizeZero() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 0,
                  "funFact": "An empty fish.",
                  "palette": ["#FF0000"],
                  "solution": []
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("invalid size"))
    }

    @Test
    fun rejectsSizeNegative() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": -1,
                  "funFact": "A negative fish.",
                  "palette": ["#FF0000"],
                  "solution": []
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("invalid size"))
    }

    @Test
    fun rejectsMissingTopLevelId() {
        val json = """
            { "schemaVersion": 1, "name": "Ocean", "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'id'"))
    }

    @Test
    fun rejectsMissingTopLevelName() {
        val json = """
            { "schemaVersion": 1, "id": 1, "puzzles": [] }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'name'"))
    }

    @Test
    fun rejectsMissingTopLevelPuzzles() {
        val json = """
            { "schemaVersion": 1, "id": 1, "name": "Ocean" }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'puzzles'"))
    }

    @Test
    fun rejectsMissingPaletteField() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 1,
                  "funFact": "Hello.",
                  "solution": [[1]]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'palette'"))
    }

    @Test
    fun rejectsBlankPuzzleName() {
        val json = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "   ",
                  "size": 1,
                  "funFact": "Hello.",
                  "palette": ["#FF0000"],
                  "solution": [[1]]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("blank name"))
    }

    @Test
    fun rejectsAllZeroSolutionV1() {
        val json = puzzleJson(
            size = 3,
            palette = listOf("#FF0000"),
            solutionRows = listOf(
                "[0,0,0]",
                "[0,0,0]",
                "[0,0,0]"
            )
        )
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("no filled cells"))
    }

    @Test
    fun v2KeyedPaletteParsesCorrectly() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 5,
                  "funFact": "Fish facts.",
                  "palette": { "O": "#F57C00" },
                  "solution": [
                    ".OOO.",
                    "O...O",
                    "OOOOO",
                    "O...O",
                    ".OOO."
                  ]
                }
              ]
            }
        """.trimIndent()
        val biome = PuzzleSerializer.loadBiome(json)
        val puzzle = biome.puzzles[0]
        assertEquals(1, puzzle.palette.size)
        assertEquals(5, puzzle.gridSize)
        // Row 0: ".OOO." -> 0,1,1,1,0
        assertEquals(listOf(0, 1, 1, 1, 0), puzzle.solution[0])
        // Row 2: "OOOOO" -> 1,1,1,1,1
        assertEquals(listOf(1, 1, 1, 1, 1), puzzle.solution[2])
    }

    @Test
    fun v2MultiKeyPaletteAssignsIndicesInOrder() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "TwoColor",
                  "size": 2,
                  "funFact": "Two colors.",
                  "palette": { "A": "#FF0000", "B": "#00FF00" },
                  "solution": [
                    "AB",
                    "BA"
                  ]
                }
              ]
            }
        """.trimIndent()
        val biome = PuzzleSerializer.loadBiome(json)
        val puzzle = biome.puzzles[0]
        assertEquals(listOf(1, 2), puzzle.solution[0])
        assertEquals(listOf(2, 1), puzzle.solution[1])
    }

    @Test
    fun v2RejectsMultiCharPaletteKey() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 1,
                  "funFact": "Hi.",
                  "palette": { "OO": "#F57C00" },
                  "solution": ["O"]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("exactly 1 character"))
    }

    @Test
    fun v2RejectsSolutionCharNotInPalette() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 2,
                  "funFact": "Hi.",
                  "palette": { "O": "#F57C00" },
                  "solution": ["OZ", "OO"]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("'Z'"))
    }

    @Test
    fun v2RejectsAllZeroSolution() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Empty",
                  "size": 3,
                  "funFact": "Hi.",
                  "palette": { "O": "#F57C00" },
                  "solution": ["...", "...", "..."]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("no filled cells"))
    }

    @Test
    fun v2RejectsReservedDotKey() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Bad",
                  "size": 1,
                  "funFact": "Hi.",
                  "palette": { ".": "#F57C00" },
                  "solution": ["."]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("reserved"))
    }

    @Test
    fun v2RowLengthMismatch() {
        val json = """
            {
              "schemaVersion": 2,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 3,
                  "funFact": "Hi.",
                  "palette": { "O": "#F57C00" },
                  "solution": ["OO", "OOO", "OOO"]
                }
              ]
            }
        """.trimIndent()
        val ex = assertThrows(PuzzleValidationException::class.java) {
            PuzzleSerializer.loadBiome(json)
        }
        assert(ex.message!!.contains("row 0"))
    }

    private fun puzzleJson(
        size: Int,
        palette: List<String>,
        solutionRows: List<String>
    ): String = """
        {
          "schemaVersion": 1,
          "id": 1,
          "name": "Ocean",
          "puzzles": [
            ${puzzleObject(id = 1, name = "Test", size = size, palette = palette, solutionRows = solutionRows)}
          ]
        }
    """.trimIndent()

    private fun puzzleObject(
        id: Int,
        name: String,
        size: Int,
        palette: List<String>,
        solutionRows: List<String>
    ): String {
        val paletteJson = palette.joinToString(",") { "\"$it\"" }
        val solutionJson = solutionRows.joinToString(",")
        return """
            {
              "id": $id,
              "name": "$name",
              "size": $size,
              "funFact": "Some fun fact about $name.",
              "palette": [$paletteJson],
              "solution": [$solutionJson]
            }
        """.trimIndent()
    }

    companion object {
        private val VALID_V3_OCEAN = """
            {
              "schemaVersion": 3,
              "id": 1,
              "name": "Ocean",
              "stages": [
                {
                  "id": 1,
                  "name": "Shores",
                  "targetPuzzleCount": 4,
                  "puzzles": [
                    {
                      "id": 1,
                      "name": "Fish",
                      "size": 5,
                      "funFact": "Fish facts.",
                      "palette": { "O": "#F57C00" },
                      "solution": [".OOO.", "O...O", "OOOOO", "O...O", ".OOO."]
                    },
                    {
                      "id": 2,
                      "name": "Crab",
                      "size": 5,
                      "funFact": "Crab facts.",
                      "palette": { "R": "#C62828" },
                      "solution": ["R.R.R", "RRRRR", ".RRR.", "RRRRR", ".R.R."]
                    }
                  ]
                },
                { "id": 2, "name": "Shallows", "targetPuzzleCount": 4, "puzzles": [] },
                { "id": 3, "name": "Depths", "targetPuzzleCount": 4, "puzzles": [] },
                { "id": 4, "name": "Open Ocean", "targetPuzzleCount": 8, "puzzles": [] }
              ]
            }
        """.trimIndent()

        private val VALID_FISH_BIOME = """
            {
              "schemaVersion": 1,
              "id": 1,
              "name": "Ocean",
              "puzzles": [
                {
                  "id": 1,
                  "name": "Fish",
                  "size": 5,
                  "funFact": "Fish sleep with eyes open.",
                  "palette": ["#F57C00"],
                  "solution": [
                    [0, 1, 1, 1, 0],
                    [1, 0, 0, 0, 1],
                    [1, 1, 1, 1, 1],
                    [1, 0, 0, 0, 1],
                    [0, 1, 1, 1, 0]
                  ]
                }
              ]
            }
        """.trimIndent()
    }
}
