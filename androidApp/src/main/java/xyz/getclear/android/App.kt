package xyz.getclear.android

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import xyz.getclear.android.di.appModule
import xyz.getclear.data.di.dataModule
import xyz.getclear.domain.di.domainModule

open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger(level = Level.NONE)
            androidContext(this@App)
            modules(appModule, domainModule, dataModule)
        }
    }
}
