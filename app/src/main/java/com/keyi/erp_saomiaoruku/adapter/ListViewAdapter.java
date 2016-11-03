package com.keyi.erp_saomiaoruku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.keyi.erp_saomiaoruku.R;
import com.keyi.erp_saomiaoruku.bean.StockNoMsg;
import com.keyi.erp_saomiaoruku.scanner.CaptureActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/4/29.
 */
public class ListViewAdapter extends BaseAdapter implements View.OnClickListener {
    private StockNoMsg stockNoMsg;
    private Button button;
    private Context context;

    public ListViewAdapter(Context context, StockNoMsg stockNoMsg) {
        this.context = context;
        this.stockNoMsg = stockNoMsg;
    }

    @Override
    public int getCount() {
        return stockNoMsg.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return stockNoMsg.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_car, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.textView.setText( stockNoMsg.getData().get(stockNoMsg.getData().size()-1-position).getStockNo());
        viewHolder.button.setOnClickListener(this);
        viewHolder.button.setTag(stockNoMsg.getData().size()-1-position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.putExtra("stockNo", stockNoMsg.getData().get((int) v.getTag()).getStockNo());
        intent.putExtra("flag",0);
        context.startActivity(intent);
    }

    static class ViewHolder {
        @Bind(R.id.tv_car)
        TextView textView;
        @Bind(R.id.bt_car)
        Button button;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
