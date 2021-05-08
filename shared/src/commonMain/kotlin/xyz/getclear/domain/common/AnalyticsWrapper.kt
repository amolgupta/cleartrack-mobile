package xyz.getclear.domain.common

// MainActivity
const val EVENT_TAB_SELECTED = "tab_selected"

// POT CRUD
const val EVENT_ADD_POT_INITIALIZED = "add_pot_initialised"
const val EVENT_ADD_POT = "add_pot"
const val EVENT_DELETE_POT = "delete_pot"

//TRANSACTION CRUD
const val EVENT_ADD_TRANSACTION_INITIALIZED = "add_transaction_initialised"
const val EVENT_ADD_TRANSACTION = "add_transaction"
const val EVENT_DELETE_ADD_TRANSACTION = "delete_transaction"
const val EVENT_UPDATE_TRANSACTION = "update_transaction"

// Login
const val EVENT_LOGIN_CLICKED = "login_clicked"
const val EVENT_LOGIN_SUCCESS = "login_success"
const val EVENT_REGISTER_CLICKED = "register_clicked"
const val EVENT_REGISTER_SUCCESS = "register_success"

//settings
const val EVENT_LOGOUT = "logout"
const val EVENT_CURRENCY_CHANGE = "currency_change"
const val EVENT_SHARE = "share"
const val EVENT_GRAVATAR_LOADED = "gravatar_loaded"

const val PROP_SOURCE = "source"

interface AnalyticsWrapper {
    fun logEvent(name: String, values: Map<String, String>)
    fun logEvent(name: String)
}