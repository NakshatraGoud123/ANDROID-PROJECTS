package com.nisr.sauservices.ui.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.data.model.Delivery

class DeliveryAdapter(
    private var deliveries: List<Delivery>,
    private val onStatusChange: (Delivery) -> Unit
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    class DeliveryViewHolder(val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView) {
        val nameText: TextView = cardView.findViewById(1)
        val pickupText: TextView = cardView.findViewById(2)
        val dropText: TextView = cardView.findViewById(3)
        val distanceText: TextView = cardView.findViewById(4)
        val statusButton: MaterialButton = cardView.findViewById(5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val context = parent.context
        val cardView = MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 8, 16, 8) }
            radius = 16f * context.resources.displayMetrics.density
            cardElevation = 8f * context.resources.displayMetrics.density
            setCardBackgroundColor(Color.WHITE)
            setContentPadding(32, 32, 32, 32)
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val nameTxt = TextView(context).apply { id = 1; textSize = 18f; setTextColor(Color.BLACK); setTypeface(null, Typeface.BOLD) }
        val pickupTxt = TextView(context).apply { id = 2; textSize = 14f; setTextColor(Color.GRAY) }
        val dropTxt = TextView(context).apply { id = 3; textSize = 14f; setTextColor(Color.GRAY) }
        val distanceTxt = TextView(context).apply { id = 4; textSize = 14f; setTextColor(Color.parseColor("#2E7D6B")) }
        val btn = MaterialButton(context).apply {
            id = 5
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.END; topMargin = 16 }
            cornerRadius = (24 * context.resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
        }

        layout.addView(nameTxt)
        layout.addView(pickupTxt)
        layout.addView(dropTxt)
        layout.addView(distanceTxt)
        layout.addView(btn)
        cardView.addView(layout)

        return DeliveryViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val delivery = deliveries[position]
        holder.nameText.text = delivery.customerName
        holder.pickupText.text = "From: ${delivery.pickupAddress}"
        holder.dropText.text = "To: ${delivery.dropAddress}"
        holder.distanceText.text = "Distance: ${delivery.distance}"
        holder.statusButton.text = delivery.status

        holder.statusButton.setOnClickListener {
            val nextStatus = when (delivery.status) {
                "Pending" -> "Picked"
                "Picked" -> "On The Way"
                "On The Way" -> "Delivered"
                else -> "Delivered"
            }
            if (nextStatus != delivery.status) {
                delivery.status = nextStatus
                notifyItemChanged(position)
                onStatusChange(delivery)
            }
        }
    }

    override fun getItemCount() = deliveries.size

    fun updateData(newDeliveries: List<Delivery>) {
        deliveries = newDeliveries
        notifyDataSetChanged()
    }
}
