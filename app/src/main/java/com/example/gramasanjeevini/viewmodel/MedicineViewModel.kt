package com.example.gramasanjeevini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gramasanjeevini.data.MockData
import com.example.gramasanjeevini.model.Medicine
import com.example.gramasanjeevini.model.Shop
import com.example.gramasanjeevini.model.UserProfile
import com.example.gramasanjeevini.repository.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Pair<Medicine, Shop>>>(emptyList())
    val searchResults: StateFlow<List<Pair<Medicine, Shop>>> = _searchResults

    val emergencyMedicines: StateFlow<List<Pair<Medicine, Shop>>> = repository.getEmergencyMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        seedDatabaseIfEmpty()
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            // Check if we already have medicines
            val currentMedicines = repository.searchMedicines("").first()
            if (currentMedicines.isEmpty()) {
                repository.preseedDatabase(MockData.shops, MockData.medicines)
            }
        }
    }

    fun search(query: String, maxDistance: Double = 20.0) {
        viewModelScope.launch {
            repository.searchMedicines(query, maxDistance).collect {
                _searchResults.value = it
            }
        }
    }

    fun getMedicinesByShop(shopId: String): Flow<List<Medicine>> = 
        repository.getPharmacistMedicines(shopId)

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.addMedicine(medicine)
        }
    }

    fun saveUser(name: String, role: String) {
        viewModelScope.launch {
            repository.saveUserProfile(name, role)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
    }

    companion object {
        fun provideFactory(repository: MedicineRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MedicineViewModel(repository)
            }
        }
    }
}
