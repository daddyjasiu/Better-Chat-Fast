package pl.edu.uj.ii.skwarczek.betterchatfast.signin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class SignInAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val totalTabs: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> SignInTabFragment()
            else -> SignUpTabFragment()
        }
    }
}