package com.emtiaz.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var team1Name: TextView
    private lateinit var team2Name: TextView
    private lateinit var team1Score: TextView
    private lateinit var team2Score: TextView
    private lateinit var roundsText: TextView
    private lateinit var team1Card: LinearLayout
    private lateinit var team2Card: LinearLayout
    private lateinit var winnerText: TextView

    private var score1 = 0
    private var score2 = 0
    private var roundCount = 0

    private val history = ArrayList<Pair<Int, Int>>()
    private val prefsName = "shemr_score"
    private val team1Key = "team1"
    private val team2Key = "team2"

    private val darkGreen = Color.rgb(4, 42, 29)
    private val green = Color.rgb(12, 105, 68)
    private val green2 = Color.rgb(25, 135, 88)
    private val gold = Color.rgb(245, 185, 45)
    private val lightGold = Color.rgb(255, 224, 115)
    private val cream = Color.rgb(255, 249, 228)
    private val dark = Color.rgb(20, 30, 28)
    private val red = Color.rgb(180, 45, 45)
    private val blue = Color.rgb(42, 82, 108)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = darkGreen
        window.navigationBarColor = Color.BLACK

        showSplash()
    }

    // ---------------------------------------------------------
    // Splash
    // ---------------------------------------------------------

    private fun showSplash() {
        val root = FrameLayout(this)
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(2, 25, 17),
                Color.rgb(7, 82, 52),
                Color.rgb(2, 35, 24)
            )
        )

        val glow = TextView(this)
        glow.text = "✦"
        glow.textSize = 72f
        glow.setTextColor(lightGold)
        glow.gravity = Gravity.CENTER
        root.addView(
            glow,
            FrameLayout.LayoutParams(
                dp(160),
                dp(100),
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            ).apply { topMargin = dp(25) }
        )

        val logo = ImageView(this)
        try {
            logo.setImageResource(R.drawable.app_logo)
        } catch (_: Exception) {
            logo.setImageDrawable(createLogoFallback())
        }
        logo.scaleType = ImageView.ScaleType.CENTER_INSIDE
        logo.alpha = 0f
        logo.scaleX = 0.65f
        logo.scaleY = 0.65f

        root.addView(
            logo,
            FrameLayout.LayoutParams(
                dp(300),
                dp(300),
                Gravity.CENTER
            )
        )

        val title = TextView(this)
        title.text = "امتیاز"
        title.textSize = 36f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(lightGold)
        title.gravity = Gravity.CENTER
        title.alpha = 0f
        root.addView(
            title,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(65),
                Gravity.BOTTOM
            ).apply { bottomMargin = dp(105) }
        )

        val subtitle = TextView(this)
        subtitle.text = "مدیریت امتیاز بازی شلم"
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER
        subtitle.alpha = 0f
        root.addView(
            subtitle,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(45),
                Gravity.BOTTOM
            ).apply { bottomMargin = dp(65) }
        )

        setContentView(root)

        val logoScale = ScaleAnimation(
            0.65f, 1f, 0.65f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 850
            fillAfter = true
        }

        val logoFade = AlphaAnimation(0f, 1f).apply {
            duration = 850
            fillAfter = true
        }

        logo.startAnimation(logoScale)
        logo.startAnimation(logoFade)

        title.startAnimation(
            AlphaAnimation(0f, 1f).apply {
                duration = 700
                startOffset = 450
                fillAfter = true
            }
        )

        subtitle.startAnimation(
            AlphaAnimation(0f, 1f).apply {
                duration = 700
                startOffset = 700
                fillAfter = true
            }
        )

        Handler(Looper.getMainLooper()).postDelayed({
            showTeamNamesDialog()
        }, 1800)
    }

    private fun createLogoFallback(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(20, 125, 78),
                Color.rgb(3, 50, 32)
            )
        ).apply {
            cornerRadius = dp(45).toFloat()
            setStroke(dp(3), gold)
        }
    }

    // ---------------------------------------------------------
    // ورود نام گروه‌ها
    // ---------------------------------------------------------

    private fun showTeamNamesDialog() {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER_HORIZONTAL
        container.layoutDirection = View.LAYOUT_DIRECTION_RTL
        container.setPadding(dp(24), dp(18), dp(24), dp(10))

        container.background = roundedGradient(
            intArrayOf(Color.WHITE, Color.rgb(236, 248, 241)),
            28,
            Color.rgb(215, 225, 219),
            2
        )
        container.elevation = dp(16).toFloat()

        val icon = TextView(this)
        icon.text = "♠   ♥   ♣   ♦"
        icon.textSize = 22f
        icon.setTextColor(gold)
        icon.gravity = Gravity.CENTER
        container.addView(icon, lpMatch(dp(42)))

        val title = TextView(this)
        title.text = "امتیاز شلم"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(darkGreen)
        title.gravity = Gravity.CENTER
        container.addView(title, lpMatch(dp(50)))

        val subtitle = TextView(this)
        subtitle.text = "نام دو گروه را وارد کنید"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.rgb(80, 95, 88))
        subtitle.gravity = Gravity.CENTER
        container.addView(subtitle, lpMatch(dp(45)))

        val label1 = createLabel("♠  گروه اول")
        container.addView(label1, lpMatch(dp(32)))

        val input1 = createTeamInput("نام گروه اول")
        container.addView(
            input1,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            ).apply { setMargins(0, 0, 0, dp(12)) }
        )

        val label2 = createLabel("♥  گروه دوم")
        container.addView(label2, lpMatch(dp(32)))

        val input2 = createTeamInput("نام گروه دوم")
        container.addView(
            input2,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .setPositiveButton("شروع بازی  ✦", null)
            .setNegativeButton("انصراف", null)
            .create()

        dialog.setOnShowListener {
            val start = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            start.setTextColor(darkGreen)
            start.setTextSize(16f)
            start.setTypeface(null, Typeface.BOLD)
            cancel.setTextColor(Color.DKGRAY)

            start.setOnClickListener {
                val name1 = input1.text.toString().trim()
                val name2 = input2.text.toString().trim()

                if (name1.isEmpty()) {
                    input1.error = "نام گروه اول را وارد کنید"
                    input1.requestFocus()
                    return@setOnClickListener
                }

                if (name2.isEmpty()) {
                    input2.error = "نام گروه دوم را وارد کنید"
                    input2.requestFocus()
                    return@setOnClickListener
                }

                getSharedPreferences(prefsName, MODE_PRIVATE)
                    .edit()
                    .putString(team1Key, name1)
                    .putString(team2Key, name2)
                    .apply()

                score1 = 0
                score2 = 0
                roundCount = 0
                history.clear()

                createMainScreen(name1, name2)
                dialog.dismiss()
            }
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun createLabel(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            setTextColor(darkGreen)
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
    }

    private fun createTeamInput(hintText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 17f
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setPadding(dp(18), 0, dp(18), 0)
            background = roundedGradient(
                intArrayOf(Color.WHITE, Color.rgb(235, 245, 239)),
                18,
                Color.rgb(205, 220, 212),
                2
            )
            elevation = dp(5).toFloat()
        }
    }

    // ---------------------------------------------------------
    // صفحه اصلی جدید
    // ---------------------------------------------------------

    private fun createMainScreen(name1: String, name2: String) {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(darkGreen)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.setPadding(dp(16), dp(12), dp(16), dp(25))

        root.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(3, 42, 28),
                Color.rgb(8, 103, 66),
                Color.rgb(3, 50, 33)
            )
        )

        // عنوان
        val titleCard = LinearLayout(this)
        titleCard.orientation = LinearLayout.VERTICAL
        titleCard.gravity = Gravity.CENTER
        titleCard.background = roundedGradient(
            intArrayOf(
                Color.argb(210, 12, 108, 70),
                Color.argb(190, 5, 70, 45)
            ),
            26,
            Color.argb(150, 255, 220, 110),
            1
        )
        titleCard.elevation = dp(10).toFloat()

        val title = TextView(this)
        title.text = "♠   امتیاز شلم   ♥"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(lightGold)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "جدول امتیازات بازی"
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.rgb(220, 238, 228))
        subtitle.gravity = Gravity.CENTER

        titleCard.addView(title, lpMatch(dp(48)))
        titleCard.addView(subtitle, lpMatch(dp(30)))

        root.addView(
            titleCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(88)
            ).apply { setMargins(0, 0, 0, dp(10)) }
        )

        // کارت‌های امتیاز
        val scoreRow = LinearLayout(this)
        scoreRow.orientation = LinearLayout.HORIZONTAL
        scoreRow.gravity = Gravity.CENTER
        scoreRow.layoutDirection = View.LAYOUT_DIRECTION_LTR

        team1Card = createScoreCard(name1, true)
        team2Card = createScoreCard(name2, false)

        scoreRow.addView(
            team1Card,
            LinearLayout.LayoutParams(0, dp(235), 1f).apply {
                setMargins(0, dp(5), dp(6), dp(5))
            }
        )
        scoreRow.addView(
            team2Card,
            LinearLayout.LayoutParams(0, dp(235), 1f).apply {
                setMargins(dp(6), dp(5), 0, dp(5))
            }
        )

        root.addView(scoreRow)

        winnerText = TextView(this)
        winnerText.text = "شروع بازی ✦"
        winnerText.textSize = 15f
        winnerText.setTypeface(null, Typeface.BOLD)
        winnerText.setTextColor(lightGold)
        winnerText.gravity = Gravity.CENTER
        winnerText.alpha = 0f
        root.addView(winnerText, lpMatch(dp(38)))

        roundsText = TextView(this)
        roundsText.text = "✦  تعداد دورها: 0  ✦"
        roundsText.textSize = 16f
        roundsText.setTypeface(null, Typeface.BOLD)
        roundsText.setTextColor(lightGold)
        roundsText.gravity = Gravity.CENTER
        root.addView(roundsText, lpMatch(dp(44)))

        // دکمه‌ها
        val addButton = create3DButton(
            "➕  ثبت امتیاز دور جدید",
            green2
        )
        addButton.setOnClickListener {
            animateClick(addButton)
            showAddScoreDialog()
        }
        root.addView(
            addButton,
            buttonParams(dp(66), dp(10), dp(11))
        )

        val historyButton = create3DButton(
            "📋  تاریخچه امتیازات",
            blue
        )
        historyButton.setOnClickListener {
            animateClick(historyButton)
            showHistory()
        }
        root.addView(
            historyButton,
            buttonParams(dp(66), 0, dp(11))
        )

        val newGameButton = create3DButton(
            "🔄  بازی جدید",
            red
        )
        newGameButton.setOnClickListener {
            animateClick(newGameButton)
            confirmNewGame()
        }
        root.addView(newGameButton, buttonParams(dp(66), 0, 0))

        scroll.addView(root)
        setContentView(scroll)

        animateMainEntrance(titleCard, scoreRow, roundsText, addButton, historyButton, newGameButton)
        updateWinner()
    }

    private fun createScoreCard(name: String, first: Boolean): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.layoutDirection = View.LAYOUT_DIRECTION_RTL
        card.setPadding(dp(8), dp(8), dp(8), dp(8))

        card.background = roundedGradient(
            if (first) {
                intArrayOf(
                    Color.rgb(255, 252, 233),
                    Color.rgb(235, 247, 239)
                )
            } else {
                intArrayOf(
                    Color.rgb(252, 253, 253),
                    Color.rgb(224, 237, 242)
                )
            },
            28,
            if (first) gold else Color.rgb(178, 198, 205),
            2
        )
        card.elevation = dp(18).toFloat()

        val icon = TextView(this)
        icon.text = if (first) "♠" else "♥"
        icon.textSize = 28f
        icon.gravity = Gravity.CENTER
        icon.setTextColor(if (first) gold else red)
        card.addView(icon, lpMatch(dp(38)))

        val nameText = TextView(this)
        nameText.text = name
        nameText.textSize = 18f
        nameText.setTypeface(null, Typeface.BOLD)
        nameText.gravity = Gravity.CENTER
        nameText.setTextColor(dark)
        nameText.maxLines = 1
        nameText.ellipsize = android.text.TextUtils.TruncateAt.END
        card.addView(nameText, lpMatch(dp(42)))

        val divider = View(this)
        divider.setBackgroundColor(if (first) gold else Color.rgb(170, 185, 190))
        card.addView(
            divider,
            LinearLayout.LayoutParams(dp(78), dp(2)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        )

        val scoreText = TextView(this)
        scoreText.text = "0"
        scoreText.textSize = 52f
        scoreText.setTypeface(null, Typeface.BOLD)
        scoreText.gravity = Gravity.CENTER
        scoreText.setTextColor(
            if (first) Color.rgb(10, 105, 67)
            else Color.rgb(43, 72, 101)
        )
        card.addView(scoreText, lpMatch(dp(82)))

        val caption = TextView(this)
        caption.text = "امتیاز"
        caption.textSize = 13f
        caption.setTextColor(Color.rgb(100, 110, 108))
        caption.gravity = Gravity.CENTER
        card.addView(caption, lpMatch(dp(28)))

        if (first) {
            team1Name = nameText
            team1Score = scoreText
        } else {
            team2Name = nameText
            team2Score = scoreText
        }

        return card
    }

    private fun create3DButton(text: String, color: Int): Button {
        val button = Button(this)
        button.text = text
        button.textSize = 17f
        button.setTypeface(null, Typeface.BOLD)
        button.setTextColor(Color.WHITE)
        button.gravity = Gravity.CENTER
        button.isAllCaps = false
        button.setPadding(dp(10), 0, dp(10), 0)
        button.background = roundedGradient(
            intArrayOf(
                lightenColor(color),
                color,
                darkenColor(color)
            ),
            20,
            Color.argb(150, 255, 255, 255),
            1
        )
        button.elevation = dp(12).toFloat()
        button.stateListAnimator = null

        button.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.96f).scaleY(0.96f)
                        .setDuration(70).start()
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f)
                        .setDuration(100).start()
                }
            }
            false
        }

        return button
    }

    // ---------------------------------------------------------
    // ثبت امتیاز
    // ---------------------------------------------------------

    private fun showAddScoreDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutDirection = View.LAYOUT_DIRECTION_RTL
        layout.setPadding(dp(28), dp(20), dp(28), dp(5))

        val title = TextView(this)
        title.text = "✦  ثبت امتیاز دور جدید  ✦"
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(darkGreen)
        layout.addView(title, lpMatch(dp(55)))

        val input1 = createScoreInput("امتیاز ${team1Name.text}")
        val input2 = createScoreInput("امتیاز ${team2Name.text}")

        layout.addView(
            input1,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            ).apply { setMargins(0, dp(5), 0, dp(12)) }
        )
        layout.addView(input2, lpMatch(dp(58)))

        val dialog = AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("ثبت امتیاز", null)
            .setNegativeButton("انصراف", null)
            .create()

        dialog.setOnShowListener {
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positive.setTextColor(green)
            positive.setTypeface(null, Typeface.BOLD)

            positive.setOnClickListener {
                val s1 = input1.text.toString().trim().toIntOrNull()
                val s2 = input2.text.toString().trim().toIntOrNull()

                if (s1 == null) {
                    input1.error = "امتیاز را وارد کنید"
                    input1.requestFocus()
                    return@setOnClickListener
                }

                if (s2 == null) {
                    input2.error = "امتیاز را وارد کنید"
                    input2.requestFocus()
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

    private fun createScoreInput(hintText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 17f
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
            setSingleLine(true)
            setPadding(dp(18), 0, dp(18), 0)
            background = roundedGradient(
                intArrayOf(Color.rgb(248, 251, 249), Color.WHITE),
                17,
                Color.rgb(205, 220, 212),
                2
            )
            elevation = dp(4).toFloat()
        }
    }

    // ---------------------------------------------------------
    // بروزرسانی امتیاز
    // ---------------------------------------------------------

    private fun updateScores() {
        team1Score.text = score1.toString()
        team2Score.text = score2.toString()
        roundsText.text = "✦  تعداد دورها: $roundCount  ✦"

        animateScore(team1Score)
        animateScore(team2Score)
        updateWinner()
    }

    private fun updateWinner() {
        if (!::winnerText.isInitialized) return

        val message = when {
            score1 == 0 && score2 == 0 -> "شروع بازی ✦"
            score1 == score2 -> "⚖️ مساوی"
            score1 > score2 -> "🏆 ${team1Name.text} پیشتاز است"
            else -> "🏆 ${team2Name.text} پیشتاز است"
        }

        winnerText.text = message
        winnerText.alpha = 1f

        if (::team1Card.isInitialized && ::team2Card.isInitialized) {
            team1Card.elevation = if (score1 > score2) dp(24).toFloat() else dp(18).toFloat()
            team2Card.elevation = if (score2 > score1) dp(24).toFloat() else dp(18).toFloat()
        }
    }

    private fun animateScore(view: View) {
        view.animate()
            .scaleX(1.16f)
            .scaleY(1.16f)
            .setDuration(140)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .start()
            }
            .start()
    }

    // ---------------------------------------------------------
    // تاریخچه
    // ---------------------------------------------------------

    private fun showHistory() {
        if (history.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("📋 تاریخچه")
                .setMessage("هنوز هیچ دوری ثبت نشده است.")
                .setPositiveButton("باشه", null)
                .show()
            return
        }

        val text = StringBuilder()

        for (i in history.indices) {
            val item = history[i]
            text.append("🏆 دور ${i + 1}\n")
            text.append("${team1Name.text}  :  ${item.first}\n")
            text.append("${team2Name.text}  :  ${item.second}\n")
            text.append("━━━━━━━━━━━━━━\n")
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
            .setTitle("🔄 بازی جدید")
            .setMessage(
                "امتیازات فعلی پاک می‌شوند.\n\nآیا می‌خواهید بازی جدید شروع کنید؟"
            )
            .setNegativeButton("انصراف", null)
            .setPositiveButton("بله، شروع کن") { _, _ ->
                score1 = 0
                score2 = 0
                roundCount = 0
                history.clear()
                showTeamNamesDialog()
            }
            .show()
    }

    // ---------------------------------------------------------
    // انیمیشن ورود صفحه اصلی
    // ---------------------------------------------------------

    private fun animateMainEntrance(
        titleCard: View,
        scoreRow: View,
        rounds: View,
        add: View,
        history: View,
        newGame: View
    ) {
        val views = listOf(titleCard, scoreRow, rounds, add, history, newGame)

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = dp(28).toFloat()

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 80).toLong())
                .setDuration(420)
                .start()
        }
    }

    private fun animateClick(view: View) {
        view.animate()
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(70)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    // ---------------------------------------------------------
    // ابزارها
    // ---------------------------------------------------------

    private fun roundedGradient(
        colors: IntArray,
        radius: Int,
        strokeColor: Int,
        strokeWidth: Int
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            colors
        ).apply {
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(strokeWidth), strokeColor)
        }
    }

    private fun lpMatch(height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        )
    }

    private fun buttonParams(
        height: Int,
        top: Int,
        bottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        ).apply {
            setMargins(0, top, 0, bottom)
        }
    }

    private fun darkenColor(color: Int): Int {
        val r = (Color.red(color) * 0.68f).toInt()
        val g = (Color.green(color) * 0.68f).toInt()
        val b = (Color.blue(color) * 0.68f).toInt()
        return Color.rgb(r, g, b)
    }

    private fun lightenColor(color: Int): Int {
        val r = (Color.red(color) + 255) / 2
        val g = (Color.green(color) + 255) / 2
        val b = (Color.blue(color) + 255) / 2
        return Color.rgb(r, g, b)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
