@file:Suppress("DEPRECATION")

package com.example.audio_overlay

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.audio_overlay.databinding.ActivityMainBinding
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Environment
import android.media.MediaPlayer
import android.Manifest
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // variable for video picker
    lateinit var binding: ActivityMainBinding

    // variables for audio recording
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isRecording = false

    // request codes for permission
    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

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

        // call audio setup function defined below
        audioSetup()

        // audio play
        // get reference to button
        val playBtn = findViewById<Button>(R.id.playButton)
        // set on-click listener
        playBtn.setOnClickListener {
            playAudio(it)
        }

        // audio record
        val recordBtn = findViewById<Button>(R.id.recordButton)
        // set on-click listener
        recordBtn.setOnClickListener {
            recordAudio(it)
        }

        // audio stop
        val stopBtn = findViewById<Button>(R.id.stopButton)
        // set on-click listener
        stopBtn.setOnClickListener {
            stopAudio(it)
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

    // Audio Recording

    // function to check Microphone availability
    private fun hasMicrophone(): Boolean {
        val pmanager = this.packageManager
        return pmanager.hasSystemFeature(
            PackageManager.FEATURE_MICROPHONE)
    }

    private fun audioSetup() {

        if (!hasMicrophone()) {
            stopButton.isEnabled = false
            playButton.isEnabled = false
            recordButton.isEnabled = false
        } else {
            playButton.isEnabled = false
            stopButton.isEnabled = false
        }

        audioFilePath = Environment.getExternalStorageDirectory().absolutePath + "/myaudio.3gp"
        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE)
    }

    // function for audio recording
    fun recordAudio(view: View) {
        isRecording = true
        stopButton.isEnabled = true
        playButton.isEnabled = false
        recordButton.isEnabled = false

        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(
                MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setOutputFile(audioFilePath)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder?.start()
    }

    // function to stop audio recording
    fun stopAudio(view: View) {

        stopButton.isEnabled = false
        playButton.isEnabled = true

        if (isRecording) {
            recordButton.isEnabled = false
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            recordButton.isEnabled = true
        }
    }

    // function to play audio
    fun playAudio(view: View) {
        playButton.isEnabled = false
        recordButton.isEnabled = false
        stopButton.isEnabled = true

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioFilePath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    // function to request permissions
    private fun requestPermission(permissionType: String, requestCode: Int) {
        val permission = ContextCompat.checkSelfPermission(this,
            permissionType)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permissionType), requestCode
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0]
                    != PackageManager.PERMISSION_GRANTED) {

                    recordButton.isEnabled = false

                    Toast.makeText(this,
                        "Record permission required",
                        Toast.LENGTH_LONG).show()
                } else {
                    requestPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        STORAGE_REQUEST_CODE)
                }
                return
            }
            STORAGE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0]
                    != PackageManager.PERMISSION_GRANTED) {
                    recordButton.isEnabled = false
                    Toast.makeText(this,
                        "External Storage permission required",
                        Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
