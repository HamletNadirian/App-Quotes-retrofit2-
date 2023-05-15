package nadirian.hamlet.android.quotesusingretrofit2

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nadirian.hamlet.android.myapplication.QuotesApi
import nadirian.hamlet.android.myapplication.RetrofitHelper
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val pgsBar: ProgressBar? = null
    private var i = 0
    private val hdlr = Handler()
    val list = listOf(
        Color.LTGRAY,
        Color.MAGENTA,
        Color.YELLOW,
        Color.GREEN,
        Color.CYAN,
        Color.RED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val quoteTv = findViewById<TextView>(R.id.quoteTv)
        val authorTv = findViewById<TextView>(R.id.authorTv)
        var lineV = findViewById<View>(R.id.view_line)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar

        i = progressBar.progress
        thread {
            kotlin.run {
                while (i < 100) {
                    i++
                    hdlr.post(kotlinx.coroutines.Runnable {
                        pgsBar?.progress = i;
                    })
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val quotesApi = RetrofitHelper.getInstance().create(QuotesApi::class.java)

        GlobalScope.launch {
            var result = quotesApi.getQuotes(1)
            Log.d("nadirian: ", quotesApi.getQuotes(1).body()?.totalPages.toString())
            val max_total_pages = quotesApi.getQuotes(1).body()?.totalPages.toString().toInt()

            val rndsPage = (1..max_total_pages).random()
            val rnds_color_index = (list.indices).random()
            result = quotesApi.getQuotes(rndsPage)
            val max_count_quotes_on_page = quotesApi.getQuotes(rndsPage).body()?.count.toString().toInt()
            val rndsQuote = (1..max_count_quotes_on_page).random()

            runOnUiThread {
                progressBar.visibility = View.INVISIBLE
                Log.d("nadirian: ", rndsPage.toString())
                lineV.visibility = View.VISIBLE;
                val string =
                    SpannableString(result.body()?.results?.get(rndsQuote-1)?.content.toString())
                Log.d("nadirian: ", result.body()?.results?.get(rndsQuote-1)?.content.toString())

                string.setSpan(
                    BackgroundColorSpan(list.get(rnds_color_index)),
                    0,
                    string.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                quoteTv.text = string
                authorTv.text = result.body()?.results?.get(rndsQuote-1)?.author
            }
        }

    }
}