package xyz.getclear.domain.common

interface GravatarUrlGenerator {
    fun getUrl(email: String): String
}