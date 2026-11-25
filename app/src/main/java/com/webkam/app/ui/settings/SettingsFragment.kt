package com.webkam.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.webkam.app.data.model.FontSize
import com.webkam.app.data.model.ThemeMode
import com.webkam.app.databinding.FragmentSettingsBinding
import com.webkam.app.ui.language.LanguageActivity
import com.webkam.app.ui.main.MainActivity
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var lang = "ar"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lang = PreferenceManager.getLanguage(requireContext())

        setupUI()
        setupClickListeners()
        loadCurrentSettings()
    }

    private fun setupUI() {
        binding.tvSettingsTitle.text =
            if (lang == "ar") "الإعدادات" else "Settings"
        binding.tvLanguageLabel.text =
            if (lang == "ar") "اللغة" else "Language"
        binding.tvThemeLabel.text =
            if (lang == "ar") "وضع الشاشة" else "Display Mode"
        binding.tvFontLabel.text =
            if (lang == "ar") "حجم الخط" else "Font Size"
        binding.tvHelpLabel.text =
            if (lang == "ar") "المساعدة" else "Help"

        // Theme options
        binding.btnLight.text = if (lang == "ar") "مشمس" else "Light"
        binding.btnDark.text = if (lang == "ar") "مظلم" else "Dark"

        // Font options
        binding.btnFontSmall.text = if (lang == "ar") "صغير" else "Small"
        binding.btnFontMedium.text = if (lang == "ar") "متوسط" else "Medium"
        binding.btnFontLarge.text = if (lang == "ar") "كبير" else "Large"

        // Language buttons
        binding.btnArabic.text = "عربي"
        binding.btnEnglish.text = "English"

        // Help button
        binding.btnHelp.text = if (lang == "ar") "تواصل مع المبرمج" else "Contact Developer"

        // Footer
        binding.tvSettingsFooter.text =
            if (lang == "ar") "المبرمج احمد امليمدي" else "Developer: Ahmed Amleimdi"
    }

    private fun setupClickListeners() {
        // Language change
        binding.btnArabic.setOnClickListener { changeLanguage("ar") }
        binding.btnEnglish.setOnClickListener { changeLanguage("en") }

        // Theme change
        binding.btnLight.setOnClickListener { changeTheme(ThemeMode.LIGHT) }
        binding.btnDark.setOnClickListener { changeTheme(ThemeMode.DARK) }

        // Font size
        binding.btnFontSmall.setOnClickListener { changeFontSize(FontSize.SMALL) }
        binding.btnFontMedium.setOnClickListener { changeFontSize(FontSize.MEDIUM) }
        binding.btnFontLarge.setOnClickListener { changeFontSize(FontSize.LARGE) }

        // Help = WhatsApp
        binding.btnHelp.setOnClickListener { openWhatsApp() }
    }

    private fun loadCurrentSettings() {
        val theme = PreferenceManager.getTheme(requireContext())
        val fontSize = PreferenceManager.getFontSize(requireContext())
        val currentLang = PreferenceManager.getLanguage(requireContext())

        highlightTheme(theme)
        highlightFont(fontSize)
        highlightLanguage(currentLang)
    }

    private fun changeLanguage(newLang: String) {
        PreferenceManager.setLanguage(requireContext(), newLang)
        LocaleHelper.setLocale(requireContext(), newLang)
        (activity as? MainActivity)?.refreshActivity()
    }

    private fun changeTheme(theme: ThemeMode) {
        PreferenceManager.setTheme(requireContext(), theme)
        highlightTheme(theme)
        (activity as? MainActivity)?.refreshActivity()
    }

    private fun changeFontSize(size: FontSize) {
        PreferenceManager.setFontSize(requireContext(), size)
        highlightFont(size)
    }

    private fun highlightTheme(theme: ThemeMode) {
        val ctx = requireContext()
        val activeColor = ctx.getColor(com.webkam.app.R.color.color_primary)
        val inactiveColor = ctx.getColor(com.webkam.app.R.color.color_surface)
        binding.btnLight.setBackgroundColor(if (theme == ThemeMode.LIGHT) activeColor else inactiveColor)
        binding.btnDark.setBackgroundColor(if (theme == ThemeMode.DARK) activeColor else inactiveColor)
    }

    private fun highlightFont(size: FontSize) {
        val ctx = requireContext()
        val activeColor = ctx.getColor(com.webkam.app.R.color.color_primary)
        val inactiveColor = ctx.getColor(com.webkam.app.R.color.color_surface)
        binding.btnFontSmall.setBackgroundColor(if (size == FontSize.SMALL) activeColor else inactiveColor)
        binding.btnFontMedium.setBackgroundColor(if (size == FontSize.MEDIUM) activeColor else inactiveColor)
        binding.btnFontLarge.setBackgroundColor(if (size == FontSize.LARGE) activeColor else inactiveColor)
    }

    private fun highlightLanguage(lang: String) {
        val ctx = requireContext()
        val activeColor = ctx.getColor(com.webkam.app.R.color.color_primary)
        val inactiveColor = ctx.getColor(com.webkam.app.R.color.color_surface)
        binding.btnArabic.setBackgroundColor(if (lang == "ar") activeColor else inactiveColor)
        binding.btnEnglish.setBackgroundColor(if (lang == "en") activeColor else inactiveColor)
    }

    private fun openWhatsApp() {
        try {
            val phone = "218934274024"
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://wa.me/$phone"))
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback: open number in dialer
            val intent = Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:+218934274024"))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
