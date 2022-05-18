package pl.edu.uj.ii.skwarczek.betterchatfast.settings

interface ISettings {
    fun setUserNickname(nickname: String)
    fun setUserFirstName(firstName: String)
    fun setUserLastName(lastName: String)
}