package com.nisr.sauservices.ui.auth

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
            setPadding(48, 48, 48, 48)
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Reset Password"
            textSize = 28f
            setTextColor(Color.parseColor("#2E7D6B"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 64)
            gravity = Gravity.CENTER
        }

        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radius = 16 * resources.displayMetrics.density
            cardElevation = 8 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
        }

        val formLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 48)
        }

        val emailInput = TextInputLayout(this).apply {
            hint = "Enter your Email"
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            setBoxCornerRadii(8f, 8f, 8f, 8f)
        }
        val editText = TextInputEditText(this)
        emailInput.addView(editText)

        val resetBtn = MaterialButton(this).apply {
            text = "Send Reset Link"
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 48
            }
            cornerRadius = (24 * resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
            setTextColor(Color.WHITE)
        }

        resetBtn.setOnClickListener {
            val email = editText.text.toString()
            if (email.isNotEmpty()) {
                Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
                finish()
            } else {
                emailInput.error = "Please enter your email"
            }
        }

        formLayout.addView(emailInput)
        formLayout.addView(resetBtn)
        card.addView(formLayout)

        root.addView(title)
        root.addView(card)

        setContentView(root)
    }
}
