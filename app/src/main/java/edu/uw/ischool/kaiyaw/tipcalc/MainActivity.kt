package edu.uw.ischool.kaiyaw.tipcalc

import android.icu.text.NumberFormat
import android.icu.util.ULocale
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private var current = BigDecimal(0)
    private var tipFactor = 1.15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtAmount = findViewById<EditText>(R.id.edtAmount)
        val spnTip = findViewById<Spinner>(R.id.spnTip)
        val btnTip = findViewById<Button>(R.id.btnTip)

        val usdFormatter = NumberFormat.getCurrencyInstance(ULocale.US)

        // Init Amount EditText
        edtAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                edtAmount.removeTextChangedListener(this)

                btnTip.isEnabled = !s.isNullOrEmpty()

                val cleanString = s?.replace("""[$,.]""".toRegex(), "") ?: ""
                if (cleanString.isEmpty()) {
                    current = BigDecimal(0)
                    btnTip.isEnabled = false
                    edtAmount.addTextChangedListener(this)
                    return
                }
                if (cleanString == "00") {
                    edtAmount.setText("")
                    current = BigDecimal(0)
                    btnTip.isEnabled = false
                    edtAmount.addTextChangedListener(this)
                    return
                }
                current = cleanString.toBigDecimal()
                val formatted = usdFormatter.format(current.divide(BigDecimal(100)))

                edtAmount.setText(formatted)
                edtAmount.setSelection(formatted.length)

                edtAmount.addTextChangedListener(this)
            }
        })

        // Init Tip Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.arr_tips,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnTip.adapter = it
        }
        spnTip.setSelection(1)
        spnTip.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tipFactor = when (position) {
                    0 -> 1.10
                    1 -> 1.15
                    2 -> 1.18
                    3 -> 1.20
                    else -> 1.15
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spnTip.setSelection(1)
            }
        }

        // Init Tip Button
        btnTip.setOnClickListener {
            val total = usdFormatter.format(
                (current * BigDecimal(tipFactor)).divide(BigDecimal(100))
            )
            Toast.makeText(this, total, Toast.LENGTH_SHORT).show()
        }
    }
}