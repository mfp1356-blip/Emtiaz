package com.emtiaz.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.ArrayList

class MainActivity : Activity() {

    private lateinit var team1Name: TextView
    private lateinit var team2Name: TextView
    private lateinit var team1Score: TextView
    private lateinit var team2Score: TextView
    private lateinit var roundsText: TextView
    private lateinit var winnerText: TextView
    private lateinit var undoButton: Button

    private var score1 = 0
    private var score2 = 0
    private var roundCount = 0

    private val history = ArrayList<Pair<Int, Int>>()

    private val prefsName = "shemr_score"

    private val team1Key = "team1"
    private val team2Key = "team2"
    private val score1Key = "score1"
    private val score2Key = "score2"
    private val roundsKey = "rounds"
    private val historyKey = "history"

    // رنگ‌های اصلی طراحی
    private val backgroundColor = Color.rgb(243, 246, 244)
    private val darkGreen = Color.rgb(18, 78, 63)
    private val green = Color.rgb(25, 118, 91)
    private val gold = Color.rgb(194, 145, 45)
    private val darkGray = Color.rgb(55, 67, 70)
    private val red = Color.rgb(170, 65, 55)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = darkGreen

        if (hasSavedGame()) {
            loadGame()
        } else {
            showTeamNamesDialog()
        }
    }

    // =========================================================
    // بازی ذخیره شده؟
    // =========================================================

    private fun hasSavedGame(): Boolean {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)

        return !prefs.getString(team1Key, "").isNullOrEmpty() &&
                !prefs.getString(team2Key, "").isNullOrEmpty()
    }

    // =========================================================
    // نام دو گروه
    // =========================================================

    private fun showTeamNamesDialog() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutDirection = View.LAYOUT_DIRECTION_RTL
        layout.setPadding(45, 30, 45, 15)

        val title = TextView(this)
        title.text = "♠  امتیاز شلم  ♥"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(darkGreen)

        val subtitle = TextView(this)
        subtitle.text = "برای شروع، نام دو گروه را وارد کنید"
        subtitle.textSize = 16f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.setPadding(0, 10, 0, 25)

        val team1Input = EditText(this)
        team1Input.hint = "نام گروه اول"
        team1Input.textSize = 17f
        team1Input.gravity = Gravity.RIGHT
        team1Input.setSingleLine(true)

        val team2Input = EditText(this)
        team2Input.hint = "نام گروه دوم"
        team2Input.textSize = 17f
        team2Input.gravity = Gravity.RIGHT
        team2Input.setSingleLine(true)

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(team1Input)
        layout.addView(team2Input)

        val dialog = AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("شروع بازی", null)
            .setNegativeButton("انصراف", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val name1 = team1Input.text.toString().trim()
                    val name2 = team2Input.text.toString().trim()

                    if (name1.isEmpty()) {
                        team1Input.error = "نام گروه اول را وارد کنید"
                        return@setOnClickListener
                    }

                    if (name2.isEmpty()) {
                        team2Input.error = "نام گروه دوم را وارد کنید"
                        return@setOnClickListener
                    }

                    startNewGame(name1, name2)
                    dialog.dismiss()
                }
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // =========================================================
    // شروع بازی جدید
    // =========================================================

    private fun startNewGame(name1: String, name2: String) {

        score1 = 0
        score2 = 0
        roundCount = 0
        history.clear()

        saveGame(name1, name2)

        createMainScreen(name1, name2)
        updateScores()
    }

    // =========================================================
    // بارگذاری بازی
    // =========================================================

    private fun loadGame() {

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)

        val name1 = prefs.getString(team1Key, "گروه اول") ?: "گروه اول"
        val name2 = prefs.getString(team2Key, "گروه دوم") ?: "گروه دوم"

        score1 = prefs.getInt(score1Key, 0)
        score2 = prefs.getInt(score2Key, 0)
        roundCount = prefs.getInt(roundsKey, 0)

        history.clear()

        val savedHistory = prefs.getString(historyKey, "") ?: ""

        if (savedHistory.isNotEmpty()) {

            for (round in savedHistory.split(";")) {

                if (round.isEmpty()) continue

                val values = round.split(",")

                if (values.size == 2) {

                    val first = values[0].toIntOrNull()
                    val second = values[1].toIntOrNull()

                    if (first != null && second != null) {
                        history.add(Pair(first, second))
                    }
                }
            }
        }

        createMainScreen(name1, name2)
        updateScores()
    }

    // =========================================================
    // ذخیره بازی
    // =========================================================

    private fun saveGame(name1: String, name2: String) {

        val historyString = StringBuilder()

        for (item in history) {
            historyString.append(item.first)
            historyString.append(",")
            historyString.append(item.second)
            historyString.append(";")
        }

        getSharedPreferences(prefsName, MODE_PRIVATE)
            .edit()
            .putString(team1Key, name1)
            .putString(team2Key, name2)
            .putInt(score1Key, score1)
            .putInt(score2Key, score2)
            .putInt(roundsKey, roundCount)
            .putString(historyKey, historyString.toString())
            .apply()
    }

    // =========================================================
    // صفحه اصلی جدید
    // =========================================================

    private fun createMainScreen(name1: String, name2: String) {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.setBackgroundColor(backgroundColor)
        root.setPadding(22, 18, 22, 22)

        // ---------------- عنوان ----------------

        val title = TextView(this)

        title.text = "♠  امتیاز شلم  ♥"
        title.textSize = 29f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(darkGreen)
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        val subtitle = TextView(this)

        subtitle.text = "امتیازشمار حرفه‌ای بازی شلم"
        subtitle.textSize = 14f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.GRAY)

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // ---------------- کارت امتیازات ----------------

        val scoreRow = LinearLayout(this)

        scoreRow.orientation = LinearLayout.HORIZONTAL
        scoreRow.gravity = Gravity.CENTER
        scoreRow.layoutDirection = View.LAYOUT_DIRECTION_LTR

        val card1 = createScoreCard(name1, true)
        val card2 = createScoreCard(name2, false)

        scoreRow.addView(
            card1,
            LinearLayout.LayoutParams(
                0,
                235,
                1f
            ).apply {
                setMargins(0, 8, 7, 0)
            }
        )

        scoreRow.addView(
            card2,
            LinearLayout.LayoutParams(
                0,
                235,
                1f
            ).apply {
                setMargins(7, 8, 0, 0)
            }
        )

        root.addView(scoreRow)

        // ---------------- وضعیت ----------------

        winnerText = TextView(this)

        winnerText.textSize = 17f
        winnerText.setTypeface(null, Typeface.BOLD)
        winnerText.gravity = Gravity.CENTER
        winnerText.setPadding(0, 15, 0, 4)

        root.addView(winnerText)

        roundsText = TextView(this)

        roundsText.text = "تعداد دورها: 0"
        roundsText.textSize = 15f
        roundsText.gravity = Gravity.CENTER
        roundsText.setTextColor(Color.DKGRAY)

        root.addView(
            roundsText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // ---------------- ثبت امتیاز ----------------

        val addButton = createButton(
            "➕   ثبت امتیاز دور جدید",
            green
        )

        addButton.textSize = 17f

        addButton.setOnClickListener {
            showAddScoreDialog()
        }

        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                64
            ).apply {
                setMargins(0, 10, 0, 9)
            }
        )

        // ---------------- برگشت ----------------

        undoButton = createButton(
            "↩   برگشت آخرین دور",
            gold
        )

        undoButton.setOnClickListener {
            undoLastRound()
        }

        root.addView(
            undoButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            ).apply {
                setMargins(0, 0, 0, 9)
            }
        )

        // ---------------- تاریخچه ----------------

        val historyButton = createButton(
            "📋   تاریخچه امتیازات",
            darkGray
        )

        historyButton.setOnClickListener {
            showHistory()
        }

        root.addView(
            historyButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            ).apply {
                setMargins(0, 0, 0, 9)
            }
        )

        // ---------------- بازی جدید ----------------

        val newGameButton = createButton(
            "🔄   بازی جدید",
            red
        )

        newGameButton.setOnClickListener {
            confirmNewGame()
        }

        root.addView(
            newGameButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            )
        )

        setContentView(root)
    }

    // =========================================================
    // کارت امتیاز
    // =========================================================

    private fun createScoreCard(
        name: String,
        first: Boolean
    ): LinearLayout {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(10, 12, 10, 12)

        val background = GradientDrawable()

        background.cornerRadius = 42f

        if (first) {
            background.setColor(Color.rgb(220, 239, 231))
            background.setStroke(3, Color.rgb(170, 215, 199))
        } else {
            background.setColor(Color.rgb(231, 235, 242))
            background.setStroke(3, Color.rgb(195, 202, 215))
        }

        card.background = background

        // علامت گروه

        val icon = TextView(this)

        icon.text = if (first) "♠" else "♥"
        icon.textSize = 25f
        icon.gravity = Gravity.CENTER

        if (first) {
            icon.setTextColor(green)
        } else {
            icon.setTextColor(Color.rgb(75, 90, 135))
        }

        // نام گروه

        val nameText = TextView(this)

        nameText.text = name
        nameText.textSize = 18f
        nameText.setTypeface(null, Typeface.BOLD)
        nameText.gravity = Gravity.CENTER
        nameText.setTextColor(Color.rgb(30, 45, 45))

        // امتیاز

        val scoreText = TextView(this)

        scoreText.text = "0"
        scoreText.textSize = 48f
        scoreText.setTypeface(null, Typeface.BOLD)
        scoreText.gravity = Gravity.CENTER

        if (first) {
            scoreText.setTextColor(green)

            team1Name = nameText
            team1Score = scoreText
        } else {
            scoreText.setTextColor(Color.rgb(65, 80, 125))

            team2Name = nameText
            team2Score = scoreText
        }

        card.addView(icon)
        card.addView(nameText)
        card.addView(scoreText)

        return card
    }

    // =========================================================
    // ثبت امتیاز
    // =========================================================

    private fun showAddScoreDialog() {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.layoutDirection = View.LAYOUT_DIRECTION_RTL
        layout.setPadding(40, 20, 40, 5)

        val title = TextView(this)

        title.text = "➕  ثبت امتیاز دور جدید"
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(darkGreen)

        val input1 = EditText(this)

        input1.hint = "امتیاز ${team1Name.text}"
        input1.inputType = 2
        input1.textSize = 18f
        input1.gravity = Gravity.RIGHT

        val input2 = EditText(this)

        input2.hint = "امتیاز ${team2Name.text}"
        input2.inputType = 2
        input2.textSize = 18f
        input2.gravity = Gravity.RIGHT

        layout.addView(title)
        layout.addView(input1)
        layout.addView(input2)

        val dialog = AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("ثبت امتیاز", null)
            .setNegativeButton("انصراف", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val s1 = input1.text.toString().toIntOrNull()
                    val s2 = input2.text.toString().toIntOrNull()

                    if (s1 == null) {
                        input1.error = "امتیاز را وارد کنید"
                        return@setOnClickListener
                    }

                    if (s2 == null) {
                        input2.error = "امتیاز را وارد کنید"
                        return@setOnClickListener
                    }

                    if (s1 < 0 || s2 < 0) {
                        Toast.makeText(
                            this,
                            "امتیاز نمی‌تواند منفی باشد",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    history.add(Pair(s1, s2))

                    score1 += s1
                    score2 += s2

                    roundCount++

                    saveGame(
                        team1Name.text.toString(),
                        team2Name.text.toString()
                    )

                    updateScores()

                    dialog.dismiss()
                }
        }

        dialog.show()
    }

    // =========================================================
    // برگشت آخرین دور
    // =========================================================

    private fun undoLastRound() {

        if (history.isEmpty()) {

            AlertDialog.Builder(this)
                .setTitle("↩ برگشت آخرین دور")
                .setMessage("هنوز هیچ دوری ثبت نشده است.")
                .setPositiveButton("باشه", null)
                .show()

            return
        }

        val last = history.last()

        AlertDialog.Builder(this)
            .setTitle("↩ برگشت آخرین دور")
            .setMessage(
                "امتیاز این دور حذف شود؟\n\n" +
                        "${team1Name.text}: ${last.first}\n" +
                        "${team2Name.text}: ${last.second}"
            )
            .setNegativeButton("انصراف", null)
            .setPositiveButton("حذف دور") { _, _ ->

                score1 -= last.first
                score2 -= last.second

                history.removeAt(history.lastIndex)

                roundCount--

                if (roundCount < 0) {
                    roundCount = 0
                }

                saveGame(
                    team1Name.text.toString(),
                    team2Name.text.toString()
                )

                updateScores()
            }
            .show()
    }

    // =========================================================
    // بروزرسانی
    // =========================================================

    private fun updateScores() {

        team1Score.text = score1.toString()
        team2Score.text = score2.toString()

        roundsText.text = "تعداد دورها: $roundCount"

        updateWinner()

        undoButton.isEnabled = history.isNotEmpty()

        undoButton.alpha =
            if (history.isEmpty()) 0.55f else 1f
    }

    // =========================================================
    // برنده / پیشتاز
    // =========================================================

    private fun updateWinner() {

        val name1 = team1Name.text.toString()
        val name2 = team2Name.text.toString()

        when {

            score1 > score2 -> {

                winnerText.text = "🏆  پیشتاز: $name1"
                winnerText.setTextColor(green)
            }

            score2 > score1 -> {

                winnerText.text = "🏆  پیشتاز: $name2"
                winnerText.setTextColor(Color.rgb(65, 80, 125))
            }

            else -> {

                winnerText.text = "⚖️  امتیاز دو گروه برابر است"
                winnerText.setTextColor(Color.DKGRAY)
            }
        }
    }

    // =========================================================
    // تاریخچه
    // =========================================================

    private fun showHistory() {

        if (history.isEmpty()) {

            AlertDialog.Builder(this)
                .setTitle("📋 تاریخچه")
                .setMessage("هنوز هیچ دوری ثبت نشده است.")
                .setPositiveButton("باشه", null)
                .show()

            return
        }

        val name1 = team1Name.text.toString()
        val name2 = team2Name.text.toString()

        val text = StringBuilder()

        for (i in history.indices.reversed()) {

            val item = history[i]

            text.append("دور ${i + 1}\n")
            text.append("$name1 : ${item.first}\n")
            text.append("$name2 : ${item.second}\n")
            text.append("--------------------\n")
        }

        text.append("\n🏁 مجموع نهایی\n")
        text.append("$name1 : $score1\n")
        text.append("$name2 : $score2")

        AlertDialog.Builder(this)
            .setTitle("📋 تاریخچه بازی")
            .setMessage(text.toString())
            .setPositiveButton("بستن", null)
            .show()
    }

    // =========================================================
    // بازی جدید
    // =========================================================

    private fun confirmNewGame() {

        AlertDialog.Builder(this)
            .setTitle("🔄 بازی جدید")
            .setMessage(
                "تمام امتیازها و تاریخچه بازی فعلی پاک می‌شود.\n\n" +
                        "آیا مطمئن هستید؟"
            )
            .setNegativeButton("انصراف", null)
            .setPositiveButton("بله، بازی جدید") { _, _ ->

                clearSavedGame()

                showTeamNamesDialog()
            }
            .show()
    }

    // =========================================================
    // پاک کردن ذخیره
    // =========================================================

    private fun clearSavedGame() {

        getSharedPreferences(prefsName, MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        score1 = 0
        score2 = 0
        roundCount = 0

        history.clear()
    }

    // =========================================================
    // ساخت دکمه
    // =========================================================

    private fun createButton(
        text: String,
        color: Int
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 16f
        button.setTypeface(null, Typeface.BOLD)
        button.setTextColor(Color.WHITE)
        button.gravity = Gravity.CENTER
        button.isAllCaps = false

        val drawable = GradientDrawable()

        drawable.setColor(color)
        drawable.cornerRadius = 28f

        button.background = drawable

        return button
    }
}
