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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.wewatch.App
import com.raywenderlich.wewatch.R
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.view.adapter.MovieListAdapter
import com.raywenderlich.wewatch.view.viewmodel.MainViewModel
import com.raywenderlich.wewatch.view.viewmodel.factory.BaseViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*

class MainActivity : BaseActivity() {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var viewModel: MainViewModel

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        moviesRecyclerView.adapter = MovieListAdapter(emptyList())
        initViewModel()
        initObservers()
        initClickListeners()
        initItemTouchHelper()
    }

    private fun initViewModel() {
        val factory = BaseViewModelFactory((application as App).getInteractor())
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.stateLiveData.observe(this, Observer(::render))
    }

    private fun initClickListeners() {
        fab.setOnClickListener { goToAddActivity() }
    }

    private fun goToAddActivity() = startActivity(Intent(this, AddMovieActivity::class.java))

    private fun initItemTouchHelper() {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val movie =
                        (moviesRecyclerView.adapter as MovieListAdapter).getMoviesAtPosition(
                            position
                        )
                    viewModel.deleteMovieIntent(movie)
                }
            })
        helper.attachToRecyclerView(moviesRecyclerView)
    }

    override fun render(state: MovieState) {
        Log.d(TAG, "MainActivity State: ${state.javaClass.simpleName}")
        when (state) {
            is MovieState.LoadingState -> renderLoadingState()
            is MovieState.DataState -> renderDataState(state)
            is MovieState.ErrorState -> renderErrorState(state)
        }
    }

    private fun renderLoadingState() {
        moviesRecyclerView.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    private fun renderDataState(dataState: MovieState.DataState) {
        progressBar.visibility = View.GONE
        moviesRecyclerView.apply {
            isEnabled = true
            (adapter as MovieListAdapter).setMovies(dataState.data.sortedBy { it.releaseDate })
        }
    }

    private fun renderErrorState(dataState: MovieState.ErrorState) =
        Toast.makeText(this, dataState.data, Toast.LENGTH_SHORT).show()

    private companion object{
        const val TAG = "MVI_Example"
    }
}

