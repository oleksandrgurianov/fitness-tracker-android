package com.example.fitnesstracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnesstracker.ui.theme.FitnessTrackerTheme
import com.example.fitnesstracker.ui.theme.Typography
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var totalDays = 0
    private var totalSteps = 0
    private var morningSteps = 0
    private var daySteps = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the sensor manager and step sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Calculate the total number of days
        totalDays = TimeUnit.MILLISECONDS.toDays(SystemClock.elapsedRealtime()).toInt()

        // Retrieve saved steps from SharedPreferences
        val sharedPrefs = getSharedPreferences("STEP_COUNTER", Context.MODE_PRIVATE)
        morningSteps = sharedPrefs.getInt("MORNING_STEPS", 0)
        daySteps = sharedPrefs.getInt("DAY_STEPS", 0)

        // Check for permission and display the StepCounter component
        setContent {
            val context = LocalContext.current
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                FitnessTrackerTheme {
                    FitnessTrackerView(totalDays, totalSteps, morningSteps, daySteps)
                }
            } else {
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Register the sensor listener
        stepSensor?.let { sensor ->
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        saveSteps()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the sensor listener and save the steps to SharedPreferences
        sensorManager.unregisterListener(sensorEventListener)
        saveSteps()
    }

    private fun saveSteps() {
        // Save steps to SharedPreferences
        val sharedPrefs = getSharedPreferences("STEP_COUNTER", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // Check if it's a new day and reset the steps if necessary
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val savedDay = sharedPrefs.getInt("SAVED_DAY", 0)

        if (currentDay != savedDay) {
            editor.putInt("MORNING_STEPS", 0)
            editor.putInt("DAY_STEPS", 0)
            editor.putInt("SAVED_DAY", currentDay)
        } else {
            editor.putInt("MORNING_STEPS", morningSteps)
            editor.putInt("DAY_STEPS", daySteps)
        }

        editor.apply()
        morningSteps = sharedPrefs.getInt("MORNING_STEPS", 0)
        daySteps = sharedPrefs.getInt("DAY_STEPS", 0)

    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Do nothing
        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values[0].toInt()

                    // Update the step counts
                    if (totalSteps == 0) {
                        totalSteps = steps
                    }
                    val calendar = Calendar.getInstance()
                    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                    if (hourOfDay in 6..11) {
                        morningSteps = steps - totalSteps
                    }
                    daySteps = steps - totalSteps

                    // Update the StepCounter component
                    setContent {
                        FitnessTrackerTheme {
                            FitnessTrackerView(totalDays, steps, morningSteps, daySteps)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FitnessTrackerView(
    totalDays: Int,
    totalSteps: Int,
    morningSteps: Int,
    daySteps: Int
) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Rounded.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text(text = "Home") }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Rounded.List,
                            contentDescription = "Journal"
                        )
                    },
                    label = { Text(text = "Journal") }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Rounded.AccountCircle,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text(text = "Profile") }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Keep it up\nYou've tracked",
                style = Typography.h2
            )
            Row {
                Text(
                    text = "$totalDays ${if (totalDays == 1) "day" else "days"}",
                    style = Typography.h1
                )
                Text(
                    text = " in a row",
                    style = Typography.h2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val color = Color(0xFF2E2E2E)
            val shape = RoundedCornerShape(48.dp)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 1f)
                        .padding(0.dp, 8.dp)
                        .background(color, shape)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "TOTAL",
                            style = Typography.h3
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$totalSteps",
                                style = Typography.h1
                            )
                            Text(
                                text = " steps",
                                style = Typography.h3,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(top = 8.dp, end = 8.dp)
                            .background(color, shape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "MORNING",
                                style = Typography.h3
                            )
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = "$morningSteps",
                                    style = Typography.h1
                                )
                                Text(
                                    text = " steps",
                                    style = Typography.h3
                                )
                            }
                        }
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(start = 8.dp, top = 8.dp)
                            .background(color, shape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "TODAY",
                                style = Typography.h3
                            )
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = "$daySteps",
                                    style = Typography.h1
                                )
                                Text(
                                    text = " steps",
                                    style = Typography.h3,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StepCounterPreview() {
    FitnessTrackerTheme {
        FitnessTrackerView(totalDays = 7, totalSteps = 112345, morningSteps = 3456, daySteps = 8901)
    }
}