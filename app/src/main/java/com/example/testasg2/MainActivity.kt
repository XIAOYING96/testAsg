package com.example.testasg2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.example.testasg2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.stream.Stream


class MainActivity : AppCompatActivity() {
    var sImage:String= " "
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseReference
    private lateinit var storage:StorageReference
    private lateinit var mProfileUri: Uri

    private lateinit var imageView: ImageView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById(R.id.displayImage)
        storage=FirebaseStorage.getInstance().getReference("Images")
        db = FirebaseDatabase.getInstance().getReference("donation")
    }

    data class donation(
        val id:String,
        val text:String,
        val imageUrl: String
    )

    fun save_data(view: View) {
        val itemName = binding.editTextText.text.toString()
        var item : donation

        //val id = db.push().key
        val id = db.push().key!!

        mProfileUri?.let {
            storage.child(id).putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            Toast.makeText(this," Image stored successfully", Toast.LENGTH_SHORT).show()
                            val imgUrl = mProfileUri.toString()


                            item = donation(id, itemName, imgUrl)

                            db.child(id).setValue(item)
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        this,
                                        " data stored successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        "error ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                        }
                }
        }
    }


            private val startForProfileImageResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    val resultCode = result.resultCode
                    val data = result.data

                    if (resultCode == Activity.RESULT_OK) {
                        val fileUri = data?.data!!

                        mProfileUri = fileUri
                        imageView.setImageURI(fileUri)

                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }

            fun Upload_image(view: View) {
                ImagePicker.with(this)
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )  //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

        }



