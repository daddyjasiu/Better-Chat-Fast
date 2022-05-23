package pl.edu.uj.ii.skwarczek.betterchatfast.room

import pl.edu.uj.ii.skwarczek.betterchatfast.users.IUser

data class RoomModel (
    val roomId: String,
    val users: MutableList<IUser>,
    val time: Float,
    val freeSlots: Int,
    val maxSlots: Int = 2
){

}