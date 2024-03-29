package com.xereon.xereon.util.map

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.xereon.xereon.data.category.util.CategoryConverter
import com.xereon.xereon.data.store.LocationStore

class ClusterRenderer(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<LocationStore>?,
    clusterSize: Int = 5
) : DefaultClusterRenderer<LocationStore>(context, map, clusterManager) {

    init{
        minClusterSize = clusterSize
    }

    override fun getClusterText(bucket: Int): String {
        return bucket.toString()
    }

    override fun getBucket(cluster: Cluster<LocationStore>): Int {
        return cluster.size
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor("#E84C3D")
    }


    override fun onBeforeClusterItemRendered(item: LocationStore, markerOptions: MarkerOptions) {
        val marker: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker( CategoryConverter.getCategoryHue(item.category) );
        markerOptions.icon(marker)
    }
}