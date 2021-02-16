package com.xereon.xereon.util.adapter

import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Almost copy from
 * @see PagingDataAdapter
 *
 * Added custom updateCallback in differ-constructor
 * @see CustomAdapterListUpdateCallback
 */
abstract class CustomPagingDataAdapter<T : Any, VH : RecyclerView.ViewHolder> @JvmOverloads constructor(
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
    offset: Int = 0
) : RecyclerView.Adapter<VH>() {
    private val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = CustomAdapterListUpdateCallback(
            this,
            offset
        ),
        mainDispatcher = mainDispatcher,
        workerDispatcher = workerDispatcher
    )


    final override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    fun submitData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        differ.submitData(lifecycle, pagingData)
    }

    fun retry() {
        differ.retry()
    }

    fun refresh() {
        differ.refresh()
    }

    protected fun getItem(@IntRange(from = 0) position: Int) = differ.getItem(position)

    fun peek(@IntRange(from = 0) index: Int) = differ.peek(index)

    fun snapshot(): ItemSnapshotList<T> = differ.snapshot()

    override fun getItemCount() = differ.itemCount

    @OptIn(FlowPreview::class)
    val loadStateFlow: Flow<CombinedLoadStates> = differ.loadStateFlow

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.removeLoadStateListener(listener)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.APPEND] [LoadState] as a list item at the end of the presented list.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeaderAndFooter
     * @see withLoadStateFooter
     */
    fun withLoadStateHeader(
        header: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
        }
        return ConcatAdapter(header, this)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.PREPEND] [LoadState] as a list item at the start of the presented list.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeaderAndFooter
     * @see withLoadStateHeader
     */
    fun withLoadStateFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(this, footer)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.PREPEND] and [LoadType.APPEND] [LoadState]s as list items at the start and end
     * respectively.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeader
     * @see withLoadStateFooter
     */
    fun withLoadStateHeaderAndFooter(
        header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(header, this, footer)
    }
}
