package sarzhane.e.stopfundwar_android.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import sarzhane.e.stopfundwar_android.domain.store.ItemStore

class HeapItemStore<T : Any>(initialValue: T? = null) : ItemStore<T> {

    private val itemState = MutableStateFlow<T?>(initialValue)

    override var item: T?
        get() = itemState.value
        set(newValue: T?) {
            itemState.value = newValue
        }

    override fun observeItem(): Flow<T?> =
        itemState

    override fun updateSafely(updateAction: (T) -> T) =
        itemState.update { prev ->
            if (prev != null) {
                updateAction(prev)
            } else {
                prev
            }
        }
}