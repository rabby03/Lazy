package net.lazybd.vpn.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.lazybd.vpn.R;
import net.lazybd.vpn.util.api.models.SockServer;

import java.util.List;

public class ServerListViewAdapter extends RecyclerView.Adapter<ServerListViewAdapter.ViewHolder> {
    private List<SockServer> sockServers;
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(SockServer sockServer);
    }

    public ServerListViewAdapter(List<SockServer> sockServers,OnItemClickListener listener) {
        this.sockServers = sockServers;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.server_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SockServer sockServer = sockServers.get(position);
        holder.serverNameView.setText(sockServer.getName());
        holder.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   listener.onItemClick(sockServer);


            }
        });

    }

  public void  gotoConnection(){

    }


    @Override
    public int getItemCount() {
        return sockServers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView serverNameView,serverNameView1;
        public LinearLayout linearLayout;
        public Button connectButton;

        public ViewHolder(View itemView) {
            super(itemView);
            serverNameView = (TextView) itemView.findViewById(R.id.server_name);

            connectButton = (Button) itemView.findViewById(R.id.connect_btn);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.server_item_view);
        }
    }
}