package com.tutorials.eu.favdish.view.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.contextu.al.BuildConfig
import com.contextu.al.Contextual
import com.contextu.al.core.CtxEventObserver
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ActivityMainBinding
import com.tutorials.eu.favdish.model.notification.NotifyWorker
import com.tutorials.eu.favdish.utils.Constants
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Contextual.init(application, getString(R.string.app_key), object : CtxEventObserver {
            override fun onInstallRegistered(installId: UUID, context: Context) {
                Contextual.tagStringArray(mutableMapOf(
                    "sh_cuid" to "favdish-dev-user ${Date()} | pz-${BuildConfig.CTX_VERSION_NAME}",
                    "sh_email" to "qa@contextu.al", "sh_first_name" to "QA",
                    "sh_last_name" to "Contextual"))
            }

            override fun onInstallRegisterError(errorMsg: String) {
                Toast.makeText(application, errorMsg, Toast.LENGTH_LONG).show()
            }
        })


        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //requestDrawPermission()
        mNavController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dish,
                R.id.navigation_maps
            )
        )
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navView.setupWithNavController(mNavController)

        // TODO Step 19: Handle the Notification when user clicks on it.
        // START
        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            Log.i("Notification Id", "$notificationId")

            // The Random Dish Fragment is selected when user is redirect in the app via Notification.
            mBinding.navView.selectedItemId = R.id.navigation_random_dish
        }
        // END
        startWork()

        val checkedItems = booleanArrayOf(false, false, false, false)
        val multiChoiceItems = arrayOf("Work", "Fun", "Learning", "Social")

        Contextual.registerGuideBlock("MultipleChoiceCustom").observe(this){ contextualContainer ->

            AlertDialog.Builder(this@MainActivity)
                .setTitle("What do you usually use our app for?")
                .setMultiChoiceItems(multiChoiceItems, checkedItems) { dialog, which, isChecked ->

                }
                .setPositiveButton("OK") { dialog, which ->

                }
                .create()
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    /**
     * A function to hide the BottomNavigationView with animation.
     */
    fun hideBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(mBinding.navView.height.toFloat()).duration = 300
        mBinding.navView.visibility = View.GONE
    }

    /**
     * A function to show the BottomNavigationView with Animation.
     */
    fun showBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(0f).duration = 300
        mBinding.navView.visibility = View.VISIBLE
    }

    /**
     * Constraints ensure that work is deferred until optimal conditions are met.
     *
     * A specification of the requirements that need to be met before a WorkRequest can run.
     * By default, WorkRequests do not have any requirements and can run immediately.
     * By adding requirements, you can make sure that work only runs in certain situations
     * - for example, when you have an unmetered network and are charging.
     */
    // For more details visit the link https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712
    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)  // if connected to WIFI
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)                 // if the battery is not low
        .build()

    /**
     * You can use any of the work request builder that are available to use.
     * We will you the PeriodicWorkRequestBuilder as we want to execute the code periodically.
     *
     * The minimum time you can set is 15 minutes. You can check the same on the below link.
     * https://developer.android.com/reference/androidx/work/PeriodicWorkRequest
     *
     * You can also set the TimeUnit as per your requirement. for example SECONDS, MINUTES, or HOURS.
     */
    // setting period to 15 Minutes
    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
        .setConstraints(createConstraints())
        .build()

    private fun startWork() {
        /* enqueue a work, ExistingPeriodicWorkPolicy.KEEP means that if this work already exists, it will be kept
        if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced */
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "FavDish Notify Work", ExistingPeriodicWorkPolicy.KEEP,
                createWorkRequest()
            )
    }

    private fun requestDrawPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 12345)
            }
        }
    }
}