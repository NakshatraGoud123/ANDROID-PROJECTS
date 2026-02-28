package com.nisr.sauservices.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7F6"))
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Choose Your Role"
            textSize = 28f
            setTextColor(Color.parseColor("#2E7D6B"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 60)
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // First row: Customer and Shopkeeper
        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        row1.addView(createRoleCircle("Customer", "customer"))
        row1.addView(createRoleCircle("Shopkeeper", "shopkeeper"))
        root.addView(row1)

        val spacer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (32 * resources.displayMetrics.density).toInt())
        }
        root.addView(spacer)

        // Second row: Worker and Delivery
        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        row2.addView(createRoleCircle("Worker", "service_worker"))
        row2.addView(createRoleCircle("Delivery", "delivery"))
        root.addView(row2)

        setContentView(root)
    }

    private fun createRoleCircle(label: String, role: String): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val size = (90 * resources.displayMetrics.density).toInt()
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size)
            radius = size / 2f
            cardElevation = 12 * resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
            strokeWidth = (2 * resources.displayMetrics.density).toInt()
            strokeColor = Color.parseColor("#2E7D6B")
            
            setOnClickListener {
                val intent = Intent(this@SelectionActivity, AuthOptionsActivity::class.java)
                intent.putExtra("ROLE", role)
                startActivity(intent)
            }
        }

        val icon = ImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(40, 40, 40, 40)
            setColorFilter(Color.parseColor("#2E7D6B"))
            setImageResource(android.R.drawable.ic_menu_myplaces) // Placeholder icon
        }
        card.addView(icon)

        val text = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 16, 0, 0)
            gravity = Gravity.CENTER
        }

        container.addView(card)
        container.addView(text)
        return container
    }
}
