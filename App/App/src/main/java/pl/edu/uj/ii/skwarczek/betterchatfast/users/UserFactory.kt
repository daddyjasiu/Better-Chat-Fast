package pl.edu.uj.ii.skwarczek.betterchatfast.users

import android.location.Location
import pl.edu.uj.ii.skwarczek.betterchatfast.users.IUser
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EUserTypes
import pl.edu.uj.ii.skwarczek.betterchatfast.users.PremiumUser
import pl.edu.uj.ii.skwarczek.betterchatfast.users.StandardUser

object UserFactory {

    fun createUser(
        type: EUserTypes,
        userId: String,
        nickname: String,
        firstName: String,
        lastName: String,
        email: String,
        profilePicture: String,
        location: Location,
        matchmakingStates: EMatchmakingStates = EMatchmakingStates.NOT_MATCHMAKING
        ): IUser {
        return when (type) {
            EUserTypes.STANDARD -> {
                StandardUser(userId, nickname, firstName, lastName, email, profilePicture, location, matchmakingStates)
            }
            EUserTypes.PREMIUM -> {
                PremiumUser(userId,nickname, firstName, lastName, email, profilePicture, location, matchmakingStates)
            }
        }
    }

}