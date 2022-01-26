package pl.edu.uj.ii.skwarczek.betterchatfast.models

import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.ISettings
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper

class Settings : ISettings {
    override fun setUserNickname(nickname: String) {
        FirestoreHelper.updateCurrentUserNicknameInFirebase(nickname)
    }

    override fun setUserFirstName(firstName: String) {
        FirestoreHelper.updateCurrentUserFirstNameInFirebase(firstName)
    }

    override fun setUserLastName(lastName: String) {
        FirestoreHelper.updateCurrentUserLastNameInFirebase(lastName)
    }
}