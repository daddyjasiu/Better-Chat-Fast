package pl.edu.uj.ii.skwarczek.betterchatfast.interfaces

import android.location.Location

open class IUser(
    open var firstName: String,
    open var lastName: String,
    open var email: String,
    open var profilePicture: String,
    open var isPremium: Boolean,
    open var location: Location,

    ) {

}