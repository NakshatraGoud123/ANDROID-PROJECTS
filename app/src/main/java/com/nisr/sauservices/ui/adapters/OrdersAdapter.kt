package com.nisr.sauservices.ui.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.data.model.Order

class OrdersAdapter(
    private var orders: List<Order>,
    private val onStatusChange: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView) {
        val idText: TextView = cardView.findViewById(1)
        val nameText: TextView = cardView.findViewById(2)
        val amountText: TextView = cardView.findViewById(3)
        val statusButton: MaterialButton = cardView.findViewById(4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
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

        val idTxt = TextView(context).apply { id = 1; textSize = 14f; setTextColor(Color.GRAY) }
        val nameTxt = TextView(context).apply { id = 2; textSize = 18f; setTextColor(Color.BLACK); setTypeface(null, android.graphics.Typeface.BOLD) }
        val amountTxt = TextView(context).apply { id = 3; textSize = 16f; setTextColor(Color.DKGRAY) }
        val btn = MaterialButton(context).apply {
            id = 4
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.END; topMargin = 16 }
            cornerRadius = (24 * context.resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
        }

        layout.addView(idTxt)
        layout.addView(nameTxt)
        layout.addView(amountTxt)
        layout.addView(btn)
        cardView.addView(layout)

        return OrderViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.idText.text = "Order ID: ${order.orderId}"
        holder.nameText.text = order.customerName
        holder.amountText.text = "Total: ${order.amount}"
        holder.statusButton.text = order.status

        holder.statusButton.setOnClickListener {
            val nextStatus = when (order.status) {
                "Pending" -> "Accepted"
                "Accepted" -> "Completed"
                else -> "Completed"
            }
            if (nextStatus != order.status) {
                order.status = nextStatus
                notifyItemChanged(position)
                onStatusChange(order)
            }
        }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
