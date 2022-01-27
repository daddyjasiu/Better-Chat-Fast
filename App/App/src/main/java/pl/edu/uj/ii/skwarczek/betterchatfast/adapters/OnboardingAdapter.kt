package pl.edu.uj.ii.skwarczek.betterchatfast.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.OnboardingActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.fragments.Onboarding1Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.fragments.Onboarding2Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.fragments.Onboarding3Fragment
import pl.edu.uj.ii.skwarczek.betterchatfast.fragments.Onboarding4Fragment

class OnboardingAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val NUM_PAGES = 4

    override fun getCount(): Int {
        return NUM_PAGES
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 ->{
                val onboardingTab1 = Onboarding1Fragment()
                return onboardingTab1
            }
            1 ->{
                val onboardingTab2 = Onboarding2Fragment()
                return onboardingTab2
            }
            2 ->{
                val onboardingTab3 = Onboarding3Fragment()
                return onboardingTab3
            }
            3 ->{
                val onboardingTab4 = Onboarding4Fragment()
                return onboardingTab4
            }
        }
        return Onboarding1Fragment()
    }
}