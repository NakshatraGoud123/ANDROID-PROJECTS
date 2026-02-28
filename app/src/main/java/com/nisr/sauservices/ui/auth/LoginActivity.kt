package com.nisr.sauservices.ui.auth

import android.content.Intent
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
import com.nisr.sauservices.ui.dashboard.ShopkeeperDashboardActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
            setPadding(48, 48, 48, 48)
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Sign In"
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

        val emailInput = createInputLayout("Email")
        val passwordInput = createInputLayout("Password", true)

        val loginBtn = MaterialButton(this).apply {
            text = "Login"
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 48
            }
            cornerRadius = (24 * resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
            setTextColor(Color.WHITE)
        }

        loginBtn.setOnClickListener {
            // Mock login - navigate to Shopkeeper as default or based on logic
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ShopkeeperDashboardActivity::class.java)
            intent.putExtra("ROLE", "shopkeeper")
            startActivity(intent)
            finish()
        }

        formLayout.addView(emailInput)
        formLayout.addView(passwordInput)
        formLayout.addView(loginBtn)
        card.addView(formLayout)

        root.addView(title)
        root.addView(card)

        setContentView(root)
    }

    private fun createInputLayout(hint: String, isPassword: Boolean = false): TextInputLayout {
        val layout = TextInputLayout(this).apply {
            this.hint = hint
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 16
            }
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            setBoxCornerRadii(8f, 8f, 8f, 8f)
            if (isPassword) endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        }
        val editText = TextInputEditText(this)
        if (isPassword) editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(editText)
        return layout
    }
}
