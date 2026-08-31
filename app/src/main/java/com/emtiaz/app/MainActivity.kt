package com.emtiaz.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var team1Name: TextView
    private lateinit var team2Name: TextView
    private lateinit var team1Score: TextView
    private lateinit var team2Score: TextView
    private lateinit var roundsText: TextView

    private var score1 = 0
    private var score2 = 0
    private var roundCount = 0

    private val history = ArrayList<Pair<Int, Int>>()

    private val prefsName = "shemr_score"
    private val team1Key = "team1"
    private val team2Key = "team2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(20, 48, 42)

        showTeamNamesDialog()
    }

    // ---------------------------------------------------------
    // دریافت نام دو گروه
    // ---------------------------------------------------------

    private fun showTeamNamesDialog() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(45, 25, 45, 10)
        layout.layoutDirection = View.LAYOUT_DIRECTION_RTL

        val title = TextView(this)
        title.text = "امتیاز شلم"
        title.textSize = 26f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(20, 80, 65))

        val subtitle = TextView(this)
        subtitle.text = "نام دو گروه را وارد کنید"
        subtitle.textSize = 16f
        subtitle.gravity = Gravity.CENTER
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
            .setNegativeButton("لغو", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

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

                getSharedPreferences(prefsName, MODE_PRIVATE)
                    .edit()
                    .putString(team1Key, name1)
                    .putString(team2Key, name2)
                    .apply()

                createMainScreen(name1, name2)
                dialog.dismiss()
            }
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // ---------------------------------------------------------
    // صفحه اصلی
    // ---------------------------------------------------------

    private fun createMainScreen(name1: String, name2: String) {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.setBackgroundColor(Color.rgb(245, 247, 246))
        root.setPadding(25, 25, 25, 25)

        // عنوان
        val title = TextView(this)
        title.text = "♠  امتیاز شلم  ♥"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.rgb(20, 80, 65))
        title.gravity = Gravity.CENTER
        title.setPadding(0, 10, 0, 25)

        root.addView(title)

        // کارت امتیازات
        val scoreRow = LinearLayout(this)
        scoreRow.orientation = LinearLayout.HORIZONTAL
        scoreRow.gravity = Gravity.CENTER
        scoreRow.layoutDirection = View.LAYOUT_DIRECTION_LTR

        val card1 = createScoreCard(name1, true)
        val card2 = createScoreCard(name2, false)

        scoreRow.addView(
            card1,
            LinearLayout.LayoutParams(0, 270, 1f).apply {
                setMargins(0, 0, 10, 0)
            }
        )

        scoreRow.addView(
            card2,
            LinearLayout.LayoutParams(0, 270, 1f).apply {
                setMargins(10, 0, 0, 0)
            }
        )

        root.addView(scoreRow)

        // وضعیت دور
        roundsText = TextView(this)
        roundsText.text = "تعداد دورها: 0"
        roundsText.textSize = 17f
        roundsText.gravity = Gravity.CENTER
        roundsText.setTextColor(Color.DKGRAY)
        roundsText.setPadding(0, 20, 0, 20)

        root.addView(roundsText)

        // دکمه ثبت امتیاز
        val addButton = createButton(
            "➕  ثبت امتیاز دور جدید",
            Color.rgb(25, 110, 85)
        )

        addButton.setOnClickListener {
            showAddScoreDialog()
        }

        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            ).apply {
                setMargins(0, 5, 0, 12)
            }
        )

        // دکمه تاریخچه
        val historyButton = createButton(
            "📋  تاریخچه امتیازات",
            Color.rgb(55, 75, 90)
        )

        historyButton.setOnClickListener {
            showHistory()
        }

        root.addView(
            historyButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            ).apply {
                setMargins(0, 0, 0, 12)
            }
        )

        // دکمه بازی جدید
        val newGameButton = createButton(
            "🔄  بازی جدید",
            Color.rgb(150, 65, 55)
        )

        newGameButton.setOnClickListener {
            confirmNewGame()
        }

        root.addView(
            newGameButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        setContentView(root)
    }

    // ---------------------------------------------------------
    // کارت امتیاز
    // ---------------------------------------------------------

    private fun createScoreCard(
        name: String,
        first: Boolean
    ): LinearLayout {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(10, 15, 10, 15)

        val background = GradientDrawable()
        background.cornerRadius = 35f

        if (first) {
            background.setColor(Color.rgb(225, 241, 235))
        } else {
            background.setColor(Color.rgb(235, 237, 242))
        }

        card.background = background

        val nameText = TextView(this)
        nameText.text = name
        nameText.textSize = 19f
        nameText.setTypeface(null, Typeface.BOLD)
        nameText.gravity = Gravity.CENTER
        nameText.setTextColor(Color.rgb(30, 45, 45))

        val scoreText = TextView(this)
        scoreText.text = "0"
        scoreText.textSize = 50f
        scoreText.setTypeface(null, Typeface.BOLD)
        scoreText.gravity = Gravity.CENTER

        if (first) {
            scoreText.setTextColor(Color.rgb(20, 105, 80))
        } else {
            scoreText.setTextColor(Color.rgb(50, 70, 110))
        }

        if (first) {
            team1Name = nameText
            team1Score = scoreText
        } else {
            team2Name = nameText
            team2Score = scoreText
        }

        card.addView(nameText)
        card.addView(scoreText)

        return card
    }

    // ---------------------------------------------------------
    // ثبت امتیاز
    // ---------------------------------------------------------

    private fun showAddScoreDialog() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutDirection = View.LAYOUT_DIRECTION_RTL
        layout.setPadding(40, 20, 40, 5)

        val title = TextView(this)
        title.text = "ثبت امتیاز دور جدید"
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(20, 80, 65))

        val input1 = EditText(this)
        input1.hint = "امتیاز گروه اول"
        input1.inputType = 2
        input1.textSize = 18f
        input1.gravity = Gravity.RIGHT

        val input2 = EditText(this)
        input2.hint = "امتیاز گروه دوم"
        input2.inputType = 2
        input2.textSize = 18f
        input2.gravity = Gravity.RIGHT

        layout.addView(title)
        layout.addView(input1)
        layout.addView(input2)

        val dialog = AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("ثبت", null)
            .setNegativeButton("انصراف", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

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

                history.add(Pair(s1, s2))

                score1 += s1
                score2 += s2
                roundCount++

                updateScores()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // ---------------------------------------------------------
    // بروزرسانی امتیاز
    // ---------------------------------------------------------

    private fun updateScores() {

        team1Score.text = score1.toString()
        team2Score.text = score2.toString()

        roundsText.text = "تعداد دورها: $roundCount"
    }

    // ---------------------------------------------------------
    // تاریخچه
    // ---------------------------------------------------------

    private fun showHistory() {

        if (history.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("تاریخچه")
                .setMessage("هنوز هیچ دوری ثبت نشده است.")
                .setPositiveButton("باشه", null)
                .show()

            return
        }

        val team1 = team1Name.text.toString()
        val team2 = team2Name.text.toString()

        val text = StringBuilder()

        for (i in history.indices) {

            val item = history[i]

            text.append("دور ${i + 1}\n")
            text.append("$team1 : ${item.first}\n")
            text.append("$team2 : ${item.second}\n")
            text.append("--------------------\n")
        }

        AlertDialog.Builder(this)
            .setTitle("📋 تاریخچه بازی")
            .setMessage(text.toString())
            .setPositiveButton("بستن", null)
            .show()
    }

    // ---------------------------------------------------------
    // بازی جدید
    // ---------------------------------------------------------

    private fun confirmNewGame() {

        AlertDialog.Builder(this)
            .setTitle("بازی جدید")
            .setMessage("امتیازات فعلی پاک می‌شوند. ادامه می‌دهید؟")
            .setNegativeButton("انصراف", null)
            .setPositiveButton("بله، بازی جدید") { _, _ ->

                score1 = 0
                score2 = 0
                roundCount = 0
                history.clear()

                showTeamNamesDialog()
            }
            .show()
    }

    // ---------------------------------------------------------
    // ساخت دکمه
    // ---------------------------------------------------------

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

        val drawable = GradientDrawable()
        drawable.setColor(color)
        drawable.cornerRadius = 25f

        button.background = drawable

        return button
    }
}
