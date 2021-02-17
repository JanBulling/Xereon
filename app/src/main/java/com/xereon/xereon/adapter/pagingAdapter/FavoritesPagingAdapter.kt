package com.xereon.xereon.adapter.pagingAdapter

/*class FavoritesPagingAdapter() :
    PagingDataAdapter<FavoriteStore, FavoritesPagingAdapter.ViewHolder>(COMPARATOR) {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerStoreVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_STORE) {
            val favorite = getItem(position)

            if (favorite != null)
                holder.bind(favorite)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            itemCount -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_STORE
        }
    }


    fun getItemAtPosition(positon: Int) = getItem(positon)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener { fun onItemClick(favoriteStore: FavoriteStore) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val binding: RecyclerStoreVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val item = getItem(currentIndex)
                    if (item != null)
                        itemClickListener.onItemClick(item)
                }
            }
        }

        fun bind(favorite: FavoriteStore) {
            binding.apply {
                Glide.with(recyclerStoreImage).load(favorite.logoImageURL)
                    .into(recyclerStoreImage)
                recyclerStoreName.text = favorite.name
                recyclerStoreType.text = favorite.type
                @ColorRes val colorId = CategoryUtils.getCategoryColorResourceId(favorite.category)
                recyclerStoreType.setTextColor(ContextCompat.getColor(recyclerStoreType.context, colorId))
                recyclerStoreCity.text = favorite.city
            }
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FavoriteStore>() {
            override fun areItemsTheSame(oldItem: FavoriteStore, newItem: FavoriteStore) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoriteStore, newItem: FavoriteStore) =
                oldItem.hashCode() == newItem.hashCode()
        }

        const val VIEW_TYPE_STORE = 0
        const val VIEW_TYPE_LOADING = 1
    }
}*/