package com.nisr.sauservices.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.dashboard.ShopkeeperDashboardActivity

class ShopkeeperRegisterActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor("#F5F7F6"))
            isFillViewport = true
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "Shopkeeper Registration"
            textSize = 24f
            setTextColor(Color.parseColor("#2E7D6B"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 48)
        }

        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radius = 16 * resources.displayMetrics.density
            cardElevation = 8 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
        }

        val formLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val nameInput = createInputLayout("Full Name")
        val phoneInput = createInputLayout("Phone Number")
        val emailInput = createInputLayout("Email")
        val passwordInput = createInputLayout("Password", true)
        val confirmPasswordInput = createInputLayout("Confirm Password", true)
        val shopNameInput = createInputLayout("Shop Name")
        val shopAddressInput = createInputLayout("Shop Address")

        val registerBtn = MaterialButton(this).apply {
            text = "Register"
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 48
            }
            cornerRadius = (24 * resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
            setTextColor(Color.WHITE)
        }

        registerBtn.setOnClickListener {
            if (validate(nameInput, phoneInput, emailInput, passwordInput, confirmPasswordInput, shopNameInput, shopAddressInput)) {
                val user = User(
                    id = System.currentTimeMillis().toString(),
                    role = "shopkeeper",
                    name = nameInput.editText?.text.toString(),
                    phone = phoneInput.editText?.text.toString(),
                    email = emailInput.editText?.text.toString(),
                    extraFields = mapOf(
                        "shopName" to shopNameInput.editText?.text.toString(),
                        "shopAddress" to shopAddressInput.editText?.text.toString()
                    )
                )
                userRepository.registerUser(user)
                val intent = Intent(this, ShopkeeperDashboardActivity::class.java)
                intent.putExtra("ROLE", "shopkeeper")
                startActivity(intent)
                finish()
            }
        }

        formLayout.addView(nameInput)
        formLayout.addView(phoneInput)
        formLayout.addView(emailInput)
        formLayout.addView(passwordInput)
        formLayout.addView(confirmPasswordInput)
        formLayout.addView(shopNameInput)
        formLayout.addView(shopAddressInput)
        formLayout.addView(registerBtn)

        card.addView(formLayout)
        container.addView(title)
        container.addView(card)
        root.addView(container)

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

    private fun validate(vararg inputs: TextInputLayout): Boolean {
        var isValid = true
        inputs.forEach { it.error = null }

        if (inputs[0].editText?.text.isNullOrBlank()) { inputs[0].error = "Required"; isValid = false }
        val phone = inputs[1].editText?.text.toString()
        if (phone.length != 10) { inputs[1].error = "Enter 10 digits"; isValid = false }
        val email = inputs[2].editText?.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { inputs[2].error = "Invalid Email"; isValid = false }
        val pass = inputs[3].editText?.text.toString()
        if (pass.length < 6) { inputs[3].error = "Min 6 characters"; isValid = false }
        val confirmPass = inputs[4].editText?.text.toString()
        if (pass != confirmPass) { inputs[4].error = "Passwords do not match"; isValid = false }
        if (inputs[5].editText?.text.isNullOrBlank()) { inputs[5].error = "Required"; isValid = false }
        if (inputs[6].editText?.text.isNullOrBlank()) { inputs[6].error = "Required"; isValid = false }

        return isValid
    }
}
