package aetherealtech.metroidstore.customerclient.utilities

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Quadruple<out T1, out T2, out T3, out T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)

data class Quintuple<out T1, out T2, out T3, out T4, out T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5
)

data class Sextuple<out T1, out T2, out T3, out T4, out T5, out T6>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5,
    val sixth: T6
)

data class Septuple<out T1, out T2, out T3, out T4, out T5, out T6, out T7>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5,
    val sixth: T6,
    val seventh: T7
)

data class Octuple<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5,
    val sixth: T6,
    val seventh: T7,
    val eighth: T8
)

data class Nonuple<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5,
    val sixth: T6,
    val seventh: T7,
    val eighth: T8,
    val ninth: T9
)

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

class StateFlows private constructor() {
    companion object {
        fun <T1, T2> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>
        ): StateFlow<Pair<T1, T2>> {
            return object : StateFlow<Pair<T1, T2>> {
                override val replayCache: List<Pair<T1, T2>>
                    get() {
                        return listOf(
                            Pair(
                                first = first.replayCache[0],
                                second = second.replayCache[0]
                            )
                        )
                    }

                override val value: Pair<T1, T2>
                    get() {
                        return Pair(
                            first = first.value,
                            second = second.value
                        )
                    }

                override suspend fun collect(collector: FlowCollector<Pair<T1, T2>>): Nothing {
                    var current = value

                    coroutineScope {
                        launch {
                            first.collect(object : FlowCollector<T1> {
                                override suspend fun emit(value: T1) {
                                    current = current.copy(first = value)
                                    collector.emit(current)
                                }
                            })
                        }

                        launch {
                            second.collect(object : FlowCollector<T2> {
                                override suspend fun emit(value: T2) {
                                    current = current.copy(second = value)
                                    collector.emit(current)
                                }
                            })
                        }
                    }

                    while(true) {
                        delay(Long.MAX_VALUE)
                    }
                }
            }
        }

        fun <T1, T2, T3> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>
        ): StateFlow<Triple<T1, T2, T3>> {
            return combine(combine(first, second), third)
                .mapState { values ->
                    return@mapState Triple<T1, T2, T3>(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>
        ): StateFlow<Quadruple<T1, T2, T3, T4>> {
            return combine(combine(first, second, third), fourth)
                .mapState { values ->
                    return@mapState Quadruple<T1, T2, T3, T4>(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4, T5> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>,
            fifth: StateFlow<T5>
        ): StateFlow<Quintuple<T1, T2, T3, T4, T5>> {
            return combine(combine(first, second, third, fourth), fifth)
                .mapState { values ->
                    return@mapState Quintuple(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.first.fourth,
                        fifth = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4, T5, T6> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>,
            fifth: StateFlow<T5>,
            sixth: StateFlow<T6>
        ): StateFlow<Sextuple<T1, T2, T3, T4, T5, T6>> {
            return combine(combine(first, second, third, fourth, fifth), sixth)
                .mapState { values ->
                    return@mapState Sextuple(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.first.fourth,
                        fifth = values.first.fifth,
                        sixth = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4, T5, T6, T7> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>,
            fifth: StateFlow<T5>,
            sixth: StateFlow<T6>,
            seventh: StateFlow<T7>
        ): StateFlow<Septuple<T1, T2, T3, T4, T5, T6, T7>> {
            return combine(combine(first, second, third, fourth, fifth, sixth), seventh)
                .mapState { values ->
                    return@mapState Septuple(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.first.fourth,
                        fifth = values.first.fifth,
                        sixth = values.first.sixth,
                        seventh = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4, T5, T6, T7, T8> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>,
            fifth: StateFlow<T5>,
            sixth: StateFlow<T6>,
            seventh: StateFlow<T7>,
            eighth: StateFlow<T8>
        ): StateFlow<Octuple<T1, T2, T3, T4, T5, T6, T7, T8>> {
            return combine(combine(first, second, third, fourth, fifth, sixth, seventh), eighth)
                .mapState { values ->
                    return@mapState Octuple(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.first.fourth,
                        fifth = values.first.fifth,
                        sixth = values.first.sixth,
                        seventh = values.first.seventh,
                        eighth = values.second
                    )
                }
        }

        fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> combine(
            first: StateFlow<T1>,
            second: StateFlow<T2>,
            third: StateFlow<T3>,
            fourth: StateFlow<T4>,
            fifth: StateFlow<T5>,
            sixth: StateFlow<T6>,
            seventh: StateFlow<T7>,
            eighth: StateFlow<T8>,
            ninth: StateFlow<T9>
        ): StateFlow<Nonuple<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
            return combine(combine(first, second, third, fourth, fifth, sixth, seventh, eighth), ninth)
                .mapState { values ->
                    return@mapState Nonuple(
                        first = values.first.first,
                        second = values.first.second,
                        third = values.first.third,
                        fourth = values.first.fourth,
                        fifth = values.first.fifth,
                        sixth = values.first.sixth,
                        seventh = values.first.seventh,
                        eighth = values.first.eighth,
                        ninth = values.second
                    )
                }
        }
    }
}