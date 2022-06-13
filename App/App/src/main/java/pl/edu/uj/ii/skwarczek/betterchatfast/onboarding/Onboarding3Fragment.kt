package pl.edu.uj.ii.skwarczek.betterchatfast.onboarding

import android.app.Activity
import android.content.Intent
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext


class Onboarding3Fragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var imagePreview: ImageView
    lateinit var btn_choose_image: Button
    lateinit var btn_upload_image: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_3, container, false)
        initView(view)
        btn_choose_image.setOnClickListener { launchGallery() }
        btn_upload_image.setOnClickListener { uploadImage() }
        return view
    }
    private fun initView(view: View) {
        auth = FirebaseAuth.getInstance()

        btn_choose_image = view.findViewById(R.id.btn_choose_image)
        btn_upload_image = view.findViewById(R.id.btn_upload_image)
        imagePreview = view.findViewById(R.id.image_preview)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
                imagePreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("myImages/" + auth.currentUser!!.uid)
            val uploadTask = ref?.putFile(filePath!!)

        }else{
            Toast.makeText(activity, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }
}