// Импортируем необходимые библиотеки
package ru.zarichan.zar_tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import ru.zarichan.zar_tictactoe.databinding.ActivitySettingsBinding

// Объявляем константы для настроек
const val PREF_SOUND = "ru.zarichan.zar_tictactoe.SOUND"
const val PREF_LEVEL = "ru.zarichan.zar_tictactoe.LEVEL"
const val PREF_RULES = "ru.zarichan.zar_tictactoe.RULES"

// Создаем класс SettingsActivity, который наследуется от AppCompatActivity
class SettingsActivity : AppCompatActivity() {

    // Объявляем переменные для хранения текущих настроек
    private lateinit var settingsBinding: ActivitySettingsBinding
    private var currentLevel: Int = 0
    private var currentVolumeSound: Int = 0
    private var currentRules: Int = 0

    // Метод onCreate вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем settingsBinding
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)

        // Получаем текущие настройки
        val currentSettings = getCurrentSettings()

        // Устанавливаем текущие значения уровня, звука и правил из полученных настроек
        currentLevel = currentSettings.level
        currentVolumeSound = currentSettings.sound
        currentRules = currentSettings.rules

        // Если текущий уровень равен 0, делаем кнопку "prevLvl" невидимой
        if (currentLevel == 0) {
            settingsBinding.prevLvl.visibility = View.INVISIBLE
        } else if (currentLevel == 2) {
            // Если текущий уровень равен 2, делаем кнопку "nextLvl" невидимой
            settingsBinding.nextLvl.visibility = View.INVISIBLE
        }

        // Устанавливаем текст для infoLevel, используя текущий уровень
        settingsBinding.settingsDifficulty.text =
            resources.getStringArray(R.array.level)[currentLevel]
        // Устанавливаем прогресс для soundBar, используя текущий уровень звука
        settingsBinding.settingsSoundBar.progress = currentVolumeSound

        // В зависимости от текущих правил, устанавливаем соответствующие флажки
        when (currentSettings.rules) {
            1 -> settingsBinding.checkBoxHorizontal.isChecked = true
            2 -> settingsBinding.checkBoxVertical.isChecked = true
            3 -> {
                settingsBinding.checkBoxHorizontal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
            }

            4 -> settingsBinding.checkBoxDiagonal.isChecked = true
            5 -> {
                settingsBinding.checkBoxDiagonal.isChecked = true
                settingsBinding.checkBoxHorizontal.isChecked = true
            }

            6 -> {
                settingsBinding.checkBoxDiagonal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
            }

            7 -> {
                settingsBinding.checkBoxHorizontal.isChecked = true
                settingsBinding.checkBoxVertical.isChecked = true
                settingsBinding.checkBoxDiagonal.isChecked = true
            }
        }


        settingsBinding.settingsBtnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


        // Устанавливаем слушателей для кнопок "prevLvl" и "nextLvl"
        settingsBinding.prevLvl.setOnClickListener {
            // Когда пользователь нажимает на "prevLvl", уменьшаем текущий уровень на 1
            currentLevel--

            // Если текущий уровень становится равным 0, делаем кнопку "prevLvl" невидимой
            if (currentLevel == 0) {
                settingsBinding.prevLvl.visibility = View.INVISIBLE
            } else if (currentLevel == 1) {
                // Если текущий уровень становится равным 1, делаем кнопку "nextLvl" видимой
                settingsBinding.nextLvl.visibility = View.VISIBLE
            }

            // Обновляем уровень в настройках и устанавливаем новый текст для infoLevel
            updateLevel(currentLevel)
            settingsBinding.settingsDifficulty.text =
                resources.getStringArray(R.array.level)[currentLevel]
        }

        settingsBinding.nextLvl.setOnClickListener {
            // Когда пользователь нажимает на "nextLvl", увеличиваем текущий уровень на 1
            currentLevel++

            // Если текущий уровень становится равным 2, делаем кнопку "nextLvl" невидимой
            if (currentLevel == 2) {
                settingsBinding.nextLvl.visibility = View.INVISIBLE
            } else if (currentLevel == 1) {
                // Если текущий уровень становится равным 1, делаем кнопку "prevLvl" видимой
                settingsBinding.prevLvl.visibility = View.VISIBLE
            }

            // Обновляем уровень в настройках и устанавливаем новый текст для infoLevel
            updateLevel(currentLevel)
            settingsBinding.settingsDifficulty.text =
                resources.getStringArray(R.array.level)[currentLevel]
        }

        // Устанавливаем слушатели для soundBar и checkBox'ов
        settingsBinding.settingsSoundBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                // Когда пользователь изменяет положение ползунка,
                // обновляем текущий уровень громкости звука
                currentVolumeSound = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // Когда пользователь отпускает ползунок, сохраняем текущий уровень громкости звука
                updateVolumeSound(currentVolumeSound)
            }
        })

        settingsBinding.checkBoxHorizontal.setOnCheckedChangeListener { _, isChecked ->
            // Когда пользователь изменяет состояние checkBoxHorizontal, обновляем текущие правила
            if (isChecked) {
                currentRules += 1
            } else {
                currentRules -= 1
            }
            updateRules(currentRules)
        }

        settingsBinding.checkBoxVertical.setOnCheckedChangeListener { _, isChecked ->
            // Когда пользователь изменяет состояние checkBoxVertical, обновляем текущие правила
            if (isChecked) {
                currentRules += 2
            } else {
                currentRules -= 2
            }
            updateRules(currentRules)
        }

        settingsBinding.checkBoxDiagonal.setOnCheckedChangeListener { _, isChecked ->
            // Когда пользователь изменяет состояние checkBoxDiagonal, обновляем текущие правила
            if (isChecked) {
                currentRules += 4
            } else {
                currentRules -= 4
            }
            updateRules(currentRules)
        }

        // Устанавливаем слушатель для кнопки "Назад"
        settingsBinding.settingsSoundBar.setOnClickListener {
            // Когда пользователь нажимает кнопку "Назад", возвращаем RESULT_OK
            // и вызываем метод onBackPressed
            setResult(RESULT_OK)
            onBackPressed()
        }

        // Устанавливаем корневой вид для этой активности
        setContentView(settingsBinding.root)
    }

    // Вспомогательные функции для обновления настроек звука, уровня и правил
    private fun updateVolumeSound(volume: Int) {
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putInt(PREF_SOUND, volume)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateLevel(level: Int) {
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putInt(PREF_LEVEL, level)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateRules(rules: Int) {
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putInt(PREF_RULES, rules)
            apply()
        }
        setResult(RESULT_OK)
    }

    // Функция для получения текущих настроек
    private fun getCurrentSettings(): SettingsInfo {
        this.getSharedPreferences("game", MODE_PRIVATE).apply {

            val sound = getInt(PREF_SOUND, 100)
            val level = getInt(PREF_LEVEL, 1)
            val rules = getInt(PREF_RULES, 7)

            return SettingsInfo(sound, level, rules)
        }
    }

    // Класс данных для хранения информации о настройках
    data class SettingsInfo(val sound: Int, val level: Int, val rules: Int)
}