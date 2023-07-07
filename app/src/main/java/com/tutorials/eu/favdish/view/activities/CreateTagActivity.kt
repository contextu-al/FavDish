package com.tutorials.eu.favdish.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.contextu.al.Contextual
import com.tutorials.eu.favdish.databinding.ActivityCreateTagBinding
import java.lang.NumberFormatException

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
    }

    private fun createTag(tag: String, value: String) {
        if (tag.isEmpty() || value.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        } else {
            try {
                val newValue = value.toDouble()
                Contextual.tagNumeric(tag, newValue)
            } catch (e: NumberFormatException) {
                Contextual.tagString(tag, value)
            }
        }
        Toast.makeText(this, "Tag saved", Toast.LENGTH_SHORT).show()
    }
}