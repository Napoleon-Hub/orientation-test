package com.funnygaytest.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.data.prefs.PrefsEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect>(
    private val preferences: PrefsEntity
) : ViewModel() {

    protected val isConnected: Boolean
        get() = preferences.isConnected

    protected var gameBegun: Boolean
        get() = preferences.gameBegun
        set(value) {
            preferences.gameBegun = value
        }

    protected var points: Int
        get() = preferences.points
        set(value) {
            preferences.points = value
        }

    protected var lastQuestionIndex: Int
        get() = preferences.lastQuestionIndex
        set(value) {
            preferences.lastQuestionIndex = value
        }

    /**
     * Create Initial State of View
     */
    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    /**
     * Get Current State
     */
    private val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    private val event = _event.asSharedFlow()

    /**
     * We use channel for one-time events,
     * because State and Shared flow safe state
     */
    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    /**
     * Set new Event
     */
    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    /**
     * Set new Ui State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }

    /**
     * Set new Effect
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    init {
        subscribeEvents()
    }

    /**
     * Start listening to Event
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle each event
     */
    abstract fun handleEvent(event: Event)

}