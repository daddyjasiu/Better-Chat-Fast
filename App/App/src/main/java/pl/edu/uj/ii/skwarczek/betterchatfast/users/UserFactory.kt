package pl.edu.uj.ii.skwarczek.betterchatfast.users


object UserFactory {

    fun createUser(
        type: EUserTypes,
        userId: String,
        nickname: String,
        firstName: String,
        lastName: String,
        email: String,
        profilePicture: String,
        matchmakingStates: EMatchmakingStates = EMatchmakingStates.NOT_MATCHMAKING,
        roomId: String = ""
        ): IUser {
        return when (type) {
            EUserTypes.STANDARD -> {
                StandardUser(userId, nickname, firstName, lastName, email, profilePicture, matchmakingStates,roomId)
            }
            EUserTypes.PREMIUM -> {
                PremiumUser(userId,nickname, firstName, lastName, email, profilePicture, matchmakingStates,roomId)
            }
        }
    }

}