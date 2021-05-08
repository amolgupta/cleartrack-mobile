package xyz.getclear

import org.koin.core.context.startKoin
import xyz.getclear.data.di.dataModule
import xyz.getclear.domain.di.domainModule
import xyz.getclear.vm.vmModule


fun initKoinAndroid() = startKoin {
    modules(dataModule, domainModule, vmModule)
}


