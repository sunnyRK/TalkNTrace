package module;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Dhrumit on 3/23/2017.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}

