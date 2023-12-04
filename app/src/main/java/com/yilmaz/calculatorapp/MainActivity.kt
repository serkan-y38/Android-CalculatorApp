package com.yilmaz.calculatorapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yilmaz.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var addOperation = false
    private var addDecimal = true

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun allClearAction(view: View) {
        binding.operationsTV.text = ""
        binding.resultTV.text = ""
    }

    fun backSpaceAction(view: View) {
        if (binding.operationsTV.length() > 0) binding.operationsTV.text =
            binding.operationsTV.text.subSequence(0, binding.operationsTV.length() - 1)
    }

    fun equalsAction(view: View) {
        binding.resultTV.text = calculateResult()
    }

    @SuppressLint("SetTextI18n")
    fun operationAction(view: View) {
        if (view is Button && addOperation) {
            binding.operationsTV.text = binding.operationsTV.text.toString() + view.text.toString()
            addOperation = false
            addDecimal = true
        }
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == "." && addDecimal) {
                binding.operationsTV.append(view.text)
                addDecimal = false
            } else {
                binding.operationsTV.append(view.text)
            }
            addOperation = true
        }
    }

    private fun calculateResult(): String {
        return if (digitOperators().isEmpty() || timesModDivisionCalculate(digitOperators()).isEmpty())
            ""
        else
            addSubtractCalculate(timesModDivisionCalculate(digitOperators())).toString()
    }

    private fun digitOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in binding.operationsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if (currentDigit != "")
            list.add(currentDigit.toFloat())
        return list
    }

    private fun addSubtractCalculate(list: MutableList<Any>): Float {
        var result = list[0] as Float

        for (i in list.indices) {
            if (list[i] is Char && i != list.lastIndex) {
                val operator = list[i]
                val nextDigit = list[i + 1] as Float
                if (operator == '+') result += nextDigit
                if (operator == '-') result -= nextDigit
            }

        }
        return result
    }

    private fun timesModDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/') || list.contains('%'))
            list = calculateTimesModDivision(list)
        return list
    }

    private fun calculateTimesModDivision(list: MutableList<Any>): MutableList<Any> {

        val newList = mutableListOf<Any>()
        var restartIndex = list.size

        for (i in list.indices) {
            if (list[i] is Char && i != list.lastIndex && i < restartIndex) {

                val operator = list[i]
                val previousDigit = list[i - 1] as Float
                val nextDigit = list[i + 1] as Float

                when (operator) {
                    'x' -> {
                        newList.add(previousDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '/' -> {
                        newList.add(previousDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    '%' -> {
                        newList.add(previousDigit % nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(previousDigit)
                        newList.add(operator)
                    }

                }

            }
            if (i > restartIndex) newList.add(list[i])
        }
        return newList
    }

}