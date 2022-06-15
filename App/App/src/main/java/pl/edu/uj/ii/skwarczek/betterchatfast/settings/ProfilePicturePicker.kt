package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import java.io.IOException

class ProfilePicturePicker : AppCompatActivity() {

    private var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private lateinit var imagePreview: ImageView
    private lateinit var btn_choose_image: Button
    private lateinit var btn_upload_image: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture_picker)

        auth = FirebaseAuth.getInstance()

        launchGallery()
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                uploadImage()
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference?.child("myImages/" + auth.currentUser!!.uid)
            ref?.putFile(filePath!!)
            Toast.makeText(this, "Profile photo uploaded!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
        }
    }
}

