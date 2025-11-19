package com.example.wandermonsters

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import kotlin.math.sqrt
import java.io.File

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
    private lateinit var image: ImageView
    private lateinit var button: Button
    private var progress = 0f
    private var diff = 0f
    private var decay = 0f
    private var hasFinished = false
    private val SHAKE_THRESHOLD = 6.0f
    private val handler = Handler(Looper.getMainLooper())
    private var monster: Monster? = null
    private var lastMovementTime = System.currentTimeMillis()
    private val decayIntervalMs = 100L
    private val decayRunnable = object : Runnable {
        override fun run() {
            if (hasFinished) return

            val decayThisTick = decay * (decayIntervalMs / 1000f) // percent to remove this tick
            val now = System.currentTimeMillis()
            val idleMs = now - lastMovementTime

            // optional: only start decaying after short idle
            val idleDelayMs = 300L
            if (idleMs >= idleDelayMs) {
                progress = (progress - decayThisTick).coerceAtLeast(0f)
                // update immediately so small changes are visible
                progressBar.max = 100
                progressBar.progress = progress.toInt()
            }

            handler.postDelayed(this, decayIntervalMs)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame)

        progressBar = findViewById(R.id.progressBar)
        titleText = findViewById(R.id.titleText)
        button = findViewById(R.id.button)
        image = findViewById(R.id.monsterImage)

        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler())


        monster = Monster.createRandomMonster(this)

        val draw = ContextCompat.getDrawable(this, R.drawable.image_shadow) as GradientDrawable
        when (monster!!.rarity) {
            0 -> {draw.colors =
                intArrayOf(ContextCompat.getColor(this, R.color.Common), Color.TRANSPARENT)
                decay = 2f
                diff = COMMON}

            1 -> {draw.colors =
                intArrayOf(ContextCompat.getColor(this, R.color.Uncommon), Color.TRANSPARENT)
                decay = 3f
                diff = UNCOMMON}

            2 -> {draw.colors =
                intArrayOf(ContextCompat.getColor(this, R.color.Rare), Color.TRANSPARENT)
                decay = 5f
                diff = RARE}

            3 -> {draw.colors =
                intArrayOf(ContextCompat.getColor(this, R.color.Epic), Color.TRANSPARENT)
                decay = 7f
                diff = EPIC}

            4 -> {draw.colors =
                intArrayOf(ContextCompat.getColor(this, R.color.Legendary), Color.TRANSPARENT)
                decay = 9f
                diff = LEGENDARY}
        }

        val monsterImageId =
            getResources().getIdentifier(monster?.type?.lowercase(), "drawable", packageName)

        image.setImageDrawable(AppCompatResources.getDrawable(this, monsterImageId))
        image.setBackgroundResource(R.drawable.image_shadow)

        button.setOnClickListener {
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

        handler.removeCallbacks(decayRunnable)
        handler.postDelayed(decayRunnable, decayIntervalMs)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(decayRunnable)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER && !hasFinished) return


        val (x, y, z) = event!!.values
        val totalAcceleration = sqrt(x + y + z)

        if (totalAcceleration > SHAKE_THRESHOLD) {
            lastMovementTime = System.currentTimeMillis()
            val increment = diff * (totalAcceleration / SHAKE_THRESHOLD)
            progress = (progress + increment).coerceAtMost(100f)
            updateProgressBar(progress)

            if (progress >= 100f && !hasFinished) {
                hasFinished = true
                handler.removeCallbacks(decayRunnable)

                val layout = findViewById<View>(R.id.container)
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

                progressBar.visibility = View.GONE
                val text = findViewById<TextView>(R.id.completedText)
                text.visibility = View.VISIBLE

                button.text = "Store Monster"

                button.setOnClickListener {
                    //TODO store monster object. and navigate to collection
                    finish()
                }

                startPulseAndWobble(image)

            }
        }
    }

    private fun updateProgressBar(value: Float) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, value.toInt())
        animator.duration = 100
        animator.start()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun startPulseAndWobble(imageView: ImageView) {
        val scaleUpX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1f, 1.2f)

        val scaleDownX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1.2f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1.2f, 1f)

        val rotateLeft = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, -10f)
        val rotateRight = ObjectAnimator.ofFloat(imageView, View.ROTATION, -10f, 10f)
        val rotateCenter = ObjectAnimator.ofFloat(imageView, View.ROTATION, 10f, 0f)

        val scaleSet = AnimatorSet().apply {
            playSequentially(scaleUpX, scaleUpY, scaleDownX, scaleDownY)
            duration = 300
        }

        val rotateSet = AnimatorSet().apply {
            playSequentially(rotateLeft, rotateRight, rotateCenter)
            duration = 300
        }

        val set = AnimatorSet().apply {
            playTogether(scaleSet, rotateSet)
            interpolator = AccelerateDecelerateInterpolator()
        }

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Repeat the animation
                startPulseAndWobble(imageView)
            }
        })

        set.start()
    }

    private inner class CustomExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            try {
                val file = File("/storage/emulated/0/Documents/dump.txt")
                file.writeText(throwable.stackTraceToString())
            } catch (e: Exception) {
                Toast.makeText(this@MiniGameActivity, "Error saving stack trace to dump file", Toast.LENGTH_SHORT).show()
            }finally {
                val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
