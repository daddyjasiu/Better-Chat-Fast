package pl.edu.uj.ii.skwarczek.betterchatfast.models

import android.location.Location
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.IUser

data class PremiumUser(
    override var userId: String,
    override var nickname: String,
    override var firstName: String,
    override var lastName: String,
    override var email: String,
    override var profilePicture: String,
    override var location: Location,

    override var isPremium: Boolean = true,
    override var isAfterOnboarding: Boolean = false,

    ): IUser