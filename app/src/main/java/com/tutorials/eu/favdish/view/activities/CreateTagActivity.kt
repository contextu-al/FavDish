package com.tutorials.eu.favdish.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.contextu.al.Contextual
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.tutorials.eu.favdish.databinding.ActivityCreateTagBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale

class CreateTagActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityCreateTagBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCreateTagBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.saveTag.setOnClickListener {
            val tag = mBinding.etTagName.text.toString()
            val value = mBinding.etTagValue.text.toString()
           createTag(tag,value)
        }
        mBinding.tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTag = mBinding.tagSpinner.selectedItem.toString()
                mBinding.etTagValue.setText("")
                if(selectedTag.startsWith("Date Time")){
                    mBinding.calendarButton.visibility = View.VISIBLE
                } else {
                    mBinding.calendarButton.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mBinding.calendarButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener { dateFromCalendar ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(dateFromCalendar)
                val timePicker = MaterialTimePicker.Builder()
                    .setTitleText("Select time")
                    .build()
                timePicker.show(supportFragmentManager, "timePicker")
                timePicker.addOnPositiveButtonClickListener {
                    mBinding.etTagValue.setText("")
                    val timePickerHour = if (timePicker.hour <= 9){
                        "0" + timePicker.hour
                    } else {
                        timePicker.hour
                    }
                    val timePickerMin = if (timePicker.minute <= 9){
                        "0" + timePicker.minute
                    } else {
                        timePicker.minute
                    }
                    val timeZone = ZoneId.systemDefault().rules.getOffset(Instant.now())
                    val dateTime = date + "T" + timePickerHour + ":" + timePickerMin + ":00" + timeZone
                    mBinding.etTagValue.setText(dateTime)
                }
            }

        }

    }

    private fun createTag(tag: String, value: String) {
        if (tag.isEmpty() || value.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        } else {
            val selectedTag = mBinding.tagSpinner.selectedItem.toString()
            if(selectedTag.startsWith("String")){
                Contextual.tagString(tag, value)
            } else if(selectedTag.startsWith("Numeric")){
                val newValue = value.toDouble()
                Contextual.tagNumeric(tag, newValue)
            } else if(selectedTag.startsWith("Date Time")){
                try {
                    val exception = CoroutineExceptionHandler{ _, exception ->
                        Toast.makeText(this, "Error trying to tag: $value", Toast.LENGTH_SHORT).show()
                    }
                    val taggedDateTime = OffsetDateTime.parse(value)
                    CoroutineScope(Dispatchers.IO + exception).launch {
                        Contextual.tagDatetime(tag, taggedDateTime).collectLatest { tags ->
                            println("Tagged: " + tags?.tagStringValue)
                        }
                    }
                } catch (exception: Exception){
                    Toast.makeText(this, "Tag is not ISO-8601 compliant!", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this, "$value saved", Toast.LENGTH_SHORT).show()
        }
    }
}