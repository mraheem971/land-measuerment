# 📐 Pak Land Measure (Android App)

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Material 3](https://img.shields.io/badge/UI-Material%203-0B6623?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Theme](https://img.shields.io/badge/Theme-Pakistani%20Green%20%26%20Gold-01411C?style=for-the-badge)](https://github.com/mraheem971/land-measuerment)

A high-precision, elegant Android application tailored for **Pakistani Land Measurement Systems** (پیمائش اراضی). It supports 4-sided plots (both equal rectangular and irregular shapes with diagonal via Heron's formula), dynamic visual plot diagrams, local SQLite storage, 1-tap WhatsApp sharing, and multi-unit conversions.

---

## ✨ Features

- 🇵🇰 **Pakistani Land Standards**:
  - **272.25 Sq Ft / Marla** (*Default Patwari / Government Revenue Standard* — $1\text{ Sarsahi} = 30.25\text{ sq ft}$ / $16\text{ Karam}^2$).
  - **225 Sq Ft / Marla** (*Punjab / Housing Schemes / LDA / DHA Standard* — $1\text{ Sarsahi} = 25\text{ sq ft}$).
  - **250 Sq Ft / Marla** (*Custom / Commercial Standard*).
- 📏 **4-Sided Plot Calculation Modes**:
  - **Equal / Regular 4 Sides (Average Method)**: For rectangular and quadrilateral plots calculated by average length $\times$ average width.
  - **4 Irregular Sides + Diagonal (Heron's Formula)**: For uneven plots with non-90° corners. Computes precise area by splitting the plot into two triangles along the diagonal and validating triangle inequality theorems.
- 🎨 **Real-Time Interactive Plot Diagram**:
  - Custom dynamic Canvas view (`PlotPreviewView`) that draws the plot shape, corner vertices (A, B, C, D), labeled dimensions, and diagonal line in real-time as dimensions are typed.
- 🔄 **Instant Land Unit Converter**:
  - 2-way conversion across **Square Feet**, **Marlas**, **Kanals**, **Gaj / Square Yards**, **Square Meters**, **Sarsahi**, **Acres / Qila**, and **Murabba (25 Acres)**.
- 💾 **Local Storage & History**:
  - Save measurements with plot title, location/owner, dimensions, and timestamp using local SQLite database.
  - Search and filter records by title or location.
  - 1-Tap formatted share to **WhatsApp**, SMS, or clipboard.
- 📱 **Responsive & Keyboard-Aware UI**:
  - Fully responsive on all device screen sizes.
  - Smooth Light & Dark theme support with Pakistan Flag Green (`#013214` / `#0B6623`) and Crescent Gold (`#D97706`).
  - Dynamic soft keyboard (IME) insets handling and auto-scroll focus so input fields are never blocked.
  - Single-line, uncrowded buttons with a maximum of 2 buttons per row.

---

## 📐 Mathematical Formulation

### 1. Irregular 4-Sided Plots (Heron's Formula)
Given a quadrilateral with side lengths $A, B, C, D$ and diagonal $D_{diag}$ connecting opposite corners:

$$\text{Triangle 1 } (A, B, D_{diag}): \quad s_1 = \frac{A + B + D_{diag}}{2}, \quad \text{Area}_1 = \sqrt{s_1(s_1 - A)(s_1 - B)(s_1 - D_{diag})}$$

$$\text{Triangle 2 } (C, D, D_{diag}): \quad s_2 = \frac{C + D + D_{diag}}{2}, \quad \text{Area}_2 = \sqrt{s_2(s_2 - C)(s_2 - D)(s_2 - D_{diag})}$$

$$\text{Total Area} = \text{Area}_1 + \text{Area}_2$$

### 2. Pakistani Unit Decomposition Hierarchy
$$\text{Kanals} = \lfloor \frac{\text{Total Sq Ft}}{\text{Sq Ft per Kanal}} \rfloor$$
$$\text{Marlas} = \lfloor \frac{\text{Remaining Sq Ft}}{\text{Sq Ft per Marla}} \rfloor$$
$$\text{Sarsahi} = \lfloor \frac{\text{Remaining Sq Ft}}{\text{Sq Ft per Sarsahi}} \rfloor$$

---

## 🏗️ Project Architecture

```
app/src/main/java/com/land/measurement/
├── MainActivity.java                # Main navigation container with ViewPager2 & IME insets
├── calculator/
│   └── PakLandCalculator.java       # Core geometric & conversion calculation engine
├── db/
│   └── LandDatabaseHelper.java      # SQLite database helper for local storage
├── model/
│   ├── CalculationMode.java         # Calculation mode enum (Equal vs Heron)
│   ├── InputUnit.java               # Input units (Feet, Gaj, Meters, Karam)
│   ├── LandCalculationResult.java   # Calculated area metrics & breakdown
│   ├── LandRecord.java              # Local storage entity
│   └── MarlaStandard.java           # 272.25 vs 225 vs 250 Sq Ft standards
└── ui/
    ├── CalculatorFragment.java      # Primary measurement & calculation tab
    ├── ConverterFragment.java       # Instant multi-unit converter tab
    ├── HistoryFragment.java         # Saved plot records & search tab
    ├── LandGuideDialog.java         # Pakistani land units reference guide
    ├── PlotPreviewView.java         # Dynamic Canvas plot renderer
    ├── RecordDetailsDialog.java     # Detailed record inspector & WhatsApp share
    ├── RecordsAdapter.java          # RecyclerView adapter for saved plots
    └── SaveRecordDialog.java        # Modal dialog for saving plot records
```

---

## 🚀 Getting Started & Building

### Prerequisites
- Android Studio Hedgehog (or newer)
- JDK 17+ (or Android Studio bundled JBR)
- Android SDK 34+

### Build from Command Line
```bash
# Clone the repository
git clone https://github.com/mraheem971/land-measuerment.git
cd land-measuerment

# Run unit tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📜 License
Open-source under the [MIT License](LICENSE).
