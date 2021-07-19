package com.example.myapplication

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var hangul: String = ""
    lateinit var tts: TextToSpeech
    val context = this
    val jasoList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(context, OnInitListener { status ->
            if(status != TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
            } else{
            }
        })

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Sensor")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var value: Int = dataSnapshot.child("data").value.toString().toInt()
                var value2: Int = dataSnapshot.child("data2").value.toString().toInt()
                var value3: Int = dataSnapshot.child("data3").value.toString().toInt()
                var value4: Int = dataSnapshot.child("data4").value.toString().toInt()
                var value5: Int = dataSnapshot.child("data5").value.toString().toInt()
                var value6: Int = dataSnapshot.child("data6").value.toString().toInt()
                var total: Int = value*32+value2*16+value3*8+value4*4+value5*2+value6
                Log.d(total.toString()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", total.toString())

//                val nien: Int = 18
//                val yeo: Int = 35
//                val oo: Int = 27

                val codeBook:HashMap<Int,String> = HashMap<Int,String>()
                codeBook[49] = "ㅏ"  //110001
                codeBook[18] = "ㄴ" // 종성  010010
                codeBook[36] = "ㄴ" // 초성  100100
                codeBook[35] = "ㅕ" // 100011
                codeBook[27] = "ㅇ" // 011011
                codeBook[0] = "" //

                try {
                    if (codeBook[total] != "") {
                        jasoList.add(codeBook[total]!!)
                        Toast.makeText(context, codeBook[total], Toast.LENGTH_SHORT).show()
                    }
                } catch(e:KotlinNullPointerException){
                    e.printStackTrace()
                }
                Log.d("now ch: ",jasoList.toString())

                if(jasoList.size>5){
                    Log.d("size>1", "!!!!!!!!!!!!!!")
                    hangul = HangulParser.assemble(jasoList)
                    Log.d(hangul, hangul)
                    brilleWord.text = hangul

                    if(hangul==""){
                        Toast.makeText(context, "Enter text", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(context, hangul, Toast.LENGTH_SHORT).show()
                        tts.speak("안녕", TextToSpeech.QUEUE_FLUSH, null, null)
                        Toast.makeText(context, hangul, Toast.LENGTH_SHORT).show()
                        button.performClick()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ERROR","Failed to read value.", error.toException())
            }
        })
        button.setOnClickListener {
            if(hangul!="") {
                tts.speak(hangul, TextToSpeech.QUEUE_FLUSH, null, null)
                Toast.makeText(context, hangul, Toast.LENGTH_SHORT).show()
            }
        }
        button.performClick()
    }

    override fun onPause(){
        if(tts.isSpeaking){
            tts.stop()
        }
        super.onPause()
    }
}
