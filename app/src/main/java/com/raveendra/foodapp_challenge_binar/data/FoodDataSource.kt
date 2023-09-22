package com.raveendra.foodapp_challenge_binar.data

import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.model.FoodResponse

interface FoodDataSource {
    fun getFoodData() : List<FoodResponse>
}

class FoodDataSourceImpl : FoodDataSource{
    override fun getFoodData(): List<FoodResponse> {
        return  mutableListOf(
            FoodResponse(
                title = "Ayam Krispi",
                description = "Ayang krispi yang sangat renyah dan kriuk",
                price = 15000.00,
                address = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
                addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
                imageUrl = R.drawable.img_chicken
            ),
            FoodResponse(
                title = "Dimsum",
                description = "Dimsum adalah hidangan kecil berupa makanan ringan atau makanan pembuka yang berasal dari masakan tradisional China.",
                price = 17000.00,
                address = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
                addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
                imageUrl = R.drawable.img_dimsum
            ),
            FoodResponse(
                title = "Ayam Bakar",
                description = "Ayam Bakar adalah hidangan Indonesia yang terkenal. Hidangan ini terdiri dari potongan ayam yang telah marinated atau dilumuri dengan bumbu khas Indonesia",
                price = 25000.00,
                address = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
                addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
                imageUrl = R.drawable.img_grilled_chicken
            ),
            FoodResponse(
                title = "Mie Goreng Jawa",
                description = "Mie Goreng Jawa adalah hidangan khas Indonesia yang terdiri dari mie yang digoreng dengan berbagai bumbu dan rempah khas Jawa",
                price = 20000.00,
                address = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
                addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
                imageUrl = R.drawable.img_mie
            ),

        )
    }

}