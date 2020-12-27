/*
 *
 *  Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.raywenderlich.wewatch.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.wewatch.App
import com.raywenderlich.wewatch.R
import com.raywenderlich.wewatch.action
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.snack
import com.raywenderlich.wewatch.view.viewmodel.AddViewModel
import com.raywenderlich.wewatch.view.viewmodel.factory.BaseViewModelFactory
import kotlinx.android.synthetic.main.activity_add_movie.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*

class AddMovieActivity : BaseActivity() {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var viewModel: AddViewModel

    override fun getToolbarInstance() = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)
        initViewModel()
        initObservers()
        initClickListeners()
    }

    private fun initViewModel() {
        val factory = BaseViewModelFactory((application as App).getInteractor())
        viewModel = ViewModelProvider(this, factory).get(AddViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.stateLiveData.observe(this, Observer(::render))
    }

    private fun initClickListeners() {
        searchButton.setOnClickListener { goToSearchMovieActivity() }
        addMovieButton.setOnClickListener { addMovieClick() }
    }

    private fun goToSearchMovieActivity() {
        if (titleEditText.text.toString().isNotBlank()) {
            val intent = Intent(this, SearchMovieActivity::class.java).apply {
                putExtra("title", titleEditText.text.toString())
            }
            startActivity(intent)
        } else {
            viewModel.showMessageIntent(getString(R.string.error_message))
        }
    }

    private fun addMovieClick() {
        if (titleEditText.text.toString().isNotBlank()) {
            viewModel.addMovieIntent(
                Movie(
                    title = titleEditText.text.toString(),
                    releaseDate = yearEditText.text.toString()
                )
            )
        } else {
            viewModel.showMessageIntent(getString(R.string.error_message))
        }
    }

    override fun render(state: MovieState) {
        Log.d(TAG, "AddMovieActivity State: ${state.javaClass.simpleName}")
        when (state) {
            is MovieState.FinishState -> renderFinishState()
            is MovieState.ErrorState -> showMessage(state.data)
        }
    }

    private fun renderFinishState() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    private fun showMessage(msg: String) {
        addLayout.snack((msg), Snackbar.LENGTH_LONG) {
            action(getString(R.string.ok)) {
            }
        }
    }

    private companion object {
        const val TAG = "MVI_Example"
    }
}
