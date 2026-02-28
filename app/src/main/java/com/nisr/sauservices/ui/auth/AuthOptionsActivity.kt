package com.nisr.sauservices.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class AuthOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE") ?: "shopkeeper"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Welcome ${role.replace("_", " ").capitalize()}"
            textSize = 24f
            setTextColor(Color.parseColor("#2E7D6B"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 80)
            gravity = Gravity.CENTER
        }
        root.addView(title)

        val circlesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        circlesLayout.addView(createOptionCircle("Sign In", Color.parseColor("#2E7D6B")) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("ROLE", role)
            startActivity(intent)
        })

        circlesLayout.addView(createOptionCircle("Sign Up", Color.parseColor("#2E7D6B")) {
            val intent = when (role) {
                "shopkeeper" -> Intent(this, ShopkeeperRegisterActivity::class.java)
                "service_worker" -> Intent(this, ServiceWorkerRegisterActivity::class.java)
                else -> Intent(this, DeliveryPartnerRegisterActivity::class.java)
            }
            intent.putExtra("ROLE", role)
            startActivity(intent)
        })

        circlesLayout.addView(createOptionCircle("Forgot PW", Color.parseColor("#E57373")) {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        })

        root.addView(circlesLayout)
        setContentView(root)
    }

    private fun createOptionCircle(label: String, color: Int, onClick: () -> Unit): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val size = (90 * resources.displayMetrics.density).toInt()
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size)
            radius = size / 2f
            cardElevation = 8 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
            strokeWidth = (2 * resources.displayMetrics.density).toInt()
            strokeColor = color
            
            setOnClickListener { onClick() }
        }

        val textInside = TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            text = label.split(" ").joinToString("\n")
            textSize = 12f
            setTextColor(color)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        card.addView(textInside)

        container.addView(card)
        return container
    }
}
