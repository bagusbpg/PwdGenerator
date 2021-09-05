package com.example.pwdgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.pwdgenerator.databinding.ActivityMainBinding
import java.security.SecureRandom
import java.util.Calendar
import kotlin.streams.asSequence

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    companion object {
        private const val STATE_RESULT = "state_result"
    }

    private fun generator(charPool: List<Any>, length: Int): String {
        val random = SecureRandom()
        val calendar = Calendar.getInstance().timeInMillis
        random.setSeed(calendar)

        val source = charPool.joinToString(separator = "", prefix = "", postfix = "")

        return random.ints(length.toLong(), 0, source.length)
            .asSequence()
            .map { source[it] }
            .joinToString(separator = "", prefix = "", postfix = "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lengthList = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        val adapter =  ArrayAdapter(
            this,
            R.layout.custom_spinner,
            lengthList
        )
        binding.inputLength.adapter = adapter

        binding.inputLength.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.generateButton.setOnClickListener(this@MainActivity)
                binding.copyButton.setOnClickListener(this@MainActivity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        if (savedInstanceState != null) {
            val result = savedInstanceState.getString(STATE_RESULT)
            binding.generatedPassword.text = result
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val about = Intent(this@MainActivity, AboutMe::class.java)
        startActivity(about)
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_RESULT, binding.generatedPassword.text.toString())
    }

    override fun onClick(v: View?) {
        val symbol = "~`!@#\$%^&*()-_+={[]}|\\:;\"'<,.>?/"
        val digit: CharRange = ('0'..'9')
        val lowercase: CharRange = ('a'..'z' )
        val uppercase: CharRange = ('A'..'Z')
        val similar = "`10IQl"
        val ambiguous = "{}[]()/\\'\"`~,;:.<>"
        val charPool: MutableList<Any> = mutableListOf()
        val length = binding.inputLength.selectedItem.toString().toInt()
        val alert = "At least one list of special characters, numbers, or letters must be used"
        var flag = 0

        if (v?.id == R.id.generateButton) {
            if (binding.symbolIncluded.isChecked) {
                charPool.addAll(symbol.toList())
                flag = 1
            }
            if (binding.numberIncluded.isChecked) {
                charPool.addAll(digit.toList())
                flag = 1
            }
            if (binding.lowercaseIncluded.isChecked) {
                charPool.addAll(lowercase.toList())
                flag = 1
            }
            if (binding.uppercaseIncluded.isChecked) {
                charPool.addAll(uppercase.toList())
                flag = 1
            }
            if (binding.similarExcluded.isChecked) {
                charPool.removeAll(similar.toList())
            }
            if (binding.ambiguousExcluded.isChecked) {
                charPool.removeAll(ambiguous.toList())
            }
            binding.generatedPassword.text = if (flag == 1) generator(charPool, length) else alert
        }

        if (v?.id == R.id.copyButton) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", binding.generatedPassword.text)

            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Password is successfully copied!", Toast.LENGTH_LONG).show()
        }
    }
}