package aetherealtech.metroidstore.customerclient.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun <E, R> Collection<E>.parallelMap(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    transform: suspend (E) -> R
) = coroutineScope {
    this@parallelMap
        .map { element ->
            async(dispatcher) {
                transform(element)
            }
        }
        .map { element -> element.await() }
}