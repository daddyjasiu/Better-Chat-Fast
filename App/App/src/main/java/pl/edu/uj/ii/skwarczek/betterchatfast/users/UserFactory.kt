package pl.edu.uj.ii.skwarczek.betterchatfast.users

import android.location.Location

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
        matchmakingStates: EMatchmakingStates = EMatchmakingStates.NOT_MATCHMAKING,
        roomId: String = ""
        ): IUser {
        return when (type) {
            EUserTypes.STANDARD -> {
                StandardUser(userId, nickname, firstName, lastName, email, profilePicture, location, matchmakingStates,roomId)
            }
            EUserTypes.PREMIUM -> {
                PremiumUser(userId,nickname, firstName, lastName, email, profilePicture, location, matchmakingStates,roomId)
            }
        }
    }

}