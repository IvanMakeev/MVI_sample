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
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.wewatch.App
import com.raywenderlich.wewatch.R
import com.raywenderlich.wewatch.action
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.snack
import com.raywenderlich.wewatch.view.AddView
import com.raywenderlich.wewatch.view.presenter.AddPresenter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_movie.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*

class AddMovieActivity : BaseActivity(), AddView {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var presenter: AddPresenter
    private val publishSubject: PublishSubject<Movie> = PublishSubject.create()

    override fun getToolbarInstance() = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)
        presenter = AddPresenter((application as App).getInteractor())
        presenter.bind(this)
        searchButton.setOnClickListener { goToSearchMovieActivity() }
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

    private fun goToSearchMovieActivity() {
        if (titleEditText.text.toString().isNotBlank()) {
            val intent = Intent(this, SearchMovieActivity::class.java).apply {
                putExtra("title", titleEditText.text.toString())
            }
            startActivity(intent)
        } else {
            showMessage("You must enter a title")
        }
    }

    private fun showMessage(msg: String) {
        addLayout.snack((msg), Snackbar.LENGTH_LONG) {
            action(getString(R.string.ok)) {
            }
        }
    }

    fun addMovieClick(view: View) {
        if (titleEditText.text.toString().isNotBlank()) {
            publishSubject.onNext(
                Movie(
                    title = titleEditText.text.toString(),
                    releaseDate = yearEditText.text.toString()
                )
            )
        } else {
            showMessage("You must enter a title")
        }
    }

    override fun addMovieIntent(): Observable<Movie> =
        publishSubject

    override fun render(state: MovieState) {
        when (state) {
            is MovieState.FinishState -> renderFinishState()
        }
    }

    private fun renderFinishState() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}
