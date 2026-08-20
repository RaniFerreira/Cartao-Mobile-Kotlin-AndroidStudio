@file:Suppress("SpellCheckingInspection")

package com.example.mycard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView
    private lateinit var tvCardNumber: TextView
    private lateinit var tvHolderName: TextView
    private lateinit var tvExpiry: TextView
    private lateinit var tvBrandLogo: TextView
    private lateinit var tvCvvBack: TextView

    private lateinit var etCardNumber: EditText
    private lateinit var etHolderName: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etCvv: EditText
    private lateinit var btnSubmit: Button

    private var isFrontShowing = true

    // flags para evitar loop infinito no TextWatcher quando reescrevemos o texto
    private var isEditingCardNumber = false
    private var isEditingExpiry = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupCameraDistance()
        setupCardFlip()
        setupCardNumberMask()
        setupHolderNameWatcher()
        setupExpiryMask()
        setupCvvWatcher()
        setupSubmit()
    }

    private fun bindViews() {
        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)
        tvCardNumber = findViewById(R.id.tvCardNumber)
        tvHolderName = findViewById(R.id.tvHolderName)
        tvExpiry = findViewById(R.id.tvExpiry)
        tvBrandLogo = findViewById(R.id.tvBrandLogo)
        tvCvvBack = findViewById(R.id.tvCvvBack)

        etCardNumber = findViewById(R.id.etCardNumber)
        etHolderName = findViewById(R.id.etHolderName)
        etExpiry = findViewById(R.id.etExpiry)
        etCvv = findViewById(R.id.etCvv)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    // Aumenta a distância da "câmera" para o giro 3D não ficar achatado/distorcido
    private fun setupCameraDistance() {
        val distance = 8000 * resources.displayMetrics.density
        cardFront.cameraDistance = distance
        cardBack.cameraDistance = distance
    }

    // ---------- Desafio 1: girar o cartão ao focar no CVV ----------

    private fun setupCardFlip() {
        etCvv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) flipToBack() else flipToFront()
        }

        val backToFrontListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) flipToFront()
        }
        etCardNumber.onFocusChangeListener = backToFrontListener
        etHolderName.onFocusChangeListener = backToFrontListener
        etExpiry.onFocusChangeListener = backToFrontListener
    }

    private fun flipToBack() {
        if (!isFrontShowing) return
        isFrontShowing = false
        animateFlip(viewOut = cardFront, viewIn = cardBack)
    }

    private fun flipToFront() {
        if (isFrontShowing) return
        isFrontShowing = true
        animateFlip(viewOut = cardBack, viewIn = cardFront)
    }

    // Anima a saída até 90°, troca a visibilidade e entra a outra face de -90° até 0°
    private fun animateFlip(viewOut: View, viewIn: View) {
        val outAnim = ObjectAnimator.ofFloat(viewOut, "rotationY", 0f, 90f)
        outAnim.duration = 150
        outAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewOut.visibility = View.INVISIBLE

                viewIn.rotationY = -90f
                viewIn.visibility = View.VISIBLE
                val inAnim = ObjectAnimator.ofFloat(viewIn, "rotationY", -90f, 0f)
                inAnim.duration = 150
                inAnim.start()
            }
        })
        outAnim.start()
    }

    // ---------- Máscara do número do cartão + Desafio 2 (bandeira) ----------

    private fun setupCardNumberMask() {
        etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditingCardNumber) return
                isEditingCardNumber = true

                val digits = s.toString().filter { it.isDigit() }.take(16)
                val masked = digits.chunked(4).joinToString(" ")

                etCardNumber.setText(masked)
                etCardNumber.setSelection(masked.length)

                updateCardNumberDisplay(digits)
                updateBrand(digits)

                isEditingCardNumber = false
            }
        })
    }

    private fun updateCardNumberDisplay(digits: String) {
        val padded = digits.padEnd(16, '•')
        tvCardNumber.text = padded.chunked(4).joinToString(" ")
    }

    // Identifica a bandeira pelos primeiros dígitos e atualiza o "logo" no cartão
    private fun updateBrand(digits: String) {
        tvBrandLogo.text = when {
            digits.isEmpty() -> "•••"
            digits.startsWith("4") -> "VISA"
            digits.startsWith("5") -> "MASTERCARD"
            digits.startsWith("34") || digits.startsWith("37") -> "AMEX"
            else -> "CARTÃO"
        }
    }

    // ---------- Nome do titular ----------

    private fun setupHolderNameWatcher() {
        etHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                tvHolderName.text = name.ifBlank { "NOME DO TITULAR" }.uppercase()
            }
        })
    }

    // ---------- Máscara da validade MM/AA ----------

    private fun setupExpiryMask() {
        etExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditingExpiry) return
                isEditingExpiry = true

                val digits = s.toString().filter { it.isDigit() }.take(4)
                val masked = if (digits.length > 2) {
                    "${digits.substring(0, 2)}/${digits.substring(2)}"
                } else digits

                etExpiry.setText(masked)
                etExpiry.setSelection(masked.length)

                tvExpiry.text = masked.ifEmpty { "MM/AA" }

                isEditingExpiry = false
            }
        })
    }

    // ---------- CVV (mostrado no verso do cartão) ----------

    private fun setupCvvWatcher() {
        etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cvv = s.toString()
                tvCvvBack.text = cvv.ifEmpty { "•••" }
            }
        })
    }

    // ---------- Validação final ----------

    private fun setupSubmit() {
        btnSubmit.setOnClickListener {
            val digits = etCardNumber.text.toString().filter { it.isDigit() }
            val name = etHolderName.text.toString().trim()

            when {
                digits.length != 16 -> {
                    etCardNumber.error = "O número do cartão deve ter 16 dígitos"
                    etCardNumber.requestFocus()
                }
                name.length < 3 -> {
                    etHolderName.error = "O nome deve ter ao menos 3 caracteres"
                    etHolderName.requestFocus()
                }
                else -> {
                    Toast.makeText(this, "Dados válidos!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}