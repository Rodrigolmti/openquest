# Openquest

Openquest is a opensource Android library for requests [NOT READY]

`This library use coroutines, so is mandatory you call a request inside a suspend function!`

# Missing
 - Fix problems with gson serialization linkedTreeMap
 - Implement moshi serialization
 - Verify names in https
 - Deploy lib 
 
# How to use

The purpose of this library is study only, when you use third part libraries the most of the time you don't know how the things work behind the curtain, so i did this library, to undestand the process.

There is a class called Openquest, where you can setup your base configuration, like:

`Base config`

```
Openquest
// Here you can setup your base url for the next requests
.setBaseUrl("https://ayourapi.com.br")
// Here you can pass a serialization adapter, at this moment only GSON
.setConverterAdapter(GsonAdapter())
```

`Builder`

The builder class use kotlin DSL that allow you to configurate your requests. The builder has the follow properties:
```
// Path of the request like: /user/new ...
var path: String? = null,
// Base path for the request like: www.google.com
var baseUrl: String? = null,
// When you request has a body, you can pass it here
var requestBody: Any? = null,
// Boolean flag that if receive true, will verify if the domain is trusted with certifies
var verifyName: Boolean = false,
// Time for read timeout
var readTimeout: Int = defaultTimeout,
// Time for connection timeout
var connectionTimeout: Int = defaultTimeout,
// HashMap with headers
var headers: MutableMap<String, String> = hashMapOf(),
// HashMap with url params
var pathParams: MutableMap<String, String> = hashMapOf()
```

`Creating a builder`
Here you can pass all the previous properties informed
```
val builder = build {
    path = "/posts"
}
```

`Doing a request`

This is just a simple example of how to call, you can customize with your own way.
`The following blocks of code are inside a coroutine scope`

`GET`
```
when(val getResponse = Openquest.doRequest<List<Todo>>(GetRequest(builder))) {
    is Response.Success -> {
        getResponse.data
        [...]
    }
    is Response.Failure -> {
        [...]
    }
}
```

`POST`
```
builder.requestBody = body
when(val postResponse = Openquest.doRequest<Todo>(PostRequest(builder))) {
    is Response.Success -> {
        postResponse.data
        [...]
    }
    is Response.Failure -> {
        [...]
    }
}
```

`PUT`
```
when(val putResponse = Openquest.doRequest<Todo>(PutRequest(builder))) {
    is Response.Success -> {
        putResponse.data
        [...]
    }
    is Response.Failure -> {
        [...]
    }
}
```

`DELETE`
```
when(val deleteResponse = Openquest.doRequest<Todo>(DeleteRequest(builder))) {
    is Response.Success -> {
        deleteResponse.data
        [...]
    }
    is Response.Failure -> {
        [...]
    }
}
```

`PATCH`
```
when(val patchResponse = Openquest.doRequest<Todo>(PatchRequest(builder))) {
    is Response.Success -> {
        patchResponse.data
        [...]         
    }
    is Response.Failure -> {
        [...]
    }
}
```

# The response class

The response class is a sealed class with
 - Success: 
There is two properties for this class data : R the class that you inform when you call a request and statusCode: Int
 
 - Failue
This class has only one propertie error

