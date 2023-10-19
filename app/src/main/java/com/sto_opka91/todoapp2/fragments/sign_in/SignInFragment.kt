package com.sto_opka91.todoapp.fragments.sign_in

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
import com.sto_opka91.todoapp2.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var navController: NavController
    lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.tvMessageLog.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.btnLogIn.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        binding.progressBar2.visibility = View.GONE
                        if (it.isSuccessful) {

                            Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate(R.id.action_signInFragment_to_homeFragment)
                        } else {
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
                )
            } else {
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(
                    context,
                    getString(R.string.you_have_empty_fields),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }
}