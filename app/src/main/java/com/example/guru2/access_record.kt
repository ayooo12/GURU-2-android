package com.example.guru2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import org.w3c.dom.Text

class access_record : AppCompatActivity() {

    private val paycoPackage = "com.nhnent.payapp" // 페이코 앱의 패키지 주소
    private val intentPayco = packageManager.getLaunchIntentForPackage(paycoPackage) // 인텐트에 패키지 주소 저장

    lateinit var layout: LinearLayout
    lateinit var timeResult: TextView
    lateinit var spotResult: TextView
    lateinit var btnSelect: Button

    lateinit var myHelper: myDBHelper
    lateinit var sqlDB: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("출입 기록")

        // xml 위젯 객체 연결
        timeResult = findViewById(R.id.timeResult)
        spotResult = findViewById(R.id.spotResult)
        btnSelect = findViewById(R.id.btnSelect)

        // DB 클래스 객체 생성
        myHelper = myDBHelper(this)

        // 조회 버튼 리스너
        btnSelect.setOnClickListener {
            sqlDB = myHelper.readableDatabase

            // 커서 선언, 테이블 조회 후 대입
            var cursor: Cursor
            cursor = sqlDB.rawQuery("SELECT * FROM entryTBL;", null)

            // 시간, 장소 나타낼 문자열 선언
            var strTime = ""
            var strSpot = ""

            // 커서 움직이며 데이터값 반환, 문자열 변수에 누적
            while (cursor.moveToNext()) {
                strTime = cursor.getString(0) + "\r\n" + strTime
                strSpot = cursor.getString(1) + "\r\n" + strSpot
            }

            // 출력
            timeResult.setText(strTime)
            spotResult.setText(strSpot)

            cursor.close()
            sqlDB.close()

            Toast.makeText(applicationContext, "조회됨", Toast.LENGTH_SHORT).show()
        }


         fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.idcard_menu, menu)
            return true
        }

       fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item?.itemId){
                R.id.action_home -> {
                    val intent = Intent(this,idcard::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.record -> {
                    val intent = Intent(this,access_record::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.pay -> {
                    try {
                        startActivity(intentPayco) // 페이코 앱을 실행해본다.
                    } catch (e: Exception) {  // 만약 실행이 안된다면 (앱이 없다면)
                        val intentPlayStore = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + paycoPackage)) // 설치 링크를 인텐트에 담아
                        startActivity(intentPlayStore) // 플레이스토어로 이동시켜 설치유도.
                        return true
                    }
                }
                R.id.Logout-> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }


    }


    inner class myDBHelper(context: Context) : SQLiteOpenHelper(context, "sampleDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE entryTBL (time text, spot text);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS entryTBL")
            onCreate(db)
        }
    }
}