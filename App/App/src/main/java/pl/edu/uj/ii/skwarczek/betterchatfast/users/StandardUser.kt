package pl.edu.uj.ii.skwarczek.betterchatfast.users

import com.google.firebase.firestore.PropertyName

data class StandardUser(
    override var userId: String,
    override var nickname: String,
    override var firstName: String,
    override var lastName: String,
    override var email: String,
    override var profilePicture: String,
    override var matchmakingState: EMatchmakingStates,
    override var roomId: String,

    var isPremium: Boolean = false,
    var isAfterOnboarding: Boolean = false,

    ) : IUser