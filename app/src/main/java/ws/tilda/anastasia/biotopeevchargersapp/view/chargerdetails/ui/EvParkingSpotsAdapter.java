package ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSpot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Plug;

public class EvParkingSpotsAdapter extends RecyclerView.Adapter<EvParkingSpotsAdapter.EvParkingSpotsViewHolder> {
    private List<ParkingSpot> evParkingSpots;
    private Context mContext;
    static boolean isSuccessfull = true;
    private EvParkingSpotsAdapter.EvParkingSpotsViewHolder viewHolder;

    public EvParkingSpotsAdapter(List<ParkingSpot> evParkingSpots, Context context) {
        this.evParkingSpots = evParkingSpots;
        this.mContext=context;
    }

    @Override
    public EvParkingSpotsAdapter.EvParkingSpotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ev_spot, parent, false);
        return new EvParkingSpotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EvParkingSpotsAdapter.EvParkingSpotsViewHolder holder, final int position) {
        final Button buttonUseParking = holder.buttonUseParking;
        final Button buttonUseCharger = holder.buttonUseCharger;
        viewHolder = holder;

        ParkingSpot evParkingSpot = evParkingSpots.get(position);
        Plug plug = evParkingSpot.getCharger().getPlug();
        holder.evSpotName.setText(evParkingSpot.getId());
        holder.evSpotStatus.setText(Boolean.toString(evParkingSpot.isAvailable()));
        holder.plugType.setText(plug.getPlugType());
        holder.chargingSpeed.setText(plug.getChargingSpeed());
        holder.chargingPower.setText(plug.getPower());



        if (evParkingSpot.isAvailable() == false) {
            if(evParkingSpot.getUser().equals(((EvSpotListActivity) mContext).getUser().getUsername())) {
                buttonUseParking.setEnabled(true);
                buttonUseParking.setTag(2);
                buttonUseParking.setText("Leave Parking");
            } else {
                buttonUseParking.setEnabled(false);
                buttonUseParking.setTag(1);
                buttonUseParking.setText("Use Parking");
            }
        } else {
            buttonUseParking.setEnabled(true);
            buttonUseParking.setTag(1);
            buttonUseParking.setText("Use Parking");

            buttonUseCharger.setEnabled(false);
        }



        buttonUseParking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if(status == 1) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).reserveEvParkingSpot(v, position);
                        if(isSuccessfull) {
                            viewHolder.evSpotStatus.setText("false");
                            Toast.makeText(mContext, "Parking reservation successful!",
                                    Toast.LENGTH_SHORT).show();
                            buttonUseParking.setText("Leave Parking");
                            v.setTag(2);
                            buttonUseCharger.setEnabled(true);

                        } else {
                            Toast.makeText(mContext, "Parking is already reserved for someone else",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (status == 2){
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).leaveEvParkingSpot(v, position);
                        if(isSuccessfull) {
                            viewHolder.evSpotStatus.setText("true");
                            Toast.makeText(mContext, "You successfully unbooked parking", Toast.LENGTH_SHORT).show();
                            buttonUseParking.setText("Use Parking");
                            v.setTag(1);
                            buttonUseCharger.setEnabled(false);

                        } else {
                            Toast.makeText(mContext, "Something got wrong, try again!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return evParkingSpots.size();
    }

    public static class EvParkingSpotsViewHolder extends RecyclerView.ViewHolder {
        TextView evSpotName;
        TextView evSpotStatus;
        TextView plugType;
        TextView chargingSpeed;
        TextView chargingPower;
        Button buttonUseParking;
        Button buttonUseCharger;


        public EvParkingSpotsViewHolder(View itemView) {
            super(itemView);
            evSpotName = (TextView) itemView.findViewById(R.id.ev_spot_name);
            evSpotStatus = (TextView) itemView.findViewById(R.id.ev_spot_status);
            plugType = (TextView) itemView.findViewById(R.id.plugtype);
            chargingSpeed = (TextView) itemView.findViewById(R.id.charging_speed);
            chargingPower = (TextView) itemView.findViewById(R.id.charging_power);
            buttonUseParking = (Button) itemView.findViewById(R.id.button_use_parking);
            buttonUseCharger = (Button) itemView.findViewById(R.id.button_use_charger);
        }
    }

}