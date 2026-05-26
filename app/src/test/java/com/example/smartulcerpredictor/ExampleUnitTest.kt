package com.example.smartulcerpredictor

import org.junit.Test
import org.junit.Assert.*
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ExampleUnitTest {

    fun rgbToHsv(r: Int, g: Int, b: Int, hsv: FloatArray) {
        val rf = r / 255.0f
        val gf = g / 255.0f
        val bf = b / 255.0f
        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val d = max - min
        val v = max
        val s = if (max == 0f) 0f else d / max
        var h = 0f
        if (max != min) {
            h = when (max) {
                rf -> (gf - bf) / d + (if (gf < bf) 6f else 0f)
                gf -> (bf - rf) / d + 2f
                bf -> (rf - gf) / d + 4f
                else -> 0f
            }
            h /= 6f
        }
        hsv[0] = h * 360f
        hsv[1] = s
        hsv[2] = v
    }

    private fun isWoundImageJVM(img: BufferedImage): Boolean {
        // Resize to 100x100 for analysis
        val scaled = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
        val g2d = scaled.createGraphics()
        g2d.drawImage(img, 0, 0, 100, 100, null)
        g2d.dispose()

        var skinWoundCount = 0
        var activeWoundCount = 0
        val total = 10000.0f
        
        val hsv = FloatArray(3)
        for (y in 0 until 100) {
            for (x in 0 until 100) {
                val rgb = scaled.getRGB(x, y)
                val r = (rgb shr 16) and 0xFF
                val g = (rgb shr 8) and 0xFF
                val b = rgb and 0xFF
                
                rgbToHsv(r, g, b, hsv)
                val h = hsv[0]
                val s = hsv[1]
                val v = hsv[2]
                
                // Matches logic inside UlcerClassifier.kt
                val isSkinOrWound = (h in 0f..52f || h in 330f..360f) && 
                                    (s in 0.12f..0.70f) && 
                                    (v in 0.12f..0.95f)
                                    
                if (isSkinOrWound) {
                    skinWoundCount++
                    
                    val isGranulation = (h in 0f..14f || h in 340f..360f) && (s >= 0.28f) && (v >= 0.22f)
                    val isSlough = (h in 28.0f..55f) && (s in 0.15f..0.52f) && (v >= 0.48f)
                    val isNecrotic = (v <= 0.2f) && (s >= 0.08f) && (h in 0f..50f || h in 330f..360f)
                    
                    if (isGranulation || isSlough || isNecrotic) {
                        activeWoundCount++
                    }
                }
            }
        }
        
        val skinRatio = skinWoundCount / total
        val woundRatio = activeWoundCount / total
        println("Validation Ratio Stats -> SkinWoundRatio = ${"%.2f".format(skinRatio * 100)}%, ActiveWoundRatio = ${"%.2f".format(woundRatio * 100)}%")
        return skinRatio >= 0.35f && woundRatio >= 0.015f
    }

    @Test
    fun testWoundHeuristic() {
        val files = mapOf(
            "f25b74738b5a8d7a85da75bbb6f3bbf9.jpg" to true,  // Real Ulcer
            "49e0c0ac2b0eac88eb5e0d2cd20e11db.jpg" to false, // Boy in blue shirt
            "ee6e041c8a6452f87be96608ff2b4b93.jpg" to false, // Screenshot
            "cee7d1fa209ea3b95d760b5c824a534d.jpg" to false  // Shoe on orange background
        )

        for ((fileName, expectedResult) in files) {
            val file = File("C:/xampp/htdocs/smart_ulcer_api/uploads/$fileName")
            assertTrue("File should exist: ${file.absolutePath}", file.exists())
            
            val img = ImageIO.read(file)
            assertNotNull("Image should be loaded: $fileName", img)
            
            println("Testing $fileName (Expected: $expectedResult):")
            val result = isWoundImageJVM(img)
            assertEquals("Heuristic result mismatch for $fileName", expectedResult, result)
            println("Result: Match!\n")
        }
    }
}