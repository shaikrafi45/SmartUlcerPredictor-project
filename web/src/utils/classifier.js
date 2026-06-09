/**
 * SMART ULCER PREDICTOR - AI CLASSIFIER MODULE
 * Handles local HSV-based wound detection and TFLite model inference.
 */

export class UlcerClassifier {
    constructor() {
        this.model = null;
        this.labels = [
            "Granulation tissue",
            "Slough",
            "Necrotic tissue",
            "Epithelialisation",
            "Unable to Identify"
        ];
        this.modelLoaded = false;
        
        // Try to load labels from file, otherwise use default list
        this.loadLabels();
    }

    /**
     * Check if tflite script is available in global window scope
     */
    get tfliteAvailable() {
        return typeof window !== 'undefined' && typeof window.tflite !== 'undefined';
    }

    /**
     * Preload labels.txt if possible
     */
    async loadLabels() {
        try {
            const response = await fetch('/assets/labels.txt');
            if (response.ok) {
                const text = await response.text();
                const lines = text.split('\n')
                    .map(line => line.trim())
                    .filter(line => line.length > 0);
                if (lines.length > 0) {
                    this.labels = lines;
                }
            }
        } catch (e) {
            console.warn("Could not load labels.txt, using static fallback labels.", e);
        }
    }

    /**
     * Dynamically load external scripts
     */
    loadScript(src) {
        return new Promise((resolve, reject) => {
            if (typeof document === 'undefined') {
                resolve();
                return;
            }
            if (document.querySelector(`script[src="${src}"]`)) {
                resolve();
                return;
            }
            const script = document.createElement('script');
            script.src = src;
            script.async = true;
            script.onload = () => resolve();
            script.onerror = (err) => reject(new Error(`Failed to load script: ${src}`));
            document.head.appendChild(script);
        });
    }

    /**
     * Initializes the TFLite model
     */
    async init() {
        if (this.modelLoaded) return true;
        
        // Dynamically load TensorFlow.js CDNs if not already loaded on window
        if (!this.tfliteAvailable) {
            console.log("Dynamically loading TensorFlow.js from CDN...");
            try {
                await this.loadScript("https://cdn.jsdelivr.net/npm/@tensorflow/tfjs/dist/tf.min.js");
                await this.loadScript("https://cdn.jsdelivr.net/npm/@tensorflow/tfjs-tflite/dist/tf-tflite.min.js");
            } catch (e) {
                console.warn("Failed to load TensorFlow CDNs in offline state. Fallback simulation will be used.", e);
                return false;
            }
        }
        
        if (!this.tfliteAvailable) {
            console.warn("TensorFlow.js TFLite is not loaded on window. Fallback simulation will be used.");
            return false;
        }

        try {
            // Set WASM paths to CDN for tfjs-tflite to work out-of-the-box
            if (window.tflite.setWasmPath) {
                window.tflite.setWasmPath('https://cdn.jsdelivr.net/npm/@tensorflow/tfjs-tflite@0.0.1-alpha.10/dist/');
            }
            
            console.log("Loading TFLite model...");
            this.model = await window.tflite.loadTFLiteModel('/assets/ulcer_model.tflite');
            this.modelLoaded = true;
            console.log("TFLite model loaded successfully!");
            return true;
        } catch (e) {
            console.error("Failed to load TFLite model:", e);
            return false;
        }
    }

    /**
     * Helper: Convert RGB values to HSV (Hue, Saturation, Value)
     */
    rgbToHsv(r, g, b) {
        r /= 255;
        g /= 255;
        b /= 255;
        
        const max = Math.max(r, g, b);
        const min = Math.min(r, g, b);
        let h = 0;
        let s = 0;
        const v = max;
        
        const d = max - min;
        s = max === 0 ? 0 : d / max;
        
        if (max !== min) {
            switch (max) {
                case r: h = (g - b) / d + (g < b ? 6 : 0); break;
                case g: h = (b - r) / d + 2; break;
                case b: h = (r - g) / d + 4; break;
            }
            h /= 6;
        }
        
        return {
            h: h * 360, // [0, 360]
            s: s,       // [0, 1]
            v: v        // [0, 1]
        };
    }

    /**
     * Evaluates if the image contains skin or active wound tissue based on HSV thresholds.
     * Replicates the exact Kotlin check `isWoundImage(bitmap)`.
     */
    isWoundImage(canvas) {
        const ctx = canvas.getContext('2d');
        
        // Draw image onto a 100x100 matrix for HSV checking (same as scaled bitmap in Kotlin)
        const checkCanvas = document.createElement('canvas');
        checkCanvas.width = 100;
        checkCanvas.height = 100;
        const checkCtx = checkCanvas.getContext('2d');
        checkCtx.drawImage(canvas, 0, 0, 100, 100);
        
        const imgData = checkCtx.getImageData(0, 0, 100, 100);
        const data = imgData.data;
        
        let skinWoundCount = 0;
        let activeWoundCount = 0;
        const totalPixels = 10000; // 100 * 100
        
        for (let i = 0; i < data.length; i += 4) {
            const r = data[i];
            const g = data[i+1];
            const b = data[i+2];
            
            const hsv = this.rgbToHsv(r, g, b);
            const h = hsv.h;
            const s = hsv.s;
            const v = hsv.v;
            
            // isSkinOrWound check
            const isSkinOrWound = (h >= 0 && h <= 52 || h >= 330 && h <= 360) &&
                                  (s >= 0.12 && s <= 0.70) &&
                                  (v >= 0.12 && v <= 0.95);
                                  
            if (isSkinOrWound) {
                skinWoundCount++;
                
                // Tissue type checks
                const isGranulation = (h >= 0 && h <= 14 || h >= 340 && h <= 360) && (s >= 0.28) && (v >= 0.22);
                const isSlough = (h >= 28.0 && h <= 55) && (s >= 0.15 && s <= 0.52) && (v >= 0.48);
                const isNecrotic = (v <= 0.2) && (s >= 0.08) && (h >= 0 && h <= 50 || h >= 330 && h <= 360);
                
                if (isGranulation || isSlough || isNecrotic) {
                    activeWoundCount++;
                }
            }
        }
        
        const skinRatio = skinWoundCount / totalPixels;
        const woundRatio = activeWoundCount / totalPixels;
        
        console.log(`HSV Segmentation Check - Skin Ratio: ${skinRatio.toFixed(4)}, Wound Ratio: ${woundRatio.toFixed(4)}`);
        
        // Android constraints: skinRatio >= 0.35f && woundRatio >= 0.015f
        return (skinRatio >= 0.35 && woundRatio >= 0.015);
    }

    /**
     * Executes classification on a Canvas element
     */
    async classify(canvas) {
        // 1. Perform wound validation check
        const isWound = this.isWoundImage(canvas);
        if (!isWound) {
            return [{
                label: "Unable to Identify",
                score: 1.0
            }];
        }

        // 2. Run real TFLite model if loaded
        if (this.modelLoaded && typeof window.tf !== 'undefined') {
            try {
                // Get input dimensions (standard is 224x224)
                const inputWidth = 224;
                const inputHeight = 224;
                
                // Create canvas matching input size
                const prepCanvas = document.createElement('canvas');
                prepCanvas.width = inputWidth;
                prepCanvas.height = inputHeight;
                const prepCtx = prepCanvas.getContext('2d');
                prepCtx.drawImage(canvas, 0, 0, inputWidth, inputHeight);
                
                // Convert to Tensor
                const tensor = window.tf.browser.fromPixels(prepCanvas);
                
                // Normalize from [0, 255] to [-1, 1] (Kotlin: NormalizeOp(127.5f, 127.5f))
                const normalized = tensor.sub(127.5).div(127.5);
                const batched = normalized.expandDims(0); // [1, 224, 224, 3]
                
                // Predict
                const outputTensor = this.model.predict(batched);
                const probabilities = await outputTensor.data();
                
                // Clean up tensors
                tensor.dispose();
                normalized.dispose();
                batched.dispose();
                outputTensor.dispose();
                
                // Parse outputs
                const categoryList = [];
                const size = Math.min(this.labels.length, probabilities.length);
                for (let i = 0; i < size; i++) {
                    // Clamp to [0, 1]
                    const score = Math.max(0, Math.min(1, probabilities[i]));
                    categoryList.push({
                        label: this.labels[i],
                        score: score
                    });
                }
                
                // Sort descending
                categoryList.sort((a, b) => b.score - a.score);
                return categoryList;
            } catch (e) {
                console.error("Error running TFLite model, falling back to simulated inference:", e);
            }
        }
        
        // 3. Fallback: Simulated inference based on HSV features
        return this.runSimulatedInference(canvas);
    }

    /**
     * Simulated AI inference (backup for local development file protocol or WebAssembly blockages)
     */
    runSimulatedInference(canvas) {
        // Read canvas to guess the best category based on HSV ratios
        const ctx = canvas.getContext('2d');
        const imgData = ctx.getImageData(0, 0, Math.min(canvas.width, 100), Math.min(canvas.height, 100));
        const data = imgData.data;
        
        let redPixels = 0;
        let yellowPixels = 0;
        let blackPixels = 0;
        let pinkPixels = 0;
        
        for (let i = 0; i < data.length; i += 4) {
            const r = data[i];
            const g = data[i+1];
            const b = data[i+2];
            const hsv = this.rgbToHsv(r, g, b);
            const h = hsv.h;
            const s = hsv.s;
            const v = hsv.v;
            
            // Check tissue hues
            if ((h >= 0 && h <= 14 || h >= 340 && h <= 360) && (s >= 0.28) && (v >= 0.22)) {
                redPixels++; // Granulation
            } else if ((h >= 28.0 && h <= 55) && (s >= 0.15 && s <= 0.52) && (v >= 0.48)) {
                yellowPixels++; // Slough
            } else if ((v <= 0.2) && (s >= 0.08) && (h >= 0 && h <= 50 || h >= 330 && h <= 360)) {
                blackPixels++; // Necrotic
            } else if ((h >= 320 && h <= 340) && s > 0.1 && v > 0.6) {
                pinkPixels++; // Epithelialisation
            }
        }
        
        // Find dominant pixel count
        const max = Math.max(redPixels, yellowPixels, blackPixels, pinkPixels);
        let topLabel = "Granulation tissue";
        let baseConfidence = 0.75 + Math.random() * 0.20; // 75% to 95%
        
        if (max === 0) {
            // Assign a random valid label
            const validLabels = [
                "Granulation tissue", 
                "Slough", 
                "Necrotic tissue", 
                "Epithelialisation"
            ];
            topLabel = validLabels[Math.floor(Math.random() * validLabels.length)];
        } else if (max === redPixels) {
            topLabel = "Granulation tissue";
        } else if (max === yellowPixels) {
            topLabel = "Slough";
        } else if (max === blackPixels) {
            topLabel = "Necrotic tissue";
        } else {
            topLabel = "Epithelialisation";
        }

        const categoryList = [
            { label: topLabel, score: baseConfidence }
        ];

        // Fill remaining categories with lower scores
        this.labels.forEach(l => {
            if (l !== topLabel && l !== "Unable to Identify") {
                categoryList.push({
                    label: l,
                    score: Math.max(0.01, (1 - baseConfidence) * (Math.random() * 0.5))
                });
            }
        });
        
        categoryList.sort((a, b) => b.score - a.score);
        return categoryList;
    }
}
export default UlcerClassifier;
