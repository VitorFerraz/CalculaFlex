package br.com.vitor.calculaflex.ui.login

import android.app.DownloadManager
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import br.com.vitor.calculaflex.R
import br.com.vitor.calculaflex.exceptions.EmailInvalidException
import br.com.vitor.calculaflex.exceptions.PasswordInvalidException
import br.com.vitor.calculaflex.models.RequestState
import br.com.vitor.calculaflex.ui.base.BaseFragment

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login
    private lateinit var tvSubTitleLogin: TextView
    private lateinit var containerLogin: LinearLayout
    private lateinit var tvResetPassword: TextView
    private lateinit var tvNewAccount: TextView

    private lateinit var btLogin: Button
    private lateinit var etEmailLogin: EditText
    private  lateinit var etPasswordLogin: EditText

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView(view)
        startAnimation()
        registerObserver()

    }

    private fun registerObserver() {
        loginViewModel.loginState.observe(viewLifecycleOwner, Observer {
            when(it) {
                is RequestState.Success -> showSuccess()
                is RequestState.Error -> showError(it.throwable)
                is RequestState.Loading -> showLoading("Realizando a autenticação")
            }
        })
    }

    private fun showSuccess() {
        hideLoading()
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_loginFragment_to_main_nav_graph)
    }

    private fun showError(throwable: Throwable) {
        hideLoading()

        etPasswordLogin.error = null
        etEmailLogin.error = null

        when (throwable) {
            is EmailInvalidException -> {
                etEmailLogin.error = throwable.message
                etEmailLogin.requestFocus()
            }
            is PasswordInvalidException -> {
                etPasswordLogin.error = throwable.message
                etPasswordLogin.requestFocus()
            } else -> showMessage(throwable.message)

        }
    }





    private fun startAnimation() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_form_login)
        tvSubTitleLogin.startAnimation(anim)
        containerLogin.startAnimation(anim)
        tvResetPassword.startAnimation(anim)
        tvNewAccount.startAnimation(anim)
    }

    private fun setupView(view: View) {
        tvSubTitleLogin = view.findViewById(R.id.tvSubTitleLogin)
        containerLogin = view.findViewById(R.id.containerLogin)
        tvResetPassword = view.findViewById(R.id.tvResetPassword)
        tvNewAccount = view.findViewById(R.id.tvNewAccount)

        btLogin = view.findViewById(R.id.btLogin)
        etEmailLogin = view.findViewById(R.id.etEmailLogin)
        etPasswordLogin = view.findViewById(R.id.etPasswordLogin)
        
        btLogin.setOnClickListener { 
            loginViewModel.signIn(etEmailLogin.text.toString(), etPasswordLogin.text.toString())
        }
    }

}