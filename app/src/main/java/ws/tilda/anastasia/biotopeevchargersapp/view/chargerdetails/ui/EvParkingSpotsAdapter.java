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
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSpot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Plug;


public class EvParkingSpotsAdapter extends RecyclerView.Adapter<EvParkingSpotsAdapter.EvParkingSpotsViewHolder> {
    private List<ParkingSpot> evParkingSpots;
    private Context mContext;

    public EvParkingSpotsAdapter(List<ParkingSpot> evParkingSpots, Context context) {
        this.evParkingSpots = evParkingSpots;
        this.mContext = context;

    }

    @Override
    public EvParkingSpotsAdapter.EvParkingSpotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ev_spot, parent, false);
        return new EvParkingSpotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EvParkingSpotsAdapter.EvParkingSpotsViewHolder holder, final int position) {
        final Button buttonUseParking = holder.buttonUseParking;
        final Button buttonUseCharger = holder.buttonUseCharger;

        final ParkingSpot evParkingSpot = evParkingSpots.get(position);
        Plug plug = evParkingSpot.getCharger().getPlug();
        holder.evSpotName.setText(evParkingSpot.getId());
        holder.evSpotStatus.setText(Boolean.toString(evParkingSpot.isAvailable()));
        holder.plugType.setText(plug.getPlugType());
        holder.chargingSpeed.setText(plug.getChargingSpeed());
        holder.chargingPower.setText(plug.getPower());
        Charger charger = evParkingSpot.getCharger();

        if (!evParkingSpot.isAvailable()) {
            if (evParkingSpot.getUser().equals(((EvSpotListActivity) mContext).getUser().getUsername())) {
                evParkingSpot.setBookedByOurUser(true);
            } else {
                evParkingSpot.setBookedByOurUser(false);
            }
        } else {
            evParkingSpot.setBookedByOurUser(false);
        }

        if (!evParkingSpot.isBookedByOurUser()) {
            if (!evParkingSpot.isAvailable()) {
                buttonUseParking.setEnabled(false);
                buttonUseParking.setText("Use Parking");
                buttonUseCharger.setEnabled(false);
            } else if (evParkingSpot.isAvailable()) {
                buttonUseParking.setEnabled(true);
                buttonUseParking.setText("Use Parking");
                buttonUseCharger.setEnabled(false);
            }
        } else {
            buttonUseParking.setText("Leave Parking");
            buttonUseParking.setEnabled(true);
            buttonUseCharger.setEnabled(true);
        }

        if (evParkingSpot.isBookedByOurUser()) {
            if (charger.getLidStatus().equals("Locked")) {
                charger.setLidOpenedByOurUser(false);
            } else if (charger.getLidStatus().equals("Open")) {
                charger.setLidOpenedByOurUser(true);
            }
        }

        if (!evParkingSpot.getCharger().isLidOpenedByOurUser()) {
            buttonUseCharger.setText("Open the lid");
        } else {
            buttonUseCharger.setText("Close the lid");
        }


        buttonUseParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!evParkingSpot.isBookedByOurUser()) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).reserveEvParkingSpot(v, position);
                    }
                } else if (evParkingSpot.isBookedByOurUser()) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).leaveEvParkingSpot(v, position);
                    }

                }

            }
        });

        buttonUseCharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!evParkingSpot.getCharger().isLidOpenedByOurUser()) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).openChargerLid(v, position);
                    }
                } else if (evParkingSpot.getCharger().isLidOpenedByOurUser()) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).lockChargerLid(v, position);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != evParkingSpots ? evParkingSpots.size() : 0);
    }


    public void changeParkingBookState(int position, String responseCode) {
        if (responseCode.equals("200")) {
            if (evParkingSpots.get(position).isBookedByOurUser()) {
                evParkingSpots.get(position).setBookedByOurUser(false);
                evParkingSpots.get(position).setAvailable(true);
                evParkingSpots.get(position).setUser("NONE");
                Toast.makeText(mContext, "Successfully left the parking", Toast.LENGTH_SHORT).show();
            } else if (!evParkingSpots.get(position).isBookedByOurUser()) {
                evParkingSpots.get(position).setBookedByOurUser(true);
                evParkingSpots.get(position).setAvailable(false);
                evParkingSpots.get(position).setUser("TK");
                Toast.makeText(mContext, "Successfully booked the parking", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Something got wrong, try again", Toast.LENGTH_SHORT).show();
        }
        notifyItemChanged(position);
    }

    public void changeUseChargerState(int position, String responseCode) {
        if (responseCode.equals("200")) {
            if (evParkingSpots.get(position).getCharger().isLidOpenedByOurUser()) {
                evParkingSpots.get(position).getCharger().setLidOpenedByOurUser(false);
                evParkingSpots.get(position).getCharger().setLidStatus("Locked");
                Toast.makeText(mContext, "Lid locked", Toast.LENGTH_SHORT).show();
            } else if (!evParkingSpots.get(position).getCharger().isLidOpenedByOurUser()) {
                evParkingSpots.get(position).getCharger().setLidOpenedByOurUser(true);
                evParkingSpots.get(position).getCharger().setLidStatus("Open");
                Toast.makeText(mContext, "Lid opened, you can use the charger",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Something got wrong, try again", Toast.LENGTH_SHORT).show();
        }
        notifyItemChanged(position);
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