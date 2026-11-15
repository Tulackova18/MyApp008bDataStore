package com.example.myapp008bdatastore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp008bdatastore.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Instance DataStore
val Context.dataStore by preferencesDataStore(
    name = "gamer_profile"
)

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Klíče DataStore
    private val NICKNAME_KEY = stringPreferencesKey("NICKNAME")
    private val HARDCORE_KEY = booleanPreferencesKey("HARDCORE")

    // Počítadlo kliknutí nezávislé na DataStore
    private var clickCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 1. NAČTENÍ ULOŽENÝCH DAT
        viewLifecycleOwner.lifecycleScope.launch {

            val preferences =
                requireContext().dataStore.data.first()

            binding.etNickname.setText(
                preferences[NICKNAME_KEY] ?: ""
            )

            binding.switchHardcoreMode.isChecked =
                preferences[HARDCORE_KEY] ?: false
        }

        // 2. UKLÁDÁNÍ DAT
        binding.btnSave.setOnClickListener {

            val currentName =
                binding.etNickname.text.toString()

            val currentHardcore =
                binding.switchHardcoreMode.isChecked

            viewLifecycleOwner.lifecycleScope.launch {

                // Zobrazíme načítací kolečko
                binding.progressBar.visibility = View.VISIBLE

                // Zablokujeme pouze tlačítko Uložit
                binding.btnSave.isEnabled = false

                try {
                    // Simulace ukládání po dobu 3 sekund
                    // Hlavní UI vlákno zůstává volné
                    delay(3000)

                    // Skutečné uložení do DataStore
                    requireContext().dataStore.edit { prefs ->
                        prefs[NICKNAME_KEY] = currentName
                        prefs[HARDCORE_KEY] = currentHardcore
                    }

                    Snackbar.make(
                        binding.root,
                        "Uloženo moderní cestou",
                        Snackbar.LENGTH_LONG
                    ).setAction("Zpět") {
                        Toast.makeText(
                            requireContext(),
                            "Akce vrácena zpět!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.show()

                } finally {
                    // Skryjeme načítání a povolíme další uložení
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnSave.isEnabled = true
                }
            }
        }

        // 3. MINIHR A - POČÍTÁNÍ KLIKNUTÍ
        binding.tvClickCount.text = clickCount.toString()

        binding.btnClick.setOnClickListener {

            // Zvýšíme počítadlo o 1
            clickCount++

            // Okamžitě aktualizujeme zobrazené číslo
            binding.tvClickCount.text = clickCount.toString()
        }

        // 4. RESET POČÍTADLA
        binding.btnReset.setOnClickListener {

            clickCount = 0

            binding.tvClickCount.text = clickCount.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
