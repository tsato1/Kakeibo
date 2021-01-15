package com.kakeibo

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
class AppExecutors {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
    val networkIO: Executor = Executors.newFixedThreadPool(3)
}