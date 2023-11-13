package com.example.metroidstore.utilities

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

fun <T, K> StateFlow<T>.mapState(
    transform: (value: T) -> K
): StateFlow<K> {
    return object: StateFlow<K> {
        override val replayCache: List<K>
            get() {
                return this@mapState.replayCache.map(transform)
            }

        override val value: K
            get() {
                 return transform(this@mapState.value)
            }

        override suspend fun collect(collector: FlowCollector<K>): Nothing {
            this@mapState.collect(object: FlowCollector<T> {
                override suspend fun emit(value: T) {
                    collector.emit(transform(value))
                }
            })
        }
    }
}