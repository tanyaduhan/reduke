package org.wit.reduke.activities.posts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_post.*
import org.jetbrains.anko.*
import org.wit.reduke.main.MainApp
import org.wit.reduke.models.posts.PostModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class PostAddEditActivity : AppCompatActivity(), AnkoLogger {

    var post = PostModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(org.wit.reduke.R.layout.activity_post)
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        app = application as MainApp
        var edit = false


        if (intent.hasExtra("post_edit")) {
            edit = true
            toolbarAdd.title = "Edit Reduke"
            setSupportActionBar(toolbarAdd)
            post = intent.extras.getParcelable<PostModel>("post_edit")
            cardPostTitle.setText(post.title)
            cardRedukeDescription.setText(post.text)
            deleteRedukeBtn.visibility = VISIBLE
            addRedukeBtn.setText(org.wit.reduke.R.string.button_savePost)
        }

        addRedukeBtn.setOnClickListener {
            post.title = cardPostTitle.text.toString()
            post.text = cardRedukeDescription.text.toString()
            post.timestamp = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
            if (post.title.isEmpty() or post.text.isEmpty()) {
                toast(org.wit.reduke.R.string.hint_EnterPostTitle)
            } else {
                if (edit) {
                    app.posts.update(post.copy())
                } else {
                    app.posts.create(post.copy())
                }
                info("Created: $post")
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }

        }

        deleteRedukeBtn.setOnClickListener {
            alert(org.wit.reduke.R.string.deletePrompt) {
                yesButton {
                    app.posts.delete(post)
                    finish()
                }
                noButton {}
            }.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(org.wit.reduke.R.menu.menu_post_add_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            org.wit.reduke.R.id.item_cancel -> {
                alert(org.wit.reduke.R.string.unsavedPrompt) {
                    yesButton {
                        finish()
                    }
                    noButton {}
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        alert(org.wit.reduke.R.string.unsavedPrompt) {
            yesButton {
                finish()
                super.onBackPressed()
            }
            noButton {}
        }.show()
    }


}

