// Импортируем необходимые библиотеки
package ru.zarichan.zar_tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.zarichan.zar_tictactoe.GameActivity
import ru.zarichan.zar_tictactoe.databinding.ActivityMainBinding

// Объявляем константы для передачи данных между активностями
const val EXTRA_TIME = "ru.zarichan.zar_tictactoe.TIME"
const val EXTRA_GAME_FIELD = "ru.zarichan.zar_tictactoe.GAME_FIELD"

// Создаем класс MainActivity, который наследуется от AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Объявляем переменную для привязки данных
    private lateinit var binding: ActivityMainBinding

    // Метод onCreate вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем тему для активности
        setTheme(R.style.Theme_Zar_TicTacToe)

        super.onCreate(savedInstanceState)

        // Инициализируем привязку данных
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Устанавливаем слушатель для кнопки "toNewGame"
        binding.mainNew.setOnClickListener {
            // Создаем намерение для перехода к GameActivity
            val intent = Intent(this, GameActivity::class.java)
            // Запускаем GameActivity
            startActivity(intent)
        }

        // Устанавливаем слушатель для кнопки "toContinueGame"
        binding.mainContinue.setOnClickListener {
            // Получаем информацию о последней игре
            val gameInfo = getInfoAboutLastGame()
            // Создаем намерение для перехода к GameActivity и добавляем дополнительные данные
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(EXTRA_TIME, gameInfo.time)
                putExtra(EXTRA_GAME_FIELD, gameInfo.gameField)
            }
            // Запускаем GameActivity
            startActivity(intent)
        }

        // Устанавливаем слушатель для кнопки "toSettings"
        binding.mainSettings.setOnClickListener {
            // Создаем намерение для перехода к SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            // Запускаем SettingsActivity
            startActivity(intent)
        }

        // Устанавливаем корневой вид для этой активности
        setContentView(binding.root)
    }

    // Вспомогательная функция для получения информации о последней игре
    private fun getInfoAboutLastGame(): GameInfo {
        // Получаем настройки игры
        with(getSharedPreferences("game", MODE_PRIVATE)) {
            // Получаем время и игровое поле из настроек
            val time = getLong("time", 0)
            val gameField = getString("gameField", "")

            // Возвращаем информацию о игре
            return if (gameField != null) {
                GameInfo(time, gameField)
            } else {
                GameInfo(0, "")
            }
        }
    }

    // Класс данных для хранения информации об игре
    data class GameInfo(val time: Long, val gameField: String)
}