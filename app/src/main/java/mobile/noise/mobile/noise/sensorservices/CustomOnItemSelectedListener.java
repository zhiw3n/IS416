package mobile.noise.mobile.noise.sensorservices;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Created by jessXW on 16/10/16.
 */

public class CustomOnItemSelectedListener implements OnItemSelectedListener {
    public static String globalSpinnerValue ;
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //shows value
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        globalSpinnerValue = parent.getItemAtPosition(pos).toString();
      //  SpinnerValue sv = new SpinnerValue(parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}