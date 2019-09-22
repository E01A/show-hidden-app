package sam.hidden.app.detector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler().postDelayed({
            val i = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(i)

            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {

        private val SPLASH_TIME_OUT = 4000
    }

}