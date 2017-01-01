package br.edu.ifba.tcc.iot.winerfid.fachadaWEB;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.edu.ifba.tcc.iot.winerfid.bean.WineBean;

/**
 * Created by randler on 19/09/16.
 */
public class FachadaWeb extends AsyncTask<String, Void, String>{

    private String URL = "https://winerfid.herokuapp.com";
    private WineBean wine;
    private final String HOME = "/";
    private final String SAVE_WINE = "/savewine/";
    private final String LIST_WINE = "/listwines/";
    private final String LIST_ONE_WINE = "/listonewine/";
    private final String UPDATE_WINE = "/updatewine/";
    private static String result = "";

    HttpClient httpClient = new DefaultHttpClient();


    public void setWine(WineBean wine) {
        this.wine = wine;
    }

    @Override
    protected String doInBackground(String... params) {


        if(params[0].equals("/")){
            executeGETHome(params[0]);

        }else if(params[0].equals("/listwines/")){
            executeGETList(params[0]);

        }else if(params[0].equals("/savewine/")) {
            executePOST(params[0]);

        }else if(params[0].equals("/updatewine/")){
            executePOST(params[0]);

        }

        return result;

    }

    private void executeGETList(String param) {

        HttpGet httpGet = new HttpGet(URL + param);
        try {
            HttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // get the response content as a string
                String resp = EntityUtils.toString(entity);
                // consume the entity
                entity.consumeContent();
                httpClient.getConnectionManager().shutdown();

                   result = resp;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executePOST(String params) {

        HttpPost httpPost = new HttpPost(URL + params);

        // 3. build jsonObject
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.accumulate("pallet_id",     wine.getPalete_id());
            jsonObj.accumulate("caixas_pallet",     wine.getCaixas_tag_id());
            jsonObj.accumulate("status_pedido", wine.getStatus_pedido_pallet());


            // 4. convert JSONObject to JSON to String
            String json = jsonObj.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse response = httpClient.execute(httpPost);

            // 9. receive response as inputStream
            InputStream inputStream = response.getEntity().getContent();

            // 10. convert inputstream to string
            result = getStringFromInputStream(inputStream);
            inputStream.close();
            if(!result.equals(""))
                result = getResponseCorrect(result);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeGETHome(String params) {

        HttpGet httpGet = new HttpGet(URL + params);

        try {
            HttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();

                String json = getStringFromInputStream(instream);
                instream.close();
                result = getResponseCorrect(json);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseCorrect(String result) {

        String [] resp = result.split(":");

        resp[1] = resp[1].substring(1,(resp[1].length()-2));

        return resp[1];
    }



    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public String getHOME() {
        return HOME;
    }


    public String getSAVE_WINE() {
        return SAVE_WINE;
    }

    public String getUPDATE_WINE() {
        return UPDATE_WINE;
    }

    public String getLIST_WINE() {
        return LIST_WINE;
    }

    public String getLIST_ONE_WINE() {
        return LIST_ONE_WINE;
    }
}
