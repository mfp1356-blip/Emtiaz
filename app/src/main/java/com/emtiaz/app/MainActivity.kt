package com.emtiaz.app

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private var team1Score = 0
    private var team2Score = 0
    private var roundNumber = 0

    private lateinit var team1Name: String
    private lateinit var team2Name: String

    private lateinit var scoreText: TextView
    private lateinit var historyLayout: LinearLayout

    private lateinit var readInput: EditText
    private lateinit var opponentInput: EditText
    private lateinit var bidderGroup: RadioGroup

    private val backgroundColor = Color.rgb(15, 12, 30)
    private val cardColor = Color.rgb(40, 34, 62)
    private val purpleColor = Color.rgb(112, 78, 220)
    private val greenColor = Color.rgb(34, 170, 105)
    private val redColor = Color.rgb(205, 55, 70)
    private val normalColor = Color.rgb(30, 30, 35)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStartScreen()
    }

    private fun rootLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(backgroundColor)
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    private fun text(
        value: String,
        size: Float = 18f
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setPadding(8, 8, 8, 8)
        }
    }

    private fun input(hintText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 17f
            setSingleLine(true)
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(18, 0, 18, 0)

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 28f
                setColor(cardColor)
                setStroke(2, Color.argb(50, 255, 255, 255))
            }
        }
    }

    private fun button(
        title: String,
        color: Int,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            elevation = 10f

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 30f
                setColor(color)
                setStroke(2, Color.argb(60, 255, 255, 255))
            }

            setOnClickListener {
                action()
            }
        }
    }

    private fun fullParams(height: Int = 56): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        ).apply {
            setMargins(0, 6, 0, 6)
        }
    }

    private fun showStartScreen() {

        val root = rootLayout()

        root.addView(
            text("♠  امتیاز شلم  ♠", 30f),
            fullParams(70)
        )

        root.addView(
            text("شروع یک بازی جدید", 18f),
            fullParams(45)
        )

        root.addView(
            text("نام چهار بازیکن", 17f),
            fullParams(42)
        )

        val player1 = input("بازیکن ۱")
        val player2 = input("بازیکن ۲")
        val player3 = input("بازیکن ۳")
        val player4 = input("بازیکن ۴")

        root.addView(player1, fullParams())
        root.addView(player2, fullParams())
        root.addView(player3, fullParams())
        root.addView(player4, fullParams())

        root.addView(
            text("نام تیم‌ها", 17f),
            fullParams(42)
        )

        val team1Input = input("تیم اول")
        val team2Input = input("تیم دوم")

        root.addView(team1Input, fullParams())
        root.addView(team2Input, fullParams())

        root.addView(
            text("نوع بازی", 17f),
            fullParams(42)
        )

        val gameType = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val withoutJoker = RadioButton(this).apply {
            id = 101
            text = "بدون جوکر"
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        val withJoker = RadioButton(this).apply {
            id = 102
            text = "با جوکر"
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        gameType.addView(withoutJoker)
        gameType.addView(withJoker)
        gameType.check(101)

        root.addView(
            gameType,
            fullParams(55)
        )

        root.addView(
            button("شروع بازی  ✦", purpleColor) {

                team1Name =
                    if (team1Input.text.toString().trim().isEmpty())
                        "تیم اول"
                    else
                        team1Input.text.toString().trim()

                team2Name =
                    if (team2Input.text.toString().trim().isEmpty())
                        "تیم دوم"
                    else
                        team2Input.text.toString().trim()

                team1Score = 0
                team2Score = 0
                roundNumber = 0

                showGameScreen()
            },
            fullParams(64)
        )

        setContentView(root)
    }

    private fun showGameScreen() {

        val root = rootLayout()

        root.addView(
            text("♠  امتیاز شلم  ♠", 28f),
            fullParams(62)
        )

        scoreText = text(
            "$team1Name: 0     |     $team2Name: 0",
            19f
        )

        scoreText.setBackgroundColor(cardColor)

        root.addView(
            scoreText,
            fullParams(64)
        )

        root.addView(
            text("امتیاز خوانده‌شده", 16f),
            fullParams(38)
        )

        readInput = input("مثلاً ۱۲۰")
        root.addView(
            readInput,
            fullParams()
        )

        root.addView(
            text("امتیاز تیم مقابل", 16f),
            fullParams(38)
        )

        opponentInput = input("مثلاً ۷۰")
        root.addView(
            opponentInput,
            fullParams()
        )

        root.addView(
            text("تیم حاکم / خواننده", 16f),
            fullParams(40)
        )

        bidderGroup = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val team1Radio = RadioButton(this).apply {
            id = 201
            text = team1Name
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        val team2Radio = RadioButton(this).apply {
            id = 202
            text = team2Name
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        bidderGroup.addView(team1Radio)
        bidderGroup.addView(team2Radio)
        bidderGroup.check(201)

        root.addView(
            bidderGroup,
            fullParams(52)
        )

        root.addView(
            text("نوع دست را انتخاب کنید", 16f),
            fullParams(40)
        )

        val typeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val normalButton =
            button("معمولی", normalColor) {
                addRound(RoundType.NORMAL)
            }

        val yasaButton =
            button("یاسا", greenColor) {
                addRound(RoundType.YASA)
            }

        val shelemButton =
            button("شلم", redColor) {
                addRound(RoundType.SHELEM)
            }

        typeRow.addView(
            normalButton,
            LinearLayout.LayoutParams(0, 62, 1f).apply {
                setMargins(3, 3, 3, 3)
            }
        )

        typeRow.addView(
            yasaButton,
            LinearLayout.LayoutParams(0, 62, 1f).apply {
                setMargins(3, 3, 3, 3)
            }
        )

        typeRow.addView(
            shelemButton,
            LinearLayout.LayoutParams(0, 62, 1f).apply {
                setMargins(3, 3, 3, 3)
            }
        )

        root.addView(
            typeRow,
            fullParams(70)
        )

        root.addView(
            text("دست‌های ثبت‌شده", 18f),
            fullParams(44)
        )

        historyLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(historyLayout)
        }

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private enum class RoundType {
        NORMAL,
        YASA,
        SHELEM
    }

    private fun addRound(type: RoundType) {

        val read =
            readInput.text.toString().trim().toIntOrNull()

        val opponent =
            opponentInput.text.toString().trim().toIntOrNull()

        if (read == null || opponent == null || read <= 0 || opponent < 0) {

            Toast.makeText(
                this,
                "لطفاً امتیازها را صحیح وارد کنید",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val bidderTeam =
            if (bidderGroup.checkedRadioButtonId == 201)
                1
            else
                2

        val bidderDelta =
            when (type) {

                RoundType.NORMAL ->
                    read

                RoundType.YASA ->
                    -2 * read

                RoundType.SHELEM ->
                    2 * read
            }

        val finalBidderDelta =
            if (type == RoundType.NORMAL) {

                /*
                 * در حالت معمولی فرض می‌کنیم اگر امتیاز
                 * تیم مقابل ثبت شده باشد، تیم حاکم
                 * به خوانده رسیده است.
                 *
                 * در نسخه نهایی می‌توانیم یک گزینه
                 * «خوانده / نخوانده» هم اضافه کنیم.
                 */
                read

            } else {
                bidderDelta
            }

        val otherDelta = opponent

        if (bidderTeam == 1) {

            team1Score += finalBidderDelta
            team2Score += otherDelta

        } else {

            team2Score += finalBidderDelta
            team1Score += otherDelta
        }

        roundNumber++

        val roundColor =
            when (type) {

                RoundType.NORMAL ->
                    normalColor

                RoundType.YASA ->
                    greenColor

                RoundType.SHELEM ->
                    redColor
            }

        val title =
            when (type) {

                RoundType.NORMAL ->
                    "معمولی"

                RoundType.YASA ->
                    "یاسا"

                RoundType.SHELEM ->
                    "شلم"
            }

        val firstTeam =
            if (bidderTeam == 1)
                team1Name
            else
                team2Name

        val secondTeam =
            if (bidderTeam == 1)
                team2Name
            else
                team1Name

        val firstDelta =
            finalBidderDelta

        val secondDelta =
            otherDelta

        val row = text(
            "دست $roundNumber  •  $title\n" +
                    "$firstTeam: ${formatScore(firstDelta)}   |   " +
                    "$secondTeam: ${formatScore(secondDelta)}",
            15f
        )

        row.setBackgroundColor(roundColor)

        historyLayout.addView(
            row,
            0,
            fullParams(70)
        )

        scoreText.text =
            "$team1Name: ${formatScore(team1Score)}     |     " +
                    "$team2Name: ${formatScore(team2Score)}"

        readInput.text.clear()
        opponentInput.text.clear()

        checkGameOver()
    }

    private fun formatScore(score: Int): String {

        return if (score > 0)
            "+$score"
        else
            score.toString()
    }

    private fun checkGameOver() {

        val gameOver =
            team1Score >= 1200 ||
                    team2Score >= 1200 ||
                    abs(team1Score - team2Score) >= 1200

        if (!gameOver)
            return

        val winner =
            when {

                team1Score > team2Score ->
                    team1Name

                team2Score > team1Score ->
                    team2Name

                else ->
                    "مساوی"
            }

        AlertDialog.Builder(this)
            .setTitle("🏆 پایان بازی")
            .setMessage(
                "برنده: $winner\n\n" +
                        "$team1Name: $team1Score\n" +
                        "$team2Name: $team2Score"
            )
            .setPositiveButton("بازی جدید") { _, _ ->
                showStartScreen()
            }
            .setCancelable(false)
            .show()
    }
}
