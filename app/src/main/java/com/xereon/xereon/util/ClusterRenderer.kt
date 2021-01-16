package com.xereon.xereon.util

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.util.CategoryUtils

class ClusterRenderer(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<LocationStore>?
) : DefaultClusterRenderer<LocationStore>(context, map, clusterManager) {

    init{
        minClusterSize = 5
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
        val marker: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker( CategoryUtils.getCategoryHue(item.category) );
        markerOptions.icon(marker)
    }
}