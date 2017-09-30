package com.example.affilinet.publisherdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.rm.affilinet.SessionCallback;
import com.rm.affilinet.adview.AdDisplayView;
import com.rm.affilinet.communication.Request;
import com.rm.affilinet.communication.RequestResponse;
import com.rm.affilinet.exceptions.SessionException;
import com.rm.affilinet.models.Platform;
import com.rm.affilinet.models.PublisherAccount;
import com.rm.affilinet.publisher.Session;
import com.rm.affilinet.publisher.models.AdSize;
import com.rm.affilinet.publisher.models.ContainerAd;
import com.rm.affilinet.webservice.models.WSFilterQuery;
import com.rm.affilinet.webservice.models.WSProductImageScale;
import com.rm.affilinet.webservice.models.WSProductSortBy;
import com.rm.affilinet.webservice.models.WSShopLogoScale;
import com.rm.affilinet.webservice.models.WSSortOrder;
import com.rm.affilinet.webservice.requests.GetCategoryListRequest;
import com.rm.affilinet.webservice.requests.GetProductsRequest;
import com.rm.affilinet.webservice.requests.GetShopListRequest;
import com.rm.affilinet.webservice.requests.SearchProductsRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.rm.affilinet.webservice.models.WSShopIdMode.Include;


public class MainActivity extends Activity {

    private static final String TAG = "affilinet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runPublisherTests();
    }

    private void runPublisherTests() {

        setContentView(R.layout.activity_main);

        PublisherAccount account = new PublisherAccount({PUBLISHER-ID}, {PRODUCT-DATA-WEBSERVICE-PASSWORD});
        Session session = Session.getInstance();

        try {
            session.open(getApplicationContext(), account, {PLATFORM-ID});
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }

        //
        // AD CONTAINER
        //

        // Display Ad Container (you get container ids via the affilinet publisher portal, see http://publisher.affili.net/Creatives/Adspaces.aspx
        ContainerAd containerAd = new ContainerAd(session, "{PUBLISHER-CONTAINER-ID}", "{PUBLISHER-SUBID}");
        //ContainerAd containerAd = new ContainerAd(session, "{PUBLISHER-CONTAINER-ID}");

        // Choose from standard adsizes defined or use your custom adsize
        containerAd.setAdSize(AdSize.FeaturePhoneLargeBanner);
        //containerAd.setAdSize(new AdSize(new Size(300,300)));

        AdDisplayView adView = new AdDisplayView(this, containerAd);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        mainLayout.addView(adView, params);

        //
        // PRODUCT DATA WS REQUESTS
        //
        List<WSProductImageScale> imagescales = new ArrayList<>();
        imagescales.add(WSProductImageScale.Image90);
        imagescales.add(WSProductImageScale.Image180);
        List<WSShopLogoScale> logoscales = new ArrayList<>();
        logoscales.add(WSShopLogoScale.Logo120);
        List<Integer> shopIds = new ArrayList<>();
        shopIds.add(0);

        // GetCategoryList Request
        GetCategoryListRequest req1 = new GetCategoryListRequest(session);
        req1.setShopId(102);

        // GetShopList Request
        GetShopListRequest req2 = new GetShopListRequest(session);
        long timestamp = 1388601005;
        timestamp *= 1000;
        req2.setUpdatedAfter(new Date(timestamp));
        req2.setLogoScale(WSShopLogoScale.NoLogo);

        // GetProducts Request
        GetProductsRequest req3 = new GetProductsRequest(session);
        List<String> productIDs = new ArrayList<>();
        productIDs.add("493822602");
        productIDs.add("526068620");
        productIDs.add("437092183");
        req3.setProductIds(productIDs);
        req3.setImageScales(imagescales);
        req3.setLogoScales(logoscales);

        // SearchProducts Request within specific shopIds (= feeds of an advertiser) and categoryIDs
        SearchProductsRequest req4 = new SearchProductsRequest(session);
        List<String> categoryIDs = new ArrayList<>();
        categoryIDs.add("33388558");
        req4.setCategoryIds(categoryIDs);
        req4.setUseAffilinetCategories(false);
        req4.setExcludeSubCategories(false);
        req4.setShopIds(shopIds);
        req4.setShopIdMode(Include);
        req4.setWithImageOnly(true);
        req4.setImageScales(imagescales);
        req4.setLogoScales(logoscales);
        req4.setMinimumPrice(1.00);
        req4.setMaximumPrice(999.00);
        req4.setSortBy(WSProductSortBy.valueOf("Name"));
        req4.setSortOrder(WSSortOrder.Ascending);

        // SearchProducts Request with "Query" method
        SearchProductsRequest req5 = new SearchProductsRequest(session);
        req5.setQuery("iPhone");
        req5.setShopIds(shopIds);
        req5.setShopIdMode(Include);
        req5.setWithImageOnly(true);
        req5.setImageScales(imagescales);
        req5.setLogoScales(logoscales);
        req5.setMinimumPrice(50.00);
        req5.setMaximumPrice(999.00);
        req5.setSortBy(WSProductSortBy.valueOf("Name"));
        req5.setSortOrder(WSSortOrder.Ascending);
        WSFilterQuery fq1 = new WSFilterQuery();
        fq1.setDataField("ShopCategoryPath");
        fq1.setFilterValue("Handy");
        WSFilterQuery fq2 = new WSFilterQuery();
        fq2.setDataField("Brand");
        fq2.setFilterValue("Apple");
        List<WSFilterQuery> q = new ArrayList<>(2);
        q.add(fq1);
        q.add(fq2);
        req5.setFilterQueries(q);

        // Execute ProductData Webservice (PDWS3) Requests
        List<Request> requests = new ArrayList<>();
        requests.add(req1);
        requests.add(req2);
        requests.add(req3);
        requests.add(req4);
        requests.add(req5);

        try {
            Session.getInstance().executeRequests(requests, new SessionCallback() {

                @Override
                public void onRequestsFinished() {
                    Log.d(TAG, "Requests finished");
                }

                @Override
                public void onRequestsError(Error error) {
                    Log.d(TAG, "Requests finished with error: " + error);
                }

                @Override
                public void onRequestResponse(Request request, RequestResponse response) {
                    if (response.error != null) {
                        Log.d(TAG, String.format("Request %s finished with error %s", request.toString(), response.error.getMessage()));
                    } else {
                        Log.d(TAG, String.format("Request %s finished with response %s", request.toString(), response.toString()));
                    }
                }
            });
        } catch (SessionException e) {
            Log.d(TAG, e.getMessage());
        }

    }

}
