package pl.edu.uj.ii.skwarczek.betterchatfast.interfaces

import android.location.Location

interface IUser{
    var userId: String
    var nickname: String
    var firstName: String
    var lastName: String
    var email: String
    var profilePicture: String
    var location: Location
}