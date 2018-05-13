package mahipal.signaturepaddemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(), View.OnClickListener {

    var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clear_btn.setOnClickListener(this)
        save_btn.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.clear_btn -> {
                canvasLayout.clear()
            }
            R.id.save_btn -> {
                canvasLayout.getSignatureBitmap()
            }

        }
    }
}
