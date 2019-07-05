package com.vortex.networking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vortex.openquest.*
import com.vortex.openquest.config.Response
import com.vortex.openquest.request.GetRequest
import com.vortex.openquest.util.build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class Todo(
    val userId: String? = null,
    val id: Int? = null,
    val title: String? = null,
    val completed: Boolean? = null
)

data class Photo(
    var id: Int? = null,
    var title: String? = null,
    var url: String? = null,
    var thumbnailUrl: String? = null
)

class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uiScope.launch {

            val response = Openquest.processRequest<List<Todo>>(GetRequest(build {
                pathUrl = "https://jsonplaceholder.typicode.com/posts"
            }))

            when(response) {
                is Response.Success -> {
                    print(response.data)
                }
                is Response.Failure -> {

                }
            }


//            val builder = build {
//                pathUrl = "/todos"
//            }
//
//            val photo = Photo(
//                title = "Xablau",
//                url = "Xablau.com.br",
//                thumbnailUrl = "Xablau.com.br"
//            )
//
//            val postBuilder = build<Photo> {
//                connectionUrl = "https://jsonplaceholder.typicode.com/photos"
//                requestBody = photo
//            }
//
//            val postResponse = doPost<Photo, Photo>(postBuilder)
//            print(postResponse)
//
//            val response = doGet<List<Todo>>(builder)
//            when (response) {
//                is Result.Success -> {
//                    response.data.forEach {
//                        Log.d("PRINT", it.title)
//                    }
//                }
//            }

        }
    }
}
