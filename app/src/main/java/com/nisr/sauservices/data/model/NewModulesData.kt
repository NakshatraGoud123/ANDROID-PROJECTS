package com.nisr.sauservices.data.model

object NewModulesData {
    val essentialSupplies = listOf(
        SupplyCategory("esc_1", "Electronics & Mobile Accessories", listOf(
            SupplySubcategory("ess_1", "Chargers", "₹150–₹400", "Electronics"),
            SupplySubcategory("ess_2", "USB Cables", "₹80–₹200", "Electronics"),
            SupplySubcategory("ess_3", "Earphones/Headphones", "₹150–₹500", "Electronics"),
            SupplySubcategory("ess_4", "Power Banks", "₹600–₹1500", "Electronics"),
            SupplySubcategory("ess_5", "Mobile Covers", "₹50–₹200", "Electronics"),
            SupplySubcategory("ess_6", "Smart LED Bulbs", "₹250–₹500", "Electronics")
        )),
        SupplyCategory("esc_2", "Home Improvement & Tools", listOf(
            SupplySubcategory("ess_7", "Screwdriver Sets", "₹80–₹250", "Tools"),
            SupplySubcategory("ess_8", "Small Tool Kits", "₹150–₹500", "Tools"),
            SupplySubcategory("ess_9", "LED Bulbs", "₹50–₹150", "Home"),
            SupplySubcategory("ess_10", "Door Locks", "₹200–₹500", "Home"),
            SupplySubcategory("ess_11", "Adhesive Tapes", "₹20–₹80", "Home"),
            SupplySubcategory("ess_12", "Extension Boards", "₹150–₹350", "Home")
        )),
        SupplyCategory("esc_3", "Stationery & Office Needs", listOf(
            SupplySubcategory("ess_13", "Pens", "₹5–₹30", "Stationery"),
            SupplySubcategory("ess_14", "Notebooks", "₹20–₹150", "Stationery"),
            SupplySubcategory("ess_15", "Files & Folders", "₹10–₹40", "Stationery"),
            SupplySubcategory("ess_16", "Markers", "₹20–₹50", "Stationery"),
            SupplySubcategory("ess_17", "Printer Paper", "₹100–₹200", "Office")
        )),
        SupplyCategory("esc_4", "Pet Supplies", listOf(
            SupplySubcategory("ess_18", "Pet Food", "₹80–₹600", "Pet"),
            SupplySubcategory("ess_19", "Pet Shampoo", "₹100–₹300", "Pet"),
            SupplySubcategory("ess_20", "Collars", "₹100–₹250", "Pet"),
            SupplySubcategory("ess_21", "Treats", "₹50–₹150", "Pet"),
            SupplySubcategory("ess_22", "Grooming Brushes", "₹100–₹200", "Pet")
        )),
        SupplyCategory("esc_5", "Baby & Kids Essentials", listOf(
            SupplySubcategory("ess_23", "Toys", "₹50–₹300", "Kids"),
            SupplySubcategory("ess_24", "Diapers", "₹150–₹500", "Baby"),
            SupplySubcategory("ess_25", "Baby Wipes", "₹60–₹200", "Baby"),
            SupplySubcategory("ess_26", "Baby Lotion", "₹80–₹200", "Baby"),
            SupplySubcategory("ess_27", "Kids Water Bottles", "₹50–₹150", "Kids")
        )),
        SupplyCategory("esc_6", "Health & Wellness", listOf(
            SupplySubcategory("ess_28", "First Aid Kits", "₹100–₹250", "Health"),
            SupplySubcategory("ess_29", "Bandages", "₹10–₹40", "Health"),
            SupplySubcategory("ess_30", "Sanitizers", "₹20–₹80", "Health"),
            SupplySubcategory("ess_31", "Masks", "₹5–₹20", "Health"),
            SupplySubcategory("blob_32", "Nutrition Drinks", "₹100–₹300", "Health"),
            SupplySubcategory("ess_33", "Fitness Bands", "₹200–₹600", "Health")
        )),
        SupplyCategory("esc_7", "Home Decor & Small Items", listOf(
            SupplySubcategory("ess_34", "Candles", "₹30–₹100", "Decor"),
            SupplySubcategory("ess_35", "Photo Frames", "₹50–₹200", "Decor"),
            SupplySubcategory("ess_36", "Artificial Plants", "₹100–₹300", "Decor"),
            SupplySubcategory("ess_37", "Wall Clocks", "₹150–₹400", "Decor"),
            SupplySubcategory("ess_38", "Small Lamps", "₹200–₹600", "Decor")
        )),
        SupplyCategory("esc_8", "Car & Bike Essentials", listOf(
            SupplySubcategory("ess_39", "Helmets", "₹300–₹800", "Auto"),
            SupplySubcategory("ess_40", "Microfiber Cloth", "₹50–₹150", "Auto"),
            SupplySubcategory("ess_41", "Air Fresheners", "₹50–₹120", "Auto"),
            SupplySubcategory("ess_42", "Mini Tyre Inflator", "₹300–₹800", "Auto"),
            SupplySubcategory("ess_43", "Engine Oil", "₹100–₹250", "Auto")
        )),
        SupplyCategory("esc_9", "Kitchen Utility (Non-Food)", listOf(
            SupplySubcategory("ess_44", "Storage Boxes", "₹50–₹200", "Kitchen"),
            SupplySubcategory("ess_45", "Containers", "20–₹100", "Kitchen"),
            SupplySubcategory("ess_46", "Chopping Boards", "₹50–₹150", "Kitchen"),
            SupplySubcategory("ess_47", "Water Bottles", "₹50–₹200", "Kitchen"),
            SupplySubcategory("ess_48", "Lunch Boxes", "₹80–₹250", "Kitchen")
        )),
        SupplyCategory("esc_10", "Cleaning & Safety Supplies", listOf(
            SupplySubcategory("ess_49", "Cleaning Gloves", "₹20–₹50", "Cleaning"),
            SupplySubcategory("ess_50", "Scrub Pads", "₹10–₹30", "Cleaning"),
            SupplySubcategory("ess_51", "Cleaning Cloths", "₹20–₹60", "Cleaning"),
            SupplySubcategory("ess_52", "Garbage Bags", "₹50–₹120", "Cleaning"),
            SupplySubcategory("ess_53", "Masks", "₹5–₹20", "Safety")
        ))
    )

    val bookings = listOf(
        BookingCategory("bc_1", "Ticket Bookings", listOf(
            BookingSubcategory("bs_1", "Bus Tickets", listOf(
                BookingItem("AC Sleeper", "₹800–₹1500", "bt_1"),
                BookingItem("Non-AC Sleeper", "₹400–₹800", "bt_2"),
                BookingItem("Volvo", "₹1200–₹2000", "bt_3"),
                BookingItem("Semi Sleeper", "₹500–₹1000", "bt_4")
            )),
            BookingSubcategory("bs_2", "Train Tickets", listOf(
                BookingItem("General", "₹50–₹150", "tt_1"),
                BookingItem("Sleeper", "₹200–₹500", "tt_2"),
                BookingItem("3AC", "₹600–₹1200", "tt_3"),
                BookingItem("2AC", "₹1200–₹2000", "tt_4"),
                BookingItem("1AC", "₹2000–₹3500", "tt_5")
            )),
            BookingSubcategory("bs_3", "Flight Tickets", listOf(
                BookingItem("Domestic Economy", "₹2500–₹8000", "ft_1"),
                BookingItem("Premium Economy", "₹6000–₹15000", "ft_2"),
                BookingItem("Business Class", "₹20000–₹45000", "ft_3"),
                BookingItem("International", "₹15000–₹60000", "ft_4")
            )),
            BookingSubcategory("bs_4", "Event Tickets", listOf(
                BookingItem("Movies", "₹100–₹300", "et_1"),
                BookingItem("Concerts", "₹500–₹2500", "et_2"),
                BookingItem("Stand-Up Comedy", "₹300–₹800", "et_3"),
                BookingItem("Sports Matches", "₹200–₹2000", "et_4"),
                BookingItem("Exhibitions", "₹50–₹200", "et_5")
            )),
            BookingSubcategory("bs_5", "Local Experience Tickets", listOf(
                BookingItem("Zoo", "₹30–₹100", "let_1"),
                BookingItem("Boat Ride", "₹50–₹200", "let_2"),
                BookingItem("Theme Park", "₹300–₹1500", "let_3"),
                BookingItem("Museum", "₹50–₹150", "let_4"),
                BookingItem("Water Park", "₹400–₹1000", "let_5")
            ))
        )),
        BookingCategory("bc_2", "Hotel Bookings", listOf(
            BookingSubcategory("bs_6", "Hotel Types", listOf(
                BookingItem("Budget Hotel", "₹600–₹1500/night", "ht_1"),
                BookingItem("Standard Hotel", "₹1500–₹3000/night", "ht_2"),
                BookingItem("Premium Hotel", "₹3000–₹6000/night", "ht_3"),
                BookingItem("Resort", "₹4000–₹12000/night", "ht_4"),
                BookingItem("5-Star", "₹8000–₹25000/night", "ht_5")
            )),
            BookingSubcategory("bs_7", "Room Types", listOf(
                BookingItem("Single Room", "₹600–₹1500", "rt_1"),
                BookingItem("Double Room", "₹1200–₹2500", "rt_2"),
                BookingItem("Deluxe Room", "₹2500–₹4500", "rt_3"),
                BookingItem("Family Room", "₹3000–₹6000", "rt_4"),
                BookingItem("Suite", "₹6000–₹15000", "rt_5")
            )),
            BookingSubcategory("bs_8", "Add-ons", listOf(
                BookingItem("Breakfast", "+₹300–₹600", "ao_1"),
                BookingItem("Extra Bed", "+₹300–₹500", "ao_2"),
                BookingItem("AC/Non-AC", "+₹200–₹400", "ao_3"),
                BookingItem("Early Check-In", "₹200–₹500", "ao_4")
            ))
        )),
        BookingCategory("bc_3", "Travel Bookings", listOf(
            BookingSubcategory("bs_9", "Cab Booking", listOf(
                BookingItem("Auto", "₹30–₹150", "cb_1"),
                BookingItem("Sedan", "₹10–₹14 per km", "cb_2"),
                BookingItem("SUV", "₹15–₹20 per km", "cb_3"),
                BookingItem("Outstation Cab", "₹12–₹25 per km", "cb_4")
            )),
            BookingSubcategory("bs_10", "Vehicle Rentals", listOf(
                BookingItem("Scooter", "₹300–₹500/day", "vr_1"),
                BookingItem("Bike", "₹400–₹900/day", "vr_2"),
                BookingItem("Car", "₹1500–₹3500/day", "vr_3")
            )),
            BookingSubcategory("bs_11", "Travel Packages", listOf(
                BookingItem("One-Day Trips", "₹500–₹2000", "tp_1"),
                BookingItem("Weekend Trips", "₹2000–₹6000", "tp_2"),
                BookingItem("Family Packages", "₹5000–₹15000", "tp_3"),
                BookingItem("Adventure Trips", "₹1500–₹5000", "tp_4")
            ))
        )),
        BookingCategory("bc_4", "Appointment Bookings", listOf(
            BookingSubcategory("bs_12", "Doctor", listOf(
                BookingItem("Physician", "₹200–₹500", "dr_1"),
                BookingItem("Dentist", "₹300–₹800", "dr_2"),
                BookingItem("Eye Checkup", "₹200–₹600", "dr_3"),
                BookingItem("Skin Specialist", "₹400–₹1000", "dr_4")
            ))
        )),
        BookingCategory("bc_5", "Repair & Delivery Bookings", listOf(
            BookingSubcategory("bs_13", "Repairs", listOf(
                BookingItem("Mobile", "₹200–₹1500", "rp_1"),
                BookingItem("Laptop", "₹400–₹2500", "rp_2"),
                BookingItem("Appliance", "₹200–₹1200", "rp_3")
            )),
            BookingSubcategory("bs_14", "Delivery", listOf(
                BookingItem("Parcel Pickup", "₹50–₹150", "dl_1"),
                BookingItem("Delivery", "₹50–₹200", "dl_2"),
                BookingItem("Local Courier", "₹50–₹120", "dl_3")
            ))
        )),
        BookingCategory("bc_6", "Business Bookings", listOf(
            BookingSubcategory("bs_15", "Business Spaces", listOf(
                BookingItem("Meeting Room", "₹200–₹800/hour", "bs_1"),
                BookingItem("Co-working", "₹200–₹500/day", "bs_2"),
                BookingItem("Conference Hall", "₹1000–₹4000/day", "bs_3")
            ))
        ))
    )
}
