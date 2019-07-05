package com.vortex.networking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vortex.openquest.Openquest
import com.vortex.openquest.request.GetRequest
import com.vortex.openquest.request.PostRequest
import com.vortex.openquest.util.Response
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

class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uiScope.launch {

            val builder = build {
                pathUrl = "/posts"
            }

            when(val getResponse = Openquest.processRequest<List<Todo>>(GetRequest(builder))) {
                is Response.Success -> {
                    print(getResponse.data)
                }
                is Response.Failure -> {

                }
            }

            val body = Todo(
                "1",
                201,
                "Xablau das neves",
                false
            )

            builder.requestBody = body

            when(val postResponse = Openquest.processRequest<Todo>(PostRequest(builder))) {
                is Response.Success -> {
                    print(postResponse.data)
                }
                is Response.Failure -> {

                }
            }
        }
    }
}
