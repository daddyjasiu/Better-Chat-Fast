package pl.edu.uj.ii.skwarczek.betterchatfast.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.edu.uj.ii.skwarczek.productlist.fragments.SignInTabFragment
import pl.edu.uj.ii.skwarczek.productlist.fragments.SignUpTabFragment

public class SignInAdapter(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
    private val context: Context,
    private val totalTabs: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return SignInTabFragment()
            else -> return SignUpTabFragment()
        }
    }
}