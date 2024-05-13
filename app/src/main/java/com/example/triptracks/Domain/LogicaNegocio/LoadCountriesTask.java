
package com.example.triptracks.Domain.LogicaNegocio;

import android.os.AsyncTask;
import com.beastwall.localisation.Localisation;
import com.beastwall.localisation.model.Country;
import com.example.triptracks.ItinActivity;
import com.example.triptracks.Presenter.ItneraryActivityView;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadCountriesTask extends AsyncTask<Void, Void, List<Country>> {
    private WeakReference<ItneraryActivityView> activityReference;

    public LoadCountriesTask(ItneraryActivityView context) {
        this.activityReference = new WeakReference<>(context);
    }

    @Override
    protected List<Country> doInBackground(Void... voids) {

        return Localisation.getAllCountriesStatesAndCities();
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        super.onPostExecute(countries);
        ItneraryActivityView activity = activityReference.get();
        if (activity != null && !activity.isFinishing()) {
            activity.onCountriesLoaded(countries);
        }
    }
}