package com.tutorials.eu.favdish.view.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.tutorials.eu.favdish.databinding.ActivityDrawerBinding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tutorials.eu.favdish.view.fragments.drawer.FirstDrawerFragment
import com.tutorials.eu.favdish.view.fragments.drawer.SecondDrawerFragment
import com.tutorials.eu.favdish.view.fragments.drawer.ThirdDrawerFragment
import java.lang.Exception

import androidx.appcompat.app.ActionBarDrawerToggle
import com.tutorials.eu.favdish.R


class DrawerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDrawerBinding

    private lateinit var mDrawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private  lateinit var nvDrawer: NavigationView

    private lateinit var  drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)

        nvDrawer = mBinding.nvView

        setupDrawerContent(nvDrawer)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDrawer = mBinding.drawerLayout

        drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        mDrawer.addDrawerListener(drawerToggle)
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return ActionBarDrawerToggle(
            this,
            mDrawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*> = when (menuItem.itemId) {
            R.id.drawer_item_1 -> FirstDrawerFragment::class.java
            R.id.drawer_item_2 -> SecondDrawerFragment::class.java
            R.id.drawer_item_3 -> ThirdDrawerFragment::class.java
            else -> FirstDrawerFragment::class.java
        }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager: FragmentManager = supportFragmentManager
        fragment!!.let { fragmentManager.beginTransaction().replace(R.id.filContent, it).commit() }

        // Highlight the selected item has been done by NavigationView
        menuItem.isChecked = true
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        mDrawer.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        when(item.itemId) {
            R.id.d_home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.drawer_toolbar_menu, menu)
        return true
    }
}