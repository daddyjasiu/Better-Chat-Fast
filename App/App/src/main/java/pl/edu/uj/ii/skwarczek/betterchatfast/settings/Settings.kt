package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import pl.edu.uj.ii.skwarczek.betterchatfast.util.FirestoreHelper

class Settings : ISettings {
    override fun setUserNickname(nickname: String) {
        FirestoreHelper.updateCurrentUserNickname(nickname)
    }

    override fun setUserFirstName(firstName: String) {
        FirestoreHelper.updateCurrentUserFirstName(firstName)
    }

    override fun setUserLastName(lastName: String) {
        FirestoreHelper.updateCurrentUserLastName(lastName)
    }
}