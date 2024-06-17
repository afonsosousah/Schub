package pt.afonsosousah.scientificconferences

import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser


internal open class InputStreamVolleyRequest(
    method: Int, mUrl: String?, listener: Response.Listener<ByteArray>,
    errorListener: Response.ErrorListener?
) : Request<ByteArray?>(method, mUrl, errorListener) {
    private val mListener: Response.Listener<ByteArray>

    /*
    @get:Throws(AuthFailureError::class)
    protected val params: Map<String, String>?
     */

    //create a static map for directly accessing headers
    private var responseHeaders: Map<String, String>? = null

    init {
        // TODO Auto-generated constructor stub
        // this request would never use cache.
        setShouldCache(false)
        mListener = listener
    }

    override fun deliverResponse(response: ByteArray?) {
        mListener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray?>? {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers

        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

}