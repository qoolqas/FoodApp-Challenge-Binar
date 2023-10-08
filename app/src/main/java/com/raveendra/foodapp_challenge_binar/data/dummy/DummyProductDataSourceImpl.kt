package com.raveendra.foodapp_challenge_binar.data.dummy

import com.raveendra.foodapp_challenge_binar.model.Food


interface DummyProductDataSource {
    fun getProductList(): List<Food>
}

class DummyProductDataSourceImpl : DummyProductDataSource {
    override fun getProductList(): List<Food> = listOf(
        Food(
            id = 1,
            title = "Ayam Krispi",
            price = 18000.0,
            addressDescription = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
            addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
            rating = 4.8,
            desc = "Ayang krispi yang sangat renyah dan kriuk",
            productImgUrl = "https://raw.githubusercontent.com/qoolqas/image-assets/master/img_chicken.webp"
        ),
        Food(
            id = 2,
            title = "Dimsum",
            price = 17000.0,
            addressDescription = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
            addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
            rating = 4.5,
            desc = "Dimsum adalah hidangan kecil berupa makanan ringan atau makanan pembuka yang berasal dari masakan tradisional China.",
            productImgUrl = "https://raw.githubusercontent.com/qoolqas/image-assets/master/img_dimsum.webp"
        ),
        Food(
            id = 3,
            title = "Ayam Bakar",
            price = 25000.0,
            addressDescription = "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten 15345",
            addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
            rating = 4.6,
            desc = "Ayam Bakar adalah hidangan Indonesia yang terkenal. Hidangan ini terdiri dari potongan ayam yang telah marinated atau dilumuri dengan bumbu khas Indonesia",
            productImgUrl = "https://raw.githubusercontent.com/qoolqas/image-assets/master/img_grilled_chicken.webp"
        ),
        Food(
            id = 4,
            title = "Mie Goreng Jawa",
            price = 14000.0,
            addressDescription = "Mie Goreng Jawa adalah hidangan khas Indonesia yang terdiri dari mie yang digoreng dengan berbagai bumbu dan rempah khas Jawa",
            addressUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77",
            rating = 4.6,
            desc = "Mie Goreng Jawa adalah hidangan khas Indonesia yang terdiri dari mie yang digoreng dengan berbagai bumbu dan rempah khas Jawa",
            productImgUrl = "https://raw.githubusercontent.com/qoolqas/image-assets/master/img_mie.webp"
        ),
    )
}