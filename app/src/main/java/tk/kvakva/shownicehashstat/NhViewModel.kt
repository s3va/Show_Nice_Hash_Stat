package tk.kvakva.shownicehashstat

import android.app.Application
import android.app.AuthenticationRequiredException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.crypto.tink.subtle.Hex
import java.util.*
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.http.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


const val SECRET_PREF_FN = "secret_shared_prefs"
const val API_KEY = "APIkey"
const val API_SECRET = "APIsecret"
const val API_ORG_ID = "APIorganizationId"


const val BASEURL = "https://api2.nicehash.com"


// const val PATH_MINING_INFO = "/main/api/v2/mining/info"
const val PATH_SERVER_TIME = "/api/v2/time"

// const val PATH_GRP_LIST = "/main/api/v2/mining/groups/list"
// const val PATH_MININGS_RIGS = "/main/api/v2/mining/rigs"
const val PATH_MININGS_RIGS2 = "/main/api/v2/mining/rigs2"
const val PATH_ACCOUNTING2_BTC = "/main/api/v2/accounting/account2/BTC"
const val PATH_RIGS_STATUS2 = "/main/api/v2/mining/rigs/status2"

class NhViewModel(application: Application) : AndroidViewModel(application) {

    private var _showRefreshLayout = MutableLiveData<Boolean>()
    val showRefreshLayout: LiveData<Boolean>
        get() = _showRefreshLayout

    private var _showPassUserLayout = MutableLiveData<Boolean>()
    val showPassUserLayout: LiveData<Boolean>
        get() = _showPassUserLayout

    private var _nicehashRigsList = MutableLiveData<List<NiceHashMiningRigs2.MiningRig?>?>()
    val nicehashRigsList: LiveData<List<NiceHashMiningRigs2.MiningRig?>?>
        get() = _nicehashRigsList

    private var _btcBalanceNext = MutableLiveData<String>()
    val btcBalanceNext: LiveData<String>
        get() = _btcBalanceNext

    fun setShowPassUserLayout() {
        _showPassUserLayout.value = !(showPassUserLayout.value ?: false)
        if (showPassUserLayout.value == false) {
            sharedPreferences.edit(commit = true) {
                putString(API_KEY, nhApiKey.value)
                putString(API_SECRET, nhApiSecret.value)
                putString(API_ORG_ID, nhOrgId.value)
            }
        }
    }

    fun onClinkSetApiKeySecret() {
        Log.d(
            "M_NhViewModel",
            "showPassUserLayout: ${showPassUserLayout.value} | nhApiKey ${nhApiKey.value} | nhApiSecret: ${nhApiSecret.value}"
        )
        sharedPreferences.edit(commit = true) {
            putString(API_KEY, nhApiKey.value)
            putString(API_SECRET, nhApiSecret.value)
            putString(API_ORG_ID, nhOrgId.value)
        }
        _showPassUserLayout.value = false
    }

    private var _textviewtxt = MutableLiveData<String>()
    val textviewtxt: LiveData<String>
        get() = _textviewtxt

    fun onClinkGetNiceHashStatistics() {
        _showRefreshLayout.value=true

        var xTime: Long
        //var xTime = System.currentTimeMillis()
        var xTimes: String
        //var xTimes = xTime.toString()
        var xNonce: String
        //var xNonce = UUID.randomUUID().toString()
        var xOrganizationId: String
        //var xOrganizationId = nhOrgId.value.orEmpty()
        var xRequestId: String
        //var xRequestId = UUID.randomUUID().toString()

        var inputstring: ByteArray?
/*
        var inputstring = nhApiKey.value?.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
            ?.plus(xTimes.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
            ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus(0.toByte())?.plus("GET".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus(PATH_MINING_INFO.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())


        Log.d(
            "M_NhViewModel",
            """!!!!!!!!!!!!!!!!!! ${nhApiKey.value} 0 $xTimes 0 $xNonce 0 0 $xOrganizationId 0 0 GET 0 $PATH_MINING_INFO !!!!!!!!!!!!!!!!!!!!!!!!!"""
        )
*/
        var nhsha265HMAC = Mac.getInstance("HmacSHA256")
        //var nhsha265HMAC = Mac.getInstance("HmacSHA256")
        val apisecret = SecretKeySpec(nhApiSecret.value?.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
        nhsha265HMAC.init(apisecret)
        //var hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
        var hash: String

/*
        var nhMiningINfo: MiningInfo
*/
        var headerMap: Map<String, String>

        viewModelScope.launch {
            //getNiceHashMiningInfo
/*            nhMiningINfo = NicehashApi.retrofitService.getNiceHashMiningInfo(headerMap)
            Log.d("M_NhViewModel", "$nhMiningINfo}")*/
            //---------------
            //getNiceHashTime
//            val nicehashTime = NicehashApi.retrofitService.getNiceHashTime()
            //---------------
            //getNiceHashMiningGrpList
            /*           xTime = System.currentTimeMillis()
                       xTimes = xTime.toString()
                       xNonce = UUID.randomUUID().toString()
                       xOrganizationId = nhOrgId.value.orEmpty()
                       xRequestId = UUID.randomUUID().toString()

                       inputstring = nhApiKey.value?.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
                           ?.plus(xTimes.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                           ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
                           ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                           ?.plus(0.toByte())?.plus("GET".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                           ?.plus(PATH_GRP_LIST.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                           ?.plus("extendedResponse=true".toByteArray(Charsets.ISO_8859_1))

                       Log.d(
                           "M_NhViewModel",
                           "!!!!!!!!!!!!!!!!!! ${nhApiKey.value} 0 $xTimes 0 $xNonce 0 0 $xOrganizationId 0 0 GET 0 $PATH_SERVER_TIME !!!!!!!!!!!!!!!!!!!!!!!!!"
                       )

                       nhsha265HMAC = Mac.getInstance("HmacSHA256")
                       //val apisecret = SecretKeySpec(nhApiSecret.value?.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
                       nhsha265HMAC.init(apisecret)
                       hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
                       headerMap = mapOf(
                           "Accept" to "application/json",
                           "X-Time" to xTimes,
                           "X-Nonce" to xNonce,
                           "X-Organization-Id" to xOrganizationId,
                           "X-Request-Id" to xRequestId,
                           "X-Auth" to nhApiKey.value + ":" + hash
                       )

                       val grpList = NicehashApi.retrofitService.getNiceHashMiningGrpLst(headerMap, "true")*/
            //---------------------

            //getNiceHasAccounting2Btc
            xTime = System.currentTimeMillis()
            xTimes = xTime.toString()
            xNonce = UUID.randomUUID().toString()
            xOrganizationId = nhOrgId.value.orEmpty()
            xRequestId = UUID.randomUUID().toString()

            inputstring = nhApiKey.value?.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
                ?.plus(xTimes.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
                ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(0.toByte())?.plus("GET".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(PATH_ACCOUNTING2_BTC.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus("extendedResponse=true".toByteArray(Charsets.ISO_8859_1))

            Log.d(
                "M_NhViewModel",
                "!!!!!!!!!!!!!!!!!! ${nhApiKey.value} 0 $xTimes 0 $xNonce 0 0 $xOrganizationId 0 0 GET 0 $PATH_ACCOUNTING2_BTC !!!!!!!!!!!!!!!!!!!!!!!!!"
            )

            nhsha265HMAC = Mac.getInstance("HmacSHA256")
            //val apisecret = SecretKeySpec(nhApiSecret.value?.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
            nhsha265HMAC.init(apisecret)
            hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
            headerMap = mapOf(
                "Accept" to "application/json",
                "X-Time" to xTimes,
                "X-Nonce" to xNonce,
                "X-Organization-Id" to xOrganizationId,
                "X-Request-Id" to xRequestId,
                "X-Auth" to nhApiKey.value + ":" + hash
            )

            val mAccount2 = try {
                val mAccounts2Btc = NicehashApi.retrofitService.getNiceHashAccounging2Btc(headerMap, "true")
                "active: ${mAccounts2Btc.active}\navailable:${mAccounts2Btc.available} ${mAccounts2Btc.currency}  ----- pending:${mAccounts2Btc.pending} ${mAccounts2Btc.currency}\n"
            } catch (e: HttpException) {
                e.printStackTrace()
                Log.d("M_NhViewModel","code: " + e.code() + " message: " + e.message() + "\nresponce: " + e.response())
                Toast.makeText(getApplication(), "" + e.code() + " " + e.message() + "\n" + e.response(), Toast.LENGTH_LONG).show()
                _showPassUserLayout.postValue(true)
                return@launch
                ""
            }
            //----------------------


            //getNiceHashMiningRings2
            xTime = System.currentTimeMillis()
            xTimes = xTime.toString()
            xNonce = UUID.randomUUID().toString()
            xOrganizationId = nhOrgId.value.orEmpty()
            xRequestId = UUID.randomUUID().toString()

            inputstring = nhApiKey.value?.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
                ?.plus(xTimes.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
                ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(0.toByte())?.plus("GET".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
                ?.plus(PATH_MININGS_RIGS2.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())

            Log.d(
                "M_NhViewModel",
                "!!!!!!!!!!!!!!!!!! ${nhApiKey.value} 0 $xTimes 0 $xNonce 0 0 $xOrganizationId 0 0 GET 0 $PATH_SERVER_TIME !!!!!!!!!!!!!!!!!!!!!!!!!"
            )

            nhsha265HMAC = Mac.getInstance("HmacSHA256")
            //val apisecret = SecretKeySpec(nhApiSecret.value?.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
            nhsha265HMAC.init(apisecret)
            hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
            headerMap = mapOf(
                "Accept" to "application/json",
                "X-Time" to xTimes,
                "X-Nonce" to xNonce,
                "X-Organization-Id" to xOrganizationId,
                "X-Request-Id" to xRequestId,
                "X-Auth" to nhApiKey.value + ":" + hash
            )

            val nicehashMinigRigs2 = NicehashApi.retrofitService.getNiceHashMiningRigs2(headerMap)
            _nicehashRigsList.postValue(nicehashMinigRigs2.miningRigs)
            val t = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm.ss").format(Instant.parse(nicehashMinigRigs2.nextPayoutTimestamp).atZone(ZoneId.systemDefault()))
            } else {
                nicehashMinigRigs2.nextPayoutTimestamp
            }
            _btcBalanceNext.postValue("BTC: ${nicehashMinigRigs2.btcAddress}\nunpaid:${nicehashMinigRigs2.unpaidAmount}       nextPayout:$t\n\n$mAccount2")

            _showRefreshLayout.postValue(false)

            //----------------------

/*
            val testListRigs = listOf<NiceHashMiningRigs2.MiningRig>(
                NiceHashMiningRigs2.MiningRig(
                    cpuExists = true,
                    cpuMiningEnabled = true,
                    devices = listOf<NiceHashMiningRigs2.MiningRig.Device>(
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId001.001", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            100.0, "dwevName001001", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 36.6
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId001.002", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            99.0, "dwevName001002", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 36.7
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId001.003", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            98.0, "dwevName001003", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 36.8
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId001.004", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            97.0, "dwevName001004", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 36.9
                        )
                    ),
                    groupName = "groupName001",
                    joinTime = 123123,
                    minerStatus = "ONLINE",
                    name = "rigNmae001",
                    notifications = emptyList(),
                    profitability = 123123.0,
                    rigId = "rigId001",
                    rigPowerMode = "rigPowerMode01",
                    softwareVersions = "softwareVersion",
                    stats = null,
                    statusTime = 123123,
                    type = "MANAGED",
                    unpaidAmount = "0.00000076"
                ),
                NiceHashMiningRigs2.MiningRig(
                    cpuExists = true,
                    cpuMiningEnabled = true,
                    devices = listOf<NiceHashMiningRigs2.MiningRig.Device>(
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId002.001", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            99.0, "dwevName002001", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 37.6
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId002.002", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            99.2, "dwevName002002", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 37.7
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId002.003", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            98.2, "dwevName002003", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 37.8
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId002.004", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            97.2, "dwevName002004", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 37.9
                        )
                    ),
                    groupName = "groupName001",
                    joinTime = 123123,
                    minerStatus = "ONLINE",
                    name = "rigName002",
                    notifications = emptyList(),
                    profitability = 100002.0,
                    rigId = "rigId002",
                    rigPowerMode = "rigPowerMode01",
                    softwareVersions = "softwareVersion",
                    stats = null,
                    statusTime = 11111112,
                    type = "MANAGED",
                    unpaidAmount = "0.00000076"
                ),
                NiceHashMiningRigs2.MiningRig(
                    cpuExists = true,
                    cpuMiningEnabled = true,
                    devices = listOf<NiceHashMiningRigs2.MiningRig.Device>(
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.001", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            80.0, "dwevName003001", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.6
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.002", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            89.0, "dwevName003002", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.7
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.003", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            88.0, "dwevName003003", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.8
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.004", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName003004", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.005", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName003005", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId003.006", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName003006", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        )
                    ),
                    groupName = "groupName001",
                    joinTime = 123123,
                    minerStatus = "ONLINE",
                    name = "Rig003nameDeviceString",
                    notifications = emptyList(),
                    profitability = 100002.0,
                    rigId = "rigId003",
                    rigPowerMode = "rigPowerMode03",
                    softwareVersions = "softwareVersion",
                    stats = null,
                    statusTime = 11111112,
                    type = "MANAGED",
                    unpaidAmount = "0.00000076"


                ),
                NiceHashMiningRigs2.MiningRig(
                    cpuExists = true,
                    cpuMiningEnabled = true,
                    devices = listOf<NiceHashMiningRigs2.MiningRig.Device>(
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId004.001", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            80.0, "dwevName004001", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.6
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId004.002", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            89.0, "dwevName004002", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.7
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId004.003", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            88.0, "dwevName004003", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.8
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId004.004", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName004004", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        )
                    ),
                    groupName = "groupName001",
                    joinTime = 123123,
                    minerStatus = "ONLINE",
                    name = "name004DeviceString",
                    notifications = emptyList(),
                    profitability = 100002.0,
                    rigId = "rigId004",
                    rigPowerMode = "rigPowerMode04",
                    softwareVersions = "softwareVersion",
                    stats = null,
                    statusTime = 11111112,
                    type = "MANAGED",
                    unpaidAmount = "0.000004076"


                ),
                NiceHashMiningRigs2.MiningRig(
                    cpuExists = true,
                    cpuMiningEnabled = true,
                    devices = listOf<NiceHashMiningRigs2.MiningRig.Device>(
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId005.001", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            80.0, "dwevName005001", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.6
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId005.002", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            89.0, "dwevName005002", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.7
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId005.003", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            88.0, "dwevName005003", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.8
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId005.004", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName005004", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        ),
                        NiceHashMiningRigs2.MiningRig.Device(
                            NiceHashMiningRigs2.MiningRig.Device.DeviceType("Nvidia", "NVIDIA"),
                            "devId005.005", NiceHashMiningRigs2.MiningRig.Device.Intensity("intens", "intenc"),
                            87.0, "dwevName005005", NiceHashMiningRigs2.MiningRig.Device.PowerMode("powerSesc", "POWERMODEe"), 0.0,
                            emptyList(), NiceHashMiningRigs2.MiningRig.Device.Status("status", "STATUS"), 38.9
                        )
                    ),
                    groupName = "groupName001",
                    joinTime = 123123,
                    minerStatus = "ONLINE",
                    name = "nameDeviceStrin005g",
                    notifications = emptyList(),
                    profitability = 100002.0,
                    rigId = "rigId005",
                    rigPowerMode = "rigPowerMode05",
                    softwareVersions = "softwareVersion",
                    stats = null,
                    statusTime = 11111112,
                    type = "MANAGED",
                    unpaidAmount = "0.00000076"

                )


            )

            //_nicehashRigsList.postValue(testListRigs + nicehashMinigRigs2.miningRigs)
            _nicehashRigsList.postValue( nicehashMinigRigs2.miningRigs.orEmpty() + testListRigs )*/
            Toast.makeText(getApplication(),"refreshed",Toast.LENGTH_LONG).show()

/*            val grpLstStringBuilder = StringBuilder()
            grpLstStringBuilder.append("\n------------------------\n")
            grpList.groups?.x?.rigs?.forEach {
                grpLstStringBuilder.append("${it?.rigId} ${it?.name}\n${it?.status} totalDevices: ${it?.totalDevices} activeDevices: ${it?.activeDevices}\n")
            }
            grpLstStringBuilder.append("--------------------------\n")
            mMiningRigs.miningRigs?.forEach { itMiningRig ->
                grpLstStringBuilder.append("${itMiningRig?.name} ${itMiningRig?.rigId} ${itMiningRig?.minerStatus}==\n")
                itMiningRig?.devices?.forEach { itDevice ->
                    grpLstStringBuilder.append("${itDevice?.name}\nid:${itDevice?.id}\nstatus:${itDevice?.status?.enumName} T:${itDevice?.temperature} C\u00B0 load: ${itDevice?.load}%\n")
                }
                grpLstStringBuilder.append("-------------------------------\n")
            }
            grpLstStringBuilder.append("${mMiningRigs.btcAddress} unpaidAmount: ${mMiningRigs.unpaidAmount} ${mMiningRigs.nextPayoutTimestamp}\n======================================\n")

            grpLstStringBuilder.append("BTC: ${mMiningRigs2.btcAddress}\n")
            grpLstStringBuilder.append("Unpaid: ${mMiningRigs2.unpaidAmount} nextPay: ${mMiningRigs2.nextPayoutTimestamp}\n")
            mMiningRigs2.miningRigs?.forEach {itMiningRig ->
                grpLstStringBuilder.append("=== ${itMiningRig?.name} ")
                grpLstStringBuilder.append("${itMiningRig?.rigId} ")
                grpLstStringBuilder.append("${itMiningRig?.minerStatus}\n")
                grpLstStringBuilder.append("unpaidAmount:${itMiningRig?.unpaidAmount} ")
                grpLstStringBuilder.append(":${itMiningRig?.rigPowerMode}\n")
                grpLstStringBuilder.append("${itMiningRig?.softwareVersions}\n----\n")
                itMiningRig?.devices?.forEach {itDevice ->
                    grpLstStringBuilder.append("id: ${itDevice?.id}\ndeviceType: ${itDevice?.deviceType?.description}\n${itDevice?.name} ${itDevice?.status?.enumName}\n")
                    grpLstStringBuilder.append("T:${itDevice?.temperature} C\u00B0 ${itDevice?.load}%\n")
                }
                grpLstStringBuilder.append("===\n")
            }
            grpLstStringBuilder.append("===============================================\n")
            _textviewtxt.postValue(
                "$xTimes - ${nicehashTime.serverTime} = ${xTime - (nicehashTime.serverTime ?: 0)}\n$grpLstStringBuilder $mAccount2"
            )*/
        }
        // _textviewtxt.value = System.currentTimeMillis().toString()
    }

    //val contxt = application.applicationContext
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        SECRET_PREF_FN,
        masterKeyAlias,
        application.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var nhApiKey = MutableLiveData<String>()
    var nhApiSecret = MutableLiveData<String>()
    var nhOrgId = MutableLiveData<String>()

    init {

        Log.d(
            "M_SNHSViewModel",
            "SNHSViewModel init.  Application.packageName: ${application.packageName}"
        )
        _showRefreshLayout.value=false
        nhApiKey.value = sharedPreferences.getString(API_KEY, "")
        nhApiSecret.value = sharedPreferences.getString(API_SECRET, "")
        nhOrgId.value = sharedPreferences.getString(API_ORG_ID, "")

        if (sharedPreferences.getString(API_KEY, "").isNullOrEmpty() ||
            sharedPreferences.getString(API_SECRET, "").isNullOrEmpty() ||
            sharedPreferences.getString(API_ORG_ID, "").isNullOrEmpty()
        ) {
            _showPassUserLayout.value = true
        }
        onClinkGetNiceHashStatistics()
    }

}

private val moshi = Moshi.Builder()
    ///.add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    )
    .baseUrl(BASEURL)
    .build()

interface NicehashApiService {

//    @GET("auth")
//    suspend fun getBeeToken(@Query("login") number: String, @Query("password") password: String):
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
//            BeelineToken

    // @GET("info/serviceList")
//    suspend fun getNiceHashMiningInfo(@Query("ctn") number: String, @Query("token") token: String):

    /*
        @GET(PATH_MINING_INFO)
        suspend fun getNiceHashMiningInfo(@HeaderMap headerMap: Map<String, String>):
                MiningInfo


        @GET(PATH_GRP_LIST)
        suspend fun getNiceHashMiningGrpLst(  @HeaderMap headerMap: Map<String, String>, @Query("extendedResponse") extResp: String):
                GrpList
    */
    @GET(PATH_ACCOUNTING2_BTC)
    suspend fun getNiceHashAccounging2Btc(@HeaderMap headerMap: Map<String, String>, @Query("extendedResponse") extResp: String):
            Accounting2Btc

    @GET(PATH_MININGS_RIGS2)
    suspend fun getNiceHashMiningRigs2(@HeaderMap headerMap: Map<String, String>):
            NiceHashMiningRigs2

/*
    @GET(PATH_SERVER_TIME)
    suspend fun getNiceHashTime():
            NicehashTime
*/

    @POST(PATH_RIGS_STATUS2)
    suspend fun getRigsStatus2(@HeaderMap headerMap: Map<String, String>, @Body body: RigsStatusBody):
            RigsStatusReturn
}

object NicehashApi {
    val retrofitService: NicehashApiService by lazy { retrofit.create(NicehashApiService::class.java) }
}

/*
@JsonClass(generateAdapter = true)
data class MiningInfo(
    @Json(name = "btcAddress")
    var btcAddress: String?,
    @Json(name = "downloadData")
    var downloadData: DownloadData?
)

@JsonClass(generateAdapter = true)
data class DownloadData(
    @Json(name = "nhm")
    var nhm: Nhm?,
    @Json(name = "nhos")
    var nhos: Nhos?
)

@JsonClass(generateAdapter = true)
data class Nhm(
    @Json(name = "link")
    var link: String?,
    @Json(name = "size")
    var size: String?,
    @Json(name = "version")
    var version: String?
)

@JsonClass(generateAdapter = true)
data class Nhos(
    @Json(name = "link")
    var link: String?,
    @Json(name = "size")
    var size: String?,
    @Json(name = "version")
    var version: String?
)
*/


@JsonClass(generateAdapter = true)
data class NicehashTime(
    @Json(name = "serverTime")
    val serverTime: Long?
)

@JsonClass(generateAdapter = true)
data class RigsStatusReturn(
    @Json(name = "message")
    val message: String?,
    @Json(name = "successType")
    val successType: String?,
    @Json(name = "success")
    val success: Boolean?
)

@JsonClass(generateAdapter = true)
data class RigsStatusBody(
    @Json(name = "action")
    val action: String?,
    @Json(name = "rigId")
    val rigId: String?
//    @Json(name = "group")
//    val group: String = ""
)

/*
@JsonClass(generateAdapter = true)
data class GrpList(
    @Json(name = "groups")
    val groups: Groups?
)

@JsonClass(generateAdapter = true)
data class Groups(
    @Json(name = "")
    val x: XMiningRigs?
)

@JsonClass(generateAdapter = true)
data class XMiningRigs(
    @Json(name = "miningRigs")
    val miningRigs: Int?,
    @Json(name = "rigs")
    val rigs: List<Rig?>?,
    @Json(name = "totalRigs")
    val totalRigs: Int?
)

@JsonClass(generateAdapter = true)
data class Rig(
    @Json(name = "activeDevices")
    val activeDevices: Int?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "rigId")
    val rigId: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "totalDevices")
    val totalDevices: Int?
)
*/

/*
@JsonClass(generateAdapter = true)
data class NiceHashMiningRigs(
    @Json(name = "btcAddress")
    val btcAddress: String?, // 31mabQu8nsXWzoyZRbnhtFa3gfR3Ub5vH6
    @Json(name = "externalAddress")
    val externalAddress: Boolean?, // false
    @Json(name = "miningRigs")
    val miningRigs: List<MiningRig?>?,
    @Json(name = "nextPayoutTimestamp")
    val nextPayoutTimestamp: String?, // 2020-03-01T04:00:00Z
    @Json(name = "pagination")
    val pagination: Pagination?,
    @Json(name = "totalProfitability")
    val totalProfitability: Double?, // 0.0
    @Json(name = "totalRigs")
    val totalRigs: Int?, // 0
    @Json(name = "unpaidAmount")
    val unpaidAmount: String? // 0.00001796
) {
    @JsonClass(generateAdapter = true)
    data class MiningRig(
        @Json(name = "cpuExists")
        val cpuExists: Boolean?, // false
        @Json(name = "cpuMiningEnabled")
        val cpuMiningEnabled: Boolean?, // false
        @Json(name = "devices")
        val devices: List<Device?>?,
        @Json(name = "groupName")
        val groupName: String?,
        @Json(name = "joinTime")
        val joinTime: Int?, // 1582915641
        @Json(name = "minerStatus")
        val minerStatus: String?, // MINING
        @Json(name = "name")
        val name: String?, // nhosworker2
        @Json(name = "notifications")
        val notifications: List<Notification?>?,
        @Json(name = "profitability")
        val profitability: Double?, // 2.016E-4
        @Json(name = "rigId")
        val rigId: String?, // 0-YCAGvqO8XMm8r4hveZQQjg
        @Json(name = "rigPowerMode")
        val rigPowerMode: String?, // MIXED
        @Json(name = "softwareVersions")
        val softwareVersions: String?, // NHM3_NHOS/3.0.4.4,NHOS/1.1.5
        @Json(name = "stats")
        val stats: List<Stat?>?,
        @Json(name = "statusTime")
        val statusTime: Long?, // 1583029050082
        @Json(name = "type")
        val type: String?, // MANAGED
        @Json(name = "unpaidAmount")
        val unpaidAmount: String? // 0.00001796
    ) {
        @JsonClass(generateAdapter = true)
        data class Device(
            @Json(name = "deviceType")
            val deviceType: DeviceType?,
            @Json(name = "id")
            val id: String?, // 2-6
            @Json(name = "intensity")
            val intensity: Intensity?,
            @Json(name = "load")
            val load: Double?, // 100.0
            @Json(name = "name")
            val name: String?, // GeForce GTX 1080 Ti
            @Json(name = "powerMode")
            val powerMode: PowerMode?,
            @Json(name = "revolutionsPerMinute")
            val revolutionsPerMinute: Double?, // 3595.0
            @Json(name = "speeds")
            val speeds: List<Speed?>?,
            @Json(name = "status")
            val status: Status?,
            @Json(name = "temperature")
            val temperature: Double? // 74.0
        ) {
            @JsonClass(generateAdapter = true)
            data class DeviceType(
                @Json(name = "description")
                val description: String?, // Nvidia
                @Json(name = "enumName")
                val enumName: String? // NVIDIA
            )

            @JsonClass(generateAdapter = true)
            data class Intensity(
                @Json(name = "description")
                val description: String?, // High power mode
                @Json(name = "enumName")
                val enumName: String? // HIGH
            )

            @JsonClass(generateAdapter = true)
            data class PowerMode(
                @Json(name = "description")
                val description: String?, // Medium power mode
                @Json(name = "enumName")
                val enumName: String? // MEDIUM
            )

            @JsonClass(generateAdapter = true)
            data class Speed(
                @Json(name = "algorithm")
                val algorithm: String?, // GRINCUCKATOO31
                @Json(name = "displaySuffix")
                val displaySuffix: String?, // G
                @Json(name = "speed")
                val speed: String?, // 1.54999995
                @Json(name = "title")
                val title: String? // GrinCuckatoo31
            )

            @JsonClass(generateAdapter = true)
            data class Status(
                @Json(name = "description")
                val description: String?, // Mining
                @Json(name = "enumName")
                val enumName: String? // MINING
            )
        }

        @JsonClass(generateAdapter = true)
        data class Notification(
            @Json(name = "enabled")
            val enabled: Boolean?, // false
            @Json(name = "notification")
            val notification: String? // RIG_ERROR
        )

        @JsonClass(generateAdapter = true)
        data class Stat(
            @Json(name = "algorithm")
            val algorithm: Algorithm?,
            @Json(name = "difficulty")
            val difficulty: Double?, // 4.0
            @Json(name = "market")
            val market: String?, // EU
            @Json(name = "profitability")
            val profitability: Double?, // 2.016E-4
            @Json(name = "proxyId")
            val proxyId: Int?, // 0
            @Json(name = "speedAccepted")
            val speedAccepted: Double?, // 3.3655999999999997
            @Json(name = "speedRejectedR1Target")
            val speedRejectedR1Target: Double?, // 0.0
            @Json(name = "speedRejectedR2Stale")
            val speedRejectedR2Stale: Double?, // 0.0
            @Json(name = "speedRejectedR3Duplicate")
            val speedRejectedR3Duplicate: Double?, // 0.0
            @Json(name = "speedRejectedR4NTime")
            val speedRejectedR4NTime: Double?, // 0.0
            @Json(name = "speedRejectedR5Other")
            val speedRejectedR5Other: Double?, // 0.0
            @Json(name = "speedRejectedTotal")
            val speedRejectedTotal: Double?, // 0.0
            @Json(name = "statsTime")
            val statsTime: Long?, // 1583029031000
            @Json(name = "timeConnected")
            val timeConnected: Long?, // 1583025658442
            @Json(name = "unpaidAmount")
            val unpaidAmount: String?, // 0.00001777
            @Json(name = "xnsub")
            val xnsub: Boolean? // true
        ) {
            @JsonClass(generateAdapter = true)
            data class Algorithm(
                @Json(name = "description")
                val description: String?, // GrinCuckatoo31
                @Json(name = "enumName")
                val enumName: String? // GRINCUCKATOO31
            )
        }
    }

    @JsonClass(generateAdapter = true)
    data class Pagination(
        @Json(name = "page")
        val page: Int?, // 0
        @Json(name = "size")
        val size: Int?, // 100
        @Json(name = "totalPageCount")
        val totalPageCount: Int? // 1
    )
}*/

@JsonClass(generateAdapter = true)
data class NiceHashMiningRigs2(
    @Json(name = "btcAddress")
    val btcAddress: String?, // 31mabQu8nsXWzoyZRbnhtFa3gfR3Ub5vH6
    @Json(name = "devicesStatuses")
    val devicesStatuses: DevicesStatuses?,
    @Json(name = "externalAddress")
    val externalAddress: Boolean?, // false
    @Json(name = "groupPowerMode")
    val groupPowerMode: String?, // MIXED
    @Json(name = "minerStatuses")
    val minerStatuses: MinerStatuses?,
    @Json(name = "miningRigGroups")
    val miningRigGroups: List<Any?>?,
    @Json(name = "miningRigs")
    val miningRigs: List<MiningRig?>?,
    @Json(name = "nextPayoutTimestamp")
    val nextPayoutTimestamp: String?, // 2020-03-02T08:00:00Z
    @Json(name = "pagination")
    val pagination: Pagination?,
    @Json(name = "path")
    val path: String?,
    @Json(name = "rigNhmVersions")
    val rigNhmVersions: List<String?>?,
    @Json(name = "rigTypes")
    val rigTypes: RigTypes?,
    @Json(name = "totalDevices")
    val totalDevices: Int?, // 4
    @Json(name = "totalProfitability")
    val totalProfitability: Double?, // 0.0
    @Json(name = "totalRigs")
    val totalRigs: Int?, // 2
    @Json(name = "unpaidAmount")
    val unpaidAmount: String? // 0.00000092
) {
    @JsonClass(generateAdapter = true)
    data class DevicesStatuses(
        @Json(name = "DISABLED")
        val dISABLED: Int?, // 1
        @Json(name = "MINING")
        val mINING: Int?, // 2
        @Json(name = "OFFLINE")
        val oFFLINE: Int? // 2
    )

    @JsonClass(generateAdapter = true)
    data class MinerStatuses(
        @Json(name = "MINING")
        val mINING: Int?, // 1
        @Json(name = "OFFLINE")
        val oFFLINE: Int? // 1
    )

    @JsonClass(generateAdapter = true)
    data class MiningRig(
        @Json(name = "cpuExists")
        val cpuExists: Boolean?, // true
        @Json(name = "cpuMiningEnabled")
        val cpuMiningEnabled: Boolean?, // false
        @Json(name = "devices")
        val devices: List<Device?>?,
        @Json(name = "groupName")
        val groupName: String?,
        @Json(name = "joinTime")
        val joinTime: Int?, // 1583098417
        @Json(name = "minerStatus")
        val minerStatus: String?, // MINING
        @Json(name = "name")
        val name: String?, // worker1
        @Json(name = "notifications")
        val notifications: List<Any?>?,
        @Json(name = "profitability")
        val profitability: Double?, // 0.0
        @Json(name = "rigId")
        val rigId: String?, // 0-YHkWi76IElCbGvFyDwQcDg
        @Json(name = "rigPowerMode")
        val rigPowerMode: String?, // UNKNOWN
        @Json(name = "softwareVersions")
        val softwareVersions: String?, // NHM/3.0.0.5
        @Json(name = "stats")
        val stats: List<Stat?>?,
        @Json(name = "statusTime")
        val statusTime: Long?, // 1583122280527
        @Json(name = "type")
        val type: String?, // MANAGED
        @Json(name = "unpaidAmount")
        val unpaidAmount: String? // 0.00000092
    ) {
        @JsonClass(generateAdapter = true)
        data class Device(
            @Json(name = "deviceType")
            val deviceType: DeviceType?,
            @Json(name = "id")
            val id: String?, // 2-iqZnNhJKnFSVr4Ffgz9N7g
            @Json(name = "intensity")
            val intensity: Intensity?,
            @Json(name = "load")
            val load: Double?, // 100.0
            @Json(name = "name")
            val name: String?, // NVIDIA GeForce GTX 1080 Ti
            @Json(name = "powerMode")
            val powerMode: PowerMode?,
            @Json(name = "revolutionsPerMinute")
            val revolutionsPerMinute: Double?, // 4694.0
            @Json(name = "speeds")
            val speeds: List<Speed?>?,
            @Json(name = "status")
            val status: Status?,
            @Json(name = "temperature")
            val temperature: Double? // 69.0
        ) {
            @JsonClass(generateAdapter = true)
            data class DeviceType(
                @Json(name = "description")
                val description: String?, // Nvidia
                @Json(name = "enumName")
                val enumName: String? // NVIDIA
            )

            @JsonClass(generateAdapter = true)
            data class Intensity(
                @Json(name = "description")
                val description: String?, // Low power mode
                @Json(name = "enumName")
                val enumName: String? // LOW
            )

            @JsonClass(generateAdapter = true)
            data class PowerMode(
                @Json(name = "description")
                val description: String?, // Unknown
                @Json(name = "enumName")
                val enumName: String? // UNKNOWN
            )

            @JsonClass(generateAdapter = true)
            data class Speed(
                @Json(name = "algorithm")
                val algorithm: String?, // GRINCUCKATOO31
                @Json(name = "displaySuffix")
                val displaySuffix: String?, // G
                @Json(name = "speed")
                val speed: String?, // 1.421
                @Json(name = "title")
                val title: String? // GrinCuckatoo31
            )

            @JsonClass(generateAdapter = true)
            data class Status(
                @Json(name = "description")
                val description: String?, // Mining
                @Json(name = "enumName")
                val enumName: String? // MINING
            )
        }

        @JsonClass(generateAdapter = true)
        data class Stat(
            @Json(name = "algorithm")
            val algorithm: Algorithm?,
            @Json(name = "difficulty")
            val difficulty: Double?, // 4.0
            @Json(name = "market")
            val market: String?, // USA
            @Json(name = "profitability")
            val profitability: Double?, // 0.0
            @Json(name = "proxyId")
            val proxyId: Int?, // 0
            @Json(name = "speedAccepted")
            val speedAccepted: Double?, // 1.6912186666666666
            @Json(name = "speedRejectedR1Target")
            val speedRejectedR1Target: Double?, // 0.0
            @Json(name = "speedRejectedR2Stale")
            val speedRejectedR2Stale: Double?, // 0.0
            @Json(name = "speedRejectedR3Duplicate")
            val speedRejectedR3Duplicate: Double?, // 0.0
            @Json(name = "speedRejectedR4NTime")
            val speedRejectedR4NTime: Double?, // 0.0
            @Json(name = "speedRejectedR5Other")
            val speedRejectedR5Other: Double?, // 0.0
            @Json(name = "speedRejectedTotal")
            val speedRejectedTotal: Double?, // 0.0
            @Json(name = "statsTime")
            val statsTime: Long?, // 1583122273000
            @Json(name = "timeConnected")
            val timeConnected: Long?, // 1583115283309
            @Json(name = "unpaidAmount")
            val unpaidAmount: String?, // 0.00000092
            @Json(name = "xnsub")
            val xnsub: Boolean? // true
        ) {
            @JsonClass(generateAdapter = true)
            data class Algorithm(
                @Json(name = "description")
                val description: String?, // GrinCuckatoo31
                @Json(name = "enumName")
                val enumName: String? // GRINCUCKATOO31
            )
        }
    }

    @JsonClass(generateAdapter = true)
    data class Pagination(
        @Json(name = "page")
        val page: Int?, // 0
        @Json(name = "size")
        val size: Int?, // 25
        @Json(name = "totalPageCount")
        val totalPageCount: Int? // 1
    )

    @JsonClass(generateAdapter = true)
    data class RigTypes(
        @Json(name = "MANAGED")
        val mANAGED: Int? // 2
    )
}

@JsonClass(generateAdapter = true)
data class Accounting2Btc(
    @Json(name = "active")
    val active: Boolean?, // true
    @Json(name = "available")
    val available: String?, // 0.00201747
    @Json(name = "currency")
    val currency: String?, // BTC
    @Json(name = "pending")
    val pending: String?, // 0.00001322
    @Json(name = "pendingDetails")
    val pendingDetails: PendingDetails?,
    @Json(name = "totalBalance")
    val totalBalance: String? // 0.00203069
) {
    @JsonClass(generateAdapter = true)
    data class PendingDetails(
        @Json(name = "deposit")
        val deposit: String?, // 0
        @Json(name = "exchange")
        val exchange: String?, // 0
        @Json(name = "hashpowerOrders")
        val hashpowerOrders: String?, // 0
        @Json(name = "unpaidMining")
        val unpaidMining: String?, // 0.00001322
        @Json(name = "withdrawal")
        val withdrawal: String? // 0
    )
}