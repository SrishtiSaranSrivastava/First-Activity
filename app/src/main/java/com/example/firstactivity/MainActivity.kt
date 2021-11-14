package com.example.firstactivity

import android.app.ProgressDialog
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.firstactivity.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "MAIN TAG"

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.phoneLl.visibility = View.VISIBLE
        //binding.codeLl.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "OnVerificationCompleted:")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG, "OnVerificationFailed: ${e.message}")
                Toast.makeText(this@MainActivity,"${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "OnCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                Log.d(TAG, "OnCodeSent: $verificationId")
                //binding.phoneLl.visibility = View.VISIBLE
                //binding.codeLl.visibility = View.GONE
                Toast.makeText(this@MainActivity,"Verification Code Sent...", Toast.LENGTH_SHORT).show()
                binding.codeSentDescriptionTv.text = "Please type the verification code we sent to ${binding.phoneEt.text.toString()}"
            }
        }

        binding.button.setOnClickListener {
            val phone = binding.phoneEt.text.toString()
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }

        binding.resendCodeTv.setOnClickListener {
            val phone = binding.phoneEt.text.toString()
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else{
                resendVerificationCode(phone, forceResendingToken)
            }
        }

        binding.button2.setOnClickListener {
            val code = binding.phoneEt.text.toString()
            if(TextUtils.isEmpty(code)){
                Toast.makeText(this@MainActivity,"Please enter verification code", Toast.LENGTH_SHORT).show()
            }
            else{
                verifyPhoneNumberWithCode(mVerificationId, code)
            }
        }

        fun verifyPhoneNumberWithCode(verificationId: String?, code:String) {
            progressDialog.setMessage("Verifying code...")
            progressDialog.show()
            Log.d(TAG, "verificationPhoneNumberWithCode: $verificationId $code")

            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        }

    }

    private fun verifyPhoneNumberWithCode(mVerificationId: String?, code: String) {

        val verificationId = "" 
        val verificationId = ""
        Log.d(TAG, "verificationPhoneNumberWithCode: $verificationId $code")
        progressDialog.setMessage("Verifying code...")
        progressDialog.show()
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        //TODO("Not yet implemented")
        Log.d(TAG, "signInWithPhoneAuthCredential: ")
        progressDialog.setMessage("Logging in")
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val phone=firebaseAuth.currentUser.phoneNumber
                    Toast.makeText(this,"Logged in as $phone", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this,SecondActivity::class.java))
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "${e.message}",Toast.LENGTH_SHORT).show()
                }
    }

    private fun resendVerificationCode(phone: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {

        progressDialog.setMessage("Resending code...")
        progressDialog.show()
        Log.d(TAG, "resendVerificationCode: $phone")
        val token = null
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun startPhoneNumberVerification(phone: String) {

        Log.d(TAG, "startPhoneNumberVerification: $phone")
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


}