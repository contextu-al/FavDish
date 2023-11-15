package com.tutorials.eu.favdish.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.contextu.al.Contextual
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.application.FavDishApplication
import com.tutorials.eu.favdish.databinding.DialogCustomListBinding
import com.tutorials.eu.favdish.databinding.FragmentAllDishesBinding
import com.tutorials.eu.favdish.model.entities.FavDish
import com.tutorials.eu.favdish.utils.Constants
import com.tutorials.eu.favdish.view.activities.AddUpdateDishActivity
import com.tutorials.eu.favdish.view.activities.CreateTagActivity
import com.tutorials.eu.favdish.view.activities.DrawerActivity
import com.tutorials.eu.favdish.view.activities.MainActivity
import com.tutorials.eu.favdish.view.adapters.CustomListItemAdapter
import com.tutorials.eu.favdish.view.adapters.FavDishAdapter
import com.tutorials.eu.favdish.viewmodel.FavDishViewModel
import com.tutorials.eu.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    /**
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository, requireActivity().application)
    }

    // A global variable for FavDishAdapter Class
    private lateinit var mFavDishAdapter: FavDishAdapter

    // A global variable for Filter List Dialog
    private lateinit var mCustomListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the LayoutManager that this RecyclerView will use.
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        // Adapter class is initialized and list is passed in the param.
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        // adapter instance is set to the recyclerview to inflate the items.
        mBinding.rvDishesList.adapter = mFavDishAdapter

        mBinding.createTag.setOnClickListener {
            startActivity(Intent(activity, CreateTagActivity::class.java))
        }
        val guideJson = "{\n" +
                "    \"buttons\":{\n" +
                "        \"dismiss\":{\n" +
                "           \"color\":\"#000000\",\n" +
                "           \"height\":16,\n" +
                "           \"width\":16\n" +
                "        },\n" +
                "        \"layout\":\"horizontal\",\n" +
                "        \"next\":{\n" +
                "           \"align\":\"left\",\n" +
                "           \"border-color\":\"#2E7D32\",\n" +
                "           \"border-width\":1,\n" +
                "           \"button-align\":\"right\",\n" +
                "           \"color\":\"#000000\",\n" +
                "           \"corner-radius\":5,\n" +
                "           \"font-size\":14,\n" +
                "           \"margin-right\":10,\n" +
                "           \"padding-left\":10,\n" +
                "           \"padding-right\":10,\n" +
                "           \"padding-top\":0,\n" +
                "           \"placement\":\"right\",\n" +
                "           \"text-align\":\"center\",\n" +
                "           \"type\":\"button\"\n" +
                "        },\n" +
                "        \"prev\":{\n" +
                "           \"align\":\"left\",\n" +
                "           \"border-color\":\"#2E7D32\",\n" +
                "           \"border-width\":1,\n" +
                "           \"button-align\":\"left\",\n" +
                "           \"corner-radius\":5,\n" +
                "           \"margin-left\":10,\n" +
                "           \"padding-left\":10,\n" +
                "           \"padding-right\":10,\n" +
                "           \"placement\":\"left\",\n" +
                "           \"text-align\":\"center\",\n" +
                "           \"type\":\"button\"\n" +
                "        }\n" +
                "     },\n" +
                "     \"content\":{\n" +
                "        \"background-color\":\"#00000000\",\n" +
                "        \"color\":\"#000000\",\n" +
                "        \"font-size\":\"-1\",\n" +
                "        \"line-height\":\"120%\",\n" +
                "        \"margin-bottom\":10,\n" +
                "        \"margin-left\":10,\n" +
                "        \"margin-right\":10,\n" +
                "        \"margin-top\":10,\n" +
                "        \"padding-bottom\":10,\n" +
                "        \"padding-left\":1,\n" +
                "        \"padding-right\":1,\n" +
                "        \"padding-top\":10,\n" +
                "        \"text\":\"this is my first popup\",\n" +
                "        \"text-align\":\"left\"\n" +
                "     },\n" +
                "     \"image\":[\n" +
                "        \n" +
                "     ],\n" +
                "     \"interactions\":[\n" +
                "        \n" +
                "     ],\n" +
                "     \"meta\":{\n" +
                "        \"animation\":{\n" +
                "           \"transition-in\":{\n" +
                "              \"type\":\"none\"\n" +
                "           },\n" +
                "           \"transition-out\":{\n" +
                "              \"type\":\"none\"\n" +
                "           }\n" +
                "        },\n" +
                "        \"background-color\":\"#F9F9F9\",\n" +
                "        \"box-shadow\":0,\n" +
                "        \"container_type\":\"modal\",\n" +
                "        \"dismiss-params\":{\n" +
                "           \"touch-in\":true,\n" +
                "           \"touch-out\":true\n" +
                "        },\n" +
                "        \"display-params\":{\n" +
                "           \"_height_unit\":\"px\",\n" +
                "           \"_width_unit\":\"%\",\n" +
                "           \"border-color\":\"#E0E0E0\",\n" +
                "           \"border-width\":1,\n" +
                "           \"corner-radius\":5,\n" +
                "           \"width\":\"80%\"\n" +
                "        },\n" +
                "        \"dynamicUrl\":{\n" +
                "           \"display_condition\":\"any_page\",\n" +
                "           \"matching_condition\":{\n" +
                "              \"conditions\":[\n" +
                "                 {\n" +
                "                    \"operator\":\"equal\"\n" +
                "                 }\n" +
                "              ],\n" +
                "              \"match_type\":\"any\"\n" +
                "           }\n" +
                "        },\n" +
                "        \"id\":10,\n" +
                "        \"margin-bottom\":0,\n" +
                "        \"margin-left\":0,\n" +
                "        \"margin-right\":0,\n" +
                "        \"margin-top\":0,\n" +
                "        \"padding-bottom\":10,\n" +
                "        \"padding-left\":10,\n" +
                "        \"padding-right\":10,\n" +
                "        \"padding-top\":10,\n" +
                "        \"placement\":\"center\",\n" +
                "        \"tool\":\"modal\",\n" +
                "        \"view\":\"_ALL_\"\n" +
                "     },\n" +
                "     \"overlay\":{\n" +
                "        \n" +
                "     },\n" +
                "     \"screenshot\":{\n" +
                "        \"client_version\":\"Unknown\",\n" +
                "        \"height\":568,\n" +
                "        \"id\":\"_ALL_\",\n" +
                "        \"json\":[\n" +
                "           \n" +
                "        ],\n" +
                "        \"model\":\"Simulator\",\n" +
                "        \"modified\":\"Unknown\",\n" +
                "        \"orientation\":\"portrait\",\n" +
                "        \"view\":\"_ALL_\",\n" +
                "        \"width\":320\n" +
                "     },\n" +
                "     \"templateId\":213,\n" +
                "     \"theme\":\"popup\",\n" +
                "     \"title\":{\n" +
                "        \"color\":\"#000000\",\n" +
                "        \"font-size\":20,\n" +
                "        \"font-weight\":\"bold\",\n" +
                "        \"text-align\":\"left\"\n" +
                "     }\n" +
                "}"
        //Contextual.addGuide(guideJson)

        /**
         * Add an observer on the LiveData returned by getAllDishesList.
         * The onChanged() method fires when the observed data changes and the activity is in the foreground.
         */
        val arrayListOfDishes = arrayListOf<FavDish>()
        val customWidget = "AdhocRowInsertion"
        Contextual.registerGuideBlock("AdhocRowInsertion").observe(viewLifecycleOwner){ contextualContainer ->
            if(contextualContainer.guidePayload.guide.guideBlock.contentEquals(customWidget)){
                val favDish = FavDish("https://staging.contextu.al/static-image/assets/img/icons/FlatIcons/Party/dinner.png", Constants.DISH_IMAGE_SOURCE_ONLINE,
                    contextualContainer.guidePayload.guide.titleText.text ?: "", "", "", "", "", "")
                if(!arrayListOfDishes.contains(favDish)){
                    arrayListOfDishes.add(favDish)
                }
                mFavDishAdapter.dishesList(arrayListOfDishes)
            }
        }
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    dishes.forEach {  favDish ->
                        arrayListOfDishes.add(favDish)
                    }

                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                } else {

                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
        val fancyAnnouncement = "FancyAnnouncement"
        Contextual.registerGuideBlock(fancyAnnouncement).observe(viewLifecycleOwner){ contextualContainer ->
            if(contextualContainer.guidePayload.guide.guideBlock.contentEquals(fancyAnnouncement)){
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setView(R.layout.favdish_custom_dialog)
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

                alertDialog.findViewById<TextView>(R.id.title)?.text = "Fancy Announcement"
                alertDialog.findViewById<TextView>(R.id.message)?.text = "This is a basic example of a custom implementation of a contextual guide"
                val imageView = alertDialog.findViewById<ImageView>(R.id.customDialogImageView)
                imageView?.let { customImageView ->
                    Glide.with(this)
                        .load("https://staging.contextu.al/static-image/assets/img/icons/FlatIcons/Party/dinner.png")
                        .into(customImageView)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    /**
     * A function to navigate to the Dish Details Fragment.
     *
     * @param favDish
     */
    fun dishDetails(favDish: FavDish) {
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

        findNavController()
            .navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }

            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }

            R.id.action_drawer_screen -> {
                startActivity(Intent(requireActivity(), DrawerActivity::class.java))
                return  true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method is used to show the Alert Dialog while deleting the dish details.
     *
     * @param dish - Dish details that we want to delete.
     */
    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to launch the custom dialog.
     */
    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        mCustomListDialog.show()
    }


    /**
     * A function to get the filter item selection and get the list from database accordingly.
     *
     * @param filterItemSelection
     */
    fun filterSelection(filterItemSelection: String) {

        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if (filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {

                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {

                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        } else {

            mFavDishViewModel.getFilteredList(filterItemSelection)
                .observe(viewLifecycleOwner) { dishes ->
                    dishes.let {
                        if (it.isNotEmpty()) {

                            mBinding.rvDishesList.visibility = View.VISIBLE
                            mBinding.tvNoDishesAddedYet.visibility = View.GONE

                            mFavDishAdapter.dishesList(it)
                        } else {

                            mBinding.rvDishesList.visibility = View.GONE
                            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }
}