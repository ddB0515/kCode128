package ddb0515.kcode128

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ddb0515.kcode128.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.barcodeView.setData("1234567890ABCDEF")
    }
}
