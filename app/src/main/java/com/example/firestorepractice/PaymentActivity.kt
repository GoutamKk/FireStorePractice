package com.example.firestorepractice

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(),PaymentResultWithDataListener,ExternalWalletListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.payment_btn).setOnClickListener {
            payement()
        }

    }

    fun payement(){
        Checkout.preload(this)

        val co = Checkout()

        co.setKeyID("rzp_test_c51XgNNFUQLpLP")

        try{
            val option = JSONObject()
            option.put("name","Bingo")
            option.put("image",R.drawable.ic_launcher_background)
            option.put("theme.color","#256353")
            option.put("currency","INR")
            option.put("amount","1000")

            val retryObj = JSONObject()
            retryObj.put("enabled",true)
            retryObj.put("max_count",4)
            option.put("retry",retryObj)

            val prefill = JSONObject()
            co.open(this,option)
        }
        catch (e:Exception){
            Toast.makeText(this,"Errror in payment :"+ e.message ,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        print("check payment is successful or not $0 " )
        Toast.makeText(this,"Successful in payment :"+ p1.toString() ,Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        print("check payement status $0")
        Toast.makeText(this,"   Error in payment :"+ p1.toString(),Toast.LENGTH_LONG).show()
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }
}