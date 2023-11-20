/*
 * Copyright (C) 2019-2023 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */
package com.here.offlinemaps

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.here.offlinemaps.PermissionsRequestor.ResultListener
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView

class MainActivity : AppCompatActivity() {
    private var permissionsRequestor: PermissionsRequestor? = null
    private var mapView: MapView? = null
    private var offlineMapsExample: OfflineMapsExample? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usually, you need to initialize the HERE SDK only once during the lifetime of an application.
        initializeHERESDK()
        setContentView(R.layout.activity_main)

        // Get a MapView instance from layout
        mapView = findViewById(R.id.map_view)
        mapView!!.onCreate(savedInstanceState)
        loadMapScene()

    }

    private fun initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        val accessKeyID = ""
        val accessKeySecret =
            ""
        val options = SDKOptions(accessKeyID, accessKeySecret)
        try {
            val context: Context = this
            SDKNativeEngine.makeSharedInstance(context, options)
        } catch (e: InstantiationErrorException) {
            throw RuntimeException("Initialization of HERE SDK failed: " + e.error.name)
        }
    }

    private fun handleAndroidPermissions() {
        permissionsRequestor = PermissionsRequestor(this)
        permissionsRequestor!!.request(object : ResultListener {
            override fun permissionsGranted() {

            }

            override fun permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsRequestor!!.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView!!.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                offlineMapsExample = OfflineMapsExample(mapView!!)
            } else {
                Log.d(TAG, "onLoadScene failed: $mapError")
            }
        }
    }

    override fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    override fun onResume() {
        mapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mapView!!.onDestroy()
        disposeHERESDK()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView!!.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        val sdkNativeEngine = SDKNativeEngine.getSharedInstance()
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose()
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null)
        }
    }

    fun onDownloadListClicked(view: View?) {
        offlineMapsExample!!.onDownloadListClicked()
    }

    fun onDownloadMapClicked(view: View?) {
        offlineMapsExample!!.onDownloadMapClicked()
    }

    fun onCancelMapDownloadClicked(view: View?) {
        offlineMapsExample!!.onCancelMapDownloadClicked()
    }

    fun onSearchPlaceClicked(view: View?) {
        offlineMapsExample!!.onSearchPlaceClicked()
    }

    fun switchOnlineButtonClicked(view: View?) {
        offlineMapsExample!!.onSwitchOnlineButtonClicked()
    }

    fun switchOfflineButtonClicked(view: View?) {
        offlineMapsExample!!.onSwitchOfflineButtonClicked()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}