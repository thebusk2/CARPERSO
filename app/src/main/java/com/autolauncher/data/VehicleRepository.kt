package com.autolauncher.data

class VehicleRepository {
    fun getAll(): List<VehicleModel> = VehicleCatalog.ALL
    fun findById(id: String): VehicleModel = VehicleCatalog.findById(id)
    fun getCategories(): List<VehicleCategory> = VehicleCategory.values().toList()
    fun getByCategory(category: VehicleCategory): List<VehicleModel> =
        VehicleCatalog.byCategory(category)
    fun getBrands(): List<String> = VehicleCatalog.brands
}
