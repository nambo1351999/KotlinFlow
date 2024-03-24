package com.phanphuongnam.kotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownFlow = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        //emit Nạp dữ liệu
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        //collectFlow()
        //collectFlow2()
        //collectFlow3()
        //collectFlow4()
        //collectFlow5()
        //collectFlow6()
        squareNumber(3)
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number is $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")
            }
        }

    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }

    }

    private fun collectFlow() {
        viewModelScope.launch {
            val reduceResult = countDownFlow.reduce { accumulator, value ->
                accumulator + value
            }
            val reduceFold = countDownFlow.fold(100) { accumulator, value ->
                accumulator + value
            }
            println("Result ------ ${reduceResult}")
            println("Result ------ ${reduceFold}")
            countDownFlow.filter { time ->
                time % 2 == 0

            }.map { time ->
                time * time

            }.onEach { time ->
                println("The current time is ----- $time")
            }.collect { time ->
                delay(1500L)
                println("The current time is $time")
            }
            val count = countDownFlow.count()
            println("The current time is -----////////// $count")
        }
    }

    private fun collectFlow2() {
        val flow1 = flow<Int> {
            emit(1)
            delay(100L)
            emit(2)
        }
        val flow2 = flow<Int> {
            emit(1)
            delay(100L)
            emit(2)
        }

        viewModelScope.launch {
            flow1.flatMapConcat { value ->
                flow {
                    emit(value + 1)
                    delay(500L)
                    emit(value + 2)
                }
            }.collect { value ->
                println("The value is $value")
            }
        }

    }

    private fun collectFlow3() {
        val flow1 = flow<Int> {
            emit(1)
            delay(100L)
            emit(2)
        }
        viewModelScope.launch {
            flow1.flatMapMerge { source ->
                fetchPostsFromSource(source)
            }.collect { value ->
                println("Result --- $value")
            }
        }
    }

    suspend fun fetchPostsFromSource(source: Int): Flow<Int> = flow {
        emit(99 + source)
        delay(1000)
        emit(100 + source)
        delay(1000)
        emit(101 + source)
    }

    private fun collectFlow4() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("Flow: $it is delivered")
            }
                .buffer().collect {
                println("Flow: is eating $it")
                delay(1500L)
                println("Flow: Finished eating $it")
            }
        }
    }

    private fun collectFlow5() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("Flow: $it is delivered")
            }
                .collectLatest {
                    println("Flow: is eating $it")
                    delay(1500L)
                    println("Flow: Finished eating $it")
                }
        }
    }

    private fun collectFlow6() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("Flow: $it is delivered")
            }
                .conflate().collect {
                    println("Flow: is eating $it")
                    delay(1500L)
                    println("Flow: Finished eating $it")
                }
        }
    }
}