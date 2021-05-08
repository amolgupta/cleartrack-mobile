package xyz.getclear.android.settings

import xyz.getclear.domain.common.GravatarUrlGenerator

class GravatarUrlGeneratorImpl constructor(private val md5Util: Md5Util) : GravatarUrlGenerator {
    override fun getUrl(email: String) = "https://www.gravatar.com/avatar/${md5Util.md5(email)}?d=404"
}
