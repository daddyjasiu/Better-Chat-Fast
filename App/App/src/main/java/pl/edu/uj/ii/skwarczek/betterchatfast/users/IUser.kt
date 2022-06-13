package pl.edu.uj.ii.skwarczek.betterchatfast.users


interface IUser{
    var userId: String
    var nickname: String
    var firstName: String
    var lastName: String
    var email: String
    var profilePicture: String
    var matchmakingState: EMatchmakingStates
    var roomId: String
    var chattingTime: Double

}