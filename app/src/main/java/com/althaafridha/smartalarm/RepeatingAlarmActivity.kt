package com.althaafridha.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.althaafridha.smartalarm.AlarmReceiver.Companion.TYPE_REPEATING
import com.althaafridha.smartalarm.data.Alarm
import com.althaafridha.smartalarm.data.local.AlarmDB
import com.althaafridha.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.althaafridha.smartalarm.fragment.TimeDialogFragment
import com.althaafridha.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private val db by lazy { AlarmDB(this) }

    private var alarmReceiver : AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmReceiver = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeat.setOnClickListener {
                val timePickerDialog = TimeDialogFragment()
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            }
            btnAdd.setOnClickListener {
                val time = tvOnceTime.text.toString()
                val message = edtNoteRepeat.text.toString()

                if(time == "Time"){
                    Toast.makeText(this@RepeatingAlarmActivity,
                        "Tentukan waktu alarm",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    alarmReceiver?.setRepeatingAlarm(
                        applicationContext,
                        TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                message,
                                TYPE_REPEATING
                            )
                        )
                        Log.i("AddAlarm", "alarm set on $time with message $message")
                        finish()
                    }
                }
            }
            btnCancel.setOnClickListener{
                finish()
            }

        }
    }

    override fun onTimeListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }

}