package com.blockblastassistant

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Block Blast Assistant"
            textSize = 26f
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Block Blast için hamle yardımcısı"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }

        val button = Button(this).apply {
            text = "Başla"
            setOnClickListener {
                description.text =
                    "Uygulama iskeleti hazır. Hamle analizini ekleyeceğiz."
            }
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(button)

        setContentView(layout)
    }
}
