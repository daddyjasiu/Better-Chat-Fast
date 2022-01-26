package pl.edu.uj.ii.skwarczek.betterchatfast.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.edu.uj.ii.skwarczek.productlist.fragments.SignInTabFragment
import pl.edu.uj.ii.skwarczek.productlist.fragments.SignUpTabFragment

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