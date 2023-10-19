package com.sto_opka91.todoapp.fragments.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.sto_opka91.todoapp2.R
import com.sto_opka91.todoapp2.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

lateinit var auth:FirebaseAuth
lateinit var navController:NavController
lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)// Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.tvMessageLog.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.btnLogIn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val verifyPassword = binding.etRePassword.text.toString().trim()
            if(email.isNotEmpty() && password.isNotEmpty() && verifyPassword.isNotEmpty()){
                if(password==verifyPassword){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                        OnCompleteListener {

                            if(it.isSuccessful){
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(context, "Registered successfully",Toast.LENGTH_LONG).show()
                                navController.navigate(R.id.action_signUpFragment_to_homeFragment)
                            }else{
                                Toast.makeText(context, it.exception?.message,Toast.LENGTH_LONG).show()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    )
                }else{
                    Toast.makeText(context, getString(R.string.passwords_have_differences),Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }

            }else{
                Toast.makeText(context, getString(R.string.you_have_empty_fields),Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }

            }
        }


    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }


}