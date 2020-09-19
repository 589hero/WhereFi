package com.example2.wherefi

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : Fragment() {
    lateinit var myWifiDBHelper: MyWifiDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val rootView = inflater.inflate(R.layout.fragment_favorite, container, false)
//
//        tempTemplate = rootView.findViewById(R.id.myTemplate)
//
//        return rootView
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
    }

    fun init(){
//        var adLoader: AdLoader? = null
//        MobileAds.initialize(activity)
//
//        adLoader = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
//            .forUnifiedNativeAd {
////                val template: TemplateView = activity!!.findViewById(R.id.myTemplate)
//                val template: TemplateView = tempTemplate
//                template.setNativeAd(it)
//            }
//            .withAdListener(object: AdListener(){
//                override fun onAdFailedToLoad(errorCode: Int) {
//                    super.onAdFailedToLoad(errorCode)
//                }
//            })
//            .withNativeAdOptions(NativeAdOptions.Builder().build())
//            .build()
//
//        adLoader?.loadAd(AdRequest.Builder().build())

        myWifiDBHelper = MyWifiDBHelper(activity)
        addView()
    }

    fun addView(){
        // MainActivity.favoriteWifiIds.size만큼 생성하기 View생성하기.
        // 추가/삭제되면 View들을 전부 지웠다가 다시 만들기
        var favoriteWifiArray = myWifiDBHelper.selectWifiIdUsingID()

        favoriteWifiLayout.removeAllViewsInLayout()

        var cardViewParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                        RelativeLayout.LayoutParams.WRAP_CONTENT)
        cardViewParam.setMargins(20, 20, 20, 20)


        var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        var linearLayoutParam2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayoutParam2.width = 0
        linearLayoutParam2.weight = 9.0f

        
        // textViewParam와 imageView Param도 만들기
        var textViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        textViewParam.setMargins(10, 0, 10, 10)

        var imageViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        imageViewParam.width = 0
        imageViewParam.weight = 1.0f

        
        for(favoriteWifi in favoriteWifiArray){
            // CardView( LinearLayout ( LinearLayout (TextView, TextView), ImageView))
            var cardView = CardView(requireContext())
            cardView.layoutParams = cardViewParam
            cardView.radius = 10.0f
            cardView.cardElevation = 8.0f

            // cardView에 붙을 linearLayout
            var linearLayout = LinearLayout(context)
            linearLayout.layoutParams = linearLayoutParam
            linearLayout.orientation = LinearLayout.HORIZONTAL


            // linearLayout에 붙을 linearLayout
            var linearLayout2 = LinearLayout(context)
            linearLayout2.layoutParams = linearLayoutParam2
            linearLayout2.orientation = LinearLayout.VERTICAL

            // textView1 : WifiId
            // textView2 : Wifi 장소 이름.
            // textView3 : 세부장소
            // textView4 : 도로명주소
            var textView1 = TextView(context)
            var textView2 = TextView(context)
            var textView3 = TextView(context)
            var textView4 = TextView(context)

            textView1.layoutParams = textViewParam
            textView2.layoutParams = textViewParam
            textView3.layoutParams = textViewParam
            textView4.layoutParams = textViewParam

            textView1.text = favoriteWifi.wifiId.toString()
            textView2.text = favoriteWifi.placeName
            textView3.text = favoriteWifi.detailedPlaceName
            textView4.text = favoriteWifi.roadNameAddress

            // text Style 변경
            textView1.typeface = Typeface.DEFAULT_BOLD

            textView1.maxLines = 1
            textView2.maxLines = 1
            textView3.maxLines = 1
            textView4.maxLines = 1

            textView1.ellipsize = TextUtils.TruncateAt.END
            textView2.ellipsize = TextUtils.TruncateAt.END
            textView3.ellipsize = TextUtils.TruncateAt.END
            textView4.ellipsize = TextUtils.TruncateAt.END

            linearLayout2.addView(textView1)
            linearLayout2.addView(textView2)
            linearLayout2.addView(textView3)
            linearLayout2.addView(textView4)


            var imageView = ImageView(context)
            imageView.layoutParams = imageViewParam
            imageView.setImageResource(R.drawable.favorite_delete_selector)
            imageView.isClickable = true
            imageView.setOnClickListener {
                removeFavoriteWifi(it, textView1.text.toString().toInt())
                Toast.makeText(requireActivity(), "즐겨찾기에서 삭제하였습니다.", Toast.LENGTH_SHORT).show()
            }

            linearLayout.addView(linearLayout2)
            linearLayout.addView(imageView)

            cardView.addView(linearLayout)
            favoriteWifiLayout.addView(cardView)
        }
    }

    private fun removeFavoriteWifi(view: View, wifiId: Int){
        MainActivity.favoriteWifiIds.remove(wifiId)

        val linearLayout = view.parent as View
        val cardView = linearLayout.parent as View

        favoriteWifiLayout.removeView(cardView)
        favoriteWifiLayout.invalidate()
    }
}
