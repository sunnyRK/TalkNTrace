package module; /**
 * Created by Dhrumit on 3/23/2017.
 */

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}