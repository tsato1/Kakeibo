package com.kakeibo.core.util

import kotlinx.coroutines.flow.*

/*
 ResultType: the type that we load from the database (List<T> in this app)
 RequestType: the type that the api returns
 */
inline fun <ResultType, RequestType> networkBoundResource(
    // logic that stipulates how we want to extract data from our database
    crossinline query: () -> Flow<ResultType>,
    // logic to fetch data from our api
    crossinline fetch: suspend () -> RequestType,
    // once we fetched the response, insert the data into the database
    crossinline saveFetchedResult: suspend (RequestType) -> Unit,
    // logic to do if we failed in fetching data
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    // determines if we should fetch data if the data in the database (ResultType) is old
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    // first condition should be loading. Emit this now to show progressBar
    emit(Resource.Loading(null))

    // get data from database
    val data = query().first() // get the current data from database. first emission of the flow

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data)) // load the data from database until fetching from api finishes

        try {
            val fetchedResult = fetch() // get the data from server
            saveFetchedResult(fetchedResult) // insert the fetched data to database
            query().map { Resource.Success(it) } // return the result that's wrapped around Resource
        }
        catch (t: Throwable) {
            onFetchFailed(t)
            query().map { Resource.Error("Couldn't reach server. It might be down.", it)}
        }
    }
    else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}