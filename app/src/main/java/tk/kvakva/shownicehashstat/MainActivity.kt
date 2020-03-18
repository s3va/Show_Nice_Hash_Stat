package tk.kvakva.shownicehashstat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import tk.kvakva.shownicehashstat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val niceHashVM by viewModels<NhViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        binding.nhviewmodel = niceHashVM
        binding.lifecycleOwner = this
        binding.DebugTextView.movementMethod=ScrollingMovementMethod()
        val recyclerViewApapter = NiceHashMiningRigsRecyclerViewApapter()
        binding.MinersStatusRecView.adapter=recyclerViewApapter
        niceHashVM.nicehashRigsList.observe(this, Observer {
            it?.let {
                recyclerViewApapter.data = it
            }
        })
        binding.SwipeLayoutRefresh.setOnRefreshListener {
            Log.d("M_MainActivity","onRefresh called from SwipeRefreshLayout")
            niceHashVM.onClinkGetNiceHashStatistics()
        }

        niceHashVM.showRefreshLayout.observe(this, Observer {
            if(it)
                binding.SwipeLayoutRefresh.isRefreshing=false
        })

    }

    fun enterApiKeyAndSecret(item: MenuItem) {
        Log.d(
            "M_MainActivity",
            "in enterpassword(item: MenuItem) $item : ${item.itemId} ${R.menu.bomenu}"
        )

        when (item.itemId) {
            R.id.passwrd -> {
                Log.d("M_MainActivity", " R.menu.bomenu -> R.id.passwrd ")
                niceHashVM.setShowPassUserLayout()
            }
            R.id.getFromNiceHash -> {
                Log.d("M_MainActivity", " R.menu.bomenu -> R.id.getFromNiceHash")
                niceHashVM.onClinkGetNiceHashStatistics()
            }
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bomenu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

