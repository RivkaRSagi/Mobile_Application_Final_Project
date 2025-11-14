package com.example.wandermonsters

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MiniGameActivity : AppCompatActivity(), SensorEventListener {

    companion object Difficulty{
        const val COMMON = 3f
        const val UNCOMMON = 2f
        const val RARE = 1f
        const val EPIC = 0.5f
        const val LEGENDARY = 0.2f
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var titleText: TextView
    private lateinit var closeButton: Button

    private var progress = 0f
    private var hasFinished = false
    private val SHAKE_THRESHOLD = 6.0f
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame)

        progressBar = findViewById(R.id.progressBar)
        titleText = findViewById(R.id.titleText)
        closeButton = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val (x, y, z) = event.values
        val totalAcceleration = sqrt(x + y + z)

        if (totalAcceleration > SHAKE_THRESHOLD) {
            val increment = Difficulty.COMMON * (totalAcceleration / SHAKE_THRESHOLD)
            progress = (progress + increment).coerceAtMost(100f)
            updateProgressBar(progress)

            if (progress >= 100f && !hasFinished) {
                hasFinished = true
                handler.post {
                    Toast.makeText(this, "You're done!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateProgressBar(value: Float) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, value.toInt())
        animator.duration = 100
        animator.start()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
