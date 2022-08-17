package com.example.audio_overlay

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.audio_overlay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.selectVideo.setOnClickListener{

            val intent = Intent()

            intent.action = Intent.ACTION_GET_CONTENT

            intent.type="video/*"

            startActivityForResult(intent,100)
        }

        binding.videoView.setOnPreparedListener{
            it.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100){
            if(data!=null){
                val uri: Uri =data.data!!
                binding.videoView.setVideoURI(uri)
            }
        }
        else{
            Toast.makeText(this, "You didn't select any Video",Toast.LENGTH_SHORT ).show()
        }
    }
}
