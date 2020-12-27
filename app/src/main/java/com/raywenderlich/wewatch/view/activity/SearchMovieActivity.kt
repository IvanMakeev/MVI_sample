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
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.wewatch.App
import com.raywenderlich.wewatch.R
import com.raywenderlich.wewatch.action
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.snack
import com.raywenderlich.wewatch.view.adapter.SearchListAdapter
import com.raywenderlich.wewatch.view.viewmodel.SearchViewModel
import com.raywenderlich.wewatch.view.viewmodel.factory.BaseViewModelFactory
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*

class SearchMovieActivity : BaseActivity() {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var viewModel: SearchViewModel

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)
        initViewModel()
        initObservers()
        displayMoviesIntent()

        searchRecyclerView.adapter = SearchListAdapter(emptyList()) {
            viewModel.addMovieIntent(it)
        }
    }

    private fun initViewModel(){
        val factory = BaseViewModelFactory((application as App).getInteractor())
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }

    private fun initObservers(){
        viewModel.stateLiveData.observe(this, Observer(::render))
    }

    private fun displayMoviesIntent() =
        viewModel.displayMoviesIntent(intent.extras.getString("title", ""))

    override fun render(state: MovieState) {
        Log.d(TAG, "SearchMovieActivity State: ${state.javaClass.simpleName}")
        when (state) {
            is MovieState.LoadingState -> renderLoadingState()
            is MovieState.DataState -> renderDataState(state)
            is MovieState.ErrorState -> renderErrorState(state)
            is MovieState.ConfirmationState -> renderConfirmationState(state)
            is MovieState.FinishState -> renderFinishState()
        }
    }

    private fun renderLoadingState() {
        searchRecyclerView.isEnabled = false
        searchProgressBar.visibility = View.VISIBLE
    }

    private fun renderDataState(state: MovieState.DataState) {
        searchProgressBar.visibility = View.GONE
        searchRecyclerView.apply {
            isEnabled = true
            (adapter as SearchListAdapter).setMovies(state.data)
        }
    }

    private fun renderErrorState(state: MovieState.ErrorState) {
        searchProgressBar.visibility = View.GONE
        Toast.makeText(this, state.data, Toast.LENGTH_SHORT).show()
    }

    private fun renderConfirmationState(state: MovieState.ConfirmationState) {
        searchLayout.snack("Add ${state.movie.title} to your list?", Snackbar.LENGTH_LONG) {
            action(getString(R.string.ok)) {
                viewModel.confirmIntent(state.movie)
            }
        }
    }

    private fun renderFinishState() =
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })

    private companion object{
        const val TAG = "MVI_Example"
    }
}

