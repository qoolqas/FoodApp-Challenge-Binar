package com.raveendra.foodapp.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.raveendra.foodapp.data.local.database.AppDatabase
import com.raveendra.foodapp.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp.data.local.database.datasource.CartDatabaseDataSource
import com.raveendra.foodapp.data.local.datastore.UserPreferenceDataSource
import com.raveendra.foodapp.data.local.datastore.UserPreferenceDataSourceImpl
import com.raveendra.foodapp.data.local.datastore.appDataStore
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSourceImpl
import com.raveendra.foodapp.data.network.api.service.FoodService
import com.raveendra.foodapp.data.network.firebase.auth.FirebaseAuthDataSource
import com.raveendra.foodapp.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.raveendra.foodapp.data.repository.CartRepository
import com.raveendra.foodapp.data.repository.CartRepositoryImpl
import com.raveendra.foodapp.data.repository.FoodRepository
import com.raveendra.foodapp.data.repository.FoodRepositoryImpl
import com.raveendra.foodapp.data.repository.UserRepository
import com.raveendra.foodapp.data.repository.UserRepositoryImpl
import com.raveendra.foodapp.presentation.cart.CartViewModel
import com.raveendra.foodapp.presentation.checkout.CheckoutViewModel
import com.raveendra.foodapp.presentation.detail.DetailViewModel
import com.raveendra.foodapp.presentation.home.HomeViewModel
import com.raveendra.foodapp.presentation.login.LoginViewModel
import com.raveendra.foodapp.presentation.profile.ProfileViewModel
import com.raveendra.foodapp.presentation.register.RegisterViewModel
import com.raveendra.foodapp.util.AssetWrapper
import com.raveendra.foodapp.util.PreferenceDataStoreHelper
import com.raveendra.foodapp.util.PreferenceDataStoreHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object AppModules {

    private val localModule = module {
        single { AppDatabase.getInstance(androidContext()) }
        single { get<AppDatabase>().cartDao() }
        single { androidContext().appDataStore }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
    }

    private val networkModule = module {
        single { ChuckerInterceptor(androidContext()) }
        single { FoodService.invoke(get()) }
        single { FirebaseAuth.getInstance() }
    }

    private val dataSourceModule = module {
        single<CartDataSource> { CartDatabaseDataSource(get()) }
        single<UserPreferenceDataSource> { UserPreferenceDataSourceImpl(get()) }
        single<FoodDataSource> { FoodDataSourceImpl(get()) }
        single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get()) }
    }

    private val repositoryModule = module {
        single<CartRepository> { CartRepositoryImpl(get(), get()) }
        single<FoodRepository> { FoodRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::HomeViewModel)
        viewModelOf(::CartViewModel)
//        viewModel { (extras: Bundle?) -> DetailViewModel(get(), extras) }
        viewModel { params -> DetailViewModel(get(), params.get()) }
        viewModelOf(::CheckoutViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::RegisterViewModel)
        viewModelOf(::LoginViewModel)
    }
    private val utilsModule = module {
        single { AssetWrapper(androidContext()) }
    }

    val modules = listOf(
        localModule,
        networkModule,
        dataSourceModule,
        repositoryModule,
        viewModelModule,
        utilsModule
    )
}
