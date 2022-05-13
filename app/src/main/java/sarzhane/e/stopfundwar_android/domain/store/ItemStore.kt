package sarzhane.e.stopfundwar_android.domain.store

import kotlinx.coroutines.flow.Flow

interface ItemStore<T> {

    var item: T?

    fun observeItem(): Flow<T?>

    fun updateSafely(updateAction: (T) -> T)
}

fun <T> ItemStore<T>.reset() {
    this.item = null
}