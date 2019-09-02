package com.booleanull.calculator

import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var signsAll: List<String>
    private lateinit var signs: List<String>

    var answer = false
    var sign = false

    var lastFlag = false
    var lastSign: String = ""
    var lastNumber: Float = 0f

    private val clearListener = {
        scroll_result.visibility = View.GONE
        text_result.text = ""
        text_task.text = ""

        sign = false
        answer = false
    }

    private val answerListener = {
        if (answer) {
            val value = text_result.text
            clearListener.invoke()
            text_task.text = value
            answer = false
        }
    }

    private val scrollUpdateListener = {
        scrollView.viewTreeObserver
            .addOnGlobalLayoutListener {
                scrollView.post { scrollView.fullScroll(View.FOCUS_RIGHT) }
            }
    }

    private val numberListener = View.OnClickListener {
        if (answer) {
            clearListener.invoke()
            text_task.text = (it as TextView).text
        } else {
            text_task.text = getString(R.string.placeholder, text_task.text, (it as TextView).text)
        }

        answer = false
        lastFlag = false

        scrollUpdateListener.invoke()
    }

    private val signListener = View.OnClickListener {
        answerListener.invoke()

        if (!TextUtils.isEmpty(text_task.text)) {
            if (!signsAll.contains(text_task.text.last().toString())) {
                if (sign) {
                    text_task.text = getCheckedNullFractional(makeResult(text_task.text.toString()))
                }
            }
        }

        sign = true
        lastFlag = false

        if (!TextUtils.isEmpty(text_task.text)) {
            if (text_task.text.last() != '.') {
                if (!signs.contains(text_task.text.last().toString())) {
                    text_task.text =
                        getString(R.string.placeholder, text_task.text, (it as TextView).text)
                } else {
                    text_task.text = text_task.text.toString()
                        .replace(text_task.text.last().toString(), (it as TextView).text.toString())
                }
            }

            scrollUpdateListener.invoke()
        } else if ((it as TextView).text == "–") {
            text_task.text = "-"
        }
    }

    private val pointListener = View.OnClickListener {
        var isCorrect = true

        answerListener.invoke()

        if (!TextUtils.isEmpty(text_task.text)) {
            if (!signsAll.contains(text_task.text.last().toString())) {
                for (i in text_task.text.reversed()) {
                    if (signsAll.contains(i.toString())) {
                        isCorrect = i != ','
                        break
                    }
                }

                if (isCorrect) {
                    text_task.text =
                        getString(R.string.placeholder, text_task.text, '.')
                }
            }

            lastFlag = false
            scrollUpdateListener.invoke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signsAll = resources.getStringArray(R.array.signs_all).asList()
        signs = resources.getStringArray(R.array.signs).asList()

        initListeners()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("answer", answer)
        outState.putBoolean("sign", sign)
        outState.putBoolean("lastFlag", lastFlag)
        outState.putString("lastSign", lastSign)
        outState.putFloat("lastNumber", lastNumber)
        outState.putString("task", text_task.text.toString())
        outState.putString("result", text_result.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        answer = savedInstanceState.getBoolean("answer")
        sign = savedInstanceState.getBoolean("sign")
        lastFlag = savedInstanceState.getBoolean("lastFlag")
        lastSign = savedInstanceState.getString("lastSign", "")
        lastNumber = savedInstanceState.getFloat("lastNumber")
        text_task.text = savedInstanceState.getString("task")
        text_result.text = savedInstanceState.getString("result")
    }

    private fun initListeners() {
        text_one.setOnClickListener(numberListener)
        text_two.setOnClickListener(numberListener)
        text_three.setOnClickListener(numberListener)
        text_four.setOnClickListener(numberListener)
        text_five.setOnClickListener(numberListener)
        text_six.setOnClickListener(numberListener)
        text_seven.setOnClickListener(numberListener)
        text_eight.setOnClickListener(numberListener)
        text_nine.setOnClickListener(numberListener)
        text_zero.setOnClickListener(numberListener)
        text_plus.setOnClickListener(signListener)
        text_minus.setOnClickListener(signListener)
        text_division.setOnClickListener(signListener)
        text_multiply.setOnClickListener(signListener)
        text_point.setOnClickListener(pointListener)

        text_equally.setOnClickListener {
            if (!signsAll.contains(text_task.text.last().toString())) {
                if (lastFlag) {
                    text_task.text = getString(
                        R.string.placeholder_result,
                        text_result.text.toString(), lastSign, getCheckedNullFractional(lastNumber)
                    )
                    showResult(getCheckedNullFractional(makeResult(text_result.text.toString() + lastSign + lastNumber.toString())))
                } else {
                    showResult(getCheckedNullFractional(makeResult(text_task.text.toString())))
                }
            } else {
                showResult(
                    getCheckedNullFractional(
                        makeResult(
                            text_task.text.toString().substring(
                                0,
                                text_task.text.length - 1
                            )
                        )
                    )
                )
            }
        }

        text_ac.setOnClickListener {
            clearListener.invoke()
        }

        text_percent.setOnClickListener {
            if (!signsAll.contains(text_task.text.last().toString())) {
                showResult(getCheckedNullFractional(makeResult(makeResult(text_task.text.toString()).toString() + "÷100")))
            } else {
                showResult(
                    getCheckedNullFractional(
                        makeResult(
                            makeResult(
                                text_task.text.toString().substring(
                                    0,
                                    text_task.text.length - 1
                                )
                            ).toString() + "÷100"
                        )
                    )
                )
            }
            text_task.text = text_result.text
        }

        text_swap.setOnClickListener {
            if (!signsAll.contains(text_task.text.last().toString())) {
                if (text_task.text.toString().first() == '-') {
                    showResult(
                        getCheckedNullFractional(
                            makeResult(
                                makeResult(
                                    text_task.text.toString().substring(
                                        1,
                                        text_task.text.length
                                    )
                                ).toString()
                            )
                        )
                    )
                } else {
                    showResult(getCheckedNullFractional(makeResult(makeResult("-" + text_task.text.toString()).toString())))
                }
            } else {
                if (text_task.text.toString().first() == '-') {
                    showResult(
                        getCheckedNullFractional(
                            makeResult(
                                makeResult(
                                    text_task.text.toString().substring(
                                        1,
                                        text_task.text.length - 1
                                    )
                                ).toString()
                            )
                        )
                    )
                } else {
                    showResult(
                        getCheckedNullFractional(
                            makeResult(
                                makeResult(
                                    "-" +
                                            text_task.text.toString().substring(
                                                0,
                                                text_task.text.length - 1
                                            )
                                ).toString()
                            )
                        )
                    )
                }
            }
            text_task.text = text_result.text
        }
    }

    private fun makeResult(task: String): Float {
        val numbers = task.split("+", "–", "×", "÷")
        if (numbers.size == 1) return numbers[0].toFloat()

        lastNumber = numbers[1].toFloat()
        lastFlag = true
        when {
            task.contains("+") -> {
                lastSign = "+"
                return numbers[0].toFloat() + numbers[1].toFloat()
            }
            task.contains("–") -> {
                lastSign = "–"
                return numbers[0].toFloat() - numbers[1].toFloat()
            }
            task.contains("×") -> {
                lastSign = "×"
                return numbers[0].toFloat() * numbers[1].toFloat()
            }
            task.contains("÷") -> {
                lastSign = "÷"
                return if (numbers[1].toFloat() == 0f) 0f else numbers[0].toFloat() / numbers[1].toFloat()
            }
        }
        return 0f
    }

    private fun getCheckedNullFractional(value: Float): String {
        val string = value.toString()
        val substring = string.substring(string.length - 2, string.length)
        return if (substring == ".0") {
            string.substring(0, string.length - 2)
        } else {
            string
        }
    }

    private fun showResult(result: String) {
        text_result.text = result
        scroll_result.visibility = View.VISIBLE
        answer = true
    }
}
