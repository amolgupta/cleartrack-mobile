package xyz.getclear.data.net.contract

import xyz.getclear.data.data.User

interface UserStore {
    fun putUser(user: User)
    fun clear()
}