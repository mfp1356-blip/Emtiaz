package com.emtiaz.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import android.text.InputType

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

    // ---------------------------------------------------------
    // رنگ‌های اصلی
    // ---------------------------------------------------------

    private val darkGreen = Color.rgb(8, 55, 40)
    private val green = Color.rgb(15, 105, 70)
    private val gold = Color.rgb(245, 185, 45)
    private val lightGold = Color.rgb(255, 221, 105)
    private val cream = Color.rgb(255, 248, 225)
    private val dark = Color.rgb(20, 30, 28)
    private val red = Color.rgb(170, 45, 45)

    // ---------------------------------------------------------
    // شروع برنامه
    // ---------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = darkGreen
        window.navigationBarColor = Color.BLACK

        showSplash()
    }

    // ---------------------------------------------------------
    // Splash Screen انیمیشنی
    // ---------------------------------------------------------

    private fun showSplash() {

        val root = FrameLayout(this)

        val background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(4, 35, 25),
                Color.rgb(8, 75, 48),
                Color.rgb(3, 42, 30)
            )
        )

        root.background = background

        // نور طلایی بالای صفحه
        val glow = TextView(this)
        glow.text = "✦"
        glow.textSize = 70f
        glow.setTextColor(lightGold)
        glow.gravity = Gravity.CENTER

        root.addView(
            glow,
            FrameLayout.LayoutParams(
                dp(150),
                dp(100),
                Gravity.CENTER_HORIZONTAL or Gravity.TOP
            ).apply {
                topMargin = dp(35)
            }
        )

        // لوگو
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

        // عنوان زیر لوگو
        val title = TextView(this)
        title.text = "امتیاز"
        title.textSize = 34f
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
            ).apply {
                bottomMargin = dp(105)
            }
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
            ).apply {
                bottomMargin = dp(65)
            }
        )

        setContentView(root)

        // انیمیشن لوگو
        val logoAnimation = ScaleAnimation(
            0.65f,
            1f,
            0.65f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        logoAnimation.duration = 850
        logoAnimation.fillAfter = true

        val logoFade = AlphaAnimation(0f, 1f)
        logoFade.duration = 850
        logoFade.fillAfter = true

        logo.startAnimation(logoAnimation)
        logo.startAnimation(logoFade)

        // عنوان
        val titleFade = AlphaAnimation(0f, 1f)
        titleFade.duration = 700
        titleFade.startOffset = 450
        titleFade.fillAfter = true

        title.startAnimation(titleFade)

        val subtitleFade = AlphaAnimation(0f, 1f)
        subtitleFade.duration = 700
        subtitleFade.startOffset = 700
        subtitleFade.fillAfter = true

        subtitle.startAnimation(subtitleFade)

        // ورود به صفحه نام گروه‌ها
        Handler(Looper.getMainLooper()).postDelayed({
            showTeamNamesDialog()
        }, 1800)
    }

    // ---------------------------------------------------------
    // لوگوی جایگزین در صورت نبودن فایل
    // ---------------------------------------------------------

    private fun createLogoFallback(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(20, 120, 75),
                Color.rgb(5, 55, 35)
            )
        ).apply {
            cornerRadius = dp(45).toFloat()
            setStroke(dp(3), gold)
        }
    }

    // ---------------------------------------------------------
    // صفحه ورود نام گروه‌ها
    // ---------------------------------------------------------

    private fun showTeamNamesDialog() {

        val container = LinearLayout(this)

        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER_HORIZONTAL
        container.layoutDirection = View.LAYOUT_DIRECTION_RTL
        container.setPadding(
            dp(24),
            dp(20),
            dp(24),
            dp(12)
        )

        val cardBackground = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(255, 255, 255),
                Color.rgb(239, 248, 243)
            )
        )

        container.background = cardBackground
        container.elevation = dp(12).toFloat()

        // آیکن کوچک
        val icon = TextView(this)
        icon.text = "♠  ♥  ♣  ♦"
        icon.textSize = 22f
        icon.setTextColor(gold)
        icon.gravity = Gravity.CENTER
        icon.setPadding(0, dp(5), 0, dp(4))

        container.addView(
            icon,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(45)
            )
        )

        val title = TextView(this)
        title.text = "امتیاز شلم"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(darkGreen)
        title.gravity = Gravity.CENTER

        container.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        val subtitle = TextView(this)
        subtitle.text = "نام دو گروه را وارد کنید"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.rgb(80, 95, 88))
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, dp(4), 0, dp(18))

        container.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        // گروه اول
        val team1Label = createLabel("♠  گروه اول")
        container.addView(team1Label)

        val team1Input = createTeamInput("نام گروه اول")
        container.addView(
            team1Input,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            ).apply {
                setMargins(0, 0, 0, dp(12))
            }
        )

        // گروه دوم
        val team2Label = createLabel("♥  گروه دوم")
        container.addView(team2Label)

        val team2Input = createTeamInput("نام گروه دوم")
        container.addView(
            team2Input,
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

            val startButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            val cancelButton =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            startButton.setTextColor(darkGreen)
            startButton.textSize = 16f
            startButton.setTypeface(null, Typeface.BOLD)

            cancelButton.setTextColor(Color.DKGRAY)

            startButton.setOnClickListener {

                val name1 =
                    team1Input.text.toString().trim()

                val name2 =
                    team2Input.text.toString().trim()

                if (name1.isEmpty()) {
                    team1Input.error =
                        "نام گروه اول را وارد کنید"
                    team1Input.requestFocus()
                    return@setOnClickListener
                }

                if (name2.isEmpty()) {
                    team2Input.error =
                        "نام گروه دوم را وارد کنید"
                    team2Input.requestFocus()
                    return@setOnClickListener
                }

                getSharedPreferences(
                    prefsName,
                    MODE_PRIVATE
                )
                    .edit()
                    .putString(team1Key, name1)
                    .putString(team2Key, name2)
                    .apply()

                score1 = 0
                score2 = 0
                roundCount = 0
                history.clear()

                createMainScreen(
                    name1,
                    name2
                )

                dialog.dismiss()
            }
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // ---------------------------------------------------------
    // برچسب گروه
    // ---------------------------------------------------------

    private fun createLabel(text: String): TextView {

        val label = TextView(this)

        label.text = text
        label.textSize = 15f
        label.setTypeface(null, Typeface.BOLD)
        label.setTextColor(darkGreen)
        label.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL

        return label
    }

    // ---------------------------------------------------------
    // کادر ورود نام
    // ---------------------------------------------------------

    private fun createTeamInput(hintText: String): EditText {

        val input = EditText(this)

        input.hint = hintText
        input.textSize = 17f
        input.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        input.layoutDirection = View.LAYOUT_DIRECTION_RTL
        input.setSingleLine(true)
        input.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        input.setPadding(
            dp(18),
            0,
            dp(18),
            0
        )

        val bg = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.WHITE,
                Color.rgb(235, 245, 239)
            )
        )

        bg.cornerRadius = dp(18).toFloat()
        bg.setStroke(dp(2), Color.rgb(210, 220, 215))

        input.background = bg
        input.elevation = dp(5).toFloat()

        return input
    }

    // ---------------------------------------------------------
    // صفحه اصلی بازی
    // ---------------------------------------------------------

    private fun createMainScreen(
        name1: String,
        name2: String
    ) {

        val scroll = ScrollView(this)

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL

        val background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(5, 50, 35),
                Color.rgb(10, 90, 57),
                Color.rgb(4, 43, 30)
            )
        )

        root.background = background

        root.setPadding(
            dp(18),
            dp(20),
            dp(18),
            dp(25)
        )

        // عنوان
        val title = TextView(this)

        title.text = "♠   امتیاز شلم   ♥"
        title.textSize = 29f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(lightGold)
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(65)
            )
        )

        // زیرعنوان
        val subtitle = TextView(this)

        subtitle.text = "جدول امتیازات بازی"
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.rgb(220, 235, 226))
        subtitle.gravity = Gravity.CENTER

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        // کارت‌های امتیاز
        val scoreRow = LinearLayout(this)

        scoreRow.orientation =
            LinearLayout.HORIZONTAL

        scoreRow.gravity = Gravity.CENTER
        scoreRow.layoutDirection =
            View.LAYOUT_DIRECTION_LTR

        val card1 =
            createScoreCard(
                name1,
                true
            )

        val card2 =
            createScoreCard(
                name2,
                false
            )

        scoreRow.addView(
            card1,
            LinearLayout.LayoutParams(
                0,
                dp(220),
                1f
            ).apply {
                setMargins(
                    0,
                    dp(10),
                    dp(7),
                    dp(10)
                )
            }
        )

        scoreRow.addView(
            card2,
            LinearLayout.LayoutParams(
                0,
                dp(220),
                1f
            ).apply {
                setMargins(
                    dp(7),
                    dp(10),
                    0,
                    dp(10)
                )
            }
        )

        root.addView(scoreRow)

        // تعداد دور
        roundsText = TextView(this)

        roundsText.text =
            "✦  تعداد دورها: 0  ✦"

        roundsText.textSize = 17f
        roundsText.setTypeface(null, Typeface.BOLD)
        roundsText.setTextColor(lightGold)
        roundsText.gravity = Gravity.CENTER

        root.addView(
            roundsText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        // ثبت امتیاز
        val addButton =
            create3DButton(
                "➕  ثبت امتیاز دور جدید",
                green
            )

        addButton.setOnClickListener {
            animateClick(addButton)
            showAddScoreDialog()
        }

        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(65)
            ).apply {
                setMargins(
                    0,
                    dp(8),
                    0,
                    dp(12)
                )
            }
        )

        // تاریخچه
        val historyButton =
            create3DButton(
                "📋  تاریخچه امتیازات",
                Color.rgb(35, 75, 90)
            )

        historyButton.setOnClickListener {
            animateClick(historyButton)
            showHistory()
        }

        root.addView(
            historyButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(65)
            ).apply {
                setMargins(
                    0,
                    0,
                    0,
                    dp(12)
                )
            }
        )

        // بازی جدید
        val newGameButton =
            create3DButton(
                "🔄  بازی جدید",
                red
            )

        newGameButton.setOnClickListener {
            animateClick(newGameButton)
            confirmNewGame()
        }

        root.addView(
            newGameButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(65)
            )
        )

        scroll.addView(root)

        setContentView(scroll)
    }

    // ---------------------------------------------------------
    // کارت سه‌بعدی امتیاز
    // ---------------------------------------------------------

    private fun createScoreCard(
        name: String,
        first: Boolean
    ): LinearLayout {

        val card = LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.gravity = Gravity.CENTER

        card.layoutDirection =
            View.LAYOUT_DIRECTION_RTL

        card.setPadding(
            dp(8),
            dp(10),
            dp(8),
            dp(10)
        )

        val background =
            GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                if (first) {
                    intArrayOf(
                        Color.rgb(255, 250, 225),
                        Color.rgb(236, 245, 239)
                    )
                } else {
                    intArrayOf(
                        Color.rgb(250, 250, 250),
                        Color.rgb(225, 235, 239)
                    )
                }
            )

        background.cornerRadius =
            dp(28).toFloat()

        background.setStroke(
            dp(2),
            if (first) gold
            else Color.rgb(180, 195, 200)
        )

        card.background = background
        card.elevation = dp(14).toFloat()

        val crown = TextView(this)

        crown.text =
            if (first) "♠"
            else "♥"

        crown.textSize = 25f
        crown.gravity = Gravity.CENTER
        crown.setTextColor(
            if (first) gold
            else red
        )

        card.addView(
            crown,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(38)
            )
        )

        val nameText = TextView(this)

        nameText.text = name
        nameText.textSize = 18f
        nameText.setTypeface(
            null,
            Typeface.BOLD
        )

        nameText.gravity = Gravity.CENTER
        nameText.setTextColor(dark)

        card.addView(
            nameText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(42)
            )
        )

        val divider = View(this)

        divider.setBackgroundColor(
            if (first) gold
            else Color.rgb(170, 180, 185)
        )

        card.addView(
            divider,
            LinearLayout.LayoutParams(
                dp(75),
                dp(2)
            )
        )

        val scoreText = TextView(this)

        scoreText.text = "0"
        scoreText.textSize = 50f
        scoreText.setTypeface(
            null,
            Typeface.BOLD
        )

        scoreText.gravity = Gravity.CENTER

        scoreText.setTextColor(
            if (first) {
                Color.rgb(12, 105, 68)
            } else {
                Color.rgb(45, 70, 100)
            }
        )

        card.addView(
            scoreText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(80)
            )
        )

        val caption = TextView(this)

        caption.text = "امتیاز"
        caption.textSize = 13f
        caption.setTextColor(
            Color.rgb(100, 110, 108)
        )
        caption.gravity = Gravity.CENTER

        card.addView(
            caption,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        if (first) {
            team1Name = nameText
            team1Score = scoreText
        } else {
            team2Name = nameText
            team2Score = scoreText
        }

        return card
    }

    // ---------------------------------------------------------
    // دکمه سه‌بعدی
    // ---------------------------------------------------------

    private fun create3DButton(
        text: String,
        color: Int
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 17f
        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.setTextColor(Color.WHITE)
        button.gravity = Gravity.CENTER

        button.isAllCaps = false

        val darker = darkenColor(color)
        val lighter = lightenColor(color)

        val drawable =
            GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    lighter,
                    color,
                    darker
                )
            )

        drawable.cornerRadius =
            dp(20).toFloat()

        drawable.setStroke(
            dp(1),
            Color.argb(
                120,
                255,
                255,
                255
            )
        )

        button.background = drawable
        button.elevation = dp(10).toFloat()

        return button
    }

    // ---------------------------------------------------------
    // ثبت امتیاز
    // ---------------------------------------------------------

    private fun showAddScoreDialog() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.layoutDirection =
            View.LAYOUT_DIRECTION_RTL

        layout.setPadding(
            dp(28),
            dp(20),
            dp(28),
            dp(5)
        )

        val title = TextView(this)

        title.text =
            "✦  ثبت امتیاز دور جدید  ✦"

        title.textSize = 22f
        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity = Gravity.CENTER
        title.setTextColor(darkGreen)

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        val input1 =
            createScoreInput(
                "امتیاز ${team1Name.text}"
            )

        val input2 =
            createScoreInput(
                "امتیاز ${team2Name.text}"
            )

        layout.addView(
            input1,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            ).apply {
                setMargins(
                    0,
                    dp(5),
                    0,
                    dp(12)
                )
            }
        )

        layout.addView(
            input2,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(
                    "ثبت امتیاز",
                    null
                )
                .setNegativeButton(
                    "انصراف",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val positive =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            positive.setTextColor(green)
            positive.setTypeface(
                null,
                Typeface.BOLD
            )

            positive.setOnClickListener {

                val s1 =
                    input1.text.toString()
                        .trim()
                        .toIntOrNull()

                val s2 =
                    input2.text.toString()
                        .trim()
                        .toIntOrNull()

                if (s1 == null) {
                    input1.error =
                        "امتیاز را وارد کنید"
                    input1.requestFocus()
                    return@setOnClickListener
                }

                if (s2 == null) {
                    input2.error =
                        "امتیاز را وارد کنید"
                    input2.requestFocus()
                    return@setOnClickListener
                }

                history.add(
                    Pair(s1, s2)
                )

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
    // کادر امتیاز
    // ---------------------------------------------------------

    private fun createScoreInput(
        hintText: String
    ): EditText {

        val input = EditText(this)

        input.hint = hintText
        input.textSize = 17f
        input.gravity =
            Gravity.RIGHT or
                    Gravity.CENTER_VERTICAL

        input.layoutDirection =
            View.LAYOUT_DIRECTION_RTL

        input.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_SIGNED

        input.setSingleLine(true)

        input.setPadding(
            dp(18),
            0,
            dp(18),
            0
        )

        val bg = GradientDrawable()

        bg.setColor(
            Color.rgb(246, 250, 248)
        )

        bg.cornerRadius =
            dp(17).toFloat()

        bg.setStroke(
            dp(2),
            Color.rgb(205, 220, 212)
        )

        input.background = bg
        input.elevation = dp(4).toFloat()

        return input
    }

    // ---------------------------------------------------------
    // بروزرسانی امتیازات
    // ---------------------------------------------------------

    private fun updateScores() {

        team1Score.text =
            score1.toString()

        team2Score.text =
            score2.toString()

        roundsText.text =
            "✦  تعداد دورها: $roundCount  ✦"

        // انیمیشن تغییر امتیاز
        animateScore(team1Score)
        animateScore(team2Score)
    }

    // ---------------------------------------------------------
    // انیمیشن امتیاز
    // ---------------------------------------------------------

    private fun animateScore(view: View) {

        val animation =
            ScaleAnimation(
                1f,
                1.12f,
                1f,
                1.12f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

        animation.duration = 180
        animation.repeatCount = 1
        animation.repeatMode =
            Animation.REVERSE

        view.startAnimation(animation)
    }

    // ---------------------------------------------------------
    // تاریخچه
    // ---------------------------------------------------------

    private fun showHistory() {

        if (history.isEmpty()) {

            AlertDialog.Builder(this)
                .setTitle("📋 تاریخچه")
                .setMessage(
                    "هنوز هیچ دوری ثبت نشده است."
                )
                .setPositiveButton(
                    "باشه",
                    null
                )
                .show()

            return
        }

        val team1 =
            team1Name.text.toString()

        val team2 =
            team2Name.text.toString()

        val text =
            StringBuilder()

        for (i in history.indices) {

            val item = history[i]

            text.append(
                "🏆 دور ${i + 1}\n"
            )

            text.append(
                "$team1  :  ${item.first}\n"
            )

            text.append(
                "$team2  :  ${item.second}\n"
            )

            text.append(
                "━━━━━━━━━━━━━━\n"
            )
        }

        AlertDialog.Builder(this)
            .setTitle("📋 تاریخچه بازی")
            .setMessage(text.toString())
            .setPositiveButton(
                "بستن",
                null
            )
            .show()
    }

    // ---------------------------------------------------------
    // بازی جدید
    // ---------------------------------------------------------

    private fun confirmNewGame() {

        AlertDialog.Builder(this)
            .setTitle("🔄 بازی جدید")
            .setMessage(
                "امتیازات فعلی پاک می‌شوند.\n\n" +
                        "آیا می‌خواهید بازی جدید شروع کنید؟"
            )
            .setNegativeButton(
                "انصراف",
                null
            )
            .setPositiveButton(
                "بله، شروع کن"
            ) { _, _ ->

                score1 = 0
                score2 = 0
                roundCount = 0

                history.clear()

                showTeamNamesDialog()
            }
            .show()
    }

    // ---------------------------------------------------------
    // انیمیشن کلیک
    // ---------------------------------------------------------

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
    // ابزار رنگ
    // ---------------------------------------------------------

    private fun darkenColor(
        color: Int
    ): Int {

        val r =
            (Color.red(color) * 0.72f)
                .toInt()

        val g =
            (Color.green(color) * 0.72f)
                .toInt()

        val b =
            (Color.blue(color) * 0.72f)
                .toInt()

        return Color.rgb(r, g, b)
    }

    private fun lightenColor(
        color: Int
    ): Int {

        val r =
            (Color.red(color) +
                    255) / 2

        val g =
            (Color.green(color) +
                    255) / 2

        val b =
            (Color.blue(color) +
                    255) / 2

        return Color.rgb(r, g, b)
    }

    // ---------------------------------------------------------
    // تبدیل dp
    // ---------------------------------------------------------

    private fun dp(value: Int): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }
}
