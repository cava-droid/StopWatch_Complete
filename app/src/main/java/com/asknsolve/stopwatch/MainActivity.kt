package com.asknsolve.stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.asknsolve.stopwatch.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    // binding
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 초기화
    private var time = 0

    // 버튼과 이벤트 연결 시 사용
    private var isRunning = false

    // timerTask 변수를 null을 허용하는 Timer 타입으로 선언
    private var timerTask: Timer? = null

    // 몇 번째 Lap Time인지 나타내기 위한 변수 초기화
    private var lap = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            //
            isRunning = !isRunning

            //
            if(isRunning) {
                start()
            } else {
                pause()
            }
        }

        binding.lapButton.setOnClickListener {
            recordLapTime()
        }

        binding.resetFab.setOnClickListener {
            reset()
        }
    }


    private fun start() {
        // 타이머가 시작되면 FAB의 이미지를 일시정지 이미지로 변경
        binding.fab.setImageResource(R.drawable.ic_baseline_pause_24)

        // 0.01초마다 시간을 증가시키면서 UI 갱신
        // timer를 취소하려면 timer를 실행하고 반환되는 Timer 객체를 변수에 저장해 둘 필요가 있음
        // 이를 위해 timerTask 변수를 null을 허용하는 Timer 타입으로 선언
        timerTask = timer(period = 10) {
            // 시간 증가, 0.01초마다 1씩
            time++

            // time이 100이 되면 1초, 200은 2초, 258은 2.58초이므로
            // time을 100으로 나누면 몫은 sec, 나머지는 milli
            val sec = time / 100
            val milli = time % 100

            // UI 갱신
            // timer는 워커 스레드에서 동작하여 UI 조작이 불가하므로 runOnUiThread로 감싸서 UI 조작이 가능하게
            runOnUiThread {
                binding.secTextView.text = "$sec"
                binding.milliTextView.text = "$milli"
            }
        }
    }

    private fun pause(){
        // FAB의 이미지를 시작 이미지로 변경
        binding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        // 실행 중인 타이머가 있다면 타이머를 취소
        timerTask?.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun recordLapTime(){
        // 현재 시간을 지역변수에 저장
        val lapTime = this.time
        // 동적으로 TextView를 생성하여 텍스트 값 설정
        val textView = TextView(this)
        textView.text = "$lap LAP : ${lapTime / 100}.${lapTime % 100}"

        // 맨 위에 랩타임 추가
        binding.lapLayout.addView(textView, lap - 1)
        lap++

        // lapTime 값 설정에는 문제 없음
        // Log.d("lap", "laptime = ${lapTime}")
        // addView에도 문제 없었음
        // lapLayout의 width를 0dp에서 match_parent로 바꿔주니까 해결됨
        // Log.d("textview", "textView = ${textView.text}")
    }

    private fun reset(){
        // 실행 중인 타이머가 있다면 취소
        timerTask?.cancel()

        // 모든 변수와 화면에 표시되는 것 초기화
        time = 0
        isRunning = false
        binding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        binding.secTextView.text = "0"
        binding.milliTextView.text = "00"

        // 모든 랩타임 제거
        binding.lapLayout.removeAllViews()
        lap = 1
    }
}