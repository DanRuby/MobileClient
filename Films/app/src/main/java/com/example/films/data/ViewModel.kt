package com.example.films.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.films.retrofit.Common
import com.example.films.retrofit.RetrofitServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import org.xmlpull.v1.XmlPullParserException

import org.xmlpull.v1.XmlPullParser

import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.create
import okhttp3.internal.http2.Http2Reader


class ViewModel : ViewModel() {

    private var _ModelMock: MutableLiveData<Model> =
        MutableLiveData() // Модель данных временная
    val modelMock: LiveData<Model> // Внешняя временная модель данных
        get() = _ModelMock

    private var _Model: MutableLiveData<Model> = MutableLiveData() // Модель данных
    val model: LiveData<Model> // Внешняя модель данных
        get() = _Model

    var filmId = "0"

    var retrofit: RetrofitServices = Common.retrofitService

    init {
        val testResponse =
            Response(
                "",
                "",
                "",
            )

        val testList = listOf(
            testResponse,
            testResponse,
            testResponse
        )
        val testModel = Model()
        testModel.films = testList

        _ModelMock.postValue(testModel)
    }

    fun loadSoapData() {

        _Model.value = Model()

        val client = OkHttpClient()

        val mediaType: MediaType = "text/xml".toMediaTypeOrNull()!!

        val soap =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body/></soapenv:Envelope>"

        val url = "http://10.0.2.2:8081/SOAP-service/services/FilmServiceImpl"

        val body = RequestBody.create(mediaType, soap)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("SOAPAction", "GETALL")
            .addHeader("content-type", "text/xml")
            .build()

        GlobalScope.launch {
            val response = client.newCall(request).execute()

            client.newCall(request).enqueue(object : okhttp3.Callback {

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    var response = response.body?.string()
                    println(response)

                    try {
                        var index = 0

                        var model = Model()
                        var list = mutableListOf<Response>()

                        var description: String? = null
                        var id: String? = null
                        var image: String? = null
                        var isLiked: Boolean? = null
                        var name: String? = null
                        var producer: String? = null
                        var year: String? = "1900"

                        val factory = XmlPullParserFactory.newInstance()
                        factory.isNamespaceAware = true
                        val xpp = factory.newPullParser()
                        xpp.setInput(StringReader(response))
                        var eventType = xpp.eventType
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_DOCUMENT) {
                            } else if (eventType == XmlPullParser.START_TAG) {
                                if (xpp.name == "description") {
                                    eventType = xpp.next()
                                    description = xpp.text
                                }
                                if (xpp.name == "id") {
                                    eventType = xpp.next()
                                    id = xpp.text
                                }
                                if (xpp.name == "image") {
                                    eventType = xpp.next()
                                    image = xpp.text
                                }
                                if (xpp.name == "isLiked") {
                                    eventType = xpp.next()
                                    isLiked = xpp.text.toBoolean()
                                }
                                if (xpp.name == "name") {
                                    eventType = xpp.next()
                                    name = xpp.text
                                }
                                if (xpp.name == "producer") {
                                    eventType = xpp.next()
                                    producer = xpp.text
                                }
                                if (xpp.name == "year") {
                                    eventType = xpp.next()
                                    year = xpp.text

                                    list.add(
                                        Response(
                                            id,
                                            image,
                                            name,
                                            description,
                                            year,
                                            producer,
                                            isLiked
                                        )
                                    )
                                }
                            } else if (eventType == XmlPullParser.END_TAG) {
                            } else if (eventType == XmlPullParser.TEXT) {
                            }
                            eventType = xpp.next()
                        }

                        model.films = list
                        _Model.postValue(model)

                    } catch (e: XmlPullParserException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            })
        }

/*        val testResponse =
            Response(
                "1",
                "https://img.youscreen.ru/wall/14982320993031/14982320993031_1920x1200.jpg",
                "Avatar ",
                "Former Marine Jake Sully is confined to a wheelchair. Despite his frail body, Jake is still a warrior at heart. He is tasked with making a journey of several light years to the base of earthlings on the planet Pandora, where corporations extract a rare mineral that is of great importance for the Earth's exit from the energy crisis.",
                "2009",
                "James Cameron"
            )

        val testList = listOf(
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse
        )
        val testModel = Model()
        testModel.films = testList

        _Model.postValue(testModel)*/
    }

    fun loadRestData() {

        _Model.value = Model()

        val callPresentations: Call<MutableList<Response>> =
            retrofit.getMovieList()
        callPresentations.enqueue(object : Callback<MutableList<Response>?> {
            override fun onResponse(
                call: Call<MutableList<Response>?>,
                response: retrofit2.Response<MutableList<Response>?>
            ) {
                val responseModel = Model()
                responseModel.films = response.body()
                _Model.postValue(responseModel)

                println(response.body()?.get(0)?.isLiked)
            }

            override fun onFailure(call: Call<MutableList<Response>?>, t: Throwable) {
            }
        })

/*        val testResponse =
            Response(
                "1",
                "https://avatars.mds.yandex.net/get-zen_doc/1899990/pub_5e9d8d6688edb84e60bf981e_5e9d91bf3baf6874e893c7b6/scale_1200",
                "Однажды в Голливуде",
                "1969 год, золотой век Голливуда уже закончился. Известный актёр Рик Далтон и его дублер Клифф Бут пытаются найти свое место в стремительно меняющемся мире киноиндустрии.",
                "2019",
                "Квентин Тарантино"
            )

        val testList = listOf(
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse,
            testResponse
        )
        val testModel = Model()
        testModel.films = testList

        _Model.postValue(testModel)*/
    }
}