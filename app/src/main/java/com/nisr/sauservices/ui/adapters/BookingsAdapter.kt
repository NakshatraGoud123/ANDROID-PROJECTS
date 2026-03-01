package com.nisr.sauservices.ui.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nisr.sauservices.data.model.Booking

class BookingsAdapter(
    private var bookings: List<Booking>,
    private val onStatusChange: (Booking) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(NAME_ID)
        val serviceText: TextView = view.findViewById(SERVICE_ID)
        val addressText: TextView = view.findViewById(ADDRESS_ID)
        val timeText: TextView = view.findViewById(TIME_ID)
        val statusButton: MaterialButton = view.findViewById(BUTTON_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
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

        val nameTxt = TextView(context).apply { 
            id = NAME_ID
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD) 
        }
        val serviceTxt = TextView(context).apply { 
            id = SERVICE_ID
            textSize = 16f
            setTextColor(Color.parseColor("#2E7D6B")) 
        }
        val addressTxt = TextView(context).apply { 
            id = ADDRESS_ID
            textSize = 14f
            setTextColor(Color.GRAY) 
        }
        val timeTxt = TextView(context).apply { 
            id = TIME_ID
            textSize = 14f
            setTextColor(Color.GRAY) 
        }
        val btn = MaterialButton(context).apply {
            id = BUTTON_ID
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.END; topMargin = 16 }
            cornerRadius = (24 * context.resources.displayMetrics.density).toInt()
            setBackgroundColor(Color.parseColor("#2E7D6B"))
        }

        layout.addView(nameTxt)
        layout.addView(serviceTxt)
        layout.addView(addressTxt)
        layout.addView(timeTxt)
        layout.addView(btn)
        cardView.addView(layout)

        return BookingViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.nameText.text = booking.customerName
        holder.serviceText.text = booking.serviceType
        holder.addressText.text = booking.address
        holder.timeText.text = "Slot: ${booking.timeSlot}"
        holder.statusButton.text = booking.status

        holder.statusButton.setOnClickListener {
            val nextStatus = when (booking.status) {
                "Assigned" -> "On The Way"
                "On The Way" -> "Started"
                "Started" -> "Completed"
                else -> "Completed"
            }
            if (nextStatus != booking.status) {
                booking.status = nextStatus
                notifyItemChanged(position)
                onStatusChange(booking)
            }
        }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    companion object {
        private const val NAME_ID = 101
        private const val SERVICE_ID = 102
        private const val ADDRESS_ID = 103
        private const val TIME_ID = 104
        private const val BUTTON_ID = 105
    }
}
