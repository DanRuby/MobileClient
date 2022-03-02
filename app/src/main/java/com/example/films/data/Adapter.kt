package com.example.films.data

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.films.R
import com.example.films.databinding.ListItemFilmBinding
import com.example.films.retrofit.Common
import com.example.films.retrofit.RetrofitServices
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.io.StringReader

data class RecyclerViewItem(val response: Response)

class FilmAdapter(private val clickListener: FilmListener) :
    RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

    companion object {
        var retrofit: RetrofitServices = Common.retrofitService
        var serverType = ""
    }

    var data = listOf<Response>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setServerType(t: String) {
        serverType = t
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(RecyclerViewItem(item), clickListener)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder private constructor(private val binding: ListItemFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecyclerViewItem, clickListener: FilmListener) {
            binding.listItemFilmLtBackground.setOnClickListener {
                clickListener.onClick(item)
            }
            binding.listItemFilmLikeImage.setOnClickListener {
                if (serverType == "REST") {
                    val callLike: Call<Unit> =
                        retrofit.like(item.response.id!!)
                    callLike.enqueue(object : Callback<Unit> {
                        override fun onResponse(
                            call: Call<Unit>,
                            response: retrofit2.Response<Unit>
                        ) {
                            item.response.isLiked = !item.response.isLiked!!
                            binding.listItemFilmLikeImage.alpha =
                                if (item.response.isLiked == true) 1.0f else 0.2f

                            if (item.response.isLiked == true) {
                                binding.listItemFilmLikeImage.animate().alpha(1.0f).scaleY(2.0f)
                                    .scaleX(2.0f).withEndAction {
                                        it.animate().scaleY(1.0f).scaleX(1.0f).duration = 100
                                    }.duration = 100
                            } else {
                                binding.listItemFilmLikeImage.animate().alpha(0.2f).scaleY(0.1f)
                                    .scaleX(0.1f).withEndAction {
                                        it.animate().scaleY(1.0f).scaleX(1.0f).duration = 100
                                    }.duration = 100
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                        }
                    })
                }
                if (serverType == "SOAP") {
                    val client = OkHttpClient()

                    val mediaType: MediaType = "text/xml".toMediaTypeOrNull()!!

                    val soap =
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://example.com\">\n" +
                                "   <soapenv:Header/>\n" +
                                "   <soapenv:Body>\n" +
                                "      <exam:id2>${item.response.id}</exam:id2>\n" +
                                "   </soapenv:Body>\n" +
                                "</soapenv:Envelope>"

                    val url = "http://10.0.2.2:8081/SOAP-service/services/FilmServiceImpl"

                    val body = RequestBody.create(mediaType, soap)
                    val request: Request = Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("SOAPAction", "")
                        .addHeader("content-type", "text/xml")
                        .build()

                    client.newCall(request).enqueue(object : okhttp3.Callback {

                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                        }

                        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                            item.response.isLiked = !item.response.isLiked!!
                            binding.listItemFilmLikeImage.alpha =
                                if (item.response.isLiked == true) 1.0f else 0.2f

                            if (item.response.isLiked == true) {
                                binding.listItemFilmLikeImage.animate().alpha(1.0f).scaleY(2.0f)
                                    .scaleX(2.0f).withEndAction {
                                        it.animate().scaleY(1.0f).scaleX(1.0f).duration = 100
                                    }.duration = 100
                            } else {
                                binding.listItemFilmLikeImage.animate().alpha(0.2f).scaleY(0.1f)
                                    .scaleX(0.1f).withEndAction {
                                        it.animate().scaleY(1.0f).scaleX(1.0f).duration = 100
                                    }.duration = 100
                            }
                        }
                    })
                }
            }

            if (item.response.image != null)
                Glide.with(itemView.context)
                    .load(item.response.image)
                    .placeholder(R.color.white_grey)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.listItemFilmProgressBar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(binding.listItemFilmImgImage)

            if (item.response.name != null)
                binding.listItemFilmTvName.text = item.response.name

            binding.listItemFilmLikeImage.alpha = if (item.response.isLiked == true) 1.0f else 0.2f
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemFilmBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class FilmListener(val clickListener: (filmId: String) -> Unit) {
    fun onClick(film: RecyclerViewItem) = clickListener(film.response.id!!)
}

