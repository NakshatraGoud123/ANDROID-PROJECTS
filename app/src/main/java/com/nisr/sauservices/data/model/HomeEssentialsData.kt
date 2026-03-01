package com.nisr.sauservices.data.model

data class HomeCategory(
    val id: String,
    val name: String,
    val icon: String
)

data class HomeSubcategory(
    val id: String,
    val categoryId: String,
    val name: String
)

data class HomeProduct(
    val id: String,
    val subcategoryId: String,
    val name: String,
    val price: Int,
    val unit: String,
    val category: String
)

object HomeEssentialsData {
    val categories = listOf(
        HomeCategory("veg", "Vegetables", "🥦"),
        HomeCategory("fruits", "Fruits", "🍎"),
        HomeCategory("grocery", "Grocery", "🛒"),
        HomeCategory("dairy", "Milk & Dairy", "🥛"),
        HomeCategory("meat", "Meat / Fish / Eggs", "🥩"),
        HomeCategory("snacks", "Snacks", "🍿"),
        HomeCategory("water", "Drinking Water", "💧")
    )

    val subcategories = listOf(
        // Vegetables
        HomeSubcategory("veg_leafy", "veg", "Leafy Vegetables"),
        HomeSubcategory("veg_root", "veg", "Root Vegetables"),
        HomeSubcategory("veg_fruit", "veg", "Fruit Vegetables"),
        HomeSubcategory("veg_beans", "veg", "Beans & Peas"),
        HomeSubcategory("veg_exotic", "veg", "Exotic Vegetables"),
        HomeSubcategory("veg_cabbage", "veg", "Cabbage Family"),

        // Fruits
        HomeSubcategory("fruit_citrus", "fruits", "Citrus Fruits"),
        HomeSubcategory("fruit_tropical", "fruits", "Tropical Fruits"),
        HomeSubcategory("fruit_berries", "fruits", "Berries"),
        HomeSubcategory("fruit_exotic", "fruits", "Exotic Fruits"),
        HomeSubcategory("fruit_everyday", "fruits", "Everyday Fruits"),
        HomeSubcategory("fruit_seasonal", "fruits", "Seasonal Fruits"),

        // Grocery
        HomeSubcategory("groc_grains", "grocery", "Grains"),
        HomeSubcategory("groc_pulses", "grocery", "Pulses"),
        HomeSubcategory("groc_oils", "grocery", "Oils"),
        HomeSubcategory("groc_masalas", "grocery", "Masalas"),
        HomeSubcategory("groc_ready", "grocery", "Ready to Eat"),

        // Milk & Dairy
        HomeSubcategory("dairy_milk", "dairy", "Milk"),
        HomeSubcategory("dairy_butter", "dairy", "Butter & Ghee"),
        HomeSubcategory("dairy_cheese", "dairy", "Cheese & Paneer"),
        HomeSubcategory("dairy_yogurt", "dairy", "Curd & Yogurt"),
        HomeSubcategory("dairy_cream", "dairy", "Cream & Drinks"),

        // Meat / Fish / Eggs
        HomeSubcategory("meat_chicken", "meat", "Chicken"),
        HomeSubcategory("meat_mutton", "meat", "Mutton"),
        HomeSubcategory("meat_fish", "meat", "Fish"),
        HomeSubcategory("meat_eggs", "meat", "Eggs"),

        // Snacks
        HomeSubcategory("snack_chips", "snacks", "Chips"),
        HomeSubcategory("snack_biscuits", "snacks", "Biscuits"),
        HomeSubcategory("snack_namkeen", "snacks", "Namkeen"),
        HomeSubcategory("snack_sweets", "snacks", "Sweets"),
        HomeSubcategory("snack_chocolates", "snacks", "Chocolates"),

        // Drinking Water
        HomeSubcategory("water_can", "water", "Can Water"),
        HomeSubcategory("water_bottle", "water", "Bottled Water"),
        HomeSubcategory("water_mineral", "water", "Mineral Water"),
        HomeSubcategory("water_alkaline", "water", "Alkaline Water")
    )

    val products = listOf(
        // VEGETABLES
        // Leafy
        HomeProduct("v_leafy_1", "veg_leafy", "Spinach (Palak)", 20, "1 bunch", "Vegetables"),
        HomeProduct("v_leafy_2", "veg_leafy", "Coriander (Kothmir)", 15, "1 bunch", "Vegetables"),
        HomeProduct("v_leafy_3", "veg_leafy", "Mint (Pudina)", 15, "1 bunch", "Vegetables"),
        HomeProduct("v_leafy_4", "veg_leafy", "Methi Leaves", 20, "1 bunch", "Vegetables"),
        // Root
        HomeProduct("v_root_1", "veg_root", "Potato", 30, "1kg", "Vegetables"),
        HomeProduct("v_root_2", "veg_root", "Onion", 35, "1kg", "Vegetables"),
        HomeProduct("v_root_3", "veg_root", "Carrot", 40, "1kg", "Vegetables"),
        HomeProduct("v_root_4", "veg_root", "Beetroot", 45, "1kg", "Vegetables"),
        HomeProduct("v_root_5", "veg_root", "Ginger", 30, "250g", "Vegetables"),
        HomeProduct("v_root_6", "veg_root", "Garlic", 35, "250g", "Vegetables"),
        // Fruit Veg
        HomeProduct("v_fruit_1", "veg_fruit", "Tomato", 25, "1kg", "Vegetables"),
        HomeProduct("v_fruit_2", "veg_fruit", "Brinjal", 35, "1kg", "Vegetables"),
        HomeProduct("v_fruit_3", "veg_fruit", "Okra (Lady Finger)", 30, "500g", "Vegetables"),
        HomeProduct("v_fruit_4", "veg_fruit", "Capsicum", 40, "500g", "Vegetables"),
        HomeProduct("v_fruit_5", "veg_fruit", "Pumpkin", 30, "1kg", "Vegetables"),
        // Beans
        HomeProduct("v_beans_1", "veg_beans", "French Beans", 35, "500g", "Vegetables"),
        HomeProduct("v_beans_2", "veg_beans", "Green Peas", 50, "500g", "Vegetables"),
        // Exotic
        HomeProduct("v_exotic_1", "veg_exotic", "Broccoli", 60, "250g", "Vegetables"),
        HomeProduct("v_exotic_2", "veg_exotic", "Mushroom", 50, "200g", "Vegetables"),
        HomeProduct("v_exotic_3", "veg_exotic", "Lettuce", 40, "1 piece", "Vegetables"),
        HomeProduct("v_exotic_4", "veg_exotic", "Zucchini", 70, "500g", "Vegetables"),
        // Cabbage
        HomeProduct("v_cabbage_1", "veg_cabbage", "Cabbage", 25, "1kg", "Vegetables"),
        HomeProduct("v_cabbage_2", "veg_cabbage", "Cauliflower", 35, "1 piece", "Vegetables"),

        // FRUITS
        // Citrus
        HomeProduct("f_citrus_1", "fruit_citrus", "Orange", 60, "1kg", "Fruits"),
        HomeProduct("f_citrus_2", "fruit_citrus", "Mosambi", 70, "1kg", "Fruits"),
        HomeProduct("f_citrus_3", "fruit_citrus", "Lemon", 20, "250g", "Fruits"),
        // Tropical
        HomeProduct("f_trop_1", "fruit_tropical", "Banana", 50, "Dozen", "Fruits"),
        HomeProduct("f_trop_2", "fruit_tropical", "Mango", 120, "1kg", "Fruits"),
        HomeProduct("f_trop_3", "fruit_tropical", "Papaya", 45, "1kg", "Fruits"),
        HomeProduct("f_trop_4", "fruit_tropical", "Pineapple", 60, "1 piece", "Fruits"),
        // Berries
        HomeProduct("f_berry_1", "fruit_berries", "Strawberry", 80, "200g", "Fruits"),
        HomeProduct("f_berry_2", "fruit_berries", "Grapes", 50, "500g", "Fruits"),
        // Exotic
        HomeProduct("f_exot_1", "fruit_exotic", "Kiwi", 100, "3 pcs", "Fruits"),
        HomeProduct("f_exot_2", "fruit_exotic", "Dragon Fruit", 90, "1 pc", "Fruits"),
        HomeProduct("f_exot_3", "fruit_exotic", "Avocado", 120, "1 pc", "Fruits"),
        // Everyday
        HomeProduct("f_every_1", "fruit_everyday", "Apple", 120, "1kg", "Fruits"),
        HomeProduct("f_every_2", "fruit_everyday", "Pomegranate", 150, "1kg", "Fruits"),
        HomeProduct("f_every_3", "fruit_everyday", "Watermelon", 30, "1kg", "Fruits"),
        // Seasonal
        HomeProduct("f_seas_1", "fruit_seasonal", "Litchi", 90, "500g", "Fruits"),
        HomeProduct("f_seas_2", "fruit_seasonal", "Jamun", 70, "500g", "Fruits"),

        // MILK & DAIRY
        // Milk
        HomeProduct("d_milk_1", "dairy_milk", "Cow Milk", 28, "500ml", "Milk & Dairy"),
        HomeProduct("d_milk_2", "dairy_milk", "Buffalo Milk", 32, "500ml", "Milk & Dairy"),
        HomeProduct("d_milk_3", "dairy_milk", "Toned Milk", 54, "1L", "Milk & Dairy"),
        HomeProduct("d_milk_4", "dairy_milk", "Full Cream Milk", 60, "1L", "Milk & Dairy"),
        HomeProduct("d_milk_5", "dairy_milk", "Organic Milk", 70, "1L", "Milk & Dairy"),
        // Butter & Ghee
        HomeProduct("d_butt_1", "dairy_butter", "Butter", 55, "100g", "Milk & Dairy"),
        HomeProduct("d_butt_2", "dairy_butter", "Ghee", 320, "500ml", "Milk & Dairy"),
        // Cheese & Paneer
        HomeProduct("d_chee_1", "dairy_cheese", "Paneer", 90, "200g", "Milk & Dairy"),
        HomeProduct("d_chee_2", "dairy_cheese", "Cheese Slices", 110, "pack", "Milk & Dairy"),
        HomeProduct("d_chee_3", "dairy_cheese", "Mozzarella", 140, "200g", "Milk & Dairy"),
        // Curd & Yogurt
        HomeProduct("d_yog_1", "dairy_yogurt", "Curd", 40, "500g", "Milk & Dairy"),
        HomeProduct("d_yog_2", "dairy_yogurt", "Greek Yogurt", 60, "150g", "Milk & Dairy"),
        // Cream & Drinks
        HomeProduct("d_crea_1", "dairy_cream", "Fresh Cream", 60, "250ml", "Milk & Dairy"),
        HomeProduct("d_crea_2", "dairy_cream", "Buttermilk", 25, "200ml", "Milk & Dairy"),
        HomeProduct("d_crea_3", "dairy_cream", "Lassi", 35, "200ml", "Milk & Dairy"),

        // MEAT / FISH / EGGS
        // Chicken
        HomeProduct("m_chick_1", "meat_chicken", "Chicken Curry Cut", 220, "1kg", "Meat"),
        HomeProduct("m_chick_2", "meat_chicken", "Chicken Boneless", 180, "500g", "Meat"),
        HomeProduct("m_chick_3", "meat_chicken", "Chicken Breast", 200, "500g", "Meat"),
        HomeProduct("m_chick_4", "meat_chicken", "Leg Pieces", 160, "500g", "Meat"),
        HomeProduct("m_chick_5", "meat_chicken", "Wings", 140, "500g", "Meat"),
        HomeProduct("m_chick_6", "meat_chicken", "Keema", 190, "500g", "Meat"),
        HomeProduct("m_chick_7", "meat_chicken", "Whole Chicken", 300, "1 pc", "Meat"),
        // Mutton
        HomeProduct("m_mutt_1", "meat_mutton", "Mutton Curry Cut", 700, "1kg", "Meat"),
        HomeProduct("m_mutt_2", "meat_mutton", "Mutton Boneless", 450, "500g", "Meat"),
        HomeProduct("m_mutt_3", "meat_mutton", "Mutton Keema", 420, "500g", "Meat"),
        HomeProduct("m_mutt_4", "meat_mutton", "Mutton Liver", 150, "250g", "Meat"),
        HomeProduct("m_mutt_5", "meat_mutton", "Soup Bones", 120, "500g", "Meat"),
        // Fish
        HomeProduct("m_fish_1", "meat_fish", "Rohu", 250, "1kg", "Meat"),
        HomeProduct("m_fish_2", "meat_fish", "Catla", 280, "1kg", "Meat"),
        HomeProduct("m_fish_3", "meat_fish", "Seer Fish", 400, "500g", "Meat"),
        HomeProduct("m_fish_4", "meat_fish", "Pomfret", 350, "500g", "Meat"),
        HomeProduct("m_fish_5", "meat_fish", "Prawns", 300, "500g", "Meat"),
        HomeProduct("m_fish_6", "meat_fish", "Crab", 350, "1kg", "Meat"),
        HomeProduct("m_fish_7", "meat_fish", "Basa Fillet", 220, "500g", "Meat"),
        // Eggs
        HomeProduct("m_eggs_1", "meat_eggs", "6 Eggs", 45, "pack", "Meat"),
        HomeProduct("m_eggs_2", "meat_eggs", "12 Eggs", 80, "pack", "Meat"),
        HomeProduct("m_eggs_3", "meat_eggs", "Country Eggs", 70, "6 pcs", "Meat"),

        // SNACKS
        HomeProduct("s_chip_1", "snack_chips", "Lays", 20, "pack", "Snacks"),
        HomeProduct("s_chip_2", "snack_chips", "Bingo", 20, "pack", "Snacks"),
        HomeProduct("s_bisc_1", "snack_biscuits", "Good Day", 30, "pack", "Snacks"),
        HomeProduct("s_bisc_2", "snack_biscuits", "Oreo", 40, "pack", "Snacks"),
        HomeProduct("s_namk_1", "snack_namkeen", "Mixture", 50, "200g", "Snacks"),
        HomeProduct("s_namk_2", "snack_namkeen", "Sev", 40, "200g", "Snacks"),
        HomeProduct("s_swee_1", "snack_sweets", "Laddu", 150, "250g", "Snacks"),
        HomeProduct("s_swee_2", "snack_sweets", "Barfi", 180, "250g", "Snacks"),
        HomeProduct("s_choc_1", "snack_chocolates", "Dairy Milk", 40, "pc", "Snacks"),
        HomeProduct("s_choc_2", "snack_chocolates", "KitKat", 30, "pc", "Snacks"),

        // DRINKING WATER
        // Can
        HomeProduct("w_can_1", "water_can", "20L Refill", 60, "can", "Water"),
        HomeProduct("w_can_2", "water_can", "20L New", 150, "can", "Water"),
        // Bottle
        HomeProduct("w_bott_1", "water_bottle", "500ml", 10, "bottle", "Water"),
        HomeProduct("w_bott_2", "water_bottle", "1L", 20, "bottle", "Water"),
        HomeProduct("w_bott_3", "water_bottle", "2L", 35, "bottle", "Water"),
        // Mineral
        HomeProduct("w_min_1", "water_mineral", "1L Mineral", 25, "bottle", "Water"),
        HomeProduct("w_min_2", "water_mineral", "2L Mineral", 40, "bottle", "Water"),
        // Alkaline
        HomeProduct("w_alk_1", "water_alkaline", "1L Alkaline", 60, "bottle", "Water"),

        // GROCERY
        // Grains
        HomeProduct("g_grain_1", "groc_grains", "Rice", 60, "1kg", "Grocery"),
        HomeProduct("g_grain_2", "groc_grains", "Basmati", 120, "1kg", "Grocery"),
        HomeProduct("g_grain_3", "groc_grains", "Wheat flour", 45, "1kg", "Grocery"),
        // Pulses
        HomeProduct("g_pulse_1", "groc_pulses", "Toor dal", 160, "1kg", "Grocery"),
        HomeProduct("g_pulse_2", "groc_pulses", "Moong dal", 140, "1kg", "Grocery"),
        HomeProduct("g_pulse_3", "groc_pulses", "Rajma", 120, "1kg", "Grocery"),
        // Oils
        HomeProduct("g_oil_1", "groc_oils", "Sunflower oil", 150, "1L", "Grocery"),
        HomeProduct("g_oil_2", "groc_oils", "Groundnut oil", 180, "1L", "Grocery"),
        // Masalas
        HomeProduct("g_masa_1", "groc_masalas", "Turmeric", 30, "100g", "Grocery"),
        HomeProduct("g_masa_2", "groc_masalas", "Chilli powder", 40, "100g", "Grocery"),
        HomeProduct("g_masa_3", "groc_masalas", "Garam masala", 50, "100g", "Grocery"),
        // Ready
        HomeProduct("g_read_1", "groc_ready", "Noodles", 15, "pack", "Grocery"),
        HomeProduct("g_read_2", "groc_ready", "Pasta", 40, "pack", "Grocery"),
        HomeProduct("g_read_3", "groc_ready", "Poha", 50, "1kg", "Grocery")
    )
}
