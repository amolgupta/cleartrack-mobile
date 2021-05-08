package xyz.getclear.vm

import org.koin.dsl.module
import xyz.getclear.vm.addPot.AddPotViewModel
import xyz.getclear.vm.addTransaction.AddTransactionViewModel
import xyz.getclear.vm.appStart.AppStartViewModel
import xyz.getclear.vm.auth.AuthViewModel
import xyz.getclear.vm.details.DetailsViewModel
import xyz.getclear.vm.home.HomeViewModel
import xyz.getclear.vm.potList.PotsViewModel
import xyz.getclear.vm.potPicker.PotPickerViewModel
import xyz.getclear.vm.report.ReportViewModel
import xyz.getclear.vm.settings.SettingsViewModel

val vmModule = module {

    factory { AuthViewModel() }
    factory { SettingsViewModel() }
    factory { ReportViewModel() }
    factory { DetailsViewModel() }
    factory { HomeViewModel() }
    factory { AddTransactionViewModel() }
    factory { PotsViewModel() }
    factory { AddPotViewModel() }
    factory { AppStartViewModel() }
    factory { PotPickerViewModel() }

}
