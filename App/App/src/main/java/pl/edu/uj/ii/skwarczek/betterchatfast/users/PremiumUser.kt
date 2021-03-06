package pl.edu.uj.ii.skwarczek.betterchatfast.users

import com.google.firebase.firestore.PropertyName

data class PremiumUser(
    override var userId: String,
    override var nickname: String,
    override var firstName: String,
    override var lastName: String,
    override var email: String,
    override var profilePicture: String,
    override var matchmakingState: EMatchmakingStates,
    override var roomId: String,
    override var chattingTime: Double,

    var isPremium: Boolean = true,
    var isAfterOnboarding: Boolean = false,

    ): IUser