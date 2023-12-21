// Импортируем необходимые библиотеки
package ru.zarichan.zar_tictactoe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.zarichan.zar_tictactoe.databinding.ActivityGameBinding

// Создаем класс GameActivity, который наследуется от AppCompatActivity
class GameActivity : AppCompatActivity() {
    // Объявляем переменные для привязки данных, игрового поля и медиаплеера
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameField: Array<Array<String>>
    private lateinit var mediaPlayer: MediaPlayer

    // Метод onCreate вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем привязку данных
        binding = ActivityGameBinding.inflate(layoutInflater)

        // Устанавливаем слушатели для различных элементов интерфейса
        binding.gameBtnExit.setOnClickListener {
            onBackPressed()
        }

        binding.gameBtnMenu.setOnClickListener {
            showPopupMenu()
        }

        // Устанавливаем слушатели для каждой ячейки игрового поля
        binding.cell11.setOnClickListener {
            makeStepToUser(0, 0)
        }

        binding.cell12.setOnClickListener {
            makeStepToUser(0, 1)
        }

        binding.cell13.setOnClickListener {
            makeStepToUser(0, 2)
        }

        binding.cell21.setOnClickListener {
            makeStepToUser(1, 0)
        }

        binding.cell22.setOnClickListener {
            makeStepToUser(1, 1)
        }

        binding.cell23.setOnClickListener {
            makeStepToUser(1, 2)
        }

        binding.cell31.setOnClickListener {
            makeStepToUser(2, 0)
        }

        binding.cell32.setOnClickListener {
            makeStepToUser(2, 1)
        }

        binding.cell33.setOnClickListener {
            makeStepToUser(2, 2)
        }

        // Устанавливаем корневой вид для этой активности
        setContentView(binding.root)

        // Получаем время и игровое поле из намерения
        val time = intent.getLongExtra(EXTRA_TIME, 0L)
        val gameField = intent.getStringExtra(EXTRA_GAME_FIELD)

        // Если игровое поле и время не равны null, то перезапускаем игру,
        // иначе инициализируем игровое поле
        if (gameField != null && time != 0L && gameField != "") {
            restartGame(time, gameField)
        } else {
            initGameField()
        }

        // Создаем медиаплеер и устанавливаем его в циклическом режиме
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.isLooping = true
        val settingsInfo = getCurrentSettings()
        setVolumeMediaPlayer(settingsInfo.sound)

        // Запускаем хронометр и медиаплеер
        binding.gameTimer.start()
        mediaPlayer.start()
    }

    // Метод onDestroy вызывается при уничтожении активности
    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем ресурсы медиаплеера
        mediaPlayer.release()
    }

    // Метод onStop вызывается, когда активность больше не видима для пользователя
    override fun onStop() {
        super.onStop()
        // Освобождаем ресурсы медиаплеера
        mediaPlayer.release()
    }

    // Метод onActivityResult вызывается после завершения работы другой активности
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == POPUP_MENU) {
            if (resultCode == RESULT_OK) {
                // Создаем новый медиаплеер и устанавливаем его в циклическом режиме
                mediaPlayer = MediaPlayer.create(this, R.raw.music)
                mediaPlayer.isLooping = true
                val settingsInfo = getCurrentSettings()
                setVolumeMediaPlayer(settingsInfo.sound)

                // Запускаем хронометр и медиаплеер
                binding.gameTimer.start()
                mediaPlayer.start()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Вспомогательная функция для установки громкости медиаплеера
    private fun setVolumeMediaPlayer(soundValue: Int) {
        val volume = soundValue / 100.0
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
    }

    // Вспомогательная функция для перезапуска игры
    private fun restartGame(time: Long, gameField: String) {
        // Устанавливаем базовое время для хронометра
        binding.gameTimer.base = SystemClock.elapsedRealtime() - time

        // Инициализируем игровое поле
        this.gameField = arrayOf()

        // Разбиваем строку игрового поля на строки
        val rows = gameField.split("\n")

        // Для каждой строки разбиваем ее на столбцы и добавляем в игровое поле
        for (row in rows) {
            val columns = row.split(";")
            this.gameField += columns.toTypedArray()
        }

        // Обновляем пользовательский интерфейс игрового поля
        this.gameField.forEachIndexed { indexRow, columns ->
            columns.forEachIndexed { indexColumn, cell ->
                makeGameFieldUI("$indexRow$indexColumn", cell)
            }
        }
    }

    // Функция для преобразования игрового поля в строку
    private fun convertGameFieldToString(): String {
        // Создаем временный массив
        val tmpArray = arrayListOf<String>()
        // Для каждой строки игрового поля преобразуем ее в строку и добавляем во временный массив
        gameField.forEach { tmpArray.add(it.joinToString(separator = ";")) }
        // Преобразуем временный массив в строку, где каждая строка игрового поля на новой строке
        return tmpArray.joinToString(separator = "\n")
    }

    // Функция для сохранения игры
    private fun saveGame(time: Long, gameField: String) {
        // Получаем настройки игры и сохраняем время и игровое поле
        getSharedPreferences("game", MODE_PRIVATE).edit().apply {
            putLong("time", time)
            putString("gameField", gameField)
            apply()
        }
    }

    // Функция для инициализации игрового поля
    private fun initGameField() {
        // Создаем пустое игровое поле
        gameField = arrayOf()

        // Для каждой ячейки игрового поля устанавливаем значение " "
        for (i in 0..2) {
            var array = arrayOf<String>()
            for (j in 0..2) {
                array += " "
            }
            gameField += array
        }
    }

    // Функция для выполнения хода пользователя
    private fun makeStepToUser(row: Int, column: Int) {
        // Проверяем, является ли ячейка пустой
        if (isEmptyField(row, column)) {
            // Выполняем ход и проверяем состояние игры
            makeStep(row, column, PLAYER_SYMBOL)

            if (checkGameField(row, column, PLAYER_SYMBOL)) {
                showGameStatus(STATUS_WIN_PLAYER)
            } else if (!isFilledGameField()) {
                val stepOfAI = makeStepToAI()

                if (checkGameField(stepOfAI.row, stepOfAI.column, BOT_SYMBOL)) {
                    showGameStatus(STATUS_WIN_BOT)
                } else if (isFilledGameField()) {
                    showGameStatus(STATUS_DRAW)
                }
            } else {
                showGameStatus(STATUS_DRAW)
            }
        } else {
            // Если ячейка уже заполнена, показываем сообщение пользователю
            Toast.makeText(this, "Поле уже заполнено", Toast.LENGTH_SHORT).show()
        }
    }

    // Функция для выполнения хода ИИ
    private fun makeStepToAI(): CellGameField {
        // Получаем текущие настройки и выполняем ход в зависимости от уровня сложности
        val settingsInfo = getCurrentSettings()
        return when (settingsInfo.level) {
            0 -> makeStepOfAIEasyLvl()
            1 -> makeStepOfAIMediumLvl()
            2 -> makeStepOfAIHardLvl()
            else -> CellGameField(0, 0)
        }
    }

    // Функция для выполнения хода ИИ на легком уровне сложности
    private fun makeStepOfAIEasyLvl(): CellGameField {
        // Выбираем случайную пустую ячейку и выполняем ход
        var randomRow = 0
        var randomColumn = 0

        do {
            randomRow = (0..2).random()
            randomColumn = (0..2).random()
        } while (!isEmptyField(randomRow, randomColumn))

        makeStep(randomRow, randomColumn, BOT_SYMBOL)

        return CellGameField(randomRow, randomColumn)
    }

    // Функция для выполнения хода ИИ на среднем уровне сложности
    private fun makeStepOfAIMediumLvl(): CellGameField {
        // Выбираем лучший ход, используя алгоритм минимакс, и выполняем ход
        var bestScore = Double.NEGATIVE_INFINITY
        var moveCell = CellGameField(0, 0)

        var board = gameField.map { it.clone() }.toTypedArray()

        board.forEachIndexed { indexRow, columns ->
            columns.forEachIndexed { indexColumn, cell ->
                if (board[indexRow][indexColumn] == " ") {
                    board[indexRow][indexColumn] = BOT_SYMBOL
                    val score = minimax(board, false)
                    board[indexRow][indexColumn] = " "
                    if (score > bestScore) {
                        bestScore = score
                        moveCell = CellGameField(indexRow, indexColumn)
                    }
                }
            }
        }

        makeStep(moveCell.row, moveCell.column, BOT_SYMBOL)

        return moveCell
    }

    // Функция для выполнения хода ИИ на сложном уровне сложности
    private fun makeStepOfAIHardLvl(): CellGameField {
        // Выбираем лучший ход, используя алгоритм минимакс, и выполняем ход
        var bestScore = Double.NEGATIVE_INFINITY
        var moveCell = CellGameField(0, 0)

        var board = gameField.map { it.clone() }.toTypedArray()

        board.forEachIndexed { indexRow, columns ->
            columns.forEachIndexed { indexColumn, cell ->
                if (board[indexRow][indexColumn] == " ") {
                    board[indexRow][indexColumn] = BOT_SYMBOL
                    val score = minimax(board, false)
                    board[indexRow][indexColumn] = " "
                    if (score > bestScore) {
                        bestScore = score
                        moveCell = CellGameField(indexRow, indexColumn)
                    }
                }
            }
        }

        makeStep(moveCell.row, moveCell.column, BOT_SYMBOL)

        return moveCell
    }

    // Функция для реализации алгоритма минимакс
    private fun minimax(board: Array<Array<String>>, isMaximizing: Boolean): Double {
        // Проверяем, есть ли победитель на доске
        val result = checkWinner(board)
        result?.let {
            // Если есть победитель, возвращаем соответствующий счет
            return scores[result]!!
        }

        // Если максимизируем счет
        if (isMaximizing) {
            var bestScore = Double.NEGATIVE_INFINITY
            // Перебираем все ячейки на доске
            board.forEachIndexed { indexRow, columns ->
                columns.forEachIndexed { indexColumn, cell ->
                    // Если ячейка пуста
                    if (board[indexRow][indexColumn] == " ") {
                        // Делаем ход за ИИ
                        board[indexRow][indexColumn] = BOT_SYMBOL
                        // Вызываем функцию минимакс для следующего хода
                        val score = minimax(board, false)
                        // Возвращаем ячейку в исходное состояние
                        board[indexRow][indexColumn] = " "
                        // Если счет больше лучшего счета, обновляем лучший счет
                        if (score > bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            // Возвращаем лучший счет
            return bestScore
        } else {
            // Если минимизируем счет
            var bestScore = Double.POSITIVE_INFINITY
            // Перебираем все ячейки на доске
            board.forEachIndexed { indexRow, columns ->
                columns.forEachIndexed { indexColumn, cell ->
                    // Если ячейка пуста
                    if (board[indexRow][indexColumn] == " ") {
                        // Делаем ход за игрока
                        board[indexRow][indexColumn] = PLAYER_SYMBOL
                        // Вызываем функцию минимакс для следующего хода
                        val score = minimax(board, true)
                        // Возвращаем ячейку в исходное состояние
                        board[indexRow][indexColumn] = " "
                        // Если счет меньше лучшего счета, обновляем лучший счет
                        if (score < bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            // Возвращаем лучший счет
            return bestScore
        }
    }

    // Функция для проверки победителя
    private fun checkWinner(board: Array<Array<String>>): Int? {
        var countRowsUser = 0
        var countRowsAI = 0
        var countLeftDiagonalUser = 0
        var countLeftDiagonalAL = 0
        var countRightDiagonalUser = 0
        var countRightDiagonalAI = 0

        // Перебираем все строки на доске
        board.forEachIndexed { indexRow, columns ->
            // Если все ячейки в строке принадлежат игроку, игрок выигрывает
            if (columns.all { it == PLAYER_SYMBOL })
                return STATUS_WIN_PLAYER
            // Если все ячейки в строке принадлежат ИИ, ИИ выигрывает
            else if (columns.all { it == BOT_SYMBOL })
                return STATUS_WIN_BOT

            countRowsUser = 0
            countRowsAI = 0

            // Перебираем все ячейки в строке
            columns.forEachIndexed { indexColumn, cell ->
                // Если ячейка принадлежит игроку, увеличиваем счетчик игрока
                if (board[indexColumn][indexRow] == PLAYER_SYMBOL)
                    countRowsUser++
                // Если ячейка принадлежит ИИ, увеличиваем счетчик ИИ
                else if (board[indexColumn][indexRow] == BOT_SYMBOL)
                    countRowsAI++

                // Если ячейка находится на главной диагонали и принадлежит игроку,
                // увеличиваем счетчик игрока
                if (indexRow == indexColumn && board[indexRow][indexColumn] == PLAYER_SYMBOL)
                    countLeftDiagonalUser++
                // Если ячейка находится на главной диагонали и принадлежит ИИ,
                // увеличиваем счетчик ИИ
                else if (indexRow == indexColumn && board[indexRow][indexColumn] == BOT_SYMBOL)
                    countLeftDiagonalAL++

                // Если ячейка находится на побочной диагонали и принадлежит игроку,
                // увеличиваем счетчик игрока
                if (indexRow == 2 - indexColumn && board[indexRow][indexColumn] == PLAYER_SYMBOL)
                    countRightDiagonalUser++
                // Если ячейка находится на побочной диагонали и принадлежит ИИ,
                // увеличиваем счетчик ИИ
                else if (indexRow == 2 - indexColumn && board[indexRow][indexColumn] == BOT_SYMBOL)
                    countRightDiagonalAI++
            }

            // Если игрок выиграл в любой из строк, столбцов или диагоналей, игрок выигрывает
            if (countRowsUser == 3 || countLeftDiagonalUser == 3 || countRightDiagonalUser == 3)
                return STATUS_WIN_PLAYER
            // Если ИИ выиграл в любой из строк, столбцов или диагоналей, ИИ выигрывает
            else if (countRowsAI == 3 || countLeftDiagonalAL == 3 || countRightDiagonalAI == 3)
                return STATUS_WIN_BOT
        }

        // Если все ячейки заполнены и нет победителя, объявляется ничья
        board.forEach {
            if (it.find { it == " " } != null)
                return null
        }

        return STATUS_DRAW
    }

    // Функция для получения текущих настроек
    private fun getCurrentSettings(): SettingsActivity.SettingsInfo {
        // Получаем настройки игры
        this.getSharedPreferences("game", MODE_PRIVATE).apply {

            val sound = getInt(PREF_SOUND, 100)
            val level = getInt(PREF_LEVEL, 1)
            val rules = getInt(PREF_RULES, 7)

            // Возвращаем информацию о настройках
            return SettingsActivity.SettingsInfo(sound, level, rules)
        }
    }

    // Класс данных для хранения информации о ячейке игрового поля
    data class CellGameField(val row: Int, val column: Int)

    // Функция для проверки, является ли ячейка пустой
    private fun isEmptyField(row: Int, column: Int): Boolean {
        return gameField[row][column] == " "
    }

    // Функция для выполнения хода
    private fun makeStep(row: Int, column: Int, symbol: String) {
        // Заполняем ячейку символом игрока или ИИ
        gameField[row][column] = symbol

        // Обновляем пользовательский интерфейс игрового поля
        makeGameFieldUI("$row$column", symbol)
    }

    // Функция для обновления пользовательского интерфейса игрового поля
    private fun makeGameFieldUI(position: String, symbol: String) {
        // Выбираем изображение в зависимости от символа
        val drawable = when (symbol) {
            PLAYER_SYMBOL -> R.drawable.ic_figure_p1
            BOT_SYMBOL -> R.drawable.ic_figure_p2
            else -> return
        }

        // Обновляем изображение в соответствующей ячейке
        when (position) {
            "00" -> binding.cell11.setImageResource(drawable)
            "01" -> binding.cell12.setImageResource(drawable)
            "02" -> binding.cell13.setImageResource(drawable)
            "10" -> binding.cell21.setImageResource(drawable)
            "11" -> binding.cell22.setImageResource(drawable)
            "12" -> binding.cell23.setImageResource(drawable)
            "20" -> binding.cell31.setImageResource(drawable)
            "21" -> binding.cell32.setImageResource(drawable)
            "22" -> binding.cell33.setImageResource(drawable)
        }
    }

    // Функция для проверки состояния игрового поля
    private fun checkGameField(x: Int, y: Int, symbol: String): Boolean {
        // Инициализируем счетчики для строк, столбцов и диагоналей
        var col = 0
        var row = 0
        var diag = 0
        var rdiag = 0
        val n = gameField.size

        // Проверяем все ячейки на доске
        for (i in 0..2) {
            if (gameField[x][i] == symbol)
                col++
            if (gameField[i][y] == symbol)
                row++
            if (gameField[i][i] == symbol)
                diag++
            if (gameField[i][n - i - 1] == symbol)
                rdiag++
        }

        // Получаем текущие настройки и проверяем, есть ли победитель
        val settings = getCurrentSettings()
        return when (settings.rules) {
            1 -> {
                col == n
            }

            2 -> {
                row == n
            }

            3 -> {
                col == n || row == n
            }

            4 -> {
                diag == n || rdiag == n
            }

            5 -> {
                col == n || diag == n || rdiag == n
            }

            6 -> {
                row == n || diag == n || rdiag == n
            }

            7 -> {
                col == n || row == n || diag == n || rdiag == n
            }

            else -> {
                false
            }
        }
    }

    // Функция для проверки, заполнено ли игровое поле
    private fun isFilledGameField(): Boolean {
        // Если есть хотя бы одна пустая ячейка, возвращаем false
        gameField.forEach { strings ->
            if (strings.find { it == " " } != null)
                return false
        }
        // Иначе возвращаем true
        return true
    }

    // Функция для отображения статуса игры
    private fun showGameStatus(status: Int) {
        // Останавливаем хронометр
        binding.gameTimer.stop()

        // Создаем диалоговое окно для отображения статуса игры
        val dialog = Dialog(this@GameActivity, R.style.Theme_Zar_TicTacToe)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.argb
                    (50, 0, 0, 0)
            )
        )
        dialog.setContentView(R.layout.dialog_popup_status_game)
        dialog.setCancelable(true)

        // В зависимости от статуса игры отображаем соответствующее сообщение и изображение
        when (status) {
            STATUS_WIN_BOT -> {
                dialog.findViewById<TextView>(R.id.dialog_status_title).text = "Вы проиграли"
                dialog.findViewById<ImageView>(R.id.dialog_status_img)
                    .setImageResource(R.drawable.ic_lose)
            }

            STATUS_WIN_PLAYER -> {
                dialog.findViewById<TextView>(R.id.dialog_status_title).text = "Вы выиграли"
                dialog.findViewById<ImageView>(R.id.dialog_status_img)
                    .setImageResource(R.drawable.ic_win)
            }

            STATUS_DRAW -> {
                dialog.findViewById<TextView>(R.id.dialog_status_title).text = "Ничья"
                dialog.findViewById<ImageView>(R.id.dialog_status_img)
                    .setImageResource(R.drawable.ic_draw)
            }
        }

        // При нажатии на кнопку "ОК" скрываем диалоговое окно и возвращаемся назад
        dialog.findViewById<TextView>(R.id.dialog_status_ok).setOnClickListener {
            dialog.hide()
            onBackPressed()
        }
        // Отображаем диалоговое окно
        dialog.show()
    }

    // Функция для отображения всплывающего меню
    private fun showPopupMenu() {
        // Останавливаем хронометр
        binding.gameTimer.stop()

        // Вычисляем прошедшее время
        val elapsedMillis = SystemClock.elapsedRealtime() - binding.gameTimer.base

        // Создаем диалоговое окно для отображения всплывающего меню
        val dialog = Dialog(this@GameActivity, R.style.Theme_Zar_TicTacToe)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.argb
                    (50, 0, 0, 0)
            )
        )
        dialog.setContentView(R.layout.dialog_popup_menu)
        dialog.setCancelable(true)

        // Устанавливаем слушатели для различных элементов всплывающего меню
        dialog.findViewById<TextView>(R.id.dialog_continue).setOnClickListener {
            // При нажатии на "Продолжить" скрываем диалоговое окно и продолжаем игру
            dialog.hide()
            binding.gameTimer.base = SystemClock.elapsedRealtime() - elapsedMillis
            binding.gameTimer.start()
        }
        dialog.findViewById<TextView>(R.id.dialog_settings).setOnClickListener {
            // При нажатии на "Настройки" переходим к активности настроек
            dialog.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, POPUP_MENU)
        }
        dialog.findViewById<TextView>(R.id.dialog_exit).setOnClickListener {
            // При нажатии на "Выход" сохраняем игру и выходим
            saveGame(elapsedMillis, convertGameFieldToString())
            dialog.hide()
            onBackPressed()
        }

        // Отображаем диалоговое окно
        dialog.show()
    }

    // Объявляем статические константы и переменные
    companion object {
        // Константы для обозначения статуса игры
        const val STATUS_WIN_PLAYER = 1
        const val STATUS_WIN_BOT = 2
        const val STATUS_DRAW = 3
        const val POPUP_MENU = 235

        // Словарь для хранения счетов, соответствующих различным статусам игры
        val scores = hashMapOf(
            Pair(STATUS_WIN_PLAYER, -1.0), Pair(STATUS_WIN_BOT, 1.0), Pair(STATUS_DRAW, 0.0)
        )

        // Константы для обозначения символов игрока и ИИ
        const val PLAYER_SYMBOL = "X"
        const val BOT_SYMBOL = "0"
    }
}