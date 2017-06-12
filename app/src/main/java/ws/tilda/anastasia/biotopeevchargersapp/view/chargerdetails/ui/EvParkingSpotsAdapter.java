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

//        if(evParkingSpot.getUser().equals("TK")) {
//            evParkingSpot.setBookedByOurUser(true);
//        } else if ((evParkingSpot.getUser().equals("NONE"))) {
//            evParkingSpot.setBookedByOurUser(false);
//        } else {
//            evParkingSpot.setBookedByOurUser(false);
//        }



//        if (evParkingSpot.isBookedByOurUser()==true) {
//            buttonUseParking.setText("Leave Parking");
//            buttonUseParking.setEnabled(true);
//            buttonUseCharger.setEnabled(true);
//        } else if(evParkingSpot.isBookedByOurUser()==false) {
//            if(evParkingSpot.isAvailable() == true) {
//                buttonUseParking.setText("Use Parking");
//                buttonUseParking.setEnabled(true);
//                buttonUseCharger.setEnabled(false);
//            } else if (evParkingSpot.isAvailable() == false) {
//                buttonUseParking.setText("Use Parking");
//                buttonUseParking.setEnabled(false);
//                buttonUseCharger.setEnabled(false);
//            }
//        }



        if (evParkingSpot.isAvailable() == false) {
            if (evParkingSpot.getUser().equals(((EvSpotListActivity) mContext).getUser().getUsername())) {
                evParkingSpot.setBookedByOurUser(true);
                buttonUseParking.setEnabled(true);
                buttonUseParking.setText("Leave Parking");
                buttonUseCharger.setEnabled(true);
                evParkingSpot.getCharger().setLidOpened(false);
            } else {
                evParkingSpot.setBookedByOurUser(false);
                buttonUseParking.setEnabled(false);
                buttonUseParking.setText("Use Parking");
                evParkingSpot.getCharger().setLidOpened(false);
            }
        } else {
            evParkingSpot.setBookedByOurUser(false);
            buttonUseParking.setEnabled(true);
            buttonUseParking.setText("Use Parking");
            buttonUseCharger.setEnabled(true);
            evParkingSpot.getCharger().setLidOpened(false);
        }

        if (evParkingSpot.isBookedByOurUser() == false) {
            if(evParkingSpot.isAvailable() == false) {
                buttonUseParking.setEnabled(false);
                buttonUseParking.setText("Use Parking");
                buttonUseCharger.setEnabled(false);
            } else if(evParkingSpot.isAvailable() == true) {
                buttonUseParking.setEnabled(true);
                buttonUseParking.setText("Use Parking");
                buttonUseCharger.setEnabled(false);
            }
        } else {
            buttonUseCharger.setEnabled(true);

        }



        buttonUseParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (evParkingSpot.isBookedByOurUser() == false) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).reserveEvParkingSpot(v, position);
                        if (evParkingSpot.isBookedByOurUser() == true) {
                            buttonUseParking.setEnabled(true);
                            buttonUseParking.setText("Leave Parking");

                            buttonUseCharger.setEnabled(true);
                            evParkingSpot.setUser("TK");
                        }
                    }
                } else if (evParkingSpot.isBookedByOurUser() == true) {
                    if (mContext instanceof EvSpotListActivity) {
                        ((EvSpotListActivity) mContext).leaveEvParkingSpot(v, position);
                        if (evParkingSpot.isBookedByOurUser() == false) {
                            buttonUseParking.setEnabled(true);
                            buttonUseParking.setText("Use Parking");

                            buttonUseCharger.setEnabled(true);
                            evParkingSpot.setUser("NONE");
                        }

                    }

                }

            }
        });

//        buttonUseCharger.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (evParkingSpot.getCharger().isLidOpened() == false) {
//                    if (mContext instanceof EvSpotListActivity) {
//                        ((EvSpotListActivity) mContext).useCharger(position);
//                        if (evParkingSpot.getCharger().isLidOpened() == true)
//
//                            buttonUseCharger.setText("Close charger");
//
//                    }
//                }
//                } else if (evParkingSpot.isBookedByOurUser() == true) {
//                    if (mContext instanceof EvSpotListActivity) {
//                        ((EvSpotListActivity) mContext).leaveEvParkingSpot(v, position);
//                        if (evParkingSpot.isBookedByOurUser() == false) {
//                            buttonUseParking.setEnabled(true);
//                            buttonUseParking.setText("Use Parking");
//
//                            buttonUseCharger.setEnabled(true);
//                            evParkingSpot.setUser("NONE");
//                        }
//                    }
//
//                }

//            }
//        });

    }

    @Override
    public int getItemCount() {
        return (null != evParkingSpots ? evParkingSpots.size() : 0);
    }


    public void changeParkingBookState(int position, String responseCode) {
        if (responseCode.equals("200")) {
            if (evParkingSpots.get(position).isBookedByOurUser() == true) {
                evParkingSpots.get(position).setBookedByOurUser(false);
                evParkingSpots.get(position).setAvailable(true);
            } else if (evParkingSpots.get(position).isBookedByOurUser() == false) {
                evParkingSpots.get(position).setBookedByOurUser(true);
                evParkingSpots.get(position).setAvailable(false);
            }
            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Something got wrong, try again", Toast.LENGTH_SHORT).show();
        }
        notifyItemChanged(position);
    }

    public void changeUseChargerState(int position, String responseCode) {
        if (responseCode.equals("200")) {
            evParkingSpots.get(position).getCharger().setLidOpened(true);
            Toast.makeText(mContext, "Lid opened", Toast.LENGTH_SHORT).show();
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