package pl.edu.uj.ii.skwarczek.betterchatfast.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.grpc.InternalChannelz.id
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.MainScreenActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper

class Onboarding4Fragment  : Fragment() {

    private lateinit var finishOnboardingButton: Button
    private lateinit var nicknameField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_4, container, false)

        initView(view)

        finishOnboardingButton.setOnClickListener {
            Log.d("Onboarding Fragments","TESTIONG FRAGTMENT 4")

            Toast.makeText(context, this.arguments?.get("nickname").toString(), Toast.LENGTH_SHORT).show()

//            print("AUUUGHHHHH")
//
//            val nickname = nicknameField?.text?.trim().toString()
//            val firstName = firstNameField?.text?.trim().toString()
//            val lastName = lastNameField?.text?.trim().toString()
//
//            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
//                FirestoreHelper.updateCurrentUserNicknameInFirebase(nickname)
//                FirestoreHelper.updateCurrentUserFirstNameInFirebase(firstName)
//                FirestoreHelper.updateCurrentUserLastNameInFirebase(lastName)
//
//                val intent = Intent(context, MainScreenActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
//            else{
//                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
//            }

        }

        return view
    }

    private fun initView(view: View){
        finishOnboardingButton = view.findViewById(R.id.onboarding_finish_button)

    }

}