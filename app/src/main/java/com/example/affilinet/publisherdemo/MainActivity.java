package com.example.affilinet.publisherdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.rm.affilinet.SessionCallback;
import com.rm.affilinet.adview.AdDisplayView;
import com.rm.affilinet.communication.Request;
import com.rm.affilinet.communication.RequestResponse;
import com.rm.affilinet.exceptions.SessionException;
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

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demolayout);

        PublisherAccount account = new PublisherAccount({PUBLISHER - ID}, {PRODUCT - DATA - WEBSERVICE - PASSWORD});
        Session session = Session.getInstance();

        try {
            session.open(getApplicationContext(), account, {PLATFORM - ID});
            runPublisherTests(session);
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }

    }

    private void runPublisherTests(final Session session) {

        //
        // DISPLAY AD CONTAINER
        //

        // Display Ad Container (you get container ids via the affilinet publisher portal, see http://publisher.affili.net/Creatives/Adspaces.aspx
        ContainerAd containerAd = new ContainerAd(session, "{PUBLISHER-CONTAINER-ID}", "{PUBLISHER-SUBID}");
        //ContainerAd containerAd = new ContainerAd(session, "{PUBLISHER-CONTAINER-ID}");

        // Choose from standard adsizes defined or use your custom adsize
        containerAd.setAdSize(AdSize.FeaturePhoneLargeBanner);
        //containerAd.setAdSize(new AdSize(new Size(300,300)));

        AdDisplayView adView = new AdDisplayView(this, containerAd);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.demolayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainLayout.addView(adView, 1, params);

        //
        // SELECT & EXECUTE PRODUCT DATA WEBSERVICE (PDWS3) REQUESTS
        //

        /* Generate ListView to list executable webservice actions to execute */
        final ListView listview = (ListView) findViewById(R.id.main_menu_list_view);
        String[] values = new String[]{"GetShopList", "GetCategoryList", "GetProducts", "SearchProducts (Category)", "SearchProducts (FilterQuery)"};

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            list.add(values[i]);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position) {
                    case 0:
                        runGetShopList(session);
                        break;
                    case 1:
                        runGetCategoryList(session);
                        break;
                    case 2:
                        runGetProducts(session);
                        break;
                    case 3:
                        runSearchProductsByCat(session);
                        break;
                    case 4:
                        runSearchProductsByFQ(session);
                        break;

                    default:
                        break;
                }
            }

        });
    }

    /* Define PDWS3 webservice request GetShopList to get list of product feeds (=shops) you have access to (note: active partnership with program required) */
    private void runGetShopList(Session session) {
        // GetShopList Request
        GetShopListRequest req = new GetShopListRequest(session);
        long timestamp = 1388601005;
        timestamp *= 1000;
        req.setUpdatedAfter(new Date(timestamp));
        req.setLogoScale(WSShopLogoScale.NoLogo);

        this.executeRequest(req);
    }

    /* Define PDWS3 webservice request GetCategoryList to get category list for specific product feed (=shop) */
    private void runGetCategoryList(Session session) {
        // GetCategoryList Request
        GetCategoryListRequest req = new GetCategoryListRequest(session);
        req.setShopId(102);

        this.executeRequest(req);
    }

    /* Define PDWS3 webservice request GetProducts to get details of specific products */
    private void runGetProducts(Session session) {
        // GetProducts Request
        GetProductsRequest req = new GetProductsRequest(session);
        List<String> productIDs = new ArrayList<>();
        productIDs.add("493822602");
        productIDs.add("526068620");
        productIDs.add("437092183");
        req.setProductIds(productIDs);
        List<WSProductImageScale> imagescales = new ArrayList<>();
        imagescales.add(WSProductImageScale.Image90);
        imagescales.add(WSProductImageScale.Image180);
        req.setImageScales(imagescales);
        List<WSShopLogoScale> logoscales = new ArrayList<>();
        logoscales.add(WSShopLogoScale.Logo120);
        req.setLogoScales(logoscales);

        this.executeRequest(req);
    }

    /* Define PDWS3 webservice request SearchProducts using category filter */
    private void runSearchProductsByCat(Session session) {
        // SearchProducts Request within specific shopIds (= feeds of an advertiser) and categoryIDs
        SearchProductsRequest req = new SearchProductsRequest(session);
        List<String> categoryIDs = new ArrayList<>();
        categoryIDs.add("33388558");
        req.setCategoryIds(categoryIDs);
        req.setUseAffilinetCategories(false);
        req.setExcludeSubCategories(false);
        List<Integer> shopIds = new ArrayList<>();
        shopIds.add(0);
        req.setShopIds(shopIds);
        req.setShopIdMode(Include);
        req.setWithImageOnly(true);
        List<WSProductImageScale> imagescales = new ArrayList<>();
        imagescales.add(WSProductImageScale.Image90);
        imagescales.add(WSProductImageScale.Image180);
        req.setImageScales(imagescales);
        List<WSShopLogoScale> logoscales = new ArrayList<>();
        logoscales.add(WSShopLogoScale.Logo120);
        req.setLogoScales(logoscales);
        req.setMinimumPrice(1.00);
        req.setMaximumPrice(999.00);
        req.setSortBy(WSProductSortBy.valueOf("Name"));
        req.setSortOrder(WSSortOrder.Ascending);

        this.executeRequest(req);
    }

    /* Define PDWS3 webservice request SearchProducts with FilterQueries */
    private void runSearchProductsByFQ(Session session) {
        // SearchProducts Request with "Query" method
        SearchProductsRequest req = new SearchProductsRequest(session);
        req.setQuery("iPhone");
        List<Integer> shopIds = new ArrayList<>();
        shopIds.add(0);
        req.setShopIds(shopIds);
        req.setShopIdMode(Include);
        req.setWithImageOnly(true);
        List<WSProductImageScale> imagescales = new ArrayList<>();
        imagescales.add(WSProductImageScale.Image90);
        imagescales.add(WSProductImageScale.Image180);
        req.setImageScales(imagescales);
        List<WSShopLogoScale> logoscales = new ArrayList<>();
        logoscales.add(WSShopLogoScale.Logo120);
        req.setLogoScales(logoscales);
        req.setMinimumPrice(50.00);
        req.setMaximumPrice(999.00);
        req.setSortBy(WSProductSortBy.valueOf("Name"));
        req.setSortOrder(WSSortOrder.Ascending);
        WSFilterQuery fq1 = new WSFilterQuery();
        fq1.setDataField("ShopCategoryPath");
        fq1.setFilterValue("handys");
        WSFilterQuery fq2 = new WSFilterQuery();
        fq2.setDataField("Brand");
        fq2.setFilterValue("Apple");
        List<WSFilterQuery> q = new ArrayList<>(2);
        q.add(fq1);
        q.add(fq2);
        req.setFilterQueries(q);

        this.executeRequest(req);
    }

    /* Execute webservice request */
    private void executeRequest(Request request) {
        List<Request> requests = new ArrayList<Request>();
        requests.add(request);
        try {
            this.showProgressDialog();
            Session.getInstance().executeRequests(requests, new SessionCallback() {

                @Override
                public void onRequestsFinished() {
                    Log.d(TAG, "Requests finished");
                    hideProgressDialog();
                }

                @Override
                public void onRequestsError(Error error) {
                    Log.d(TAG, "Requests finished with error: " + error);
                    hideProgressDialog();
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

    /* Show progress dialog, while webservice request are executed */
    private void showProgressDialog() {
        if (this.progress == null) {
            this.progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
        }
        progress.show();
    }

    /* Hide progress dialog, after webservice request execution finished */
    private void hideProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

}
