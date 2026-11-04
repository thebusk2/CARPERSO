package com.autolauncher.data

import androidx.compose.ui.graphics.Color

/**
 * Representa un modelo de vehículo disponible para la pantalla principal del launcher.
 */
data class VehicleModel(
    val id: String,
    val displayName: String,
    val brand: String,
    val category: VehicleCategory,
    val availableColors: List<VehicleColor>,
    /** Nombre del drawable SVG/vector base (sin color) */
    val drawableName: String
)

enum class VehicleCategory(val displayName: String) {
    SEDAN("Sedán"),
    SUV("SUV"),
    PICKUP("Pick-up"),
    HATCHBACK("Hatchback"),
    COUPE("Coupé"),
    VAN("Furgoneta")
}

data class VehicleColor(
    val nameEs: String,
    val color: Color,
    /** Sufijo para el nombre del drawable, ej. "_white" */
    val drawableSuffix: String
)

/** Paleta de colores estándar de vehículos */
object VehicleColors {
    val WHITE       = VehicleColor("Blanco Perla",    Color(0xFFF5F5F0), "_white")
    val BLACK       = VehicleColor("Negro Obsidiana", Color(0xFF1A1A1A), "_black")
    val SILVER      = VehicleColor("Plata Metálico",  Color(0xFFC0C0C0), "_silver")
    val DARK_GRAY   = VehicleColor("Gris Oscuro",     Color(0xFF4A4A4A), "_gray")
    val RED         = VehicleColor("Rojo Pasión",     Color(0xFFCC0000), "_red")
    val BLUE        = VehicleColor("Azul Marino",     Color(0xFF003399), "_blue")
    val LIGHT_BLUE  = VehicleColor("Azul Cielo",      Color(0xFF4488CC), "_lightblue")
    val GREEN       = VehicleColor("Verde Bosque",    Color(0xFF2D6E2D), "_green")
    val ORANGE      = VehicleColor("Naranja Solar",   Color(0xFFDD6600), "_orange")
    val BEIGE       = VehicleColor("Beige Arena",     Color(0xFFD4C5A9), "_beige")
    val BROWN       = VehicleColor("Marrón Tierra",   Color(0xFF7B4F2E), "_brown")
    val YELLOW      = VehicleColor("Amarillo Sport",  Color(0xFFFFCC00), "_yellow")
}

/** Catálogo completo de vehículos disponibles */
object VehicleCatalog {

    private fun common() = listOf(
        VehicleColors.WHITE, VehicleColors.BLACK, VehicleColors.SILVER,
        VehicleColors.DARK_GRAY, VehicleColors.RED, VehicleColors.BLUE
    )

    private fun full() = common() + listOf(
        VehicleColors.LIGHT_BLUE, VehicleColors.GREEN, VehicleColors.BEIGE, VehicleColors.BROWN
    )

    val ALL: List<VehicleModel> = listOf(

        // ═══════════ SEDANES ═══════════
        VehicleModel(
            id = "toyota_corolla",
            displayName = "Corolla",
            brand = "Toyota",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_corolla"
        ),
        VehicleModel(
            id = "honda_civic",
            displayName = "Civic",
            brand = "Honda",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_civic"
        ),
        VehicleModel(
            id = "vw_jetta",
            displayName = "Jetta",
            brand = "Volkswagen",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_jetta"
        ),
        VehicleModel(
            id = "nissan_sentra",
            displayName = "Sentra",
            brand = "Nissan",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_sentra"
        ),
        VehicleModel(
            id = "chevrolet_malibu",
            displayName = "Malibu",
            brand = "Chevrolet",
            category = VehicleCategory.SEDAN,
            availableColors = common(),
            drawableName = "vehicle_sedan_malibu"
        ),
        VehicleModel(
            id = "hyundai_elantra",
            displayName = "Elantra",
            brand = "Hyundai",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_elantra"
        ),
        VehicleModel(
            id = "kia_cerato",
            displayName = "Cerato",
            brand = "Kia",
            category = VehicleCategory.SEDAN,
            availableColors = full(),
            drawableName = "vehicle_sedan_cerato"
        ),
        VehicleModel(
            id = "mazda3",
            displayName = "Mazda3",
            brand = "Mazda",
            category = VehicleCategory.SEDAN,
            availableColors = full() + listOf(VehicleColors.ORANGE),
            drawableName = "vehicle_sedan_mazda3"
        ),

        // ═══════════ SUVs ═══════════
        VehicleModel(
            id = "toyota_rav4",
            displayName = "RAV4",
            brand = "Toyota",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_rav4"
        ),
        VehicleModel(
            id = "honda_crv",
            displayName = "CR-V",
            brand = "Honda",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_crv"
        ),
        VehicleModel(
            id = "vw_tiguan",
            displayName = "Tiguan",
            brand = "Volkswagen",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_tiguan"
        ),
        VehicleModel(
            id = "nissan_xtrail",
            displayName = "X-Trail",
            brand = "Nissan",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_xtrail"
        ),
        VehicleModel(
            id = "hyundai_tucson",
            displayName = "Tucson",
            brand = "Hyundai",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_tucson"
        ),
        VehicleModel(
            id = "kia_sportage",
            displayName = "Sportage",
            brand = "Kia",
            category = VehicleCategory.SUV,
            availableColors = full() + listOf(VehicleColors.GREEN),
            drawableName = "vehicle_suv_sportage"
        ),
        VehicleModel(
            id = "ford_escape",
            displayName = "Escape",
            brand = "Ford",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_escape"
        ),
        VehicleModel(
            id = "chevrolet_equinox",
            displayName = "Equinox",
            brand = "Chevrolet",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_equinox"
        ),
        VehicleModel(
            id = "jeep_compass",
            displayName = "Compass",
            brand = "Jeep",
            category = VehicleCategory.SUV,
            availableColors = full() + listOf(VehicleColors.GREEN),
            drawableName = "vehicle_suv_compass"
        ),
        VehicleModel(
            id = "subaru_forester",
            displayName = "Forester",
            brand = "Subaru",
            category = VehicleCategory.SUV,
            availableColors = full(),
            drawableName = "vehicle_suv_forester"
        ),

        // ═══════════ PICK-UPS ═══════════
        VehicleModel(
            id = "ford_f150",
            displayName = "F-150",
            brand = "Ford",
            category = VehicleCategory.PICKUP,
            availableColors = full() + listOf(VehicleColors.ORANGE, VehicleColors.YELLOW),
            drawableName = "vehicle_pickup_f150"
        ),
        VehicleModel(
            id = "chevrolet_silverado",
            displayName = "Silverado",
            brand = "Chevrolet",
            category = VehicleCategory.PICKUP,
            availableColors = full(),
            drawableName = "vehicle_pickup_silverado"
        ),
        VehicleModel(
            id = "toyota_hilux",
            displayName = "Hilux",
            brand = "Toyota",
            category = VehicleCategory.PICKUP,
            availableColors = full(),
            drawableName = "vehicle_pickup_hilux"
        ),
        VehicleModel(
            id = "nissan_frontier",
            displayName = "Frontier",
            brand = "Nissan",
            category = VehicleCategory.PICKUP,
            availableColors = common(),
            drawableName = "vehicle_pickup_frontier"
        ),
        VehicleModel(
            id = "ford_ranger",
            displayName = "Ranger",
            brand = "Ford",
            category = VehicleCategory.PICKUP,
            availableColors = full(),
            drawableName = "vehicle_pickup_ranger"
        ),
        VehicleModel(
            id = "vw_amarok",
            displayName = "Amarok",
            brand = "Volkswagen",
            category = VehicleCategory.PICKUP,
            availableColors = full(),
            drawableName = "vehicle_pickup_amarok"
        ),

        // ═══════════ HATCHBACKS ═══════════
        VehicleModel(
            id = "vw_golf",
            displayName = "Golf",
            brand = "Volkswagen",
            category = VehicleCategory.HATCHBACK,
            availableColors = full() + listOf(VehicleColors.YELLOW),
            drawableName = "vehicle_hatch_golf"
        ),
        VehicleModel(
            id = "ford_focus",
            displayName = "Focus",
            brand = "Ford",
            category = VehicleCategory.HATCHBACK,
            availableColors = full(),
            drawableName = "vehicle_hatch_focus"
        ),
        VehicleModel(
            id = "honda_fit",
            displayName = "Fit",
            brand = "Honda",
            category = VehicleCategory.HATCHBACK,
            availableColors = full(),
            drawableName = "vehicle_hatch_fit"
        ),
        VehicleModel(
            id = "renault_sandero",
            displayName = "Sandero",
            brand = "Renault",
            category = VehicleCategory.HATCHBACK,
            availableColors = full(),
            drawableName = "vehicle_hatch_sandero"
        ),

        // ═══════════ COUPÉS ═══════════
        VehicleModel(
            id = "ford_mustang",
            displayName = "Mustang",
            brand = "Ford",
            category = VehicleCategory.COUPE,
            availableColors = full() + listOf(VehicleColors.ORANGE, VehicleColors.YELLOW),
            drawableName = "vehicle_coupe_mustang"
        ),
        VehicleModel(
            id = "chevrolet_camaro",
            displayName = "Camaro",
            brand = "Chevrolet",
            category = VehicleCategory.COUPE,
            availableColors = full() + listOf(VehicleColors.YELLOW, VehicleColors.ORANGE),
            drawableName = "vehicle_coupe_camaro"
        ),
        VehicleModel(
            id = "honda_civic_coupe",
            displayName = "Civic Coupé",
            brand = "Honda",
            category = VehicleCategory.COUPE,
            availableColors = full(),
            drawableName = "vehicle_coupe_civic"
        ),
    )

    fun findById(id: String): VehicleModel =
        ALL.firstOrNull { it.id == id } ?: ALL.first()

    fun byCategory(category: VehicleCategory): List<VehicleModel> =
        ALL.filter { it.category == category }

    val brands: List<String> = ALL.map { it.brand }.distinct().sorted()
}
