package br.edu.ifba.tcc.iot.winerfid.adaptercustom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.edu.ifba.tcc.iot.winerfid.R;
import br.edu.ifba.tcc.iot.winerfid.bean.WineBean;

/**
 * Created by randler on 25/10/16.
 */
public class WineListAdapterCustom extends ArrayAdapter<WineBean> {

    private int layoutResourceId;
    private static final String LOG_TAG = "MemoListAdapter";

    public WineListAdapterCustom(Context context, int resource) {
        super(context, resource);
        layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        try{
            WineBean item = getItem(position);

            View v = null;

            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceId, null);
            }else
                v = convertView;

            TextView tagid      = (TextView) v.findViewById(R.id.tag_id);
            TextView produto    = (TextView) v.findViewById(R.id.produto);
            TextView n_palete   = (TextView) v.findViewById(R.id.n_palete);
            TextView lote       = (TextView) v.findViewById(R.id.lote);
            TextView destino    = (TextView) v.findViewById(R.id.destino);

            if(item.getTag_id() != null) {
                tagid.setText   ("Tag ID: " + item.getTag_id());
                n_palete.setText("Nº Palete: " + item.getN_palete());

                if (item.isSearch()) {
                    tagid.setTextColor      (Color.GREEN);
                    produto.setTextColor    (Color.GREEN);
                    n_palete.setTextColor   (Color.GREEN);
                    lote.setTextColor       (Color.GREEN);
                    destino.setTextColor    (Color.GREEN);
                } else {
                    tagid.setTextColor      (Color.RED);
                    produto.setTextColor    (Color.RED);
                    n_palete.setTextColor   (Color.RED);
                    lote.setTextColor       (Color.RED);
                    destino.setTextColor    (Color.RED);

                }
            }else{
                tagid.setText   ("Tag ID: Não Cadastrado" );
                produto.setText ("Produto: Não Cadastrado");
                n_palete.setText("Nº Palete: Não Cadastrado");
                lote.setText    ("Lote: Não Cadastrado");
                destino.setText ("Destino: Não Cadastrado" );

                tagid.setTextColor      (Color.BLACK);
                produto.setTextColor    (Color.BLACK);
                n_palete.setTextColor   (Color.BLACK);
                lote.setTextColor       (Color.BLACK);
                destino.setTextColor    (Color.BLACK);
            }

            return v;


        }catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return null;
        }
    }
}
