package com.xereon.xereon.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) :
    MediatorLiveData<Pair<A?, B?>>(){

    init {
        addSource(a) { value = it to b.value }
        addSource(b) { value = a.value to it }
    }

}

class TripleTrigger<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>):
    MediatorLiveData<Triple<A?, B?, C?>>(){

    init {
        addSource(a) { value = Triple(it, b.value, c.value) }
        addSource(b) { value = Triple(a.value, it, c.value) }
        addSource(c) { value = Triple(a.value, b.value, it) }
    }
}

class QuadTrigger<A, B, C, D>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>, d: LiveData<D>):
    MediatorLiveData<Quad<A?, B?, C?, D?>>(){

    init {
        addSource(a) { value = Quad(it, b.value, c.value, d.value) }
        addSource(b) { value = Quad(a.value, it, c.value, d.value) }
        addSource(c) { value = Quad(a.value, b.value, it, d.value) }
        addSource(d) { value = Quad(a.value, b.value, c.value, it) }
    }
}

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)