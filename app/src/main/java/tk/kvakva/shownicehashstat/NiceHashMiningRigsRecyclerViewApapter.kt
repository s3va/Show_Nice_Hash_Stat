package tk.kvakva.shownicehashstat

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.crypto.tink.subtle.Hex
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.nicehash_mining_rig_recyclerview.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class NiceHashMiningRigsRecyclerViewApapter :
    RecyclerView.Adapter<NiceHashMiningRigsRecyclerViewApapter.ViewHolder>() {

    var data = listOf<NiceHashMiningRigs2.MiningRig?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NiceHashMiningRigs2.MiningRig?) {
            Log.d("M_ViewHolder", "!!!!!!!!!!!!!! bind ITEM: !!!!!!!!!!!!!!!!!!!!!! $item")
            itemView.rigIdTv.text = item?.rigId
            itemView.rigNameTv.text = item?.name
            itemView.rigStatusTv.text = item?.minerStatus
            itemView.unpaidMiningBalanceTv.text = item?.unpaidAmount
            itemView.devListLinLay.removeAllViews()
            itemView.stopBt.setOnClickListener {
                rigsStatus2StopStart(item, "STOP")
            }
            itemView.startBt.setOnClickListener {
                rigsStatus2StopStart(item, "START")
            }
            itemView.rebootBt.setOnClickListener {
                rigsStatus2StopStart(item, "RESTART")
            }
            // itemView.devListLinLay.setHasTransientState(true)
            item?.devices?.forEach {
                val mTextViewName = TextView(itemView.context)
                mTextViewName.text = "${it?.name}"
                mTextViewName.setBackgroundColor(Color.CYAN)
                val mTextViewId = TextView(itemView.context)
                mTextViewId.text = "${it?.id}"
                val mTextViewTemp = TextView(itemView.context)
                mTextViewTemp.text = "T:${it?.temperature} C\u00B0      Power:${it?.load} %      ${it?.powerMode?.enumName} "
                itemView.devListLinLay.addView(mTextViewName)
                itemView.devListLinLay.addView(mTextViewId)
                itemView.devListLinLay.addView(mTextViewTemp)
                mTextViewName.updateLayoutParams<LinearLayout.LayoutParams> {
                    topMargin = (itemView.context.resources.displayMetrics.density * 8 + 0.5).toInt()
                }
            }
            // itemView.devListLinLay.setHasTransientState(false)
        }

        private fun rigsStatus2StopStart(item: NiceHashMiningRigs2.MiningRig?, startstop: String) {
            val rigIdStr = item?.rigId

            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreferences = EncryptedSharedPreferences.create(
                SECRET_PREF_FN,
                masterKeyAlias,
                itemView.context.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val nhApiKey = sharedPreferences.getString(API_KEY, "")
            val nhApiSecret = sharedPreferences.getString(API_SECRET, "")
            val nhOrgId = sharedPreferences.getString(API_ORG_ID, "")

            CoroutineScope(Dispatchers.Main).launch {
                //getNiceHasAccounting2Btc
                val xTime = System.currentTimeMillis()
                val xTimes = xTime.toString()
                val xNonce = UUID.randomUUID().toString()
                val xOrganizationId = nhOrgId.orEmpty()
                val xRequestId = UUID.randomUUID().toString()
                val b = RigsStatusBody(startstop, rigIdStr)
                //val bStr=

                val mMoshi = Moshi.Builder()
                    .build()
                //.add(KotlinJsonAdapterFactory())

                val mAdapter = mMoshi.adapter(RigsStatusBody::class.java)

                val sss = mAdapter.toJson(b).toByteArray(Charsets.UTF_8)

                Log.d("M_ViewHolder", "mMoshi.adapter(RigsStatusBody::class.java) ${mAdapter.toJson(b)}")

                val inputstring = nhApiKey?.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
                    ?.plus(xTimes.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                    ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
                    ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                    ?.plus(0.toByte())?.plus("POST".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                    ?.plus(PATH_RIGS_STATUS2.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                    ?.plus(0.toByte())
                    ?.plus(sss)

                Log.d("M_ViewHolder", "${inputstring?.let { it1 -> String(it1) }}")
                val nhsha265HMAC = Mac.getInstance("HmacSHA256")
                val apisecret = SecretKeySpec(nhApiSecret?.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
                nhsha265HMAC.init(apisecret)
                val hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
                val headerMap = mapOf(
                    "Accept" to "application/json;charset=UTF-8",
                    "X-Time" to xTimes,
                    "X-Nonce" to xNonce,
                    "X-Organization-Id" to xOrganizationId,
                    "X-Request-Id" to xRequestId,
                    "X-Auth" to "$nhApiKey:$hash"
                )

                val errorRigsStatus = try {
                    val mRigsStatus = NicehashApi.retrofitService.getRigsStatus2(headerMap, b)
                    "${mRigsStatus.message} !! ${mRigsStatus.successType} !! ${mRigsStatus.success}\n"
                } catch (e: HttpException) {
                    e.printStackTrace()
                    "" + e.code() + " " + e.message() + "\n" + e.response()
                } catch (e: SocketTimeoutException) {
                    e.printStackTrace()
                    "SocketTimeoutExeption"
                }
                //----------------------
                Toast.makeText(itemView.context, errorRigsStatus, Toast.LENGTH_LONG).show()
                Log.d("M_ViewHolder", "$errorRigsStatus !!!!!!!!!!!!!!^^^^^^^^^^^^^^^^^")
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.nicehash_mining_rig_recyclerview, parent, false)
                return ViewHolder(view)
            }
        }
    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

}