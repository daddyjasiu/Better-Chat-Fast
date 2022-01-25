package pl.edu.uj.ii.skwarczek.betterchatfast.utility

import android.location.Location
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.IUser
import pl.edu.uj.ii.skwarczek.betterchatfast.enums.UserTypes
import pl.edu.uj.ii.skwarczek.betterchatfast.models.PremiumUser
import pl.edu.uj.ii.skwarczek.betterchatfast.models.StandardUser

object UserFactory {

    fun createUser(
        type: UserTypes,
        userId: String,
        firstName: String,
        lastName: String,
        email: String,
        profilePicture: String,
        location: Location,
    ): IUser {
        return when (type) {
            UserTypes.STANDARD -> {
                StandardUser(userId, firstName, lastName, email, profilePicture, location)
            }
            UserTypes.PREMIUM -> {
                PremiumUser(userId, firstName, lastName, email, profilePicture, location)
            }
        }
    }

}