package pl.edu.uj.ii.skwarczek.betterchatfast.models

data class SendbirdUser(
    val user_id: String,
    val nickname: String,
    val profile_url: String
){
    override fun toString(): String {
        return "Category [user_id: ${this.user_id}, nickname: ${this.nickname}, profile_url: ${this.profile_url}]"
    }
}
