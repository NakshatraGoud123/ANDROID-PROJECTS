package com.nisr.sauservices.data.model

data class HomeCategory(
    val id: String,
    val name: String,
    val icon: Int? = null 
)

data class ProductType(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val image: Int? = null
)

data class Product(
    val id: String,
    val typeId: String,
    val name: String,
    val price: Double,
    val unit: String,
    val rating: Double,
    val description: String,
    val image: Int? = null
)

object HomeEssentialsData {
    val categories = listOf(
        HomeCategory("vegetables", "Vegetables"),
        HomeCategory("fruits", "Fruits"),
        HomeCategory("grocery", "Grocery"),
        HomeCategory("dairy", "Milk & Dairy"),
        HomeCategory("snacks", "Snacks"),
        HomeCategory("meat", "Meat"),
        HomeCategory("fish", "Fish"),
        HomeCategory("eggs", "Eggs"),
        HomeCategory("water", "Water")
    )

    val productTypes = listOf(
        // Vegetables
        ProductType("leafy_veg", "vegetables", "Leafy Vegetables", "Spinach, Coriander, Mint, Lettuce, Fenugreek"),
        ProductType("root_veg", "vegetables", "Root Vegetables", "Potato, Carrot, Beetroot, Radish, Sweet Potato"),
        ProductType("bulb_veg", "vegetables", "Bulb Vegetables", "Onion, Garlic, Ginger"),
        ProductType("reg_veg", "vegetables", "Regular Vegetables", "Tomato, Brinjal, Capsicum, Cabbage, Cauliflower"),
        ProductType("exotic_veg", "vegetables", "Exotic Vegetables", "Broccoli, Zucchini, Bell Peppers, Cherry Tomatoes"),

        // Fruits
        ProductType("citrus_fruits", "fruits", "Citrus Fruits", "Orange, Sweet Lime, Lemon, Grapefruit"),
        ProductType("tropical_fruits", "fruits", "Tropical Fruits", "Banana, Mango, Pineapple, Papaya"),
        ProductType("berries", "fruits", "Berries", "Strawberry, Blueberry, Raspberry"),
        ProductType("pome_fruits", "fruits", "Pome Fruits", "Apple, Pear"),
        ProductType("melons", "fruits", "Melons", "Watermelon, Muskmelon"),
        ProductType("exotic_fruits", "fruits", "Exotic Fruits", "Kiwi, Dragon Fruit, Avocado"),

        // Grocery
        ProductType("staples", "grocery", "Staples", "Rice, Wheat, Atta, Dal, Pulses"),
        ProductType("oils", "grocery", "Oils & Ghee", "Sunflower Oil, Groundnut Oil, Olive Oil, Ghee"),
        ProductType("spices", "grocery", "Spices", "Turmeric, Chilli Powder, Garam Masala, Pepper"),
        ProductType("condiments", "grocery", "Condiments", "Salt, Sugar, Jaggery, Sauces, Pickles"),
        ProductType("baking", "grocery", "Baking Essentials", "Flour, Baking Soda, Cocoa Powder, Yeast"),
        ProductType("ready_cook", "grocery", "Ready to Cook", "Noodles, Pasta, Soup Mix, Instant Mixes"),

        // Dairy
        ProductType("milk", "dairy", "Milk", "Toned Milk, Full Cream Milk, Skim Milk"),
        ProductType("curd", "dairy", "Curd & Yogurt", "Curd, Greek Yogurt, Flavored Yogurt"),
        ProductType("butter_cheese", "dairy", "Butter & Cheese", "Butter, Cheese Slices, Cheese Blocks"),
        ProductType("paneer", "dairy", "Paneer", "Fresh Paneer, Malai Paneer"),
        ProductType("cream", "dairy", "Cream & Dairy Drinks", "Fresh Cream, Lassi, Buttermilk"),

        // Snacks
        ProductType("namkeen", "snacks", "Namkeen", "Mixture, Sev, Chips, Bhujia"),
        ProductType("biscuits", "snacks", "Biscuits", "Marie, Cream Biscuits, Digestive"),
        ProductType("sweets", "snacks", "Sweets", "Laddoo, Barfi, Halwa"),
        ProductType("healthy_snacks", "snacks", "Healthy Snacks", "Dry Fruits, Energy Bars, Roasted Nuts"),
        ProductType("instant_snacks", "snacks", "Instant Snacks", "Cup Noodles, Ready Poha, Upma Mix"),

        // Meat
        ProductType("chicken", "meat", "Chicken", "Curry Cut, Boneless, Wings"),
        ProductType("mutton", "meat", "Mutton", "Curry Cut, Boneless"),
        ProductType("ready_meat", "meat", "Ready to Cook Meat", "Marinated Chicken, Kebabs"),

        // Fish
        ProductType("fresh_water", "fish", "Fresh Water Fish", "Rohu, Katla"),
        ProductType("sea_fish", "fish", "Sea Fish", "Pomfret, Seer Fish, Mackerel"),
        ProductType("prawns", "fish", "Prawns", "Small Prawns, Jumbo Prawns"),

        // Eggs
        ProductType("reg_eggs", "eggs", "Regular Eggs", "White Eggs, Brown Eggs"),
        ProductType("farm_eggs", "eggs", "Farm Eggs", "Country Eggs"),

        // Water
        ProductType("packaged_water", "water", "Packaged Water", "1L Bottles, 2L Bottles"),
        ProductType("large_cans", "water", "Large Cans", "20L Can Water"),
        ProductType("mineral_water", "water", "Mineral Water Brands", "Bisleri, Kinley, Aquafina")
    )

    val products = listOf(
        // Vegetables - Leafy
        Product("v1", "leafy_veg", "Fresh Spinach (Palak)", 30.0, "250g", 4.5, "Farm fresh green spinach"),
        Product("v2", "leafy_veg", "Coriander (Dhaniya)", 10.0, "100g", 4.8, "Fresh aromatic coriander leaves"),
        Product("v3", "leafy_veg", "Mint Leaves (Pudina)", 15.0, "100g", 4.6, "Fresh mint for chutneys and tea"),
        
        // Vegetables - Root
        Product("v4", "root_veg", "Potatoes (Aloo)", 40.0, "1kg", 4.3, "Fresh farm-picked potatoes"),
        Product("v5", "root_veg", "Carrots (Gajar)", 60.0, "1kg", 4.4, "Red, crunchy and sweet carrots"),
        Product("v6", "root_veg", "Beetroot", 45.0, "500g", 4.2, "Healthy and fresh beetroots"),

        // Vegetables - Bulb
        Product("v7", "bulb_veg", "Onions", 35.0, "1kg", 4.5, "Fresh pink onions"),
        Product("v8", "bulb_veg", "Garlic", 50.0, "250g", 4.7, "Aromatic and fresh garlic bulbs"),

        // Vegetables - Regular
        Product("v9", "reg_veg", "Red Tomatoes", 30.0, "1kg", 4.4, "Fresh and juicy red tomatoes"),
        Product("v10", "reg_veg", "Green Capsicum", 40.0, "500g", 4.3, "Fresh and crunchy capsicums"),

        // Vegetables - Exotic
        Product("v11", "exotic_veg", "Broccoli", 120.0, "1 pc", 4.6, "Fresh green broccoli florets"),
        Product("v12", "exotic_veg", "Zucchini (Green)", 90.0, "500g", 4.4, "Fresh green zucchini"),

        // Fruits - Citrus
        Product("f1", "citrus_fruits", "Fresh Oranges", 120.0, "1kg", 4.5, "Sweet and juicy Nagpur oranges"),
        Product("f2", "citrus_fruits", "Sweet Lime (Mosambi)", 90.0, "1kg", 4.3, "Fresh sweet lime for juice"),
        Product("f3", "citrus_fruits", "Lemons", 20.0, "4 pcs", 4.7, "Tangy and fresh lemons"),

        // Fruits - Tropical
        Product("f4", "tropical_fruits", "Banana (Robusta)", 60.0, "1 dozen", 4.4, "Fresh and ripe bananas"),
        Product("f5", "tropical_fruits", "Alphonso Mango", 600.0, "1 dozen", 4.9, "The king of mangoes"),

        // Fruits - Berries
        Product("f6", "berries", "Strawberries", 80.0, "1 pack", 4.5, "Sweet and fresh strawberries"),
        Product("f7", "berries", "Blueberries", 250.0, "1 pack", 4.6, "Fresh imported blueberries"),

        // Fruits - Pome
        Product("f8", "pome_fruits", "Washington Apple", 180.0, "1kg", 4.7, "Crispy and sweet red apples"),
        Product("f9", "pome_fruits", "Pears", 120.0, "1kg", 4.4, "Fresh and juicy green pears"),

        // Fruits - Melons
        Product("f10", "melons", "Watermelon", 50.0, "1 pc", 4.5, "Large and sweet watermelon"),
        Product("f11", "melons", "Muskmelon", 60.0, "1 pc", 4.3, "Sweet and aromatic muskmelon"),

        // Fruits - Exotic
        Product("f12", "exotic_fruits", "Kiwi Fruit", 100.0, "3 pcs", 4.6, "Fresh green kiwis"),
        Product("f13", "exotic_fruits", "Avocado", 300.0, "1 pc", 4.5, "Butter-soft ripe avocados"),

        // Grocery - Staples
        Product("g1", "staples", "Premium Basmati Rice", 150.0, "1kg", 4.8, "Long grain aromatic rice"),
        Product("g2", "staples", "Wheat Atta", 350.0, "5kg", 4.7, "100% whole wheat atta"),
        Product("g3", "staples", "Toor Dal", 160.0, "1kg", 4.6, "Premium quality toor dal"),

        // Grocery - Oils
        Product("g4", "oils", "Sunflower Oil", 140.0, "1L", 4.5, "Healthy refined sunflower oil"),
        Product("g5", "oils", "Pure Cow Ghee", 650.0, "500ml", 4.9, "Authentic and pure cow ghee"),

        // Grocery - Spices
        Product("g6", "spices", "Turmeric Powder", 45.0, "200g", 4.7, "Pure and organic turmeric"),
        Product("g7", "spices", "Red Chilli Powder", 55.0, "200g", 4.6, "Spicy and vibrant red chilli"),

        // Grocery - Condiments
        Product("g8", "condiments", "Iodized Salt", 25.0, "1kg", 4.9, "Premium quality iodized salt"),
        Product("g9", "condiments", "Refined Sugar", 50.0, "1kg", 4.6, "Pure white refined sugar"),

        // Grocery - Baking
        Product("g10", "baking", "Maida (Refined Flour)", 40.0, "1kg", 4.5, "Fine quality maida for baking"),
        Product("g11", "baking", "Cocoa Powder", 120.0, "100g", 4.7, "Rich dark cocoa powder"),

        // Grocery - Ready Cook
        Product("g12", "ready_cook", "Instant Noodles", 15.0, "1 pack", 4.4, "Quick and tasty noodles"),
        Product("g13", "ready_cook", "Pasta (Penne)", 80.0, "500g", 4.5, "Premium durum wheat pasta"),

        // Dairy - Milk
        Product("d1", "milk", "Full Cream Milk", 66.0, "1L", 4.7, "Pure and fresh full cream milk"),
        Product("d2", "milk", "Toned Milk", 54.0, "1L", 4.5, "Fresh toned milk for daily use"),

        // Dairy - Curd
        Product("d3", "curd", "Fresh Thick Curd", 40.0, "500g", 4.6, "Traditional style thick curd"),
        Product("d4", "curd", "Greek Yogurt", 80.0, "150g", 4.7, "High protein greek yogurt"),

        // Dairy - Butter & Cheese
        Product("d5", "butter_cheese", "Amul Butter", 275.0, "500g", 4.9, "The classic salted butter"),
        Product("d6", "butter_cheese", "Cheese Slices", 150.0, "200g", 4.7, "Perfectly melted cheese slices"),

        // Dairy - Paneer
        Product("d7", "paneer", "Fresh Paneer", 100.0, "200g", 4.8, "Soft and fresh dairy paneer"),

        // Dairy - Cream
        Product("d8", "cream", "Fresh Cream", 65.0, "250ml", 4.6, "Rich and smooth fresh cream"),
        Product("d9", "cream", "Sweet Lassi", 25.0, "200ml", 4.5, "Refreshing sweet lassi"),

        // Snacks - Namkeen
        Product("s1", "namkeen", "Aloo Bhujia", 50.0, "200g", 4.7, "Crunchy potato snacks"),
        Product("s2", "namkeen", "Potato Chips", 20.0, "1 pack", 4.5, "Classic salted potato chips"),

        // Snacks - Biscuits
        Product("s3", "biscuits", "Marie Biscuits", 30.0, "250g", 4.4, "Crispy and light tea biscuits"),
        Product("s4", "biscuits", "Chocolate Cookies", 50.0, "150g", 4.6, "Rich chocolate chip cookies"),

        // Snacks - Sweets
        Product("s5", "sweets", "Motichoor Laddu", 150.0, "250g", 4.8, "Traditional festive sweets"),
        Product("s6", "sweets", "Gulab Jamun", 120.0, "250g", 4.7, "Soft and juicy gulab jamuns"),

        // Snacks - Healthy
        Product("s7", "healthy_snacks", "Mixed Dry Fruits", 450.0, "250g", 4.9, "Premium mix of nuts and raisins"),
        Product("s8", "healthy_snacks", "Energy Bar", 40.0, "1 pc", 4.4, "Nutrition-packed energy bar"),

        // Snacks - Instant
        Product("s9", "instant_snacks", "Cup Noodles", 50.0, "1 pc", 4.3, "Just add hot water and eat"),

        // Meat - Chicken
        Product("m1", "chicken", "Chicken Curry Cut", 240.0, "1kg", 4.6, "Fresh farm chicken with bones"),
        Product("m2", "chicken", "Boneless Breast", 320.0, "1kg", 4.7, "Fresh tender boneless chicken"),

        // Meat - Mutton
        Product("m3", "mutton", "Mutton Curry Cut", 850.0, "1kg", 4.8, "Fresh and tender goat meat"),

        // Meat - Ready Meat
        Product("m4", "ready_meat", "Marinated Chicken", 180.0, "500g", 4.5, "Ready to grill marinated chicken"),

        // Fish - Fresh Water
        Product("fi1", "fresh_water", "Rohu Fish", 220.0, "1kg", 4.5, "Fresh river water fish"),
        Product("fi2", "fresh_water", "Katla Fish", 250.0, "1kg", 4.4, "Fresh and large Katla fish"),

        // Fish - Sea Fish
        Product("fi3", "sea_fish", "Pomfret Fish", 600.0, "1kg", 4.7, "Premium silver pomfret fish"),
        Product("fi4", "sea_fish", "Mackerel (Bangda)", 300.0, "1kg", 4.3, "Fresh sea water mackerel"),

        // Fish - Prawns
        Product("fi5", "prawns", "Jumbo Prawns", 800.0, "1kg", 4.8, "Large sized fresh prawns"),

        // Eggs - Regular
        Product("e1", "reg_eggs", "White Eggs", 80.0, "1 dozen", 4.6, "Fresh poultry white eggs"),
        Product("e2", "reg_eggs", "Brown Eggs", 120.0, "1 dozen", 4.7, "Nutritious farm brown eggs"),

        // Eggs - Farm
        Product("e3", "farm_eggs", "Country Eggs", 150.0, "1 dozen", 4.8, "Organic free-range country eggs"),

        // Water - Packaged
        Product("w1", "packaged_water", "Mineral Water Bottle", 20.0, "1L", 4.8, "Clean and pure drinking water"),
        Product("w2", "packaged_water", "Water Bottle", 35.0, "2L", 4.7, "Clean drinking water 2L"),

        // Water - Large Cans
        Product("w3", "large_cans", "20L Water Can", 90.0, "20L", 4.6, "Standard 20L water can for home"),

        // Water - Brands
        Product("w4", "mineral_water", "Bisleri Water", 20.0, "1L", 4.9, "Trusted mineral water brand")
    )
}
