package wendu.dsbridge.test

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import wendu.dsbridge.R

class ProvisionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_provision)

        findViewById<Button>(R.id.btn_provision).setOnClickListener {
        }

    }
}