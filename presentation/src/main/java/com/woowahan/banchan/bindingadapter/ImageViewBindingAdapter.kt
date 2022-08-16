package com.woowahan.banchan.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.woowahan.banchan.R
import com.woowahan.banchan.extension.toCashString
import timber.log.Timber

@BindingAdapter("android:loadImageUseCoil")
fun loadImageUseCoil(imageView: ImageView, imageUrl: String) {
    imageView.load(imageUrl) {
        placeholder(R.drawable.bg_image_placeholder)
        error(R.drawable.bg_error)
        listener(
            onError = { _, error ->
                Timber.d("loadImageUseCoil onError => $imageUrl")
                error.throwable.printStackTrace()
            }
        )
    }
}

@BindingAdapter("android:priceLongToStr")
fun priceLongToStr(textView: TextView, priceLong: Long) {
    textView.text = priceLong.toCashString()
}