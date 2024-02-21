package com.tutorials.eu.favdish.view.activities

import android.content.Context
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
import com.bumptech.glide.Glide
import com.contextu.al.BuildConfig
import com.contextu.al.Contextual
import com.contextu.al.confetti.ConfettiGuideBlocks
import com.contextu.al.core.CtxEventObserver
import com.contextu.al.fancyannouncement.FancyAnnouncementGuideBlocks
import com.contextu.al.model.customguide.Feedback
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ActivityMainBinding
import com.tutorials.eu.favdish.model.ContextualFeedbackModel
import com.tutorials.eu.favdish.model.notification.NotifyWorker
import com.tutorials.eu.favdish.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Contextual.init(application, getString(R.string.app_key), object : CtxEventObserver {
            override fun onInstallRegistered(installId: UUID, context: Context) {
                val localDateTime = LocalDateTime.now()
                val dayOfWeek = localDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                val month = localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                Contextual.tagStringArray(mutableMapOf(
                    "sh_cuid" to "favdish-dev-user ${dayOfWeek + " " + " " +  month + " " + localDateTime.dayOfMonth} | pz-${BuildConfig.CTX_VERSION_NAME}",
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
        val multiSelectSurvey = "MultiSelectSurvey"
        var hasShown = false
        Contextual.registerGuideBlock(multiSelectSurvey).observe(this){ contextualContainer ->
            if(contextualContainer.guidePayload.guide.guideBlock.contentEquals(multiSelectSurvey) && !hasShown){
                hasShown = true
                val feedBackData = Gson().fromJson(contextualContainer.guidePayload.guide.feedBackData,
                    ContextualFeedbackModel::class.java)
                val multiChoiceItems = feedBackData.c.toTypedArray()
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(contextualContainer.guidePayload.guide.feedBackTitle ?: "")
                    .setMultiChoiceItems(multiChoiceItems, checkedItems) { dialog, which, isChecked ->
                    }
                    .setPositiveButton("Submit") { dialog, which ->
                        val jsonObject = JsonObject()
                        val updatedMultiChoice = arrayListOf<String>()
                        checkedItems.forEachIndexed { index, check ->
                            if (check){
                                updatedMultiChoice.add(multiChoiceItems[index])
                            }
                        }
                        jsonObject.addProperty("any-other-custom-data", "Example custom data")
                        contextualContainer.operations.submitFeedback(contextualContainer.guidePayload.guide.feedID,
                            Feedback(contextualContainer.guidePayload.guide.feedBackTitle ?: "", updatedMultiChoice, jsonObject))
                        dialog.dismiss()
                        if (feedBackData.i == 1) {
                            promptUserForInput("Please explain why you chose this ?")
                        }
                    }
                    .create()
                    .show()
            }
        }

        val confettiGuideBlocks = "confetti"
        Contextual.registerGuideBlock(confettiGuideBlocks).observe(this){ contextualContainer ->
            if(contextualContainer.guidePayload.guide.guideBlock.contentEquals(confettiGuideBlocks)){
                val confettiView = ConfettiGuideBlocks(this@MainActivity)
                confettiView.show({}, {
                    val baseView = findViewById<View>(android.R.id.content)

                    // "nextStep" informs the Contextual SDK that the user is accepting the guide or trying to go to next step (tapping Next or OK, etc)
                    // If there is no next step in the guide, then the guide will be "complete" (and dismissed)
                    // This provides an analytics update
                    contextualContainer.guidePayload.nextStep.onClick(baseView)
                })
            }
        }
        val fancyAnnouncement = "FancyAnnouncement"
        var hasShownFancyAnnouncement = false
        Contextual.registerGuideBlock(fancyAnnouncement).observe(this){ contextualContainer ->
            if (contextualContainer.guidePayload.guide.guideBlock.contentEquals(fancyAnnouncement) && !hasShownFancyAnnouncement) {
                hasShownFancyAnnouncement = true
                val title = contextualContainer.guidePayload.guide.titleText.text ?: ""
                val message = contextualContainer.guidePayload.guide.contentText.text ?: ""

                val buttons = contextualContainer.guidePayload.guide.buttons
                var prevButtonText = "back"
                var nextButtonText = "next"

                buttons.prevButton?.let { button ->
                    prevButtonText = button.text ?: "back"
                }

                buttons.nextButton?.let { button ->
                    nextButtonText = button.text ?: "next"
                }
                val negativeText = prevButtonText
                val positiveText = nextButtonText

                var imageURL: String? = null

                val images = contextualContainer.guidePayload.guide.images
                if (images.isNotEmpty()) {
                    imageURL = images[0].resource
                }

                val guideBlock = FancyAnnouncementGuideBlocks(this)

                guideBlock.show(
                    title,
                    message,
                    negativeText,
                    { v: View? ->
                        // "prevStep" informs the Contextual SDK that the user is dismissing/cancelling or trying to go to previous step (tapping Back, Cancel, etc)
                        // If there is no previous step in the guide, then the guide will be "rejected" (and dismissed)
                        // This provides an analytics update
                        contextualContainer.guidePayload.prevStep.onClick(v)
                        guideBlock.dismiss()
                        CoroutineScope(Dispatchers.IO).launch {
                            contextualContainer.tagManager.getTag("test_key").collect { tags ->
                                if(tags != null){
                                    runOnUiThread {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setTitle("Tagged value")
                                            .setMessage("test_key value is: " + tags.tagStringValue)
                                            .setPositiveButton("OK") { dialog, which ->
                                                dialog.dismiss()
                                            }
                                            .create()
                                            .show()
                                    }
                                }
                            }
                        }
                    },
                    positiveText,
                    { v: View? ->
                        // "nextStep" informs the Contextual SDK that the user is accepting the guide or trying to go to next step (tapping Next or OK, etc)
                        // If there is no next step in the guide, then the guide will be "complete" (and dismissed)
                        // This provides an analytics update
                        contextualContainer.guidePayload.nextStep.onClick(v)
                        guideBlock.dismiss()
                    },
                    imageURL ?: ""
                )
            }
        }
    }


    private fun promptUserForInput(textTitle: String){
        AlertDialog.Builder(this@MainActivity)
            .setTitle(textTitle)
            .setView(R.layout.dialog_guideblock)
            .setPositiveButton("Send") { dialog, which ->

                dialog.dismiss()
            }
            .create()
            .show()
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