package com.booleanull.calculator

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var signsAll: List<String>

    var answer = false

    private val clearListener = {
        text_result.visibility = View.GONE
        text_result.text = ""
        text_task.text = ""
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

        scrollUpdateListener.invoke()
    }

    private val signListener = View.OnClickListener {
        answerListener.invoke()

        if(!TextUtils.isEmpty(text_task.text)) {
            if (!signsAll.contains(text_task.text.last().toString())) {
                text_task.text =
                    getString(R.string.placeholder, text_task.text, (it as TextView).text)
            }

            scrollUpdateListener.invoke()
        }
    }

    private val pointListener = View.OnClickListener {
        var isCorrect = true

        answerListener.invoke()

        if(!TextUtils.isEmpty(text_task.text)) {
            if (!signsAll.contains(text_task.text.last().toString())) {
                for (i in text_task.text.reversed()) {
                    if (signsAll.contains(i.toString())) {
                        isCorrect = i != ','
                        break
                    }
                }

                if (isCorrect) {
                    text_task.text =
                        getString(R.string.placeholder, text_task.text, (it as TextView).text)
                }
            }

            scrollUpdateListener.invoke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signsAll = resources.getStringArray(R.array.signs_all).asList()

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
            text_result.text = "50"
            text_result.visibility = View.VISIBLE
            answer = true
        }

        text_ac.setOnClickListener {
            clearListener.invoke()
        }
    }
}
