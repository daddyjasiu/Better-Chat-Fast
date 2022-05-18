package pl.edu.uj.ii.skwarczek.betterchatfast.users

import android.location.Location
import com.google.firebase.firestore.PropertyName

data class StandardUser(
    override var userId: String,
    override var nickname: String,
    override var firstName: String,
    override var lastName: String,
    override var email: String,
    override var profilePicture: String,
    override var location: Location,

    @PropertyName("isPremium")
    var isPremium: Boolean = false,
    @PropertyName("isAfterOnboarding")
    var isAfterOnboarding: Boolean = false,

    ) : IUser