package pl.edu.uj.ii.skwarczek.betterchatfast.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.Onboarding1Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.Onboarding2Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.Onboarding3Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.Onboarding4Fragment

class OnboardingAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val NUM_PAGES = 4

    override fun getCount(): Int {
        return NUM_PAGES
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return Onboarding1Fragment()
            }
            1 -> {
                return Onboarding2Fragment()
            }
            2 -> {
                return Onboarding3Fragment()
            }
            3 -> {
                return Onboarding4Fragment()
            }
        }
        return Onboarding1Fragment()
    }
}