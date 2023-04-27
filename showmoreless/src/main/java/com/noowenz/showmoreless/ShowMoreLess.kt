package com.noowenz.showmoreless

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 *  Copyright (c) 2020 Nabin Shrestha.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Created by Nabin Shrestha on 5/8/2020.
 */

class ShowMoreLess private constructor(builder: Builder) {
    // required
    private val context: Context = builder.context
    private var onShowMoreLessClickedListener: OnShowMoreLessClickedListener? = null

    // optional
    private val textLength: Int
    private val textLengthType: Int
    private val moreLabel: String
    private val lessLabel: String
    private val moreLabelColor: Int
    private val lessLabelColor: Int
    private val labelUnderLine: Boolean
    private val labelBold: Boolean
    private val expandAnimation: Boolean
    private val textClickableInExpand: Boolean
    private val textClickableInCollapse: Boolean
    private val enableLinkify: Boolean

    init {
        this.textLength = builder.textLength
        this.textLengthType = builder.textLengthType
        this.moreLabel = builder.moreLabel
        this.lessLabel = builder.lessLabel
        this.moreLabelColor = builder.moreLabelColor
        this.lessLabelColor = builder.lessLabelColor
        this.labelUnderLine = builder.labelUnderLine
        this.labelBold = builder.labelBold
        this.expandAnimation = builder.expandAnimation
        this.textClickableInExpand = builder.textClickableInExpand
        this.textClickableInCollapse = builder.textClickableInCollapse
        this.enableLinkify = builder.enableLinkify
    }

    fun addShowMoreLess(
        textView: TextView,
        text: CharSequence,
        isContentExpanded: Boolean
    ) {
        if (textLengthType == TYPE_CHARACTER) {
            if (text.length <= textLength) {
                textView.text = text
                return
            }
        } else {//TYPE_LINE
            textView.maxLines = textLength
            textView.text = text
        }

        textView.post(Runnable {
            try {
                val trimText = trimText(text)
                textView.text = trimText
                if (trimText.isEmpty())
                    return@Runnable
                if (textLengthType == TYPE_LINE) {
                    when {
                        textView.layout != null && textView.layout.lineCount <= textLength -> {
                            textView.text = trimText
                            return@Runnable
                        }
                        isContentExpanded -> {//Initial condition is expand
                            addShowLess(textView, trimText)
                            return@Runnable
                        }
                        else -> {//Initial condition is collapse
                            addShowMore(textView, trimText)
                            return@Runnable
                        }
                    }
                } else {
                    when {
                        isContentExpanded -> {//Initial condition is expand
                            addShowLess(textView, trimText)
                            return@Runnable
                        }
                        else -> {///Initial condition is collapse
                            addShowMore(textView, trimText)
                            return@Runnable
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun addShowMore(
        textView: TextView,
        trimText: CharSequence
    ) {
        try {
            val newSubString: CharSequence
            if (textLengthType == TYPE_LINE) {
                val lp = textView.layoutParams as ViewGroup.MarginLayoutParams
                val subString = trimText.substring(
                    startIndex = textView.layout.getLineStart(0),
                    endIndex = textView.layout.getLineEnd(textLength - 1)
                )
                newSubString = if (!subString.endsWith("\n", false)) {
                    val startRange = subString.length - (moreLabel.length + 4 + lp.rightMargin / 6)
                    val endRange = subString.length
                    if (startRange > 0) {
                        subString.removeRange(startRange, endRange)
                    } else {
                        subString
                    }
                } else {
                    subString.removeSuffix("\n")
                }
            } else {
                newSubString = trimText.subSequence(0, textLength)
            }
            val spannableStringBuilder = SpannableStringBuilder(
                newSubString
            ).apply {
                this.append("...")
                this.append(moreLabel)
            }

            val ss = SpannableString.valueOf(spannableStringBuilder)
            val moreLabelClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    addShowLess(textView, trimText)
                    onShowMoreLessClickedListener?.onShowMoreClicked()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = labelUnderLine
                    ds.isFakeBoldText = labelBold
                    ds.color = moreLabelColor
                }
            }
            ss.setSpan(
                moreLabelClickableSpan,
                ss.length - moreLabel.length,
                ss.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            /**
             * [textClickableInExpand] will enable text clickable
             * if [moreLabel] is empty it will allow to click on [textView]
             * else will make [ClickableSpan]
             */
            if (textClickableInExpand) {
                if (moreLabel.isEmpty()) {
                    Handler().post {
                        textView.setOnClickListener {
                            addShowLess(textView, trimText)
                            onShowMoreLessClickedListener?.onShowMoreClicked()
                            it?.setOnClickListener(null)
                        }
                    }
                } else {
                    val exceptMoreLabelClickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            addShowLess(textView, trimText)
                            onShowMoreLessClickedListener?.onShowMoreClicked()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                            ds.color = textView.currentTextColor
                        }
                    }
                    ss.setSpan(
                        exceptMoreLabelClickableSpan,
                        0,
                        ss.length - moreLabel.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            if (expandAnimation) {
                val layoutTransition = LayoutTransition()
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                (textView.parent as ViewGroup).layoutTransition = layoutTransition
            }
            textView.text = ss
            if (enableLinkify)
                Linkify.addLinks(textView, Linkify.ALL)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addShowLess(
        textView: TextView,
        trimText: CharSequence
    ) {
        try {
            textView.maxLines = Integer.MAX_VALUE
            val spannableStringBuilder = SpannableStringBuilder(trimText).apply {
                if (lessLabel.isNotEmpty()) {
                    this.append(lessLabel)
                }
            }

            val ss = SpannableString.valueOf(spannableStringBuilder)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    if (lessLabel.isNotEmpty()) {
                        addShowMore(textView, trimText)
                        onShowMoreLessClickedListener?.onShowLessClicked()
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = labelUnderLine
                    ds.isFakeBoldText = labelBold
                    ds.color = lessLabelColor
                }
            }
            if (lessLabel.isNotEmpty())
                ss.setSpan(
                    clickableSpan,
                    ss.length - lessLabel.length,
                    ss.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            /**
             * [textClickableInCollapse] will enable text clickable
             * if [lessLabel] is empty it will allow to click on [textView]
             * else will make [ClickableSpan]
             */
            if (textClickableInCollapse) {
                if (lessLabel.isEmpty()) {
                    Handler().post {
                        textView.setOnClickListener {
                            addShowMore(textView, trimText)
                            onShowMoreLessClickedListener?.onShowLessClicked()
                            it?.setOnClickListener(null)
                        }
                    }
                } else {
                    val exceptLessLabelClickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            addShowMore(textView, trimText)
                            onShowMoreLessClickedListener?.onShowLessClicked()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                            ds.color = textView.currentTextColor
                        }
                    }
                    ss.setSpan(
                        exceptLessLabelClickableSpan,
                        0,
                        ss.length - lessLabel.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            if (expandAnimation) {
                val layoutTransition = LayoutTransition()
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
                (textView.parent as ViewGroup).layoutTransition = layoutTransition
            }
            textView.text = ss
            if (enableLinkify)
                Linkify.addLinks(textView, Linkify.ALL)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Support [SpannableStringBuilder] trim without loosing span added on it
     */
    private fun trimText(text: CharSequence): CharSequence {
        var length = text.length
        val trimmedLength: Int = TextUtils.getTrimmedLength(text)
        if (length > trimmedLength) {
            val builder = SpannableStringBuilder(text)
            // Remove white spaces from the start.
            var start = 0
            while (start < length && builder[start] <= ' ') {
                start++
            }
            builder.delete(0, start)
            length -= start
            // Remove white spaces from the end.
            var end = length
            while (end >= 0 && builder[end - 1] <= ' ') {
                end--
            }
            builder.delete(end, length)
            return builder
        }
        return text
    }

    fun setListener(listener: OnShowMoreLessClickedListener) {
        onShowMoreLessClickedListener = listener
    }

    interface OnShowMoreLessClickedListener {
        fun onShowMoreClicked()
        fun onShowLessClicked()
    }

    class Builder(// required
        val context: Context
    ) {
        // optional
        var textLength = 100
        var textLengthType = TYPE_CHARACTER
        var moreLabel = "show more"
        var lessLabel = "show less"
        var moreLabelColor = Color.parseColor("#ffffff")
        var lessLabelColor = Color.parseColor("#ffffff")
        var labelUnderLine = false
        var labelBold = false
        var expandAnimation = false
        var textClickableInExpand = false
        var textClickableInCollapse = false
        var enableLinkify = false

        /**
         * @param length can be no. of line OR no. of characters - default is 100 character
         * @param textLengthType ReadMoreOption.TYPE_LINE for no. of line OR
         * ReadMoreOption.TYPE_CHARACTER for no. of character
         * - default is ReadMoreOption.TYPE_CHARACTER
         * @return Builder obj
         */
        fun textLengthAndLengthType(length: Int, textLengthType: Int): Builder {
            this.textLength = length
            this.textLengthType = textLengthType
            return this
        }

        /**
         * @param moreLabel 's text
         */
        fun showMoreLabel(moreLabel: String): Builder {
            this.moreLabel = moreLabel
            return this
        }

        /**
         * @param lessLabel's text
         */
        fun showLessLabel(lessLabel: String): Builder {
            this.lessLabel = lessLabel
            return this
        }

        /**
         * @param moreLabelColor's text color
         *
         */
        fun showMoreLabelColor(moreLabelColor: Int): Builder {
            this.moreLabelColor = moreLabelColor
            return this
        }

        /**
         * @param lessLabelColor's text color
         */
        fun showLessLabelColor(lessLabelColor: Int): Builder {
            this.lessLabelColor = lessLabelColor
            return this
        }

        /**
         * @param labelUnderLine is boolean for to enable or disable text underline
         */
        fun labelUnderLine(labelUnderLine: Boolean): Builder {
            this.labelUnderLine = labelUnderLine
            return this
        }

        /**
         * @param labelBold is boolean to enable or disable bold label text
         */
        fun labelBold(labelBold: Boolean): Builder {
            this.labelBold = labelBold
            return this
        }

        /**
         * @param linkify is boolean to enable or disable linkify on text
         */
        fun enableLinkify(linkify: Boolean): Builder {
            this.enableLinkify = linkify
            return this
        }

        /**
         * @param textClickableInCollapse for text collapse condition
         * @param textClickableInExpand for text expand condition
         */
        fun textClickable(
            textClickableInExpand: Boolean,
            textClickableInCollapse: Boolean
        ): Builder {
            this.textClickableInExpand = textClickableInExpand
            this.textClickableInCollapse = textClickableInCollapse
            return this
        }

        /**
         * @param expandAnimation is boolean for to control text expand or collapse animation
         */
        fun expandAnimation(expandAnimation: Boolean): Builder {
            this.expandAnimation = expandAnimation
            return this
        }

        fun build(): ShowMoreLess {
            return ShowMoreLess(this)
        }
    }

    companion object {
        private val TAG = ShowMoreLess::class.java.simpleName
        const val TYPE_LINE = 1
        const val TYPE_CHARACTER = 2
    }
}