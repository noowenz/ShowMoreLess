package com.noowenz.showmoreless

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        ShowMoreLess.Builder(this)
                /*.textLengthAndLengthType(
                        length = 100,
                        textLengthType = ShowMoreLess.TYPE_CHARACTER
                )*/
                .textLengthAndLengthType(
                        length = 5,
                        textLengthType = ShowMoreLess.TYPE_LINE
                )
                .showMoreLabel("show more")
                .showLessLabel("show less")
                .showMoreLabelColor(Color.parseColor("#FF0000"))
                .showLessLabelColor(Color.parseColor("#FF0000"))
                .labelUnderLine(labelUnderLine = true)
                .expandAnimation(expandAnimation = true)
                .textClickable(
                        textClickableInExpand = true,
                        textClickableInCollapse = true
                )
                .build().apply {
                    addShowMoreLess(textView = tv_first, text = tv_first.text, isContentExpanded = false)
                    setListener(object : ShowMoreLess.OnShowMoreLessClickedListener {
                        override fun onShowMoreClicked() {
                            //We can handle or save show more state
                        }

                        override fun onShowLessClicked() {
                            //We can handle or save show less state
                        }
                    })
                }
    }
}
