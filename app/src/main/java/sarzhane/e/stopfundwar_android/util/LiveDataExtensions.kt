package sarzhane.e.stopfundwar_android.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, func: (T) -> Unit) {
    liveData.observe(this, { it?.let(func) })
}

inline fun <reified T: Any?> LiveData<T>.withValue(valueJob:(T) -> Unit) {
    this.value?.let(valueJob)
}

fun <T> MutableLiveData<List<T>>.postCombine(values: List<T>, merge: Boolean = true) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    if (merge) {
        value.addAll(values)
    } else {
        value.removeAll(values)
    }
    this.postValue(value)
}

inline fun <reified F, T> MutableLiveData<List<T>>.filteredPostCombine(values: List<T>) {
    val value = ArrayList<T>(this.value?.filter { it !is F } ?: arrayListOf())
    value.addAll(values)
    this.postValue(value)
}

fun <T> MutableLiveData<List<T>>.postAdd(item: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value.add(item)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postAddAt(position: Int, item: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value.add(position, item)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postSetAt(position: Int, item: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value[position] = item
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postMoveTo(from: Int, to: Int, item: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value.removeAt(from)
    value.add(to, item)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postDelete(item: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value.remove(item)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postDeleteAt(position: Int) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    value.removeAt(position)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.postDeleteFirst(predicate: (T) -> Boolean) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    val index = value.indexOfFirst { predicate.invoke(it) }
    if (index < 0) return
    value.removeAt(index)
    postValue(value)
}

fun <T> MutableLiveData<List<T>>.addAt(value: T, position: Int) {
    val values = ArrayList<T>(this.value ?: arrayListOf())
    values.add(position, value)
    this.postValue(values)
}

fun <T> MutableLiveData<List<T>>.combine(values: List<T>, merge: Boolean = true) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    if (merge) {
        value.addAll(values)
    } else {
        value.removeAll(values)
    }
    this.value = (value)
}

fun <T> MutableLiveData<List<T>>.replaceItem(oldValue: T, newValue: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    val index = value.indexOf(oldValue)
    if (index > -1) {
        value[index] = newValue
    } else {
        value.add(newValue)
    }
    this.value = (value)
}

fun <T> MutableLiveData<List<T>>.postReplaceItem(oldValue: T, newValue: T) {
    val value = ArrayList<T>(this.value ?: arrayListOf())
    val index = value.indexOf(oldValue)
    if (index > -1) {
        value[index] = newValue
    } else {
        value.add(newValue)
    }
    this.postValue(value)
}

inline fun <reified T: Any?> LiveData<T>.withValueOr(valueJob:(T) -> Unit, doIfNull:()-> Unit = {}) {
    this.value?.let(valueJob)?.let { doIfNull.invoke() }
}

